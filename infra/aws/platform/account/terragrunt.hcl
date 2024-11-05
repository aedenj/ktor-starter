include "root" {
  path = find_in_parent_folders()
}

terraform {
  source = "../../modules/account"
}

include "vpc" {
  path = "${dirname(find_in_parent_folders())}/common/vpc.hcl"
  expose = true
}

inputs = {
  min_required_zones = include.vpc.locals.zone_count
}