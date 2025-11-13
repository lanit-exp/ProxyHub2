FROM maven:3.9.6-eclipse-temurin-17 as build

WORKDIR /data

COPY / /data

RUN mvn package


FROM eclipse-temurin:17.0.15_6-jre-alpine

ARG APP_PORT=4448

LABEL name="ProxyHub2"

ENV APP_PORT $APP_PORT

COPY ./entrypoint .
COPY --from=build /data/target/*.jar ./proxy_hub2.jar

EXPOSE $APP_PORT

ENTRYPOINT ["sh", "entrypoint"]