package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager for MySQL/MariaDB.
 */
public class DatabaseConnection {
    
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/shopping_cart_localization";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "Lethergo123.";
    
    private final String url;
    private final String user;
    private final String password;
    
    private static DatabaseConnection instance;
    
    public DatabaseConnection() {
        this.url = System.getenv().getOrDefault("DB_URL", DEFAULT_URL);
        this.user = System.getenv().getOrDefault("DB_USER", DEFAULT_USER);
        this.password = System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);
    }
    
    public DatabaseConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
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
     * Gets a database connection.
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        
        return DriverManager.getConnection(
            url + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC",
            user,
            password
        );
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
            System.err.println("Database connection failed: " + e.getMessage());
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
