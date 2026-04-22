package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates Shopping Cart DB tables in H2 (MySQL compatibility mode) for integration tests.
 */
final class H2TestSchema {

    private H2TestSchema() {
    }

    static void createTables(Connection c) throws SQLException {
        try (Statement s = c.createStatement()) {
            s.execute("DROP TABLE IF EXISTS cart_items");
            s.execute("DROP TABLE IF EXISTS cart_records");
            s.execute("DROP TABLE IF EXISTS localization_strings");
            s.execute("""
                    CREATE TABLE cart_records (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        total_items INT NOT NULL,
                        total_cost DOUBLE NOT NULL,
                        language VARCHAR(10),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
            s.execute("""
                    CREATE TABLE cart_items (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        cart_record_id INT,
                        item_number INT NOT NULL,
                        price DOUBLE NOT NULL,
                        quantity INT NOT NULL,
                        subtotal DOUBLE NOT NULL,
                        FOREIGN KEY (cart_record_id) REFERENCES cart_records(id) ON DELETE CASCADE
                    )
                    """);
            // Backticks on `value` — unquoted VALUE is reserved in H2
            s.execute("""
                    CREATE TABLE localization_strings (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        `key` VARCHAR(100) NOT NULL,
                        `value` VARCHAR(255) NOT NULL,
                        language VARCHAR(10) NOT NULL
                    )
                    """);
        }
    }
}
