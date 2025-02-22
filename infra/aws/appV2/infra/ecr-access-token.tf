data "aws_ecr_authorization_token" "value" {
  registry_id = module.ecr.repository_registry_id

  depends_on = [
    module.ecr
  ]
}

locals {
  username          = "AWS"
  password          = data.aws_ecr_authorization_token.value.password
  registry_endpoint = data.aws_ecr_authorization_token.value.proxy_endpoint
  docker_config_json = jsonencode({
    "auths" = {
      "${local.registry_endpoint}" = {
        "username" = local.username
        "password" = local.password
        "auth"     = data.aws_ecr_authorization_token.value.authorization_token
      }
    }
  })
}

resource "kubernetes_secret" "app_secret" {
  metadata {
    name      = "ktor-starter-app-secret"
    namespace = "flux-system"
  }

  data = {
    ".dockerconfigjson" = local.docker_config_json
  }

  type = "kubernetes.io/dockerconfigjson"
}
