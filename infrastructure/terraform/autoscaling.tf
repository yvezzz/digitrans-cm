# Auto-scaling for ECS services (Application Auto Scaling)

resource "aws_appautoscaling_target" "erp_service_scalable_target" {
  max_capacity       = 4
  min_capacity       = 1
  resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.erp_service.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}

resource "aws_appautoscaling_policy" "erp_service_cpu_policy" {
  name               = "${var.project_name}-erp-cpu-scaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.erp_service_scalable_target.resource_id
  scalable_dimension = aws_appautoscaling_target.erp_service_scalable_target.scalable_dimension
  service_namespace  = aws_appautoscaling_target.erp_service_scalable_target.service_namespace

  target_tracking_scaling_policy {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }
    target_value       = 70.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 300
  }
}

resource "aws_appautoscaling_target" "supply_chain_service_scalable_target" {
  max_capacity       = 4
  min_capacity       = 1
  resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.supply_chain_service.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}

resource "aws_appautoscaling_policy" "supply_chain_service_cpu_policy" {
  name               = "${var.project_name}-supply-chain-cpu-scaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.supply_chain_service_scalable_target.resource_id
  scalable_dimension = aws_appautoscaling_target.supply_chain_service_scalable_target.scalable_dimension
  service_namespace  = aws_appautoscaling_target.supply_chain_service_scalable_target.service_namespace

  target_tracking_scaling_policy {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }
    target_value       = 70.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 300
  }
}
