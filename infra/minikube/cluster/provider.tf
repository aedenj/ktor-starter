terraform {
  required_providers {
    minikube = {
      source  = "scott-the-programmer/minikube"
      version = "0.4.2"
    }
  }
}

provider "minikube" {
  kubernetes_version = "v1.31.2"
}
