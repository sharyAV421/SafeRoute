FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./pom.xml
COPY src ./src
RUN mvn -B package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 10001 saferoute
COPY --from=build --chown=10001:10001 /workspace/target/saferoute-api-0.1.0.jar /app/app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "test -n \"$MONGODB_URI\" || { echo 'MONGODB_URI is required'; exit 1; }; exec java -XX:MaxRAMPercentage=75.0 -jar /app/app.jar"]
