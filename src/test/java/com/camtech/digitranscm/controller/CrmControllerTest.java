package com.camtech.digitranscm.controller;

import com.camtech.digitranscm.dto.CrmClientDTO;
import com.camtech.digitranscm.dto.CrmRestaurantDTO;
import com.camtech.digitranscm.service.CrmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = CrmController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
})
@ActiveProfiles("h2")
class CrmControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CrmService crmService;

    @Test
    void getAllClients_returns200() throws Exception {
        when(crmService.getAllClients()).thenReturn(List.of(new CrmClientDTO()));
        mockMvc.perform(get("/crm/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void createClient_returns201() throws Exception {
        CrmClientDTO dto = new CrmClientDTO();
        dto.setNom("Alice");
        dto.setVille("Douala");

        CrmClientDTO saved = new CrmClientDTO();
        saved.setId(1L);
        saved.setNom("Alice");
        when(crmService.createClient(any())).thenReturn(saved);

        mockMvc.perform(post("/crm/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Alice"));
    }

    @Test
    void getAllRestaurants_returns200() throws Exception {
        when(crmService.getAllRestaurants()).thenReturn(List.of(new CrmRestaurantDTO()));
        mockMvc.perform(get("/crm/restaurants"))
                .andExpect(status().isOk());
    }
}
