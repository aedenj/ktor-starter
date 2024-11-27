resource "kubernetes_manifest" "ktor_starter_infra_tf" {
  manifest = {
    apiVersion = "infra.contrib.fluxcd.io/v1alpha2"
    kind       = "Terraform"
    metadata = {
      name      = "ktor-starter-infra"
      namespace = "flux-system"
    }
    spec = {
      interval = "1m"
      approvePlan =  "auto"
      path = "./infra/aws/appV2/infra"
      sourceRef = {
        kind = "GitRepository"
        name = "ktor-starter"
        namespace = "flux-system"
      }
      vars = [
        {
          name  = "cluster_name"
          value = var.cluster_name
        },
        {
          name = "app_namespace"
          value = "ktor-starter"
        },
        {
          name = "app_name"
          value = "ktor-starter"
        }
      ]
      backendConfig = {
        customConfiguration = <<-EOF
          backend "s3" {
            bucket                      = "terraform-state-archegos-us-east-1"
            key                         = "dev/flux/ktor-starter/infra/terraform.tfstate"
            region                      = "us-east-1"
            encrypt                     = false
          }
        EOF
      }

      runnerPodTemplate = {
        spec = {
          envFrom = [
            {
              secretRef = {
                name = "aws-credentials"
              }
            }
          ]
        }
      }
    }
  }
}




