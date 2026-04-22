package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connection manager for MySQL/MariaDB (or H2 in tests).
 */
public class DatabaseConnection {

    private static final Logger LOG = Logger.getLogger(DatabaseConnection.class.getName());

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/shopping_cart_localization";
    private static final String DEFAULT_USER = "root";

    private final String url;
    private final String user;
    private final String password;

    private static DatabaseConnection instance;

    public DatabaseConnection() {
        this.url = System.getenv().getOrDefault("DB_URL", DEFAULT_URL);
        this.user = System.getenv().getOrDefault("DB_USER", DEFAULT_USER);
        this.password = System.getenv().getOrDefault("DB_PASSWORD", "");
    }

    public DatabaseConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Clears singleton (for tests only).
     */
    static void clearInstanceForTests() {
        instance = null;
    }

    /**
     * Gets a singleton instance of DatabaseConnection.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Builds JDBC URL with MySQL-only parameters when appropriate.
     */
    String buildConnectionUrl() {
        if (url.startsWith("jdbc:h2:")) {
            return url;
        }
        if (url.contains("?")) {
            return url;
        }
        return url + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    }

    /**
     * Gets a database connection.
     *
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(buildConnectionUrl(), user, password);
    }

    /**
     * Tests the database connection.
     *
     * @return true if connection is successful
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Database connection failed", e);
            return false;
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }
}
