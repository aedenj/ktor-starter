data "aws_ecr_authorization_token" "value" {
  registry_id = var.registry_id
}

locals {
  username          = "AWS"
  password          = data.aws_ecr_authorization_token.value.authorization_token
  registry_endpoint = data.aws_ecr_authorization_token.value.proxy_endpoint
  docker_config_json = jsonencode({
    "auths" = {
      "${local.registry_endpoint}" = {
        "username" = local.username
        "password" = local.password
        "auth"     = base64encode("${local.username}:${local.password}")
      }
    }
  })
}

resource "kubernetes_namespace" "app_namespace" {
  metadata {
    name = var.namespace
  }
}

resource "kubernetes_secret" "app_secret" {
  metadata {
    name      = var.secret_name
    namespace = kubernetes_namespace.app_namespace.metadata[0].name
  }

  data = {
    ".dockerconfigjson" = local.docker_config_json
  }

  type = "kubernetes.io/dockerconfigjson"
}