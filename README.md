# Ktor Starter (In Progress)
[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)
[![Build](https://github.com/aedenj/kotlin-microservice-starter/actions/workflows/build.yml/badge.svg)](https://github.com/aedenj/kotlin-microservice-starter/actions/workflows/build.yml)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)
[![OpenSSF Scorecard](https://api.securityscorecards.dev/projects/github.com/aedenj/kotlin-microservice-starter/badge)](https://securityscorecards.dev/viewer/?uri=github.com/aedenj/kotlin-microservice-starter)

This starter is a set of utilities built on top of [Ktor](https://ktor.io) to quickly bootstrap microservice. This starter
sets up the following features:

* A simple HTTP server with a default port of 8080 using Netty's EngineMain.


<!-- toc-begin -->
* [Pre-requisites](#pre-requisites)
* [Up & Running](#up--running)
* [Live Reload](#live-reload)
<!-- toc-end -->


## Pre-Requisites
1. Docker

1. [Gradle](https://gradle.org) - via your preferred development workflow.

## Up & Running

Let's first clone the repo and fire up our system,

```
git clone git@github.com:aedenj/kotlin-microservice-starter.git ~/projects/kotlin-microservice-starter
cd ~/projects/kotlin-microservice-starter;docker compose up
```

This will start the app on port 8080. Navigate to [http://localhost:8080](http://localhost:8080) to see the app running.


## Live Reload

When running the service via docker compose [Ktor's auto-reload]https://ktor.io/docs/server-auto-reload.html#recompile()
functionality is enabled. This means that if you open a separate terminal and run the command `./gradlew -t build -x test -i`
when you make changes to the code, the service will automatically reload.
