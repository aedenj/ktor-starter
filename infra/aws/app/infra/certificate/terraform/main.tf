resource "kubernetes_manifest" "app_certificate" {
  manifest = {
    apiVersion = "cert-manager.io/v1"
    kind       = "Certificate"
    metadata = {
      name = "${var.app_name}-certificate"
      namespace = var.namespace
    }

    spec = {
      secretName = "${var.app_name}-certificate"
      revisionHistoryLimit = 1
      privateKey = {
        rotationPolicy = "Always"
      }
      commonName = var.domain
      dnsNames = [
          var.domain
      ]
      usages = [
        "server auth",
        "key encipherment",
        "digital signature"
      ]
      issuerRef = {
          name = "selfsigned"
          kind = "ClusterIssuer"
      }
    }
  }
}
