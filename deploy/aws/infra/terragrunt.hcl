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

inputs = {
  cluster_name = "${include.root.locals.cluster_name}"
  app_namespace = "${include.root.locals.namespace}"
  app_name = "${include.root.locals.app_name}"
  secret_name = "${include.root.locals.app_name}-secret"
  domain = "${include.root.locals.domain}"
}