.SILENT:

default:
	java -version
	docker -v
	terraform -v
	terragrunt -v

minikube-up:
	terragrunt run-all apply --terragrunt-working-dir infra/minikube/ --terragrunt-non-interactive

minikube-down:
	terragrunt run-all destroy --terragrunt-working-dir infra/minikube/ --terragrunt-non-interactive;minikube delete --all --purge

load-image:
	./gradlew jibDockerBuild;minikube image load ktor-starter-app:latest

app-up: load-image
	terragrunt run-all apply --terragrunt-working-dir app/deploy/ --terragrunt-non-interactive

app-down:
	terragrunt run-all destroy --terragrunt-working-dir app/deploy/ --terragrunt-non-interactive
