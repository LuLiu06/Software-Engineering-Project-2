package org.example;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalizationServiceTest {

    /** Forces DB errors so LocalizationService exercises fallback paths without Mockito. */
    private static class FailingDatabaseConnection extends DatabaseConnection {
        FailingDatabaseConnection() {
            super("jdbc:mysql://127.0.0.1:1/invalid", "u", "p");
        }

        @Override
        public Connection getConnection() throws SQLException {
            throw new SQLException("forced failure for tests");
        }
    }

    @Test
    void loadStringsUsesDefaultsWhenConnectionFails() {
        LocalizationService service = new LocalizationService(new FailingDatabaseConnection());
        service.loadStrings("en_US");
        assertTrue(service.getString("welcome").contains("Welcome"));
    }

    @Test
    void getAvailableLanguagesReturnsFallbackWhenQueryFails() {
        LocalizationService service = new LocalizationService(new FailingDatabaseConnection());
        String[] langs = service.getAvailableLanguages();
        assertArrayEquals(new String[]{"en_US", "fi_FI", "sv_SE", "ja_JP"}, langs);
    }
}
