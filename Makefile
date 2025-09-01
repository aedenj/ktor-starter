.SILENT:

platform_env ?= dev
region ?= us-east-1
org_name ?= Archegos

export ORG_NAME=$(shell echo $(org_name) | tr '[:upper:]' '[:lower:]')
export DEPLOYMENT=$(platform_env)-$(region)
export TF_VAR_kube_data_auth_enabled=true

default:
	java -version
	docker -v
	terraform -v
	terragrunt -v

plan-all:
	echo "Planning all resources for ORG: $(org_name), DEPLOYMENT: $(DEPLOYMENT)"
	TF_VAR_kube_data_auth_enabled=false \
		terragrunt run --all plan \
			--working-dir $(abspath deploy/aws/) \
			--non-interactive
.PHONY: plan-all

minikube-up:
	terragrunt run --all apply --working-dir deploy/minikube/infra --non-interactive
.PHONY: minikube-up

minikube-down:
	terragrunt run --all destroy --working-dir deploy/minikube/infra --non-interactive;minikube delete --all --purge
.PHONY: minikube-down

push-image-to-minikube:
	./gradlew jibDockerBuild;minikube image load ktor-starter-app:latest
.PHONY: push-image-to-minikube

app-up: push-image-to-minikube
	terragrunt run --all apply --working-dir deploy/minikube/service --non-interactive
.PHONY: app-up

app-down:
	terragrunt run --all destroy --working-dir deploy/minikube/service --non-interactive
.PHONY: app-down

deploy-aws-infra:
	echo "Deploying resources for ORG: $(org_name), DEPLOYMENT: $(DEPLOYMENT)"
	TF_VAR_kube_data_auth_enabled=false \
		terragrunt run --all apply \
			--queue-include-dir $(abspath deploy/aws/infra/) \
			--non-interactive
.PHONY: deploy-aws-infra

destroy-aws-infra:
	echo "Destroying all resources for ORG: $(org_name), DEPLOYMENT: $(DEPLOYMENT)"
	TF_VAR_kube_data_auth_enabled=false \
		terragrunt run --all destroy \
			--queue-include-dir $(abspath deploy/aws/infra/) \
			--non-interactive
.PHONY: destroy-aws-infra

deploy-aws-service:
	echo "Deploying resources for ORG: $(org_name), DEPLOYMENT: $(DEPLOYMENT)"
	TF_VAR_kube_data_auth_enabled=false \
		terragrunt run --all apply \
			--queue-include-dir $(abspath deploy/aws/service/) \
			--non-interactive
.PHONY: deploy-aws-service

destroy-aws-service:
	echo "Destroying all resources for ORG: $(org_name), DEPLOYMENT: $(DEPLOYMENT)"
	TF_VAR_kube_data_auth_enabled=false \
		terragrunt run --all destroy \
			--queue-include-dir $(abspath deploy/aws/service/) \
			--non-interactive
.PHONY: destroy-aws-service
