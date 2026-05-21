package com.camtech.digitranscm.service;

import com.camtech.digitranscm.dto.CrmClientDTO;
import com.camtech.digitranscm.dto.CrmRestaurantDTO;
import com.camtech.digitranscm.entity.CrmClient;
import com.camtech.digitranscm.entity.CrmRestaurant;
import com.camtech.digitranscm.exception.ResourceNotFoundException;
import com.camtech.digitranscm.repository.CrmClientRepository;
import com.camtech.digitranscm.repository.CrmRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmServiceTest {

    @Mock private CrmClientRepository clientRepo;
    @Mock private CrmRestaurantRepository restaurantRepo;

    private CrmService service;

    @BeforeEach
    void setUp() {
        service = new CrmService(clientRepo, restaurantRepo);
    }

    @Test
    void getAllClients_returnsList() {
        when(clientRepo.findAll()).thenReturn(List.of(new CrmClient()));
        assertThat(service.getAllClients()).hasSize(1);
    }

    @Test
    void getClientById_found() {
        CrmClient client = new CrmClient();
        client.setId(1L);
        client.setNom("Alice");
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        assertThat(service.getClientById(1L).getNom()).isEqualTo("Alice");
    }

    @Test
    void getClientById_notFound() {
        when(clientRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getClientById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createClient_saves() {
        CrmClientDTO dto = new CrmClientDTO();
        dto.setNom("Bob");
        dto.setVille("Douala");

        CrmClient saved = new CrmClient();
        saved.setId(1L);
        saved.setNom("Bob");
        saved.setStatut("ACTIF");
        when(clientRepo.save(any())).thenReturn(saved);

        assertThat(service.createClient(dto).getNom()).isEqualTo("Bob");
    }

    @Test
    void deleteClient_exists() {
        when(clientRepo.existsById(1L)).thenReturn(true);
        service.deleteClient(1L);
        verify(clientRepo).deleteById(1L);
    }

    @Test
    void deleteClient_notFound() {
        when(clientRepo.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> service.deleteClient(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllRestaurants_returnsList() {
        when(restaurantRepo.findAll()).thenReturn(List.of(new CrmRestaurant()));
        assertThat(service.getAllRestaurants()).hasSize(1);
    }

    @Test
    void createRestaurant_saves() {
        CrmRestaurantDTO dto = new CrmRestaurantDTO();
        dto.setNom("SavoirManger Douala");
        dto.setVille("Douala");

        CrmRestaurant saved = new CrmRestaurant();
        saved.setId(1L);
        saved.setNom("SavoirManger Douala");
        saved.setStatut("OUVERT");
        when(restaurantRepo.save(any())).thenReturn(saved);

        assertThat(service.createRestaurant(dto).getNom()).isEqualTo("SavoirManger Douala");
    }
}
