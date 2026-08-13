FROM eclipse-temurin:21.0.2_13-jre-alpine

LABEL name="ProxyHub2"

WORKDIR /app

COPY proxy_hub2.jar /app/proxy_hub2.jar

EXPOSE 4448

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/proxy_hub2.jar"]