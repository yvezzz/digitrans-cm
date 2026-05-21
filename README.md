# DIGITRANS-CM

**Modernisation du Système d'Information d'AGROCAM S.A.**

Projet porté par **CAMTECH SOLUTIONS S.A.** (Douala, Cameroun) dans le cadre de la transformation numérique du groupe agroalimentaire AGROCAM S.A. Budget : 480M FCFA, janvier 2026 — juin 2027.

---

## 1. Schéma d'architecture

```
                        DIGITRANS-CM Architecture
                          Cloud hybride AWS + Azure

  ┌──────────┐  ┌──────────┐  ┌────────────┐  ┌──────────┐
  │   ERP    │  │   CRM    │  │ Supply     │  │   BI     │
  │  Module  │  │  Module  │  │ Chain      │  │  Module  │
  └────┬─────┘  └────┬─────┘  └────┬───────┘  └────┬─────┘
       │             │             │               │
       └─────────────┴─────────────┴───────────────┘
                         │
                  ┌──────┴──────┐
                  │  Azure AD   │── Authentification OAuth2
                  │  + JWT      │
                  └──────┬──────┘
                         │
                  ┌──────┴──────┐   ┌──────────────┐
                  │   MySQL     │   │   Redis      │
                  │  (RDS)      │   │ (Cache +     │
                  │             │   │  Offline sync)│
                  └──────┬──────┘   └──────────────┘
                         │
  ┌──────────────Azure──────────────────────────────┐
  │  Azure AD (Identités) + Azure Monitor (Logs)    │
  └─────────────────────────────────────────────────┘

  ┌──────────────AWS (af-south-1)───────────────────┐
  │  VPC → Subnets → ALB → Auto Scaling → RDS      │
  │  CloudWatch (Métriques) + EKS (Kubernetes)      │
  └─────────────────────────────────────────────────┘

  On-premise (Cameroun) : Données RH + Financières (loi 2010/012)
  Cloud (af-south-1)    : APIs applicatives, BI, CRM, Cache
  Azure                 : Azure AD, Azure Monitor
```

---

## 2. Choix technologiques

### 2.1. Régions cloud africaines

Conformément à la section 1.2.1 du sujet :
- **AWS af-south-1 (Cape Town)** : latence ~150-200ms depuis Douala
- **Azure South Africa North** : authentification centralisée (Azure AD) + supervision (Azure Monitor)

### 2.2. Répartition on-premise vs cloud

| Données | Localisation | Justification |
|---------|-------------|---------------|
| RH, financières, clients | On-premise (Cameroun) | Loi 2010/012, souveraineté |
| APIs, BI, CRM, Cache | Cloud af-south-1 | Haute disponibilité, scalabilité |
| Identités, Supervision | Azure (Global) | Azure AD + Azure Monitor |

### 2.3. Architecture résiliente

- Multi-AZ RDS MySQL en production (basculement automatique)
- Auto Scaling group (min 2, max 6 instances EC2)
- ALB (Application Load Balancer) pour répartition de charge
- **Kubernetes (EKS)** pour l'orchestration des conteneurs
- Cache Redis + file d'attente locale pour mode offline-first
- Snapshots quotidiens RDS + rétention 30 jours en prod

### 2.4. Stack technique

| Composant | Technologie | Justification |
|-----------|-------------|---------------|
| Backend | Java 17, Spring Boot 3.2.5 | Maturité, écosystème cloud |
| Base de données | **MySQL 8.0** (AWS RDS) | Conforme au sujet (RDS), fiable |
| Authentification | **Azure AD** (OAuth2 / JWT) | Exigé section 1.2.3 |
| API Documentation | Springdoc OpenAPI (Swagger) | Exigé section 1.2.3 |
| Cache offline-first | Redis 7 + sync queue locale | Exigé section 1.2.4 |
| Supervision | **AWS CloudWatch** + **Azure Monitor** | Exigés section 1.4.3 |
| Conteneurisation | Docker | Exigé section 1.3.4 |
| Orchestration | **Kubernetes (EKS)** | Exigé section 1.3.4 |
| CI/CD | GitHub Actions | Exigé section 1.3.3 |
| IaC | AWS CloudFormation | Exigé section 1.3.1 |

---

## 3. Modules fonctionnels

| Module | Entités | Description |
|--------|---------|-------------|
| **ERP** | Employee, Supplier, Invoice | RH, fournisseurs, comptabilité |
| **CRM** | Client, Restaurant | Relation client, restaurants SavoirManger |
| **Supply Chain** | Product, Shipment | Traçabilité plantations → transformation → vente |
| **BI** | Dashboard (stats) | Indicateurs clés (employés, clients, expéditions) |

