package main

import (
	"encoding/json"
	"fmt"
	"time"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// Shipment represents an expedition in the AGROCAM supply chain.
type Shipment struct {
	ShipmentID  string    `json:"shipmentId"`
	ProductID   string    `json:"productId"`
	ProductName string    `json:"productName"`
	Quantity    float64   `json:"quantity"`
	Origin      string    `json:"origin"`
	Destination string    `json:"destination"`
	Status      string    `json:"status"`
	CreatedBy   string    `json:"createdBy"`
	CreatedAt   time.Time `json:"createdAt"`
	UpdatedAt   time.Time `json:"updatedAt"`
	CertHash    string    `json:"certHash,omitempty"`
}

// AuditEntry records every state-changing operation.
type AuditEntry struct {
	TxID      string    `json:"txId"`
	Operation string    `json:"operation"`
	Actor     string    `json:"actor"`
	Timestamp time.Time `json:"timestamp"`
	Details   string    `json:"details"`
}

// SupplyChainContract is the main chaincode.
type SupplyChainContract struct {
	contractapi.Contract
}

// CreateShipment registers a new shipment on the blockchain.
func (s *SupplyChainContract) CreateShipment(ctx contractapi.TransactionContextInterface,
	shipmentID, productID, productName, origin, destination string,
	quantity float64) error {

	caller, err := ctx.GetClientIdentity().GetID()
	if err != nil {
		return fmt.Errorf("failed to get caller identity: %v", err)
	}

	existing, _ := ctx.GetStub().GetState(shipmentID)
	if existing != nil {
		return fmt.Errorf("shipment %s already exists", shipmentID)
	}

	if quantity <= 0 {
		return fmt.Errorf("quantity must be positive, got %f", quantity)
	}

	now := time.Now()
	shipment := Shipment{
		ShipmentID:  shipmentID,
		ProductID:   productID,
		ProductName: productName,
		Quantity:    quantity,
		Origin:      origin,
		Destination: destination,
		Status:      "CREATED",
		CreatedBy:   caller,
		CreatedAt:   now,
		UpdatedAt:   now,
	}

	shipmentJSON, _ := json.Marshal(shipment)
	err = ctx.GetStub().PutState(shipmentID, shipmentJSON)
	if err != nil {
		return fmt.Errorf("failed to store shipment: %v", err)
	}

	audit := AuditEntry{
		TxID:      ctx.GetStub().GetTxID(),
		Operation: "CREATE_SHIPMENT",
		Actor:     caller,
		Timestamp: now,
		Details:   fmt.Sprintf("Shipment %s created: %s → %s, %f %s", shipmentID, origin, destination, quantity, productName),
	}
	auditJSON, _ := json.Marshal(audit)
	ctx.GetStub().PutState("audit_"+ctx.GetStub().GetTxID(), auditJSON)

	ctx.GetStub().SetEvent("ShipmentCreated", shipmentJSON)
	return nil
}

// UpdateStatus changes the shipment status (CREATED → IN_TRANSIT → DELIVERED → VERIFIED).
func (s *SupplyChainContract) UpdateStatus(ctx contractapi.TransactionContextInterface,
	shipmentID, newStatus string) error {

	caller, err := ctx.GetClientIdentity().GetID()
	if err != nil {
		return fmt.Errorf("failed to get caller identity: %v", err)
	}

	shipmentJSON, err := ctx.GetStub().GetState(shipmentID)
	if err != nil {
		return fmt.Errorf("failed to read shipment %s: %v", shipmentID, err)
	}
	if shipmentJSON == nil {
		return fmt.Errorf("shipment %s not found", shipmentID)
	}

	var shipment Shipment
	json.Unmarshal(shipmentJSON, &shipment)

	if !isValidTransition(shipment.Status, newStatus) {
		return fmt.Errorf("invalid status transition: %s → %s", shipment.Status, newStatus)
	}

	shipment.Status = newStatus
	shipment.UpdatedAt = time.Now()

	newShipmentJSON, _ := json.Marshal(shipment)
	ctx.GetStub().PutState(shipmentID, newShipmentJSON)

	audit := AuditEntry{
		TxID:      ctx.GetStub().GetTxID(),
		Operation: "UPDATE_STATUS",
		Actor:     caller,
		Timestamp: time.Now(),
		Details:   fmt.Sprintf("Shipment %s status changed to %s", shipmentID, newStatus),
	}
	auditJSON, _ := json.Marshal(audit)
	ctx.GetStub().PutState("audit_"+ctx.GetStub().GetTxID(), auditJSON)

	ctx.GetStub().SetEvent("ShipmentUpdated", newShipmentJSON)
	return nil
}

// TransferOwnership transfers a shipment to a new responsible party.
func (s *SupplyChainContract) TransferOwnership(ctx contractapi.TransactionContextInterface,
	shipmentID, newOwner string) error {

	caller, err := ctx.GetClientIdentity().GetID()
	if err != nil {
		return fmt.Errorf("failed to get caller identity: %v", err)
	}

	shipmentJSON, err := ctx.GetStub().GetState(shipmentID)
	if err != nil || shipmentJSON == nil {
		return fmt.Errorf("shipment %s not found", shipmentID)
	}

	var shipment Shipment
	json.Unmarshal(shipmentJSON, &shipment)
	shipment.UpdatedAt = time.Now()
	shipment.CreatedBy = newOwner

	newShipmentJSON, _ := json.Marshal(shipment)
	ctx.GetStub().PutState(shipmentID, newShipmentJSON)

	audit := AuditEntry{
		TxID:      ctx.GetStub().GetTxID(),
		Operation: "TRANSFER_OWNERSHIP",
		Actor:     caller,
		Timestamp: time.Now(),
		Details:   fmt.Sprintf("Shipment %s transferred to %s", shipmentID, newOwner),
	}
	auditJSON, _ := json.Marshal(audit)
	ctx.GetStub().PutState("audit_"+ctx.GetStub().GetTxID(), auditJSON)

	ctx.GetStub().SetEvent("OwnershipTransferred", newShipmentJSON)
	return nil
}

// VerifyShipment attaches a compliance certificate hash.
func (s *SupplyChainContract) VerifyShipment(ctx contractapi.TransactionContextInterface,
	shipmentID, certHash string) error {

	caller, err := ctx.GetClientIdentity().GetID()
	if err != nil {
		return fmt.Errorf("failed to get caller identity: %v", err)
	}

	shipmentJSON, err := ctx.GetStub().GetState(shipmentID)
	if err != nil || shipmentJSON == nil {
		return fmt.Errorf("shipment %s not found", shipmentID)
	}

	var shipment Shipment
	json.Unmarshal(shipmentJSON, &shipment)

	if shipment.Status != "DELIVERED" {
		return fmt.Errorf("shipment %s must be DELIVERED before verification, current: %s",
			shipmentID, shipment.Status)
	}

	shipment.Status = "VERIFIED"
	shipment.CertHash = certHash
	shipment.UpdatedAt = time.Now()

	newShipmentJSON, _ := json.Marshal(shipment)
	ctx.GetStub().PutState(shipmentID, newShipmentJSON)

	audit := AuditEntry{
		TxID:      ctx.GetStub().GetTxID(),
		Operation: "VERIFY_SHIPMENT",
		Actor:     caller,
		Timestamp: time.Now(),
		Details:   fmt.Sprintf("Shipment %s verified with cert %s", shipmentID, certHash),
	}
	auditJSON, _ := json.Marshal(audit)
	ctx.GetStub().PutState("audit_"+ctx.GetStub().GetTxID(), auditJSON)

	ctx.GetStub().SetEvent("ShipmentVerified", newShipmentJSON)
	return nil
}

// GetShipment returns the current state of a shipment.
func (s *SupplyChainContract) GetShipment(ctx contractapi.TransactionContextInterface,
	shipmentID string) (*Shipment, error) {

	shipmentJSON, err := ctx.GetStub().GetState(shipmentID)
	if err != nil {
		return nil, fmt.Errorf("failed to read shipment %s: %v", shipmentID, err)
	}
	if shipmentJSON == nil {
		return nil, fmt.Errorf("shipment %s not found", shipmentID)
	}

	var shipment Shipment
	json.Unmarshal(shipmentJSON, &shipment)
	return &shipment, nil
}

// GetShipmentHistory returns the full audit trail for a shipment.
func (s *SupplyChainContract) GetShipmentHistory(ctx contractapi.TransactionContextInterface,
	shipmentID string) ([]AuditEntry, error) {

	iterator, err := ctx.GetStub().GetHistoryForKey(shipmentID)
	if err != nil {
		return nil, fmt.Errorf("failed to get history for %s: %v", shipmentID, err)
	}
	defer iterator.Close()

	var history []AuditEntry
	for iterator.HasNext() {
		result, err := iterator.Next()
		if err != nil {
			return nil, fmt.Errorf("failed to iterate history: %v", err)
		}

		var shipment Shipment
		json.Unmarshal(result.Value, &shipment)

		entry := AuditEntry{
			TxID:      result.TxId,
			Operation: "STATE_CHANGE",
			Actor:     shipment.CreatedBy,
			Timestamp: shipment.UpdatedAt,
			Details:   fmt.Sprintf("Shipment %s: status=%s, quantity=%f", shipmentID, shipment.Status, shipment.Quantity),
		}
		history = append(history, entry)
	}

	return history, nil
}

// GetAllShipments returns all shipments (for BI dashboard).
func (s *SupplyChainContract) GetAllShipments(ctx contractapi.TransactionContextInterface) ([]Shipment, error) {
	query := `{"selector":{"shipmentId":{"$ne":""}}}`
	iterator, err := ctx.GetStub().GetQueryResult(query)
	if err != nil {
		return nil, fmt.Errorf("failed to query shipments: %v", err)
	}
	defer iterator.Close()

	var shipments []Shipment
	for iterator.HasNext() {
		result, err := iterator.Next()
		if err != nil {
			return nil, fmt.Errorf("failed to iterate results: %v", err)
		}
		var shipment Shipment
		json.Unmarshal(result.Value, &shipment)
		shipments = append(shipments, shipment)
	}
	return shipments, nil
}

// isValidTransition enforces the allowed status flow.
func isValidTransition(current, next string) bool {
	transitions := map[string]string{
		"CREATED":    "IN_TRANSIT",
		"IN_TRANSIT": "DELIVERED",
		"DELIVERED":  "VERIFIED",
	}
	expected, ok := transitions[current]
	return ok && expected == next
}

func main() {
	chaincode, err := contractapi.NewChaincode(&SupplyChainContract{})
	if err != nil {
		panic(fmt.Sprintf("Error creating chaincode: %v", err))
	}
	if err := chaincode.Start(); err != nil {
		panic(fmt.Sprintf("Error starting chaincode: %v", err))
	}
}
