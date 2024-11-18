resource "kubernetes_manifest" "self_signed_cluster_issuer" {
  manifest = {
    apiVersion = "cert-manager.io/v1"
    kind       = "ClusterIssuer"
    metadata = {
      name = "selfsigned"
    }

    spec = { selfSigned = {} }
  }

  depends_on = [module.cert_manager]
}
