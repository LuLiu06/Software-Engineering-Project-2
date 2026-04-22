package org.example;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseConnectionTest {

    @Test
    void buildConnectionUrlAppendsMysqlParams() {
        DatabaseConnection db = new DatabaseConnection("jdbc:mysql://localhost:3306/mydb", "u", "p");
        String url = db.buildConnectionUrl();
        assertTrue(url.contains("serverTimezone"));
        assertTrue(url.contains("useUnicode"));
    }

    @Test
    void buildConnectionUrlLeavesH2Unchanged() {
        String h2 = "jdbc:h2:mem:x;MODE=MySQL;DB_CLOSE_DELAY=-1";
        DatabaseConnection db = new DatabaseConnection(h2, "sa", "");
        assertTrue(db.buildConnectionUrl().startsWith("jdbc:h2:"));
    }

    @Test
    void h2ConnectionAndTestConnection() throws SQLException {
        String url = "jdbc:h2:mem:dbconntest;MODE=MySQL;DB_CLOSE_DELAY=-1";
        DatabaseConnection db = new DatabaseConnection(url, "sa", "");
        assertTrue(db.testConnection());
        try (Connection c = db.getConnection()) {
            assertFalse(c.isClosed());
        }
    }

    @Test
    void buildConnectionUrlKeepsUrlThatAlreadyHasQueryString() {
        String withQuery = "jdbc:mysql://localhost:3306/db?useSSL=false";
        DatabaseConnection db = new DatabaseConnection(withQuery, "u", "p");
        assertEquals(withQuery, db.buildConnectionUrl());
    }

    @Test
    void getUrlAndGetUser() {
        DatabaseConnection db = new DatabaseConnection("jdbc:h2:mem:g;MODE=MySQL", "alice", "secret");
        assertEquals("jdbc:h2:mem:g;MODE=MySQL", db.getUrl());
        assertEquals("alice", db.getUser());
    }

    @Test
    void getInstanceReturnsSameSingleton() {
        DatabaseConnection.clearInstanceForTests();
        DatabaseConnection a = DatabaseConnection.getInstance();
        DatabaseConnection b = DatabaseConnection.getInstance();
        assertSame(a, b);
    }
}
