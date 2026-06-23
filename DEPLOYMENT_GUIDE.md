# DIGITRANS-CM - Guide de Déploiement

## Architecture Microservices

Le projet est organisé en 3 microservices :

1. **ERP Service** (Port 8081)
   - Gestion des employés, fournisseurs et factures
   - Base de données MySQL dédiée

2. **Supply Chain Service** (Port 8082)
   - Traçabilité des produits
   - Cache Redis pour mode offline-first

3. **API Gateway** (Port 8080)
   - Point d'entrée unique
   - Routage vers les services
   - Load balancing

## Architecture Cloud - AWS

```
┌─────────────────────────────────────────────────────────┐
│                     AWS Region: af-south-1              │
├─────────────────────────────────────────────────────────┤
│  VPC (10.0.0.0/16)                                      │
│  ├─ Subnets publiques (ALB)                             │
│  ├─ Subnets privées (ECS Fargate)                       │
│  ├─ RDS MySQL (Multi-AZ)                                │
│  ├─ ElastiCache Redis                                   │
│  ├─ ECR (Docker Registries)                             │
│  └─ CloudWatch Logs                                     │
└─────────────────────────────────────────────────────────┘
```

## Développement Local

### Prérequis

- Docker & Docker Compose
- Java 17+
- Maven 3.9+
- Git

### Démarrage Local

```bash
# Cloner le projet
git clone <repository-url>
cd digitrans-cm

# Démarrer les services avec Docker Compose
docker-compose up -d

# Vérifier l'état des services
docker-compose ps

# Consulter les logs
docker-compose logs -f api-gateway
```

### Endpoints Disponibles

| Service | Endpoint | Port |
|---------|----------|------|
| API Gateway | http://localhost:8080 | 8080 |
| ERP Service | http://localhost:8081/api/v1 | 8081 |
| Supply Chain | http://localhost:8082/api/v1 | 8082 |
| Swagger UI | http://localhost:8080/swagger-ui.html | 8080 |

## Déploiement AWS avec Terraform

### Prérequis

1. Compte AWS avec les permissions nécessaires
2. Terraform installé (version >= 1.0)
3. AWS CLI configuré
4. Docker & AWS ECR credentials

### Étapes de Déploiement

#### 1. Initialiser Terraform

```bash
cd infrastructure/terraform

# Copier le fichier de variables
cp terraform.tfvars.example terraform.tfvars

# Éditer terraform.tfvars avec vos valeurs
vi terraform.tfvars

# Initialiser Terraform
terraform init
```

#### 2. Planifier l'Infrastructure

```bash
terraform plan -out=tfplan
```

#### 3. Appliquer la Configuration

```bash
terraform apply tfplan
```

Cela créera :
- VPC avec subnets publics/privés
- RDS MySQL
- ElastiCache Redis
- ECS Cluster
- Application Load Balancer
- ECR Repositories

#### 4. Récupérer les Outputs

```bash
terraform output

# Pour un output spécifique
terraform output alb_dns_name
```

### Variables Terraform

| Variable | Description | Valeur par défaut |
|----------|-------------|-------------------|
| `aws_region` | Région AWS | af-south-1 |
| `project_name` | Nom du projet | digitrans-cm |
| `environment` | Environnement (dev/staging/prod) | prod |
| `db_name` | Nom de la base de données | digitrans_db |
| `db_username` | Utilisateur DB | digitrans_admin |
| `db_password` | Mot de passe DB | À configurer |
| `container_cpu` | CPU des conteneurs | 256 |
| `container_memory` | Mémoire des conteneurs | 512 |

## Pipeline CI/CD

### GitHub Actions Workflow

Le pipeline exécute automatiquement les étapes suivantes :

1. **Build** (Chaque push)
   - Compilation Maven
   - Tests unitaires
   - Artifacts Maven

2. **Code Quality** (Chaque push)
   - Scan SonarCloud
   - Couverture de code

