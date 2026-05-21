package com.camtech.digitranscm.controller;

import com.camtech.digitranscm.dto.SupplyProductDTO;
import com.camtech.digitranscm.dto.SupplyShipmentDTO;
import com.camtech.digitranscm.service.SupplyChainService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = SupplyChainController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
})
@ActiveProfiles("h2")
class SupplyChainControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private SupplyChainService supplyChainService;

    @Test
    void getAllProducts_returns200() throws Exception {
        when(supplyChainService.getAllProducts()).thenReturn(List.of(new SupplyProductDTO()));
        mockMvc.perform(get("/supply/products"))
                .andExpect(status().isOk());
    }

    @Test
    void createProduct_returns201() throws Exception {
        SupplyProductDTO dto = new SupplyProductDTO();
        dto.setNom("Cacao");
        dto.setType("Matiere premiere");
        dto.setPrixUnitaire(BigDecimal.valueOf(1000));

        SupplyProductDTO saved = new SupplyProductDTO();
        saved.setId(1L);
        saved.setNom("Cacao");
        when(supplyChainService.createProduct(any())).thenReturn(saved);

        mockMvc.perform(post("/supply/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Cacao"));
    }

    @Test
    void getAllShipments_returns200() throws Exception {
        when(supplyChainService.getAllShipments()).thenReturn(List.of(new SupplyShipmentDTO()));
        mockMvc.perform(get("/supply/shipments"))
                .andExpect(status().isOk());
    }
}
