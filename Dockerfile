FROM openjdk:8-jre-alpine
COPY target/uberjar/transaction-service.jar /app/transaction-service.jar
WORKDIR /app
EXPOSE 8082
CMD ["java", "-jar", "transaction-service.jar"]
