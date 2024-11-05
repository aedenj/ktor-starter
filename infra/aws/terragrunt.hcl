locals {
  env_vars = read_terragrunt_config(find_in_parent_folders("env.hcl"))

  org = "archegos"
  region = "us-east-1"
  env = local.env_vars.locals.environment
  resource_prefix = "${local.org}-${local.region}-${local.env}"
}

remote_state {
  backend = "s3"
  generate = {
    path = "backend.tf"
    if_exists = "overwrite_terragrunt"
  }
  config = {
    bucket = "terraform-state-${local.org}-${local.region}"
    key = "${path_relative_to_include()}/terraform.tfstate"
    encrypt = false
    region = "${local.region}"
    profile = "default"
  }
}

generate "provider" {
  path = "provider.tf"
  if_exists = "overwrite_terragrunt"
  contents = <<EOF
    provider "aws" {
      region = "us-east-1"

      default_tags {
        tags = {
            Organization = "${local.org}"
            Environment = "${local.env}"
            ManagedBy = "Terraform"
            Deployment = "Terragrunt"
        }
      }
    }
  EOF
}