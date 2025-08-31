resource "kubernetes_deployment" "app_deployment" {
  metadata {
    name = var.app_name
    namespace = var.namespace
    labels = {
      app = var.app_name
    }
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

          port {
            container_port = 8080
          }

          env {
            name = "KTOR_ENV"
            value = "local"
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
      name = "http"
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
      "external-dns.alpha.kubernetes.io/hostname" = "${var.domain}, www.${var.domain}"
      "kubernetes.io/ingress.class" = "alb"
      "alb.ingress.kubernetes.io/target-type" = "ip"
      "alb.ingress.kubernetes.io/scheme" = "internet-facing"
      "alb.ingress.kubernetes.io/listen-ports" = jsonencode([{"HTTP":80},{"HTTPS":443}])
      "alb.ingress.kubernetes.io/actions.ssl-redirect" = jsonencode({"Type": "redirect", "RedirectConfig": { "Protocol": "HTTPS", "Port": "443", "StatusCode": "HTTP_301"}})
      "alb.ingress.kubernetes.io/inbound-cidrs" = "0.0.0.0/0" # NOTE: this is highly recommended when using an internet-facing ALB
      # No need to declare certificate: https://kubernetes-sigs.github.io/aws-load-balancer-controller/v2.2/guide/ingress/cert_discovery/
    }
  }

  spec {
    ingress_class_name = "alb"

    # certificate discovery via ingress tls. see url above for more info
    tls {
      hosts = [var.domain, "www.${var.domain}"]
    }

    // taken from https://www.stacksimplify.com/aws-eks/aws-alb-ingress/learn-to-enable-ssl-redirect-in-alb-ingress-service-on-aws-eks/
    rule {
      http {
        path {
          path     = "/"
          path_type = "Prefix"

          backend {
            service {
              name = "ssl-redirect"

              port {
                name = "use-annotation"
              }
            }
          }
        }
      }
    }

    rule {
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

