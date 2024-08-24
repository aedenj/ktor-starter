FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

COPY . .

RUN ./gradlew buildFatJar --no-daemon


FROM eclipse-temurin:21-jre-alpine AS release

ENV PORT=8080
ENV APP=app.jar

WORKDIR /app
COPY --from=builder /build/app/build/libs/ktor-starter*.jar app.jar

CMD java \
  -Dserver.port=${PORT} \
  -jar ${APP}
