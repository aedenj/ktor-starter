variable "cluster_name" {
  description = "The name of the EKS cluster"
  type = string
}

variable "namespace" {
  description = "The namespace where the app will be created"
  type = string
}

variable "secret_name" {
  description = "The name of the secret"
  type = string
}