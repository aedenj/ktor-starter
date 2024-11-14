data "aws_eks_cluster" "this" {
  name = var.cluster_name
}

data "aws_eks_cluster_auth" "this" {
  name = var.cluster_name
}

provider "kubernetes" {
  host                   = data.aws_eks_cluster.this.endpoint
  cluster_ca_certificate = base64decode(data.aws_eks_cluster.this.certificate_authority[0].data)
  token                  = data.aws_eks_cluster_auth.this.token
}


resource "kubernetes_deployment" "app_deployment" {
  metadata {
    name = "ktor-starter-app"
    namespace = var.namespace
  }

  spec {
    replicas = 2

    selector {
      match_labels = {
        name = "ktor-starter-app"
      }
    }

    template {
      metadata {
        labels = {
          name = "ktor-starter-app"
        }
      }

      spec {
        image_pull_secrets {
          name = var.secret_name
        }

        container {
          name  = "ktor-starter-app"
          image = "541898866282.dkr.ecr.us-east-1.amazonaws.com/ktor-starter-app:latest"
          image_pull_policy = "Always"

          env {
            name = "KTOR_ENV"
            value = "local"
          }

          port {
            container_port = 8080
          }
        }
      }
    }
  }
}


resource "kubernetes_service" "app_service" {
  metadata {
    name = "ktor-starter-app-service"
    namespace = var.namespace
  }

  spec {
    selector = {
      name = "ktor-starter-app"
    }

    type = "NodePort"

    port {
      port        = 80
      target_port = 8080
      protocol = "TCP"
    }
  }
}

resource "kubernetes_ingress_v1" "app_ingress" {
  metadata {
    name = "ktor-starter-app-ingress"
    namespace = var.namespace
    annotations = {
      "kubernetes.io/ingress.class" = "alb"
      "alb.ingress.kubernetes.io/scheme" = "internet-facing"
      "external-dns.alpha.kubernetes.io/hostname" = "aedenjameson.com"
    }
  }

  spec {
    ingress_class_name = "alb"

    rule {
      host = "aedenjameson.com"

      http {
        path {
          path     = "/"
          path_type = "Prefix"

          backend {
            service {
              name = "ktor-starter-app-service"

              port {
                number = 80
              }
            }
          }
        }
      }
    }
  }
}
