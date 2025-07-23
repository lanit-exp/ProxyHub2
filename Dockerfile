FROM eclipse-temurin:17.0.10_7-jre-alpine

COPY ./entrypoint .
COPY ./target/proxy_hub.jar .

ENTRYPOINT ["sh", "entrypoint"]