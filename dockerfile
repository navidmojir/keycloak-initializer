# docker build -t keycloak-initializer:0.0.1-SNAPSHOT .

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY /target/*.jar app.jar
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
