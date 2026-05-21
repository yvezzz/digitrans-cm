package com.camtech.digitranscm.service;

import com.camtech.digitranscm.repository.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BiService {

    private final ErpEmployeeRepository employeeRepo;
    private final ErpInvoiceRepository invoiceRepo;
    private final CrmClientRepository clientRepo;
    private final CrmRestaurantRepository restaurantRepo;
    private final SupplyProductRepository productRepo;
    private final SupplyShipmentRepository shipmentRepo;

    public BiService(ErpEmployeeRepository employeeRepo,
                     ErpInvoiceRepository invoiceRepo,
                     CrmClientRepository clientRepo,
                     CrmRestaurantRepository restaurantRepo,
                     SupplyProductRepository productRepo,
                     SupplyShipmentRepository shipmentRepo) {
        this.employeeRepo = employeeRepo;
        this.invoiceRepo = invoiceRepo;
        this.clientRepo = clientRepo;
        this.restaurantRepo = restaurantRepo;
        this.productRepo = productRepo;
        this.shipmentRepo = shipmentRepo;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalEmployes", employeeRepo.count());
        stats.put("totalClients", clientRepo.count());
        stats.put("totalRestaurants", restaurantRepo.count());
        stats.put("totalProduits", productRepo.count());
        stats.put("totalExpeditions", shipmentRepo.count());
        stats.put("totalFactures", invoiceRepo.count());

        stats.put("employesActifs", employeeRepo.findByStatut("ACTIF").size());
        stats.put("clientsActifs", clientRepo.findByStatut("ACTIF").size());
        stats.put("expeditionsEnCours", shipmentRepo.findByStatut("EN_ATTENTE").size());

        return stats;
    }
}
