#!/bin/bash

# Script pour construire les images Docker et les pousser vers ECR

set -e

PROJECT_NAME="digitrans-cm"
AWS_REGION="af-south-1"

echo "========================================="
echo "DIGITRANS-CM - Docker Build & Push"
echo "========================================="
echo ""

# Get AWS Account ID
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ECR_REGISTRY="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

echo "Account ID: $ACCOUNT_ID"
echo "ECR Registry: $ECR_REGISTRY"
echo ""

# Login to ECR
echo "Connexion à ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REGISTRY

echo ""
echo "Construction et push des images Docker..."
echo ""

# Build and push ERP Service
echo "Building ERP Service..."
docker build -f erp-service/Dockerfile -t $ECR_REGISTRY/$PROJECT_NAME/erp-service:latest .
docker push $ECR_REGISTRY/$PROJECT_NAME/erp-service:latest

# Build and push Supply Chain Service
echo "Building Supply Chain Service..."
docker build -f supply-chain-service/Dockerfile -t $ECR_REGISTRY/$PROJECT_NAME/supply-chain-service:latest .
docker push $ECR_REGISTRY/$PROJECT_NAME/supply-chain-service:latest

echo ""
echo "Toutes les images ont été construites et poussées vers ECR"
