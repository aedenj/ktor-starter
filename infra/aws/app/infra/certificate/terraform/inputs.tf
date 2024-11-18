variable "cluster_name" {
    description = "The name of the EKS cluster"
    type = string
}

variable "namespace" {
    description = "The namespace to deploy the application"
    type = string
}

variable "app_name" {
    description = "The name of the application"
    type = string
}

variable "domain" {
    description = "The domain name"
    type = string
}