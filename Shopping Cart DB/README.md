# Shopping Cart with Database Localization

A Java console application that calculates shopping cart totals with multi-language support using database-driven localization.

## Features

- Read UI messages from database instead of ResourceBundle properties files
- Save shopping cart calculations to `cart_records` table
- Save individual cart items to `cart_items` table with foreign key relationship
- Multi-language support (English, Finnish, Swedish, Japanese)
- UTF-8 encoding for proper character display

## Database Schema

### Tables

1. **cart_records** - Stores main shopping cart records
   - `id` - Primary key
   - `total_items` - Number of items in cart
   - `total_cost` - Total cost of cart
   - `language` - Language used during checkout
   - `created_at` - Timestamp

2. **cart_items** - Stores individual cart items
   - `id` - Primary key
   - `cart_record_id` - Foreign key to cart_records
   - `item_number` - Item sequence number
   - `price` - Item price
   - `quantity` - Item quantity
   - `subtotal` - Item subtotal (price × quantity)

3. **localization_strings** - Stores UI messages
   - `id` - Primary key
   - `key` - Message key
   - `value` - Localized message value
   - `language` - Language code (en_US, fi_FI, sv_SE, ja_JP)

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or MariaDB
- Docker (optional)

## Setup Instructions

### 1. Database Setup

Start MySQL and run the initialization script:

```bash
mysql -u root -p < db/init.sql
```

Or manually:

```sql
CREATE DATABASE IF NOT EXISTS shopping_cart_localization
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shopping_cart_localization;
-- Run the rest of db/init.sql
```

### 2. Configure Database Connection

Default connection settings (can be overridden via environment variables):
- URL: `jdbc:mysql://localhost:3306/shopping_cart_localization`
- User: `root`
- Password: `root`

Environment variables:
- `DB_URL` - Database URL
- `DB_USER` - Database username
- `DB_PASSWORD` - Database password

### 3. Build and Run

```bash
# Build the application
mvn clean package

# Run the application
java -jar target/shopping-cart-db-1.0-SNAPSHOT.jar
```

## Docker Usage

### Using Docker Compose (Recommended)

```bash
# Start MySQL and application
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Manual Docker Build

```bash
# Build image
docker build -t shopping-cart-db .

# Run with local MySQL
docker run -it \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/shopping_cart_localization \
  -e DB_USER=root \
  -e DB_PASSWORD=root \
  shopping-cart-db
```

## Project Structure

```
Shopping Cart DB/
├── src/
│   └── main/
│       └── java/org/example/
│           ├── ShoppingCartApp.java      # Main application
│           ├── DatabaseConnection.java   # Database connection manager
│           ├── LocalizationService.java  # Fetches localized strings from DB
│           ├── CartService.java          # Saves cart records to DB
│           └── CartItem.java             # Cart item model
├── db/
│   └── init.sql                          # Database initialization script
├── Dockerfile
├── docker-compose.yml
├── Jenkinsfile
├── pom.xml
└── README.md
```

## Submission Requirements

1. **Screenshots of cart_records table** showing:
   - total_items
   - total_cost
   - language
   - timestamp

2. **Screenshots of cart_items table** showing:
   - cart_record_id (foreign key)
   - item_number
   - price
   - quantity
   - subtotal

3. **Application screenshots** with different languages

4. **GitHub repository** including:
   - Source code
   - Database schema (db/init.sql)
   - Dockerfile
   - Jenkinsfile
   - README with setup instructions

## Example Usage

```
Select your language / Valitse kieli / Välj språk / 言語を選択:
1. English
2. Suomi (Finnish)
3. Svenska (Swedish)
4. 日本語 (Japanese)
Enter choice (1-4): 1

Welcome to the Shopping Cart Application!
==================================================
Enter the number of items to purchase: 2

Item 1:
-------------------------
Enter the price for item: 10.99
Enter the quantity for item: 3
Item cost: 32.97

Item 2:
-------------------------
Enter the price for item: 5.50
Enter the quantity for item: 2
Item cost: 11.00

==================================================
Total cost: 43.97
==================================================

Shopping cart saved to database! (ID: 1)

=== Cart Records ===
ID    Total Items  Total Cost   Language   Created At          
------------------------------------------------------------
1     2            43.97        en_US      2026-03-30 12:00:00

=== Cart Items for Record #1 ===
ID    Item Number  Price      Quantity   Subtotal  
--------------------------------------------------
1     1            10.99      3          32.97     
2     2            5.50       2          11.00     

Thank you for shopping with us!
```

## Author

Lu Liu
