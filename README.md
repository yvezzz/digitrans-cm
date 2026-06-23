# DIGITRANS-CM - Microservices Architecture

**Modernisation du Système d'Information d'AGROCAM S.A.**

Projet porté par **CAMTECH SOLUTIONS S.A.** (Douala, Cameroun) — Architecture cloud native avec microservices.

---

## Démarrage Rapide

```bash
# 1. Cloner le projet
git clone <repository-url>
cd digitrans-cm

# 2. Démarrer localement
docker-compose up -d

# 3. Vérifier le statut
curl http://localhost:8081/api/v1/actuator/health
# Résultat: {"status":"UP"}
```

**Services locaux (ports par défaut)**:
- `erp-service`: http://localhost:8081
- `auth-service`: http://localhost:8082
- `supply-chain-service`: http://localhost:8083

**Swagger UI (par service)**: http://localhost:8081/api/v1/swagger-ui.html (ERP)

**Premiers pas?** Lire [QUICK_START.md](QUICK_START.md) (5 min)

---

## Vue d'ensemble

DIGITRANS-CM est une solution d'entreprise moderne basée sur :
 - **Microservices Indépendants** (ERP, Supply Chain, Auth)
 - **Cloud-Native** (AWS ECS Fargate, af-south-1)
 - **Infrastructure as Code** (Terraform)
 - **CI/CD Automatisé** (GitHub Actions)
 - **Offline-First Caching** (Redis)
 - **Haute Disponibilité** (Multi-AZ, Load Balancer)
 - **Monitoring Centralisé** (CloudWatch)

## Structure du Projet

```
digitrans-cm/
│
├── Documentation
│   ├── README.md                    # Ce fichier
│   ├── INDEX.md                     # Navigation rapide
│   ├── QUICK_START.md               # Démarrage 5 min
│   ├── DEPLOYMENT_GUIDE.md          # Déploiement production
│   ├── MIGRATION_GUIDE.md           # Migrer le code
│   ├── GITHUB_ACTIONS_SETUP.md      # Configuration CI/CD
│   ├── PROJECT_STRUCTURE.md         # Structure détaillée
│   ├── COMPLETE_CHECKLIST.md        # Checklist complète
│   └── SUMMARY.md                   # Résumé des créations
│
├── Microservices
│   ├── erp-service/                 # ERP Service (Port 8081)
│   │   └── Employees, Suppliers, Invoices
│   │
│   ├── auth-service/                # Auth Service (Port 8082)
│   │   └── Inscription, Connexion, JWT
│   │
│   └── supply-chain-service/        # Supply Chain (Port 8083)
│       └── Products, Shipments, Offline Cache
│
├── Infrastructure as Code
│   └── infrastructure/terraform/
│       ├── README.md                # Guide Terraform détaillé
│       ├── main.tf                  # Configuration AWS
│       ├── variables.tf             # Variables
│       ├── networking.tf            # VPC, Subnets, NAT
│       ├── ecs.tf                   # ECS Cluster, ECR
│       ├── services.tf              # Services ECS, RDS, Redis
│       ├── outputs.tf               # Sorties (DNS, endpoints)
│       └── terraform.tfvars.example # Template config
│
├── CI/CD Pipeline
│   ├── .github/workflows/
│   │   └── deploy.yml               # GitHub Actions pipeline
│   └── scripts/
│       ├── deploy-terraform.sh      # Déploie Terraform
│       ├── push-docker-ecr.sh       # Push Docker images
│       └── start-local.sh           # Démarre localement
│
├── Conteneurs
│   ├── docker-compose.yml           # Orchestration locale
+   ├── Dockerfile                   # (copié dans chaque service)
   └── .env.example                 # Template variables

├── Build & Configuration
│   ├── pom.xml                      # Maven parent (3 modules)
│   ├── Makefile                     # 20+ commands rapides
│   ├── .gitignore                   # Fichiers ignorés
│   └── .gitattributes               # Attributs Git
│
├── Optional
│   ├── loadtests/                   # Tests de charge
│   ├── chaincode/                   # Blockchain (optionnel)
│   ├── k8s/                         # Kubernetes manifests
│   └── docs/                        # Documentation additionnelle
│
└── Générés (ne pas modifier)
  └── target/                      # Build artifacts
```

##  Architecture

| Fournisseur | Région | Services |
|-------------|--------|----------|
| AWS | af-south-1 (Cape Town) | Compute, base de données, cache, monitoring |
| Azure | South Africa North | Identités, supervision centralisée |

### Répartition des données

| Type de données | Localisation | Raison |
|----------------|-------------|--------|
| RH, financières, clients | On-premise (Cameroun) | Loi 2010/012 — souveraineté |
| APIs, BI, CRM, Cache | Cloud AWS | Haute disponibilité, scalabilité |
| Identités, Logs | Azure | Azure AD + Azure Monitor |

---

## 2. Stack technique

