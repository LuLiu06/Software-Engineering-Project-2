# Java Shopping Cart Application

A Java console application that calculates the total cost of items in a shopping cart with localization support for multiple languages.

## Features

- Calculate total cost of items based on price and quantity
- Multi-language support (English, Finnish, Swedish, Japanese)
- UTF-8 encoding for proper character display
- Dockerized application
- CI/CD pipeline with Jenkins
- Unit tests with JaCoCo coverage

## Supported Languages

- English (en_US) - Default
- Finnish (fi_FI) - Suomi
- Swedish (sv_SE) - Svenska
- Japanese (ja_JP) - 日本語

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker (for containerization)
- Jenkins (for CI/CD)

## Building the Application

```bash
# Clean and build
mvn clean package

# Run tests
mvn test

# Generate coverage report
mvn test jacoco:report
```

## Running the Application

### Using Java

```bash
java -Dfile.encoding=UTF-8 -jar target/shopping-cart-1.0-SNAPSHOT.jar
```

### Using Docker

```bash
# Build Docker image
docker build -t shopping-cart-app .

# Run interactively
docker run -it shopping-cart-app
```

## Project Structure

```
.
├── src/
│   ├── main/
│   │   ├── java/org/example/
│   │   │   ├── ShoppingCart.java      # Cart logic
│   │   │   └── ShoppingCartApp.java   # Main application
│   │   └── resources/
│   │       ├── MessagesBundle_en_US.properties
│   │       ├── MessagesBundle_fi_FI.properties
│   │       ├── MessagesBundle_sv_SE.properties
│   │       └── MessagesBundle_ja_JP.properties
│   └── test/
│       └── java/org/example/
│           └── ShoppingCartTest.java
├── Dockerfile
├── Jenkinsfile
├── pom.xml
└── README.md
```

## Jenkins Pipeline

The Jenkinsfile includes the following stages:

1. **Checkout** - Get source code from repository
2. **Build** - Compile the application
3. **Test** - Run unit tests with JaCoCo coverage
4. **Package** - Create JAR file
5. **Build Docker Image** - Build the Docker image
6. **Push to Docker Hub** - Deploy image to Docker Hub

## Testing on Play with Docker

1. Go to https://labs.play-with-docker.com/
2. Start a new session
3. Pull the image: `docker pull your-username/shopping-cart-app:latest`
4. Run: `docker run -it your-username/shopping-cart-app:latest`

## Author

Lu Liu
