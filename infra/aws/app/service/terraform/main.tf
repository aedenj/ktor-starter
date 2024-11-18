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
    name = var.app_name
    namespace = var.namespace
  }

  spec {
    replicas = 2

    selector {
      match_labels = {
        name = var.app_name
      }
    }

    template {
      metadata {
        labels = {
          name = var.app_name
        }
      }

      spec {
        image_pull_secrets {
          name = var.secret_name
        }

        container {
          name  = var.app_name
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
    name = "${var.app_name}-service"
    namespace = var.namespace
  }

  spec {
    selector = {
      name = var.app_name
    }

    type = "NodePort"

    port {
      port        = 80
      target_port = 8080
      protocol = "TCP"
    }
  }

  wait_for_load_balancer = true
}

resource "kubernetes_ingress_v1" "app_ingress" {
  metadata {
    name = "${var.app_name}-ingress"
    namespace = var.namespace
    annotations = {
      "kubernetes.io/ingress.class" = "alb"
      "alb.ingress.kubernetes.io/scheme" = "internet-facing"
      "external-dns.alpha.kubernetes.io/hostname" = var.domain
    }
  }

  spec {
    ingress_class_name = "alb"

    rule {
      host = var.domain

      http {
        path {
          path     = "/"
          path_type = "Prefix"

          backend {
            service {
              name = "${var.app_name}-service"

              port {
                number = 80
              }
            }
          }
        }
      }
    }
  }

  wait_for_load_balancer = true
}

