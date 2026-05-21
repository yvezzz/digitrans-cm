package com.camtech.digitranscm.controller;

import com.camtech.digitranscm.service.ErpService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.camtech.digitranscm.dto.ErpEmployeeDTO;
import com.camtech.digitranscm.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ErpController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
})
@ActiveProfiles("h2")
class ErpControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ErpService erpService;

    @Test
    void getAllEmployees_returns200() throws Exception {
        when(erpService.getAllEmployees()).thenReturn(List.of(new ErpEmployeeDTO()));
        mockMvc.perform(get("/erp/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getEmployeeById_returns200() throws Exception {
        ErpEmployeeDTO dto = new ErpEmployeeDTO();
        dto.setId(1L);
        dto.setNom("Jean");
        when(erpService.getEmployeeById(1L)).thenReturn(dto);
        mockMvc.perform(get("/erp/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Jean"));
    }

    @Test
    void getEmployeeById_returns404() throws Exception {
        when(erpService.getEmployeeById(99L)).thenThrow(new ResourceNotFoundException("introuvable"));
        mockMvc.perform(get("/erp/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEmployee_returns201() throws Exception {
        ErpEmployeeDTO dto = new ErpEmployeeDTO();
        dto.setNom("Paul");
        dto.setPrenom("Martin");
        dto.setEmail("paul@test.cm");
        dto.setDepartement("IT");
        dto.setPoste("Dev");
        dto.setDateEmbauche(LocalDate.now());

        ErpEmployeeDTO saved = new ErpEmployeeDTO();
        saved.setId(1L);
        saved.setNom("Paul");
        when(erpService.createEmployee(any())).thenReturn(saved);

        mockMvc.perform(post("/erp/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Paul"));
    }

    @Test
    void deleteEmployee_returns204() throws Exception {
        doNothing().when(erpService).deleteEmployee(1L);
        mockMvc.perform(delete("/erp/employees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllSuppliers_returns200() throws Exception {
        when(erpService.getAllSuppliers()).thenReturn(List.of());
        mockMvc.perform(get("/erp/suppliers"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllInvoices_returns200() throws Exception {
        when(erpService.getAllInvoices()).thenReturn(List.of());
        mockMvc.perform(get("/erp/invoices"))
                .andExpect(status().isOk());
    }
}
