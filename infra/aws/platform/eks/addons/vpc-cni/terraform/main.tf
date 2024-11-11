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

# For more, https://github.com/aws/amazon-vpc-cni-k8s
module "eks_blueprints_addon" {
  source = "aws-ia/eks-blueprints-addon/aws"

  name        = "aws-vpc-cni"
  description = "A Helm chart to deploy aws-vpc-cni"

  chart       = "aws-vpc-cni"
  chart_version = "1.18.6"
  repository = "https://aws.github.io/eks-charts"
  namespace   = local.namespace
  create_namespace = false

  wait                       = true
  wait_for_jobs              = true

  set = [
    {
      name  = "serviceAccount.create"
      value = true
    },
    {
      name  = "serviceAccount.name"
      value = var.service_account
    }
  ]
}