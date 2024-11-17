include "root" {
  path = find_in_parent_folders()
  expose = true
}

terraform {
  source = ".//terraform"
}

dependency "secret" {
  config_path = "../infra/ecr/secret"
}

inputs = {
  cluster_name = "${include.root.locals.cluster_name}"
  namespace = "${include.root.locals.namespace}"
  app_name = "${include.root.locals.app_name}"
  secret_name = "${dependency.secret.outputs.secret_name}"
}