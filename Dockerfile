FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S digitrans && adduser -S digitrans -G digitrans
COPY --from=builder /build/target/*.jar app.jar
USER digitrans
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
