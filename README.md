# DIGITRANS-CM

**Modernisation du Système d'Information d'AGROCAM S.A.**

Projet porté par **CAMTECH SOLUTIONS S.A.** (Douala, Cameroun) dans le cadre de la transformation numérique du groupe agroalimentaire AGROCAM S.A. Budget : 480M FCFA, janvier 2026 — juin 2027.

---

## 1. Schéma d'architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                     DIGITRANS-CM Architecture                     │
│                       Cloud hybride AWS + Azure                   │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────┐  ┌──────────┐  ┌────────────┐  ┌──────────┐      │
│  │   ERP    │  │   CRM    │  │ Supply     │  │   BI     │      │
│  │  Module  │  │  Module  │  │ Chain      │  │  Module  │      │
│  └────┬─────┘  └────┬─────┘  └────┬───────┘  └────┬─────┘      │
│       │             │             │               │             │
│       └─────────────┴─────────────┴───────────────┘             │
│                         │                                        │
│                  ┌──────┴──────┐                                 │
│                  │  Spring     │  ┌────────────┐                │
│                  │  Security   │  │   Redis    │                │
│                  │  (OAuth2)   │  │  (cache)   │                │
│                  └──────┬──────┘  └────────────┘                │
│                         │                                        │
│                  ┌──────┴──────┐   ┌──────────────┐             │
│                  │ PostgreSQL  │   │ CloudWatch   │             │
│                  │  (RDS)      │   │ (Monitoring) │             │
│                  └──────┬──────┘   └──────────────┘             │
│                         │                                        │
│  ┌──────────────────────┴────────────────────────────────┐      │
│  │           AWS Cloud (af-south-1, Cape Town)           │      │
│  │  VPC → Subnets → ALB → Auto Scaling → RDS Multi-AZ   │      │
│  └───────────────────────────────────────────────────────┘      │
│                                                                   │
│  On-premise (Cameroun) : Donnees RH + Financieres                │
│  Cloud (af-south-1)     : APIs, BI, CRM                          │
└──────────────────────────────────────────────────────────────────┘
```

---

## 2. Choix technologiques

### 2.1. Regions cloud africaines

Conformement a la section 1.2.1 du sujet, nous avons choisi :
- **AWS af-south-1 (Cape Town)** : latence ~150-200ms depuis Douala, conforme a la souverainete des donnees (reste en Afrique)
- Alternative Azure South Africa North non retenue car AWS offre un meilleur rapport qualite-prix pour les services PaaS (RDS, ElastiCache)

### 2.2. Stockage on-premise vs cloud

| Donnees | Localisation | Justification |
|---------|-------------|---------------|
| RH, financieres, clients | On-premise (Cameroun) | Loi 2010/012, souverainete |
| APIs, BI, CRM, Cache | Cloud af-south-1 | Haute disponibilite, scalabilite |

### 2.3. Architecture resiliente

- Multi-AZ RDS en production (basculement automatique)
- Auto Scaling group (min 2, max 6 instances)
- ALB (Application Load Balancer) pour repartition de charge
- Cache Redis pour fonctionnement degrade hors-ligne
- Snapshots quotidiens + retention 30 jours en prod

### 2.4. Stack technique

| Composant | Technologie | Justification |
|-----------|-------------|---------------|
| Backend | Java 17, Spring Boot 3.2.5 | Maturite, ecosysteme cloud |
| Base de donnees | PostgreSQL 16 (AWS RDS) | Fiable, gratuit, recommande dans le sujet (via Azure SQL ou RDS) |
| Authentification | OAuth 2.0 / JWT | Exige section 1.2.3 |
| Documentation API | Springdoc OpenAPI (Swagger) | Exige section 1.2.3 |
| Cache offline-first | Redis 7 | Exige section 1.2.4 |
| Supervision | AWS CloudWatch | Exige section 1.4.3 |
| Conteneurisation | Docker | Exige section 1.3.4 |
| CI/CD | GitHub Actions | Exige section 1.3.3 |
| IaC | AWS CloudFormation | Exige section 1.3.1 |

---

## 3. Modules fonctionnels

| Module | Entites | Description |
|--------|---------|-------------|
| **ERP** | Employee, Supplier, Invoice | RH, fournisseurs, comptabilite |
| **CRM** | Client, Restaurant | Relation client, restaurants SavoirManger |
| **Supply Chain** | Product, Shipment | Traçabilite plantations → transformation → vente |
| **BI** | Dashboard (stats) | Indicateurs cles (employes, clients, expeditions) |

---

## 4. API REST

Documentation Swagger complete : [http://localhost:8081/api/v1/swagger-ui.html](http://localhost:8081/api/v1/swagger-ui.html)

### Endpoints

| Methode | Path | Module | Auth |
|---------|------|--------|------|
| GET | `/api/v1/erp/employees` | ERP | ADMIN |
| POST | `/api/v1/erp/employees` | ERP | ADMIN |
| GET | `/api/v1/erp/suppliers` | ERP | ADMIN |
| GET | `/api/v1/erp/invoices` | ERP | ADMIN |
| GET | `/api/v1/crm/clients` | CRM | ADMIN, SALES |
| GET | `/api/v1/crm/restaurants` | CRM | ADMIN, SALES |
| GET | `/api/v1/supply/products` | Supply Chain | ADMIN, LOGISTICS |
| GET | `/api/v1/supply/shipments` | Supply Chain | ADMIN, LOGISTICS |
| GET | `/api/v1/bi/dashboard` | BI | ADMIN, MANAGER |

---

## 5. Deploiement

### 5.1. Local (dev)

```bash
# Pre-requis : Java 17+, Docker
docker-compose up -d          # PostgreSQL + Redis
mvn spring-boot:run            # Port 8081
```

### 5.2. Profils disponibles

| Profil | Usage | DB | CloudWatch |
|--------|-------|----|------------|
| dev | Developpement local | localhost:5432 | desactive |
| test | Tests automatises | digitrans_test | desactive |
| prod | Production | AWS RDS | active |

### 5.3. Pipeline CI/CD (GitHub Actions)

```yaml
# .github/workflows/ci.yml
# Jobs : build-and-test + docker (push sur main)
```

---

## 6. Variables d'environnement

| Variable | Defaut | Description |
|----------|--------|-------------|
| `DB_HOST` | localhost | Hote PostgreSQL |
| `DB_PORT` | 5432 | Port PostgreSQL |
| `DB_NAME` | digitrans_db | Nom de la base |
| `DB_USERNAME` | postgres | Utilisateur DB |
| `DB_PASSWORD` | admin | Mot de passe DB |
| `REDIS_HOST` | localhost | Hote Redis |
| `REDIS_PORT` | 6379 | Port Redis |
| `SPRING_PROFILES_ACTIVE` | dev | Profil actif |

---

## 7. Reprise apres sinistre (RTO / RPO)

| Environnement | RPO | RTO | Strategie |
|--------------|-----|-----|-----------|
| Dev / Test | 24h | 4h | Sauvegarde quotidienne + restauration manuelle |
| Production | 1h | 30min | Multi-AZ RDS + Auto Scaling + snapshots automatiques |

---

## 8. Tests

### 8.1. Tests unitaires
- JUnit 5 + Spring Boot Test
- Contexte Spring charge via `@SpringBootTest`

### 8.2. Tests de charge (K6)
```bash
k6 run loadtests/k6-load-test.js
```
Scenarios : montee progressive 20 → 100 utilisateurs simultanes.
KPIs : P95 < 2000ms, taux d'erreur < 10%.

---

## 9. Infrastructure as Code (CloudFormation)

Fichier : `infra/cloudformation-template.yaml`

Ressources creees :
- VPC (CIDR 10.0.0.0/16)
- 2 sous-reseaux publics + 1 prive
- ALB + Target Group + Listener
- Auto Scaling Group (min 1, max 6)
- RDS PostgreSQL (Multi-AZ en prod)
- Groupes de securite (moindre privilege)

Deploiement :
```bash
aws cloudformation deploy \
  --template-file infra/cloudformation-template.yaml \
  --stack-name digitrans-cm-dev \
  --region af-south-1
```

---

## 10. Rapport d'activite (collectif)

### Repartition des taches
- Backend (API, entites, services) : equipe 1
- Securite (JWT, RBAC) : equipe 2
- DevOps (Docker, CI/CD, IaC) : equipe 3
- Tests et documentation : equipe 1 + 3

### Difficultes rencontrees
- Configuration OAuth2/JWT avec ressource server Spring Security
- Gestion du cache Redis pour le mode offline-first
- Adaptation au contexte camerounais (latence, souverainete)

---

## Auteurs

**CAMTECH SOLUTIONS S.A.** — Douala, Cameroun  
Projet DIGITRANS-CM — 2026
