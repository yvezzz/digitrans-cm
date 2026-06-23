output "rds_endpoint" {
  description = "Endpoint de la base de données RDS"
  value       = aws_db_instance.mysql.endpoint
}

output "rds_address" {
  description = "Adresse de la base de données RDS"
  value       = aws_db_instance.mysql.address
}

output "rds_port" {
  description = "Port de la base de données RDS"
  value       = aws_db_instance.mysql.port
}

output "ecs_cluster_name" {
  description = "Nom du cluster ECS"
  value       = aws_ecs_cluster.main.name
}

output "alb_dns_name" {
  description = "DNS du Application Load Balancer"
  value       = aws_lb.main.dns_name
}

output "ecr_erp_service_url" {
  description = "URL du registre ECR pour ERP Service"
  value       = aws_ecr_repository.erp_service.repository_url
}

output "ecr_supply_chain_service_url" {
  description = "URL du registre ECR pour Supply Chain Service"
  value       = aws_ecr_repository.supply_chain_service.repository_url
}


output "vpc_id" {
  description = "ID du VPC créé"
  value       = aws_vpc.main.id
}

output "cloudwatch_log_group" {
  description = "CloudWatch Log Group pour ECS"
  value       = aws_cloudwatch_log_group.ecs.name
}
