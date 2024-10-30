# Ktor Starter (In Progress)
[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)
[![Build](https://github.com/aedenj/kotlin-microservice-starter/actions/workflows/build.yml/badge.svg)](https://github.com/aedenj/kotlin-microservice-starter/actions/workflows/build.yml)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)
[![OpenSSF Scorecard](https://api.securityscorecards.dev/projects/github.com/aedenj/kotlin-microservice-starter/badge)](https://securityscorecards.dev/viewer/?uri=github.com/aedenj/kotlin-microservice-starter)

<!-- toc-begin -->
* [Overview](#overview)
* [Pre-requisites](#pre-requisites)
* [Up & Running](#up--running)
* [Live Reload](#live-reload)
* [Troubleshooting](#troubleshooting)
* Additional Topics
  * [Local Development with Minikube](https://github.com/aedenj/ktor-starter/wiki/Local-Development-with-Minikube)
<!-- toc-end -->

## Overview

This starter is a set of utilities built on top of [Ktor](https://ktor.io) to quickly bootstrap a microservice. This starter
sets up the following features:

**Service** 
* Application architecture that [favors grouping by feature](https://ktor.io/docs/server-application-structure.html#group_by_feature). 
* The follow service feautures using ktor plugins are setup :
  * [Ignore trailing slashes](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.routing/-ignore-trailing-slash.html) in routes. (e.g. `foo/` and `foo` are treated the same)
  * [Default headers](https://ktor.io/docs/server-default-headers.html#configure)
  * Response [compression](https://ktor.io/docs/server-compression.html)
  * Kubernetes style startup, readiness and liveness [health checks](https://kubernetes.io/docs/concepts/configuration/liveness-readiness-startup-probes/)
    using [Cohort](https://github.com/sksamuel/cohort).
  * Swagger UI for API documentation using [Tegral's Ktor OpenAPI]( https://tegral.zoroark.guru/docs/modules/core/openapi/ktor)
* The following Kotlin quality plugins are setup: ktlint, detekt and kover
* Supplement [Ktor's command line configuration](https://ktor.io/docs/server-configuration-file.html#command-line)
  with the ability to load configuration from a file using [Hoplite](https://github.com/sksamuel/hoplite).
  
**Infrastructure**
* Multi-environment Terraform setup using Terragrunt


## Pre-Requisites
1. Docker
2. [Terraform](https://www.terraform.io) - for infrastructure as code.
3. [Terragrunt](https://terragrunt.gruntwork.io/) - for managing multiple Terraform environments.
4. [Gradle](https://gradle.org) - via your preferred development workflow.

## Up & Running

Let's first clone the repo and fire up our system,

```
git clone git@github.com:aedenj/ktor-starter.git ~/projects/ktor-starter
cd ~/projects/ktor-starter;docker compose up
```

This will start the app on port 8080. Navigate to [http://localhost:8080](http://localhost:8080) to see the app running.


## Live Reload

When running the service via docker compose [Ktor's auto-reload](https://ktor.io/docs/server-auto-reload.html#recompile)
functionality is enabled. This means that if you open a separate terminal and run the command `./gradlew -t build -x test -i`
when you make changes to the code, the service will automatically reload.

## Troubleshooting

* The jib task may fail initially from an authentication error like following,

```shell
Got output:

credentials not found in native keychain

The credential helper (docker-credential-desktop) has nothing for server URL: registry-1.docker.io
```
Make sure the task is actually failing. Jib tries serveral hosts and you may have credentials setup
for one of them. If the task is failing, you can fix this by running the following commands,

1. Run `docker login registry-1.docker.io` and enter your Docker Hub credentials. 

2. Re-run the JIB task.