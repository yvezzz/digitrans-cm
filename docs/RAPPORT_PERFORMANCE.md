# Rapport d'Analyse de Performance - DIGITRANS-CM

## 1. Objectif

Mesurer et analyser les performances des API REST de l'application DIGITRANS-CM dans le cadre de la compétence C24.

## 2. KPIs définis

| Indicateur | Cible | Outil de mesure |
|------------|-------|-----------------|
| Uptime | > 99.9% | AWS CloudWatch |
| Latence P95 | < 2000ms | K6 |
| Débit | > 100 req/s | K6 |
| Utilisation CPU | < 70% | CloudWatch + Auto Scaling |
| Utilisation mémoire | < 80% | CloudWatch |

## 3. Scénario de test (K6)

Fichier : `loadtests/k6-load-test.js`

- Montée en charge progressive : 20 → 50 → 100 utilisateurs simultanés
- Durée totale : 10 minutes
- Endpoints testés : /erp/employees, /erp/suppliers, /erp/invoices, /crm/clients, /crm/restaurants, /supply/products, /supply/shipments, /bi/dashboard

Execution :

```bash
k6 run loadtests/k6-load-test.js
```

## 4. Résultats attendus (template)

| Endpoint | P95 (ms) | Erreurs | Débit (req/s) |
|----------|----------|---------|---------------|
| GET /erp/employees | // A MESURER | // | // |
| GET /crm/clients | // A MESURER | // | // |
| GET /supply/products | // A MESURER | // | // |
| GET /bi/dashboard | // A MESURER | // | // |

## 5. Optimisations mises en place

- Cache Redis sur les GET (TTL 30 min)
- Auto-scaling CPU > 70%
- Connexion pool HikariCP (max 20 en prod)
- Compression HTTP (a configurer dans le reverse proxy)

## 6. Recommandations

- // A COMPLETER APRES TESTS
