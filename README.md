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

This starter is a set of utilities built on top of [Ktor](https://ktor.io) to quickly bootstrap a microservice and deploy it to Kubernetes. 
This starter sets up the following features:

**Service** 
* Application architecture that [favors grouping by feature](https://ktor.io/docs/server-application-structure.html#group_by_feature). 
* Supplement [Ktor's command line configuration](https://ktor.io/docs/server-configuration-file.html#command-line) with [Hoplite](https://github.com/sksamuel/hoplite) to provide a more complete
  solution for achieving a [12 Factor App](https://12factor.net/config).
* The following service features using ktor plugins are setup :
  * OpenApi & Swagger via [Smiley4 Ktor OpenApi](https://smiley4.github.io/ktor-openapi-tools/latest/) 
  * [Ignore trailing slashes](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.routing/-ignore-trailing-slash.html) in routes. (e.g. `foo/` and `foo` are treated the same)
  * [Default headers](https://ktor.io/docs/server-default-headers.html#configure)
  * Response [compression](https://ktor.io/docs/server-compression.html)
  * Kubernetes style startup, readiness and liveness [health checks](https://kubernetes.io/docs/concepts/configuration/liveness-readiness-startup-probes/)
    using [Cohort](https://github.com/sksamuel/cohort).
* Graceful Shutdown using structured concurrency with [Arrow's SuspendApp](https://arrow-kt.io/learn/coroutines/suspendapp/ktor/)
* The following Kotlin quality plugins are setup: 
  * [ktlint](https://pinterest.github.io/ktlint/latest/) - Linter & Formatter
  * [Detekt](https://detekt.dev/) - A static code analyzer
  * [Kover](https://kotlin.github.io/kotlinx-kover/gradle-plugin/) - Code coverage
  

**Infrastructure**
* Multi-environment Terraform setup using Terragrunt

### Donations

Should you find any of this project useful, please consider donating through,

<a href="https://www.buymeacoffee.com/aeden" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a>

At a minimum it helps with the AWS bill.

## Pre-Requisites
1. Docker
2. [Terraform](https://www.terraform.io) - for infrastructure as code.
3. [Terragrunt](https://terragrunt.gruntwork.io/) - for managing multiple Terraform environments.
4. [Gradle](https://gradle.org) - via your preferred development workflow.

I typically manage installations using [asdf](https://asdf-vm.com/), but to each their own. If you do use,
asdf, there is a `.tool-versions` file in the root of the project that you can use for installation.

## Up & Running

Let's first clone the repo and fire up our system,

```shell
git clone git@github.com:aedenj/ktor-starter.git ~/projects/ktor-starter
cd ~/projects/ktor-starter;docker compose up
```

This will start the app on port 8080. Navigate to [http://localhost:8080](http://localhost:8080) to see the app running.


## Live Reload

When running the service via docker compose [Ktor's auto-reload](https://ktor.io/docs/server-auto-reload.html#recompile)
functionality is enabled. This means that if you open a separate terminal and run the command `./gradlew -t build -x test -i`
when you make changes to the code, the service will automatically reload.


## Contributing

Contributions are welcome! Please fork this repository and submit pull requests.

1. Fork it (https://github.com/aedenj/ktor-starter/fork)
2. Create your feature branch (`git checkout -b feature/my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin feature/my-new-feature`)
5. Create a new Pull Request

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
