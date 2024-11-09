output "org" {
  description = "The organization"
  value       = var.org
}

output "region" {
  description = "The target region to deploy infrastructure"
  value       = var.region
}

output "env" {
  description = "The purpose of the environment"
  value       = var.env
}

output "resource_prefix" {
  description = "The prefix for resources"
  value       = "${var.org}-${var.env}"
}

output "available_azs" {
  value = data.aws_availability_zones.available.names
  description = "The zones available to the account"
}

