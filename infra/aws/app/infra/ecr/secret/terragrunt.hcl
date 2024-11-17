include "root" {
  path = find_in_parent_folders()
  expose = true
}

include "kube_provider" {
  path = "${dirname(find_in_parent_folders())}/common/kube-provider.hcl"
}

terraform {
  source = ".//terraform"
}

dependency "ecr" {
  config_path = "../repository"
}

inputs = {
  namespace = "${include.root.locals.namespace}"
  cluster_name = "${include.root.locals.cluster_name}"
  registry_id = "${dependency.ecr.outputs.registry_id}"
  secret_name = "${include.root.locals.app_name}-secret"
}