| Composant | Technologie | Rôle |
|-----------|------------|------|
| Backend | Java 17, Spring Boot 3.2.5 | API REST |
| Base de données | MySQL 8.0 (AWS RDS) | Stockage persistant |
| Authentification | Azure AD (OAuth2 / JWT) | Contrôle d'accès centralisé |
| Documentation API | Swagger / OpenAPI (springdoc) | Interface de test interactive |
| Cache | Redis 7 | Cache offline-first + file de synchronisation |
| Monitoring | AWS CloudWatch + Azure Monitor | Métriques et logs |
| Conteneurisation | Docker | Empaquetage standardisé |
| Orchestration | Kubernetes (EKS) | Scalabilité et résilience |
| CI/CD | GitHub Actions | Intégration et déploiement continus |
| Infrastructure as Code | AWS CloudFormation | Provisionnement automatisé |

---

## 3. Modules fonctionnels

L'application est structurée en quatre modules interconnectés, chacun responsable d'un domaine métier :

| Module | Entités gérées | Fonctionnalités |
|--------|---------------|-----------------|
| **ERP** | Employee, Supplier, Invoice | Gestion RH, fournisseurs et comptabilité |
| **CRM** | Client, Restaurant | Relation client et gestion des restaurants SavoirManger |
| **Supply Chain** | Product, Shipment | Traçabilité des flux (plantation → transformation → vente) |
| **BI** | Dashboard (statistiques agrégées) | Indicateurs clés de performance (KPI) |

---

## 4. API REST

L'ensemble des endpoints est documenté via Swagger UI, accessible en local à l'adresse :

