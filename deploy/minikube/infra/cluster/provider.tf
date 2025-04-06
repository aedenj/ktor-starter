terraform {
  required_providers {
    minikube = {
      source  = "scott-the-programmer/minikube"
      version = "0.5.0"
    }
  }
}

provider "minikube" {
  kubernetes_version = "v1.31.2"
}
