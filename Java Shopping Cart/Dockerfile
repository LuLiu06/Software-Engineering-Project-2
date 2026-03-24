# Multi-stage build for Java Shopping Cart Application

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
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Set UTF-8 encoding for proper localization support
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# Copy the built JAR from builder stage
COPY --from=builder /app/target/shopping-cart-1.0-SNAPSHOT.jar app.jar

# Run the application with interactive mode
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
