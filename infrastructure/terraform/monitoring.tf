# CloudWatch Dashboard for basic service metrics
resource "aws_cloudwatch_dashboard" "digitrans_dashboard" {
  dashboard_name = "${var.project_name}-dashboard"

  dashboard_body = jsonencode({
    widgets = [
      {
        type = "metric"
        x = 0
        y = 0
        width = 12
        height = 6
        properties = {
          view = "timeSeries"
          stacked = false
          metrics = [
            [ "AWS/ECS", "CPUUtilization", "ClusterName", aws_ecs_cluster.main.name, { "stat": "Average" } ]
          ]
          region = var.aws_region
          title = "ECS Cluster CPU Utilization (Average)"
        }
      },
      {
        type = "metric"
        x = 12
        y = 0
        width = 12
        height = 6
        properties = {
          view = "timeSeries"
          metrics = [
            [ "AWS/EC2", "NetworkIn", "InstanceId", "--placeholder--" ]
          ]
          region = var.aws_region
          title = "NetworkIn (example)"
        }
      }
    ]
  })

  depends_on = [aws_cloudwatch_log_group.ecs]
}