[http://localhost:8081/api/v1/swagger-ui.html](http://localhost:8081/api/v1/swagger-ui.html)

### Points d'entrée

| Méthode | Chemin | Module | Rôle requis |
|---------|--------|--------|-------------|
| GET | `/api/v1/erp/employees` | ERP | ADMIN |
| POST | `/api/v1/erp/employees` | ERP | ADMIN |
| GET | `/api/v1/erp/suppliers` | ERP | ADMIN |
| POST | `/api/v1/erp/suppliers` | ERP | ADMIN |
| GET | `/api/v1/erp/invoices` | ERP | ADMIN |
| POST | `/api/v1/erp/invoices` | ERP | ADMIN |
| GET | `/api/v1/crm/clients` | CRM | ADMIN, SALES |
| GET | `/api/v1/crm/restaurants` | CRM | ADMIN, SALES |
| GET | `/api/v1/supply/products` | Supply Chain | ADMIN, LOGISTICS |
| GET | `/api/v1/supply/shipments` | Supply Chain | ADMIN, LOGISTICS |
| GET | `/api/v1/bi/dashboard` | BI | ADMIN, MANAGER |

### Sécurité

Tous les endpoints (sauf Swagger et health check) sont protégés par OAuth2 avec Azure AD. Les tokens JWT sont validés par le resource server Spring Security. L'accès est restreint par rôle (ADMIN, SALES, LOGISTICS, MANAGER).

---

## 5. Déploiement

### 5.1. Environnement de développement local

```bash
# Démarrer MySQL et Redis
docker-compose up -d

# Lancer l'application (port 8081)
mvn spring-boot:run
```

### 5.2. Profils disponibles

| Profil | Usage | Base de données | CloudWatch | Azure Monitor |
|--------|-------|----------------|------------|---------------|
| `dev` | Développement local | MySQL local (3306) | Désactivé | Désactivé |
| `h2` | Tests unitaires | H2 mémoire | Désactivé | Désactivé |
| `test` | Tests CI | MySQL (CI) | Désactivé | Désactivé |
| `prod` | Production | AWS RDS | Activé | Activé |

### 5.3. Kubernetes

```bash
# Déployer l'application sur un cluster Kubernetes
kubectl apply -f k8s/
```

Le déploiement comprend :
- 2 réplicas minimum, auto-scaling jusqu'à 6 (CPU > 70%)
- Health checks (liveness + readiness)
- Ingress avec termination SSL
- ConfigMap et Secrets pour la configuration

### 5.4. Pipeline CI/CD (GitHub Actions)

Le pipeline automatisé s'exécute sur chaque push :

1. **Build & Test** — Compilation Maven + exécution des 46 tests unitaires (MySQL service)
2. **Docker** (branch main uniquement) — Construction et publication de l'image sur Docker Hub

---

## 6. Variables d'environnement

| Variable | Défaut | Description |
|----------|--------|-------------|
| `DB_HOST` | localhost | Adresse du serveur MySQL |
| `DB_PORT` | 3306 | Port MySQL |
| `DB_NAME` | digitrans_db | Nom de la base de données |
| `DB_USERNAME` | root | Utilisateur MySQL |
| `DB_PASSWORD` | root | Mot de passe MySQL |
| `REDIS_HOST` | localhost | Adresse du serveur Redis |
| `REDIS_PORT` | 6379 | Port Redis |
| `SPRING_PROFILES_ACTIVE` | dev | Profil Spring actif |
| `AZURE_AD_ISSUER_URI` | — | URL du tenant Azure AD |
| `AZURE_AD_CLIENT_ID` | — | Identifiant client Azure AD |
| `AZURE_AD_CLIENT_SECRET` | — | Secret client Azure AD |

---

## 7. Résilience et reprise après sinistre

| Environnement | RPO | RTO | Stratégie |
|--------------|-----|-----|-----------|
| Développement / Test | 24 h | 4 h | Sauvegarde quotidienne, restauration manuelle |
| Production | 1 h | 30 min | Multi-AZ RDS, Auto Scaling, snapshots automatiques |

Mécanismes de résilience en place :
- **Base de données** : RDS Multi-AZ avec basculement automatique en production
- **Application** : Auto Scaling Group (2 à 6 instances) derrière un ALB
- **Cache** : Redis avec file d'attente locale pour fonctionnement hors-ligne
- **Sauvegardes** : Snapshots RDS quotidiens, rétention 30 jours en production

---

## 8. Tests

### 8.1. Tests unitaires (46 tests)

| Classe de test | Nombre |
|----------------|--------|
| ErpServiceTest | 10 |
| CrmServiceTest | 8 |
| SupplyChainServiceTest | 6 |
| BiServiceTest | 1 |
| CacheServiceTest | 6 |
| ErpControllerTest | 7 |
| CrmControllerTest | 3 |
| SupplyChainControllerTest | 3 |
| BiControllerTest | 1 |
| ContextLoadTest | 1 |

### 8.2. Tests de charge (K6)

```bash
k6 run loadtests/k6-load-test.js
```

Scénario : montée progressive de 20 à 100 utilisateurs simultanés sur 10 minutes.
Objectifs : temps de réponse P95 < 2000 ms, taux d'erreur < 10 %.

---

## 9. Infrastructure as Code

### AWS CloudFormation

Le fichier `infra/cloudformation-template.yaml` provisionne automatiquement l'infrastructure AWS :

- VPC (CIDR 10.0.0.0/16) avec 2 sous-réseaux publics et 2 privés
- ALB (Application Load Balancer) avec target group et listener HTTP
- Auto Scaling Group (1 à 6 instances EC2)
- RDS MySQL 8.0 (Multi-AZ en production)
- ElastiCache Redis 7.1
- Groupes de sécurité appliquant le principe du moindre privilège

```bash
aws cloudformation deploy \
  --template-file infra/cloudformation-template.yaml \
  --stack-name digitrans-cm-dev \
  --region af-south-1
```

### Manifests Kubernetes

Le dossier `k8s/` contient les fichiers de déploiement pour Kubernetes :

| Fichier | Description |
|---------|-------------|
| `namespace.yaml` | Espace de noms dédié |
| `configmap.yaml` | Configuration applicative |
| `secrets.yaml` | Credentials (DB, Azure AD) |
| `deployment.yaml` | 2 réplicas, probes, ressources CPU/RAM |
| `service.yaml` | Service interne ClusterIP |
| `ingress.yaml` | Exposition externe avec SSL |
| `hpa.yaml` | Auto-scaling horizontal (CPU 70 %, mémoire 80 %) |

---

## 10. Fonctionnement hors-ligne (offline-first)

Le système intègre un mécanisme de résilience aux coupures réseau :

1. **Cache Redis** : les données fréquemment consultées sont mises en cache avec un TTL de 30 minutes
2. **File locale** : en cas d'indisponibilité de Redis, les opérations d'écriture sont stockées dans une file d'attente en mémoire
3. **Synchronisation automatique** : toutes les 5 secondes, le service tente de rejouer les opérations en attente dès que Redis est de nouveau accessible

---

## 11. Supervision

Deux solutions de monitoring sont utilisées en parallèle :

| Outil | Usage | Fournisseur |
|-------|-------|-------------|
| **CloudWatch** | Métriques applicatives (CPU, mémoire, requêtes) | AWS |
| **Azure Monitor** | Logs centralisés, alertes, tableaux de bord | Azure |

En environnement de développement, les deux sont désactivés. En production, les métriques sont exportées vers les deux services simultanément.

---

## 12. Rapport d'activité (collectif)

### Répartition des tâches

- **Backend** (API, entités, services) : Équipe 1
- **Sécurité** (Azure AD, contrôle d'accès) : Équipe 2
- **DevOps** (Docker, Kubernetes, CI/CD, CloudFormation) : Équipe 3
- **Monitoring** (CloudWatch, Azure Monitor) : Équipes 2 et 3
- **Tests et documentation** : Équipes 1 et 3

### Difficultés rencontrées

- Configuration d'Azure AD avec Spring Security OAuth2
- Mise en place du mécanisme offline-first avec synchronisation automatique
- Adaptation de l'infrastructure au contexte camerounais (latence, souveraineté des données, coupures électriques)

---

## Auteurs

**CAMTECH SOLUTIONS S.A.** — Douala, Cameroun
Projet DIGITRANS-CM — 2026
