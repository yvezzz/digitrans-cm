package com.camtech.digitranscm.service;

import com.camtech.digitranscm.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BiServiceTest {

    @Mock private ErpEmployeeRepository employeeRepo;
    @Mock private ErpInvoiceRepository invoiceRepo;
    @Mock private CrmClientRepository clientRepo;
    @Mock private CrmRestaurantRepository restaurantRepo;
    @Mock private SupplyProductRepository productRepo;
    @Mock private SupplyShipmentRepository shipmentRepo;

    private BiService service;

    @BeforeEach
    void setUp() {
        service = new BiService(employeeRepo, invoiceRepo, clientRepo, restaurantRepo, productRepo, shipmentRepo);
    }

    @Test
    void getDashboardStats_returnsAllKeys() {
        when(employeeRepo.count()).thenReturn(10L);
        when(clientRepo.count()).thenReturn(5L);
        when(restaurantRepo.count()).thenReturn(3L);
        when(productRepo.count()).thenReturn(20L);
        when(shipmentRepo.count()).thenReturn(8L);
        when(invoiceRepo.count()).thenReturn(15L);
        when(employeeRepo.findByStatut("ACTIF")).thenReturn(List.of());
        when(clientRepo.findByStatut("ACTIF")).thenReturn(List.of());
        when(shipmentRepo.findByStatut("EN_ATTENTE")).thenReturn(List.of());

        var stats = service.getDashboardStats();

        assertThat(stats)
                .containsEntry("totalEmployes", 10L)
                .containsEntry("totalClients", 5L)
                .containsEntry("totalRestaurants", 3L)
                .containsEntry("totalProduits", 20L)
                .containsEntry("totalExpeditions", 8L)
                .containsEntry("totalFactures", 15L);
    }
}
