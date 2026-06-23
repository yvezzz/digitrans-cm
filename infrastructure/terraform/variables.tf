variable "aws_region" {
  description = "Region AWS pour le deploiement"
  type        = string
  default     = "af-south-1"
}

variable "project_name" {
  description = "Nom du projet"
  type        = string
  default     = "digitrans-cm"
}

variable "environment" {
  description = "Environnement (dev, staging, prod)"
  type        = string
  default     = "prod"
  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment doit être dev, staging ou prod."
  }
}

variable "db_name" {
  description = "Nom de la base de données"
  type        = string
  default     = "digitrans_db"
}

variable "db_username" {
  description = "Utilisateur de la base de données"
  type        = string
  default     = "digitrans_admin"
  sensitive   = true
}

variable "db_password" {
  description = "Mot de passe de la base de données"
  type        = string
  sensitive   = true
}

variable "instance_type" {
  description = "Type d'instance ECS"
  type        = string
  default     = "t3.micro"
}

variable "container_memory" {
  description = "Mémoire des conteneurs (MB)"
  type        = number
  default     = 512
}

variable "container_cpu" {
  description = "CPU des conteneurs (unités)"
  type        = number
  default     = 256
}
