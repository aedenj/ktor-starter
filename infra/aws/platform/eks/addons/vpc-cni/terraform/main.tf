terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.54.1"
    }
  }
}
provider "helm" {
  kubernetes {
    host                   = var.cluster_endpoint
    cluster_ca_certificate = base64decode(var.cluster_certificate_authority_data)
    exec {
      api_version = "client.authentication.k8s.io/v1beta1"
      command     = "aws"
      # This requires the awscli to be installed locally where Terraform is executed
      args = ["eks", "get-token", "--cluster-name", var.cluster_name]
    }
  }
}

locals {
  namespace = "kube-system"
  addon_name = "vpc-cni"
}

module "aws_vpc_cni_ipv4_pod_identity" {
  source = "terraform-aws-modules/eks-pod-identity/aws"
  version = "1.6.1"

  name = var.service_account

  attach_aws_vpc_cni_policy = true
  aws_vpc_cni_enable_ipv4   = true

  associations = {
    "aws_vpc_cni_ipv4" = {
      cluster_name = var.cluster_name
      namespace = local.namespace
      service_account = var.service_account
    }
  }
}

data "aws_eks_addon_version" "latest" {
  addon_name         = local.addon_name
  kubernetes_version = var.cluster_version
  most_recent        = true
}

# For more, https://github.com/aws/amazon-vpc-cni-k8s
resource "aws_eks_addon" "aws_vpc_cni" {
  cluster_name = var.cluster_name
  addon_name   = local.addon_name
  addon_version = data.aws_eks_addon_version.latest.version

  resolve_conflicts_on_create = "OVERWRITE"
  resolve_conflicts_on_update = "OVERWRITE"

  configuration_values = jsonencode({
    serviceAccount = {
      name = var.service_account
      create = true
    }
  })
}
