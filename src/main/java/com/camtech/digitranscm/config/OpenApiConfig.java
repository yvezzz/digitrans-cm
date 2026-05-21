package com.camtech.digitranscm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("DIGITRANS-CM API")
                .description("API du projet DIGITRANS-CM - CAMTECH SOLUTIONS S.A.\n" +
                    "Modernisation du SI d'AGROCAM S.A.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("CAMTECH SOLUTIONS")
                    .email("support@camtech.cm")
                    .url("https://camtech.cm"))
                .license(new License()
                    .name("Propriétaire - CAMTECH SOLUTIONS")))
            .servers(List.of(
                new Server().url("http://localhost:8080/api/v1").description("Local dev"),
                new Server().url("https://api.digitrans.camtech.cm/api/v1").description("Production")
            ));
    }
}
