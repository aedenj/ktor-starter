
resource "minikube_cluster" "docker" {
  cluster_name = "minikube" # ensure there's a profile with this name so kubectl can access the cluster
  driver       = "docker"
  container_runtime = "docker"
  memory = "4096mb"
  addons = [
    "metrics-server",
    "dashboard",
    "default-storageclass",
    "storage-provisioner",
    "ingress",
  ]
  wait = ["all"]

  provisioner "local-exec" {
    command = <<EOT
      while [[ $(kubectl get endpointslices -n ingress-nginx -l kubernetes.io/service-name="ingress-nginx-controller-admission" -o json | \
                 jq '[.items[].endpoints[] | select(.conditions.ready != true)] | length') -ne 0 ]]; do
          echo "Waiting for all ingress endpoints to be ready..."
          sleep 5
      done

      echo "All ingress endpoints are ready!"
    EOT
  }
}