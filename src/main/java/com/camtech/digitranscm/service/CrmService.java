package com.camtech.digitranscm.service;

import com.camtech.digitranscm.dto.CrmClientDTO;
import com.camtech.digitranscm.dto.CrmRestaurantDTO;
import com.camtech.digitranscm.entity.CrmClient;
import com.camtech.digitranscm.entity.CrmRestaurant;
import com.camtech.digitranscm.exception.ResourceNotFoundException;
import com.camtech.digitranscm.repository.CrmClientRepository;
import com.camtech.digitranscm.repository.CrmRestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CrmService {

    private final CrmClientRepository clientRepo;
    private final CrmRestaurantRepository restaurantRepo;

    public CrmService(CrmClientRepository clientRepo, CrmRestaurantRepository restaurantRepo) {
        this.clientRepo = clientRepo;
        this.restaurantRepo = restaurantRepo;
    }

    // --- Clients ---
    public List<CrmClientDTO> getAllClients() {
        return clientRepo.findAll().stream()
                .map(this::toClientDTO)
                .collect(Collectors.toList());
    }

    public CrmClientDTO getClientById(Long id) {
        CrmClient client = clientRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id: " + id));
        return toClientDTO(client);
    }

    public CrmClientDTO createClient(CrmClientDTO dto) {
        CrmClient client = new CrmClient();
        client.setNom(dto.getNom());
        client.setTelephone(dto.getTelephone());
        client.setEmail(dto.getEmail());
        client.setAdresse(dto.getAdresse());
        client.setVille(dto.getVille());
        client.setCategorie(dto.getCategorie());
        client.setStatut(dto.getStatut() != null ? dto.getStatut() : "ACTIF");
        return toClientDTO(clientRepo.save(client));
    }

    public CrmClientDTO updateClient(Long id, CrmClientDTO dto) {
        CrmClient client = clientRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id: " + id));
        client.setNom(dto.getNom());
        client.setTelephone(dto.getTelephone());
        client.setEmail(dto.getEmail());
        client.setAdresse(dto.getAdresse());
        client.setVille(dto.getVille());
        client.setCategorie(dto.getCategorie());
        if (dto.getStatut() != null) client.setStatut(dto.getStatut());
        return toClientDTO(clientRepo.save(client));
    }

    public void deleteClient(Long id) {
        if (!clientRepo.existsById(id)) {
            throw new ResourceNotFoundException("Client introuvable avec l'id: " + id);
        }
        clientRepo.deleteById(id);
    }

    // --- Restaurants ---
    public List<CrmRestaurantDTO> getAllRestaurants() {
        return restaurantRepo.findAll().stream()
                .map(this::toRestaurantDTO)
                .collect(Collectors.toList());
    }

    public CrmRestaurantDTO getRestaurantById(Long id) {
        CrmRestaurant rest = restaurantRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant introuvable avec l'id: " + id));
        return toRestaurantDTO(rest);
    }

    public CrmRestaurantDTO createRestaurant(CrmRestaurantDTO dto) {
        CrmRestaurant rest = new CrmRestaurant();
        rest.setNom(dto.getNom());
        rest.setAdresse(dto.getAdresse());
        rest.setVille(dto.getVille());
        rest.setTelephone(dto.getTelephone());
        rest.setGerant(dto.getGerant());
        rest.setCapacite(dto.getCapacite());
        rest.setStatut(dto.getStatut() != null ? dto.getStatut() : "OUVERT");
        return toRestaurantDTO(restaurantRepo.save(rest));
    }

    public CrmRestaurantDTO updateRestaurant(Long id, CrmRestaurantDTO dto) {
        CrmRestaurant rest = restaurantRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant introuvable avec l'id: " + id));
        rest.setNom(dto.getNom());
        rest.setAdresse(dto.getAdresse());
        rest.setVille(dto.getVille());
        rest.setTelephone(dto.getTelephone());
        rest.setGerant(dto.getGerant());
        rest.setCapacite(dto.getCapacite());
        if (dto.getStatut() != null) rest.setStatut(dto.getStatut());
        return toRestaurantDTO(restaurantRepo.save(rest));
    }

    public void deleteRestaurant(Long id) {
        if (!restaurantRepo.existsById(id)) {
            throw new ResourceNotFoundException("Restaurant introuvable avec l'id: " + id);
        }
        restaurantRepo.deleteById(id);
    }

    // --- Mappers ---
    private CrmClientDTO toClientDTO(CrmClient client) {
        CrmClientDTO dto = new CrmClientDTO();
        dto.setId(client.getId());
        dto.setNom(client.getNom());
        dto.setTelephone(client.getTelephone());
        dto.setEmail(client.getEmail());
        dto.setAdresse(client.getAdresse());
        dto.setVille(client.getVille());
        dto.setCategorie(client.getCategorie());
        dto.setStatut(client.getStatut());
        return dto;
    }

    private CrmRestaurantDTO toRestaurantDTO(CrmRestaurant rest) {
        CrmRestaurantDTO dto = new CrmRestaurantDTO();
        dto.setId(rest.getId());
        dto.setNom(rest.getNom());
        dto.setEnseigne(rest.getEnseigne());
        dto.setAdresse(rest.getAdresse());
        dto.setVille(rest.getVille());
        dto.setTelephone(rest.getTelephone());
        dto.setGerant(rest.getGerant());
        dto.setCapacite(rest.getCapacite());
        dto.setStatut(rest.getStatut());
        return dto;
    }
}
