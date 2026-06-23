.PHONY: help build test clean docker-build docker-up docker-down deploy-local deploy-aws logs

help:
	@echo "DIGITRANS-CM - Makefile Commands"
	@echo "=================================="
	@echo ""
	@echo "Development:"
	@echo "  make build           - Compiler le projet (mvn clean package)"
	@echo "  make test            - Exécuter les tests unitaires"
	@echo "  make clean           - Nettoyer les artifacts Maven"
	@echo ""
	@echo "Docker:"
	@echo "  make docker-build    - Construire les images Docker"
	@echo "  make docker-up       - Démarrer les services avec Docker Compose"
	@echo "  make docker-down     - Arrêter les services Docker"
	@echo "  make logs            - Afficher les logs des services"
	@echo ""
	@echo "Deployment:"
	@echo "  make deploy-local    - Déployer localement"
	@echo "  make deploy-aws      - Déployer sur AWS avec Terraform"
	@echo ""
	@echo "AWS:"
	@echo "  make push-docker     - Pousser les images Docker vers ECR"
	@echo ""

# Build
build:
	@echo "Compilation du projet..."
	mvn clean package -DskipTests

test:
	@echo "Exécution des tests..."
	mvn test

clean:
	@echo "Nettoyage..."
	mvn clean
	rm -rf target/
	docker-compose down -v

# Docker
docker-build:
	@echo "Construction des images Docker..."
	docker-compose build

docker-up: docker-build
	@echo "Démarrage des services..."
	docker-compose up -d
	@echo "Services démarrés!"
	@echo "Endpoints:"
	@echo "  - ERP Service: http://localhost:8081/api/v1"
	@echo "  - Auth Service: http://localhost:8080/api/v1/auth"
	@echo "  - Supply Chain: http://localhost:8082/api/v1"

docker-down:
	@echo "Arrêt des services..."
	docker-compose down

logs:
	docker-compose logs -f

# Deployment
deploy-local: docker-up
	@echo "Déploiement local terminé!"

deploy-aws:
	@echo "Déploiement AWS..."
	@chmod +x scripts/deploy-terraform.sh
	bash scripts/deploy-terraform.sh

push-docker:
	@echo "Push vers ECR..."
	@chmod +x scripts/push-docker-ecr.sh
	bash scripts/push-docker-ecr.sh

# Health checks
health:
	@echo "Health Checks..."
	@curl -s http://localhost:8080/actuator/health | jq .
	@curl -s http://localhost:8081/api/v1/actuator/health | jq .
	@curl -s http://localhost:8082/api/v1/actuator/health | jq .

# Database
db-init:
	@echo "Initialisation de la base de données..."
	docker-compose exec -T mysql mysql -udigitrans_user -pdigitrans_password digitrans_db < scripts/init-db.sql

# Utilities
format:
	@echo "Formatage Terraform..."
	terraform -chdir=infrastructure/terraform fmt -recursive

validate:
	@echo "Validation Terraform..."
	terraform -chdir=infrastructure/terraform validate
	@echo "Validation Maven..."
	mvn validate
