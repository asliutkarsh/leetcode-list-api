# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests=true

# Stage 2: Create the final image
FROM eclipse-temurin:17.0.12_7-jre-alpine
WORKDIR /app
# Set default environment variables (these can be overridden at runtime)
ENV SPRING_PROFILES_ACTIVE=prod
ENV SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/leetcode
ENV SPRING_DATASOURCE_USERNAME=default_username
ENV SPRING_DATASOURCE_PASSWORD=default_password
ENV MAIL_USERNAME=default_username
ENV MAIL_PASSWORD=default_password

COPY --from=build /app/target/leetcode-api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]