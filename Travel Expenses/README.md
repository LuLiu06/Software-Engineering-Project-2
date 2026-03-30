# Travel Expenses Calculator

A JavaFX application that calculates fuel consumption and total trip cost with multi-language support.

## Features

- Calculate fuel consumption based on trip distance
- Calculate total trip cost based on fuel price
- Multi-language support:
  - English (EN)
  - French (FR)
  - Japanese (JP)
  - Persian (IR)
- Dynamic language switching
- Modern UI with FXML layout

## Formula

```
Total Fuel = (Consumption ÷ 100) × Distance
Total Cost = Total Fuel × Fuel Price
```

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- JavaFX 21

## Running the Application

```bash
# Clean and run
mvn clean javafx:run
```

## Project Structure

```
Travel Expenses/
├── src/
│   └── main/
│       ├── java/
│       │   ├── module-info.java
│       │   └── org/example/
│       │       ├── TravelExpensesApp.java
│       │       └── TravelExpensesController.java
│       └── resources/
│           ├── TravelExpenses.fxml
│           ├── messages_en_US.properties
│           ├── messages_fr_FR.properties
│           ├── messages_ja_JP.properties
│           └── messages_fa_IR.properties
├── Dockerfile
├── Jenkinsfile
├── pom.xml
└── README.md
```

## Example Usage

1. Launch the application
2. Enter trip details:
   - Distance: 180 km
   - Fuel Consumption: 6.5 L/100 km
   - Fuel Price: 2.05 per liter
3. Click "Calculate Trip Cost"
4. View results: Total fuel needed and total cost
5. Switch language using EN/FR/JP/IR buttons

## Docker

```bash
# Build image
docker build -t travel-expenses-app .

# Run (requires X11 display)
docker run -it travel-expenses-app
```

## Author

Lu Liu
