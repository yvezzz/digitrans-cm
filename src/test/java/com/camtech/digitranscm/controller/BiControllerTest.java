package com.camtech.digitranscm.controller;

import com.camtech.digitranscm.service.BiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = BiController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
})
@ActiveProfiles("h2")
class BiControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private BiService biService;

    @Test
    void getDashboard_returns200() throws Exception {
        when(biService.getDashboardStats()).thenReturn(Map.of(
                "totalEmployes", 10L,
                "totalClients", 5L
        ));

        mockMvc.perform(get("/bi/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployes").value(10))
                .andExpect(jsonPath("$.totalClients").value(5));
    }
}
