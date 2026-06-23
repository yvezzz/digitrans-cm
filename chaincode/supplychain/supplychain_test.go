package main

import (
	"encoding/json"
	"testing"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
	"github.com/hyperledger/fabric-contract-api-go/internal"
	"github.com/stretchr/testify/mock"
)

// MockStub simule le stub Fabric pour les tests
type MockStub struct {
	mock.Mock
	state map[string][]byte
	txID  string
}

func NewMockStub() *MockStub {
	return &MockStub{
		state: make(map[string][]byte),
		txID:  "test-tx-001",
	}
}

func (m *MockStub) GetState(key string) ([]byte, error) {
	val, ok := m.state[key]
	if !ok {
		return nil, nil
	}
	return val, nil
}

func (m *MockStub) PutState(key string, value []byte) error {
	m.state[key] = value
	return nil
}

func (m *MockStub) GetTxID() string {
	return m.txID
}

func (m *MockStub) SetEvent(name string, payload []byte) error {
	return nil
}

// TransactionContextInterface mock
type MockTransactionContext struct {
	contractapi.TransactionContext
	stub *MockStub
}

func (ctx *MockTransactionContext) GetStub() contractapi.TransactionContextInterface {
	return ctx.stub
}

func (ctx *MockTransactionContext) GetClientIdentity() contractapi.TransactionContextInterface {
	return ctx
}

func (ctx *MockTransactionContext) GetID() (string, error) {
	return "x509::CN=logistics01, OU=Supply+Chain, O=AGROCAM, L=Douala, ST=LC, C=CM::CN=ca.agrocam.cm", nil
}

func TestCreateShipment_Success(t *testing.T) {
	ctx := &MockTransactionContext{stub: NewMockStub()}
	contract := new(SupplyChainContract)

	err := contract.CreateShipment(ctx,
		"SH-2026-0001",
		"PROD-001",
		"Cacao Bio",
		"Plantation Nkongsamba",
		"Usine Douala",
		500.0,
	)

	if err != nil {
		t.Fatalf("CreateShipment should succeed, got: %v", err)
	}

	// Vérifier que la donnée est bien stockée
	data, _ := ctx.GetStub().GetState("SH-2026-0001")
	if data == nil {
		t.Fatal("Shipment should be stored in ledger")
	}

	var shipment Shipment
	json.Unmarshal(data, &shipment)

	if shipment.ShipmentID != "SH-2026-0001" {
		t.Errorf("Expected SH-2026-0001, got %s", shipment.ShipmentID)
	}
	if shipment.Status != "CREATED" {
		t.Errorf("Expected CREATED, got %s", shipment.Status)
	}
	if shipment.Quantity != 500.0 {
		t.Errorf("Expected 500.0, got %f", shipment.Quantity)
	}
}

func TestCreateShipment_Duplicate(t *testing.T) {
	ctx := &MockTransactionContext{stub: NewMockStub()}
	contract := new(SupplyChainContract)

	// Créer une première fois
	contract.CreateShipment(ctx, "SH-DUP", "PROD-001", "Test", "A", "B", 10.0)

	// Tentative de doublon
	err := contract.CreateShipment(ctx, "SH-DUP", "PROD-001", "Test", "A", "B", 10.0)
	if err == nil {
		t.Fatal("Duplicate shipment should fail")
	}
}

func TestCreateShipment_InvalidQuantity(t *testing.T) {
	ctx := &MockTransactionContext{stub: NewMockStub()}
	contract := new(SupplyChainContract)

	err := contract.CreateShipment(ctx, "SH-NEG", "PROD-001", "Test", "A", "B", -5.0)
	if err == nil {
		t.Fatal("Negative quantity should fail")
	}

	err = contract.CreateShipment(ctx, "SH-ZERO", "PROD-001", "Test", "A", "B", 0.0)
	if err == nil {
		t.Fatal("Zero quantity should fail")
	}
}

func TestUpdateStatus_ValidTransitions(t *testing.T) {
	ctx := &MockTransactionContext{stub: NewMockStub()}
	contract := new(SupplyChainContract)

	contract.CreateShipment(ctx, "SH-TRANS", "PROD-001", "Test", "A", "B", 100.0)

	tests := []struct {
		from string
		to   string
		want bool
	}{
		{"CREATED", "IN_TRANSIT", true},
		{"IN_TRANSIT", "DELIVERED", true},
		{"DELIVERED", "VERIFIED", true},
		{"CREATED", "DELIVERED", false},
		{"IN_TRANSIT", "CREATED", false},
		{"VERIFIED", "DELIVERED", false},
	}

	for _, tt := range tests {
		// Forcer le statut initial
		shipment := Shipment{Status: tt.from}
		data, _ := json.Marshal(shipment)
		ctx.GetStub().PutState("SH-TRANS", data)

		err := contract.UpdateStatus(ctx, "SH-TRANS", tt.to)
		got := (err == nil)
		if got != tt.want {
			t.Errorf("Transition %s → %s: got success=%v, want %v",
				tt.from, tt.to, got, tt.want)
		}
	}
}

func TestGetShipment_NotFound(t *testing.T) {
	ctx := &MockTransactionContext{stub: NewMockStub()}
	contract := new(SupplyChainContract)

	_, err := contract.GetShipment(ctx, "NONEXISTENT")
	if err == nil {
		t.Fatal("GetShipment on nonexistent ID should fail")
	}
}

func TestGetShipmentHistory_Empty(t *testing.T) {
	ctx := &MockTransactionContext{stub: NewMockStub()}
	contract := new(SupplyChainContract)

	history, err := contract.GetShipmentHistory(ctx, "SH-EMPTY")
	if err != nil {
		t.Fatalf("GetShipmentHistory on empty should return empty, got: %v", err)
	}
	if len(history) != 0 {
		t.Errorf("Expected empty history, got %d entries", len(history))
	}
}
