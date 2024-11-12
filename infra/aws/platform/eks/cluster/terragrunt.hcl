include "root" {
  path = find_in_parent_folders()
}

terraform {
  source = "tfr:///terraform-aws-modules/eks/aws?version=20.28.0"
}

dependency "account" {
  config_path = "../../account"
}

dependency "vpc" {
  config_path = "../../vpc"
}

inputs = {
  cluster_name                 = "${dependency.account.outputs.resource_prefix}-eks"
  cluster_version              = "1.31"

  cluster_endpoint_public_access           = true
  enable_cluster_creator_admin_permissions = true

  vpc_id                       = dependency.vpc.outputs.vpc_id
  subnet_ids                   = concat(dependency.vpc.outputs.private_subnets, dependency.vpc.outputs.public_subnets)
  control_plane_subnet_ids     = dependency.vpc.outputs.private_subnets
  enable_irsa = false

  cluster_addons = {
    eks-pod-identity-agent = {
      most_recent = true
    }
    kube-proxy = {
      most_recent = true
    }
    coredns = {
      most_recent = true
    }
    vpc-cni = {
      most_recent = true
    }
  }

  eks_managed_node_group_defaults = {
    ami_type                   = "AL2_x86_64"
    instance_types             = ["t3.small"]
  }

  eks_managed_node_groups = {
    one = {
      name = "node-group-1"
      min_size     = 1
      max_size     = 3
      desired_size = 2
    }

    two = {
      name = "node-group-2"
      min_size     = 1
      max_size     = 2
      desired_size = 1
    }
  }
}