FROM gradle:jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src src
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]