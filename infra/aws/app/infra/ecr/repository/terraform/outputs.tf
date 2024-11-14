output "registry_id" {
  description = "The registry ID of the repository"
  value = module.ecr.repository_registry_id
}