# QUICK START — DIGITRANS-CM (5 min)

Suivez ces étapes pour démarrer rapidement l'environnement de développement local.

Prérequis
- Docker & Docker Compose
- Java 17 + Maven (pour lancer les applications en mode dev)
- (optionnel) kubectl si vous utilisez Kubernetes

1) Cloner le dépôt
```bash
git clone <repository-url>
cd digitrans-cm
```

2) Démarrer les dépendances (MySQL + Redis)
```bash
docker-compose up -d mysql redis
```

3) Construire le projet (optionnel)
```bash
mvn -T 1C clean package -DskipTests
```

4) Lancer un service (ex: ERP)
```bash
cd erp-service
$env:DB_HOST="localhost"; $env:DB_PORT=3306; $env:DB_NAME=digitrans_db; $env:DB_USERNAME=root; $env:DB_PASSWORD=root
mvn spring-boot:run
```

5) Vérifier l'état
- ERP Health: `http://localhost:8081/api/v1/actuator/health`
- Auth Health: `http://localhost:8082/actuator/health`
- Supply Chain Health: `http://localhost:8083/api/v1/actuator/health`

6) Lancer le test de charge local (K6)
```bash
k6 run loadtests/k6-load-test.js --env BASE_URL=http://localhost:8081/api/v1
```

Notes
- Le projet n'intègre plus l'`api-gateway` dans la version locale actuelle.
- Pour Windows, utilisez `start-local.ps1` (fourni) pour lancer rapidement les services en fenêtres séparées.
