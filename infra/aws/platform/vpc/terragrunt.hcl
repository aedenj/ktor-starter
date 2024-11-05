include "root" {
  path = find_in_parent_folders()
  expose = true
}

include "vpc" {
  path = "${dirname(find_in_parent_folders())}/platform/common/vpc.hcl"
  expose = true
}

terraform {
  source = "tfr:///terraform-aws-modules/vpc/aws?version=5.14.0"
}

dependency "account" {
  config_path = "../account"
}

locals {
  cidr = include.vpc.locals.cidr
  zone_count = include.vpc.locals.zone_count
}

inputs = {
  name = "${include.root.locals.resource_prefix}"
  cidr = local.cidr

  azs             = slice(dependency.account.outputs.available_azs, 0, local.zone_count)
  private_subnets = [
      for k, v in slice(dependency.account.outputs.available_azs, 0, local.zone_count):
        cidrsubnet(local.cidr, 8, k + 1)
  ]
  public_subnets = [
    for k, v in slice(dependency.account.outputs.available_azs, 0, local.zone_count):
        cidrsubnet(local.cidr, 8, k + 101)
  ]

  enable_nat_gateway = include.vpc.locals.enable_nat_gateway
  enable_vpn_gateway = include.vpc.locals.enable_vpn_gateway
  enable_public_redshift = include.vpc.locals.enable_public_redshift
}