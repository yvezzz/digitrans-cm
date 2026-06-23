#!/bin/bash

# Script pour démarrer les services localement avec Docker Compose

set -e

echo "========================================="
echo "DIGITRANS-CM - Démarrage Local"
echo "========================================="
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Docker n'est pas installé"
    exit 1
fi

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "Docker Compose n'est pas installé"
    exit 1
fi
echo "Docker et Docker Compose sont installés"
echo ""

echo "Démarrage des services..."
docker-compose up -d

echo ""
echo "Attendre que les services démarrent..."
sleep 10

echo ""
echo "Services démarrés!"
echo ""

echo "État des services:"
docker-compose ps

echo ""
echo "Endpoints disponibles:"
echo "  - Auth Service: http://localhost:8080/api/v1/auth"
echo "  - ERP Service: http://localhost:8081/api/v1"
echo "  - Supply Chain Service: http://localhost:8082/api/v1"
echo ""

echo "Logs disponibles avec:"
echo "  - docker-compose logs -f"
echo ""

echo "Pour arrêter les services:"
echo "  - docker-compose down"
