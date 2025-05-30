locals {
  deployment = get_env("DEPLOYMENT")
  deploy_vars = read_terragrunt_config("${get_parent_terragrunt_dir()}/deployments/${local.deployment}.hcl")

  org = local.deploy_vars.locals.organization
  region = local.deploy_vars.locals.region
  env = local.deploy_vars.locals.env

  app_name = local.deploy_vars.locals.app_name
  cluster_name = local.deploy_vars.locals.cluster_name
  namespace = local.deploy_vars.locals.namespace
  domain = local.deploy_vars.locals.domain
}

remote_state {
  backend = "s3"
  generate = {
    path = "backend.tf"
    if_exists = "overwrite_terragrunt"
  }
  config = {
    bucket = "terraform-state-${local.org}-${local.region}"
    key = "${local.env}/${local.app_name}/${path_relative_to_include()}/terraform.tfstate"
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
      region = "${local.region}"

      default_tags {
        tags = {
            Organization = "${title(local.org)}"
            Environment = "${title(local.env)}"
            ManagedBy = "Terraform"
            Deployment = "Terragrunt"
        }
      }
    }
  EOF
}
