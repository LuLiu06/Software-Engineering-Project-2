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

**Security:** do not put real passwords in source code. Set credentials via environment variables.

- URL (default): `jdbc:mysql://localhost:3306/shopping_cart_localization`
- User (default): `root`
- Password: **required** — set `DB_PASSWORD` (empty default will fail MySQL auth)

Environment variables:

- `DB_URL` — JDBC URL (MySQL or, for tests only, `jdbc:h2:...`)
- `DB_USER` — username
- `DB_PASSWORD` — your MariaDB/MySQL password

Example (macOS/Linux):

```bash
export DB_PASSWORD='your_password_here'
java -jar target/shopping-cart-db-1.0-SNAPSHOT.jar
```

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

## Week 4: SonarQube static analysis (Shopping Cart DB)

Prerequisites: SonarQube running at `http://localhost:9000`, Maven, JDK 17+ (JDK 21/25 works; this project uses JaCoCo **0.8.14** so coverage runs on newer JDKs).

1. In SonarQube, create a project with **Project key** `week4-assignment` (or change `sonar.projectKey` in `pom.xml` / `sonar-project.properties` to match your project).
2. Generate a token: **My Account → Security → Generate Token**.
3. **Do not commit the token.** Use an environment variable:

```bash
export SONAR_TOKEN='your_token_here'
cd "/path/to/Shopping Cart DB"
mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token="$SONAR_TOKEN"
```

`projectKey` and `projectName` are already set in the POM for `week4-assignment`. Override if needed:

`-Dsonar.projectKey=other-key -Dsonar.projectName='Other Name'`

JaCoCo runs on `verify` and produces `target/site/jacoco/jacoco.xml` for Sonar coverage.

4. Open `http://localhost:9000` → your project → take screenshots / export the report for Oma (per course instructions).

## Week 5: Jenkins quality analysis, coverage above 80%, Docker Hub

This week extends Week 4 so **SonarQube runs from Jenkins**, keeps **JaCoCo coverage** (run `mvn verify` and open `target/site/jacoco/index.html`; Sonar reads `target/site/jacoco/jacoco.xml`), **builds the Docker image in the pipeline**, and **pushes to Docker Hub**.

### 1. Jenkins credentials and Sonar URL

In **Manage Jenkins → Credentials**, create:

| Kind | ID | Purpose |
|------|-----|--------|
| Username with password | `docker-hub-credentials` | Docker Hub login (same as Week 4 pipeline) |
| Secret text | `sonar-token` | SonarQube **user token** (My Account → Security → Generate Token) |

If SonarQube is **not** reachable at `http://127.0.0.1:9000` from the agent, define **`SONAR_HOST_URL`** on the Jenkins job (for example **Configure** → **Build Environment** → **Inject environment variables**, or a parameterized build). If unset, the pipeline uses `http://127.0.0.1:9000`.

Alternatively, you can install the **SonarQube Scanner** Jenkins plugin and use **`withSonarQubeEnv('YourInstallationName')`** instead of `sonar-token` + `SONAR_HOST_URL`; then remove or adapt the Sonar stage accordingly.

**Checkout:** Prefer **Pipeline script from SCM** (same repo/branch as the code Jenkins builds) so the Checkout stage can use `checkout scm`. If the job uses an **inline** script instead, run **Build with Parameters** and set **`GIT_REPO_URL`** (HTTPS URL of this project) and **`GIT_BRANCH`** (e.g. `main`).

### 2. Sonar “before vs after”

1. Run the pipeline **once** (or note the current measures in Sonar).
2. Add or extend tests (or small refactors that add covered lines), push, run again.
3. In SonarQube, compare **Overall Coverage** and **Coverage on New Code** (the latter depends on your **New Code** definition in Sonar). Aim for **overall instruction/line coverage above 80%** as required by the assignment.

### 3. Docker Hub from Jenkins

The pipeline tags images as **`<dockerhub-username>/shopping-cart-db:<BUILD_NUMBER>`** and **`:latest`**, using `docker-hub-credentials`.

### 4. Run the image from Docker Hub

Replace `<your-dockerhub-user>` with the username from Jenkins credentials:

```bash
docker pull <your-dockerhub-user>/shopping-cart-db:latest
docker run -it --rm \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/shopping_cart_localization \
  -e DB_USER=root \
  -e DB_PASSWORD='your_mysql_password' \
  <your-dockerhub-user>/shopping-cart-db:latest
```

Ensure MySQL is running and `db/init.sql` has been applied. On Linux, if `host.docker.internal` is unavailable, use your host IP or `--network host` and `jdbc:mysql://127.0.0.1:3306/...` as appropriate.

### 5. What to hand in (typical)

- Jenkins **Blue Ocean / Stage View** screenshot showing **Build, Test & Coverage**, **SonarQube Analysis**, **Build Docker Image**, **Push to Docker Hub**.
- SonarQube **before and after** (coverage / issues) after extending tests.
- Proof of **`docker pull`** and **`docker run`** with the app working (screenshot or short log).

## Author

Lu Liu
