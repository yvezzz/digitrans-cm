package com.camtech.digitranscm.controller;

import com.camtech.digitranscm.service.BiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/bi")
@Tag(name = "BI", description = "Tableaux de bord et indicateurs")
public class BiController {

    private final BiService biService;

    public BiController(BiService biService) {
        this.biService = biService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Retourne les indicateurs cles du tableau de bord")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(biService.getDashboardStats());
    }
}
