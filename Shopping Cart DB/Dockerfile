# Multi-stage build for Shopping Cart with Database Localization

# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Set UTF-8 encoding for proper localization support
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# Database connection environment variables (override at runtime)
ENV DB_URL=jdbc:mysql://host.docker.internal:3306/shopping_cart_localization
ENV DB_USER=root
ENV DB_PASSWORD=root

# Copy the built JAR from builder stage
COPY --from=builder /app/target/shopping-cart-db-1.0-SNAPSHOT.jar app.jar

# Run the application with interactive mode
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
