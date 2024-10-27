resource "kubernetes_deployment" "app_deployment" {
  metadata {
    name = var.app_name
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
        container {
          name  = var.app_name
          image = "${var.app_name}:latest"
          image_pull_policy = "Never"

          env {
            name = "KTOR_ENV"
            value = var.env
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
  }

  spec {
    selector = {
      name = var.app_name
    }

    type = "NodePort"

    port {
      port        = 8080
      target_port = 8080
      node_port   = 30080
    }
  }
}

resource "kubernetes_ingress_v1" "app_ingress" {
  metadata {
    name = "${var.app_name}-ingress"
    annotations = {
      "nginx.ingress.kubernetes.io/rewrite-target" = "/$1"
    }
  }

  spec {
    ingress_class_name = "nginx"

    rule {
      host = "${var.app_name}.minikube"

      http {
        path {
          path     = "/"
          path_type = "Prefix"

          backend {
            service {
              name = "${var.app_name}-service"

              port {
                number = 8080
              }
            }
          }
        }
      }
    }
  }
}
