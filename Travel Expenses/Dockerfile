# Multi-stage build for JavaFX Travel Expenses Application

# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage with JavaFX support
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Install JavaFX dependencies for headless operation
RUN apt-get update && apt-get install -y \
    libgtk-3-0 \
    libglu1-mesa \
    libx11-6 \
    libxxf86vm1 \
    && rm -rf /var/lib/apt/lists/*

# Set UTF-8 encoding
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# Copy Maven and project files
COPY --from=builder /root/.m2 /root/.m2
COPY --from=builder /app /app

# Run the application
CMD ["mvn", "javafx:run", "-f", "/app/pom.xml"]
