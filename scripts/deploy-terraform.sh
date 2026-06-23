#!/bin/bash

# Script de déploiement Terraform sur AWS

set -e

PROJECT_NAME="digitrans-cm"
AWS_REGION="af-south-1"
ENVIRONMENT="prod"

echo "========================================="
echo "DIGITRANS-CM - Terraform Deployment"
echo "========================================="
echo ""

# Check if terraform is installed
if ! command -v terraform &> /dev/null; then
    echo "Terraform n'est pas installé"
    exit 1
fi

# Check if AWS CLI is configured
if ! aws sts get-caller-identity &> /dev/null; then
    echo "AWS CLI non configuré"
    exit 1
fi
echo "Terraform et AWS CLI sont installés"
echo ""

# Navigate to terraform directory
cd infrastructure/terraform

echo "Étape 1: Initialiser Terraform"
terraform init

echo ""
echo "Étape 2: Valider la configuration"
terraform validate

echo ""
echo "Étape 3: Planifier le déploiement"
terraform plan -out=tfplan

echo ""
echo "Étape 4: Appliquer la configuration"
read -p "Voulez-vous continuer avec le déploiement? (oui/non): " -r
echo
if [[ $REPLY =~ ^[Oo]ui$ ]]; then
    terraform apply tfplan
    echo ""
    echo "Infrastructure déployée avec succès!"
    echo ""
    echo "Récupération des informations de déploiement:"
    echo "======================================================"
    terraform output
    echo "======================================================"
else
    echo "Déploiement annulé"
    rm -f tfplan
    exit 1
fi

cd - > /dev/null

echo ""
echo "Déploiement terminé!"
