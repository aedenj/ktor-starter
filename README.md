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
<!-- toc-end -->

## Overview

This starter is a set of utilities built on top of [Ktor](https://ktor.io) to quickly bootstrap a microservice. This starter
sets up the following features:

**Service** 
* A simple HTTP server with a default port of 8080 using Netty's EngineMain.
* Ignore trailing slashes in routes. (e.g. `foo/` and `foo` are treated the same)
* Kubernetes style startup, readiness and liveness [health checks](https://kubernetes.io/docs/concepts/configuration/liveness-readiness-startup-probes/)
  using [Cohort](https://github.com/sksamuel/cohort).
* The following Kotlin quality plugins are setup: ktlint, detekt and kover 
 
**Infrastructure**
* GitHub workflows for CI/CD 



## Pre-Requisites
1. Docker

2. [Gradle](https://gradle.org) - via your preferred development workflow.

## Up & Running

Let's first clone the repo and fire up our system,

```
git clone git@github.com:aedenj/kotlin-microservice-starter.git ~/projects/kotlin-microservice-starter
cd ~/projects/kotlin-microservice-starter;docker compose up
```

This will start the app on port 8080. Navigate to [http://localhost:8080](http://localhost:8080) to see the app running.


## Live Reload

When running the service via docker compose [Ktor's auto-reload](https://ktor.io/docs/server-auto-reload.html#recompile)
functionality is enabled. This means that if you open a separate terminal and run the command `./gradlew -t build -x test -i`
when you make changes to the code, the service will automatically reload.

## Troubleshooting

* When running JIB should you encounter an authentication error like following,

```shell
Got output:

credentials not found in native keychain

The credential helper (docker-credential-desktop) has nothing for server URL: registry.hub.docker.com
```
 try doing the following,

1. Add the following to your ~/.docker/config.json file,

```json
"credHelpers" : {
  "registry.hub.docker.com":"desktop"
},
```

2. Re-run the JIB task.
