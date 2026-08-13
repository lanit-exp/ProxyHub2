FROM maven:3.9.6-eclipse-temurin-17 as build

WORKDIR /data

COPY / /data

RUN mvn package


FROM eclipse-temurin:17.0.15_6-jre-alpine

LABEL name="ProxyHub2"

COPY ./entrypoint .
COPY --from=build /data/target/*.jar ./proxy_hub2.jar

EXPOSE 4448

ENTRYPOINT ["sh", "entrypoint"]