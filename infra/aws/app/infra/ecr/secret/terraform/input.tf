variable "namespace" {
  description = "The namespace where the secret will be created"
  type = string
}

variable "cluster_name" {
  description = "The name of the EKS cluster"
  type = string
}

variable "registry_id" {
  description = "The registry ID of the repository"
  type = string
}

variable "secret_name" {
  description = "The name of the secret"
  type = string
}