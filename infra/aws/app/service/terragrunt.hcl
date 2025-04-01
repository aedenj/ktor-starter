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

dependencies {
  paths = [
    "../infra/ecr/repository",
    "../infra/ecr/secret",
    "../infra/certificate",
  ]
}

dependency "secret" {
  config_path = "../infra/ecr/secret"
}

inputs = {
  cluster_name = "${include.root.locals.cluster_name}"
  namespace = "${include.root.locals.namespace}"
  app_name = "${include.root.locals.app_name}"
  secret_name = "${dependency.secret.outputs.secret_name}"
  domain = "${include.root.locals.domain}"
}