---

## 4. API REST

Documentation Swagger : [http://localhost:8081/api/v1/swagger-ui.html](http://localhost:8081/api/v1/swagger-ui.html)

### Endpoints

| Méthode | Path | Module | Rôle requis |
|---------|------|--------|-------------|
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

## 5. Déploiement

### 5.1. Local (dev)

```bash
docker-compose up -d          # MySQL + Redis
mvn spring-boot:run            # Port 8081
```

### 5.2. Kubernetes

```bash
kubectl apply -f k8s/
```

Déploie : Deployment (2 replicas), Service (ClusterIP), Ingress, HPA (auto-scaling CPU 70%).

### 5.3. Profils disponibles

| Profil | Usage | DB | CloudWatch | Azure Monitor |
|--------|-------|----|------------|---------------|
| dev | Développement local | localhost:3306 | désactivé | désactivé |
| test | Tests automatisés | digitrans_test | désactivé | désactivé |
| prod | Production | AWS RDS | activé | activé |

### 5.4. Pipeline CI/CD (GitHub Actions)

```yaml
# .github/workflows/ci.yml
# Jobs : build-and-test (MySQL) + docker push sur main
```

---

## 6. Variables d'environnement

| Variable | Défaut | Description |
|----------|--------|-------------|
| `DB_HOST` | localhost | Hôte MySQL |
| `DB_PORT` | 3306 | Port MySQL |
| `DB_NAME` | digitrans_db | Nom de la base |
| `DB_USERNAME` | root | Utilisateur DB |
| `DB_PASSWORD` | root | Mot de passe DB |
| `REDIS_HOST` | localhost | Hôte Redis |
| `REDIS_PORT` | 6379 | Port Redis |
| `SPRING_PROFILES_ACTIVE` | dev | Profil actif |
| `AZURE_AD_ISSUER_URI` | — | Issuer URI Azure AD |
| `AZURE_AD_CLIENT_ID` | — | Client ID Azure AD |
| `AZURE_AD_CLIENT_SECRET` | — | Client Secret Azure AD |

---

## 7. Reprise après sinistre (RTO / RPO)

| Environnement | RPO | RTO | Stratégie |
|--------------|-----|-----|-----------|
| Dev / Test | 24h | 4h | Sauvegarde quotidienne + restauration manuelle |
| Production | 1h | 30min | Multi-AZ RDS + Auto Scaling + snapshots automatiques |

---

## 8. Tests

### 8.1. Tests unitaires
- JUnit 5 + Spring Boot Test
- Contexte Spring chargé via `@SpringBootTest`

### 8.2. Tests de charge (K6)
```bash
k6 run loadtests/k6-load-test.js
```
Scénarios : montée progressive 20 → 100 utilisateurs simultanés.
KPIs : P95 < 2000ms, taux d'erreur < 10%.

---

## 9. Infrastructure as Code

### AWS CloudFormation
Fichier : `infra/cloudformation-template.yaml`

Ressources créées :
- VPC (CIDR 10.0.0.0/16) avec 2 sous-réseaux publics + 1 privé
- ALB + Target Group + Listener
- Auto Scaling Group (min 1, max 6)
- RDS MySQL 8.0 (Multi-AZ en prod)
- Groupes de sécurité (moindre privilège)

```bash
aws cloudformation deploy \
  --template-file infra/cloudformation-template.yaml \
  --stack-name digitrans-cm-dev \
  --region af-south-1
```

### Kubernetes Manifests
Dossier : `k8s/`
- `configmap.yaml` : configuration applicative
- `secrets.yaml` : credentials (DB, Azure AD)
- `deployment.yaml` : 2 réplicas, probes, ressources
- `service.yaml` : ClusterIP
- `ingress.yaml` : exposition externe
- `hpa.yaml` : auto-scaling (CPU 70%, mémoire 80%)

---

## 10. Rapport d'activité (collectif)

### Répartition des tâches
- Backend (API, entités, services) : équipe 1
- Sécurité (Azure AD, RBAC) : équipe 2
- DevOps (Docker, K8s, CI/CD, IaC) : équipe 3
- Monitoring (CloudWatch, Azure Monitor) : équipe 2 + 3
- Tests et documentation : équipe 1 + 3

### Difficultés rencontrées
- Configuration Azure AD/OAuth2 avec Spring Security
- Mécanisme offline-first avec synchronisation Redis + file locale
- Adaptation au contexte camerounais (latence, souveraineté, coupures)

---

## Auteurs

**CAMTECH SOLUTIONS S.A.** — Douala, Cameroun
Projet DIGITRANS-CM — 2026
