resource "kubernetes_ingress_v1" "dashboard_ingress" {
  metadata {
    name = "dashboard-ingress"
    namespace = "kubernetes-dashboard"
  }

  spec {
    ingress_class_name = "nginx"

    rule {
      host = "dashboard.minikube"

      http {
        path {
          path     = "/"
          path_type = "Prefix"

          backend {
            service {
              name = "kubernetes-dashboard"

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
