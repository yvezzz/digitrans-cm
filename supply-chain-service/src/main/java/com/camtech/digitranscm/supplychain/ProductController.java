package com.camtech.digitranscm.supplychain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ProductController(ProductRepository productRepository,
                             StringRedisTemplate redisTemplate,
                             ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable String productId) {
        String cacheKey = "product:" + productId;
        String cachedValue = redisTemplate.opsForValue().get(cacheKey);
        if (cachedValue != null) {
            try {
                Product cachedProduct = objectMapper.readValue(cachedValue, Product.class);
                return ResponseEntity.ok(Map.of("source", "cache", "product", cachedProduct));
            } catch (JsonProcessingException e) {
                redisTemplate.delete(cacheKey);
            }
        }

        Optional<Product> optionalProduct = productRepository.findByProductId(productId);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Produit non trouvé"));
        }

        Product product = optionalProduct.get();
        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(product));
        } catch (JsonProcessingException ignored) {
        }

        return ResponseEntity.ok(Map.of("source", "database", "product", product));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        if (product.getProductId() == null || product.getProductId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (product.getHarvestDate() == null) {
            product.setHarvestDate(LocalDate.now());
        }
        if (product.getStatus() == null) {
            product.setStatus("pending");
        }
        product.setBlockchainHash(generateHash(product.getProductId(), product.getName(), product.getOrigin(), product.getHarvestDate()));
        Product saved = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Supply Chain Service is healthy");
    }

    private String generateHash(String productId, String name, String origin, LocalDate harvestDate) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String content = productId + "|" + name + "|" + origin + "|" + harvestDate;
            byte[] hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erreur de génération de hash", e);
        }
    }
}
