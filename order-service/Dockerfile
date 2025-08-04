FROM maven:3-openjdk-17 AS build
WORKDIR /code
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /code
COPY --from=build /code/target/order-service-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]