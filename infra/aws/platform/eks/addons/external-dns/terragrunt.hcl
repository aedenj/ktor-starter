include "root" {
  path = find_in_parent_folders()
}

dependency "vpc" {
  config_path = "../../../vpc"
}

dependency "eks" {
  config_path = "../../cluster"
}

terraform {
  source = ".//terraform"
}

inputs = {
  cluster_name = dependency.eks.outputs.cluster_name
  cluster_endpoint = dependency.eks.outputs.cluster_endpoint
  cluster_certificate_authority_data = dependency.eks.outputs.cluster_certificate_authority_data
  service_account = "external-dns-sa"
}