3. **Docker Build** (Push sur main/develop)
   - Build des images Docker
   - Push vers ECR
   - Tagging avec SHA commit

4. **Deploy to ECS** (Push sur main uniquement)
   - Mise à jour des services ECS
   - Attente de stabilité des services

### Configuration GitHub Secrets

Les secrets suivants doivent être configurés dans GitHub :

```
AWS_ROLE_TO_ASSUME          # ARN du rôle IAM pour GitHub Actions
SONAR_TOKEN                 # Token SonarCloud
SLACK_WEBHOOK               # URL du webhook Slack (optionnel)
```

### Créer le Rôle IAM pour GitHub Actions

```bash
# Créer un utilisateur IAM avec les permissions ECS, ECR, CloudWatch
# Voir: https://github.com/aws-actions/configure-aws-credentials#iam-policy
```

## Scripts Utiles

### Build Local

```bash
# Build complet
mvn clean package

# Build sans tests
mvn clean package -DskipTests

# Build avec tests
mvn clean test package
```

### Docker

```bash
# Build les images locales
docker-compose build

# Démarrer les services
docker-compose up -d

# Arrêter les services
docker-compose down

# Logs d'un service spécifique
docker-compose logs -f erp-service
```

### Terraform

```bash
# Planifier les modifications
terraform plan

# Appliquer les modifications
terraform apply

# Détruire l'infrastructure
terraform destroy

# Format le code Terraform
terraform fmt -recursive
```

## Monitoring et Logs

### CloudWatch

```bash
# Voir les logs ECS
aws logs tail /ecs/digitrans-cm --follow

# Voir les métriques CloudWatch
aws cloudwatch get-metric-statistics \
  --namespace DIGITRANS-CM/ERP \
  --metric-name CPUUtilization \
  --start-time 2024-01-01T00:00:00Z \
  --end-time 2024-01-02T00:00:00Z \
  --period 3600 \
  --statistics Average
```

### Health Checks

```bash
# Health check API Gateway
curl http://localhost:8080/actuator/health

# Health check ERP Service
curl http://localhost:8081/api/v1/actuator/health

# Health check Supply Chain Service
curl http://localhost:8082/api/v1/actuator/health
```

## Troubleshooting

### Problème : Les services ne démarrent pas

```bash
# Vérifier les logs Docker
docker-compose logs -f

# Vérifier la connexion à la base de données
docker exec digitrans-mysql mysql -uroot -proot_password -e "SELECT 1"
```

### Problème : ECS Task Fails

```bash
# Vérifier les logs ECS
aws logs tail /ecs/digitrans-cm --follow

# Vérifier l'état des services ECS
aws ecs describe-services --cluster digitrans-cm-cluster --services digitrans-cm-erp-service
```

### Problème : ECR Push Fails

```bash
# Vérifier la connexion ECR
aws ecr get-login-password --region af-south-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.af-south-1.amazonaws.com

# Vérifier les permissions IAM
aws iam get-user
```

## Bonnes Pratiques

1. **Secrets Management**
   - Ne jamais commiter les secrets
   - Utiliser AWS Secrets Manager
   - Utiliser GitHub Secrets pour le CI/CD

2. **Infrastructure as Code**
   - Tester la configuration Terraform localement
   - Utiliser `terraform plan` avant `apply`
   - Versionner le state Terraform (S3 backend)

3. **Monitoring**
   - Configurer les alarmes CloudWatch
   - Configurer les notifications Slack/Email
   - Monitorer les logs ECS

4. **Rollback**
   - Toujours conserver les snapshots de base de données
   - Utiliser le versioning d'images Docker
   - Documenter les procédures de rollback

## Support et Ressources

- **AWS Documentation**: https://docs.aws.amazon.com/
- **Terraform AWS Provider**: https://registry.terraform.io/providers/hashicorp/aws/latest/docs
- **Spring Boot**: https://spring.io/projects/spring-boot
- **Docker**: https://docs.docker.com/
