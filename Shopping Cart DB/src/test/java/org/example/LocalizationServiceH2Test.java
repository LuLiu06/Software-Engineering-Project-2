package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalizationServiceH2Test {

    private DatabaseConnection db;

    @BeforeEach
    void setUp() throws Exception {
        String url = "jdbc:h2:mem:localesvc;MODE=MySQL;DB_CLOSE_DELAY=-1";
        db = new DatabaseConnection(url, "sa", "");
        try (Connection c = db.getConnection()) {
            H2TestSchema.createTables(c);
            try (Statement st = c.createStatement()) {
                st.execute("""
                        INSERT INTO localization_strings (`key`, `value`, language)
                        VALUES ('welcome', 'Hello from DB', 'en_US')
                        """);
                st.execute("""
                        INSERT INTO localization_strings (`key`, `value`, language)
                        VALUES ('welcome', 'Hei', 'fi_FI')
                        """);
            }
        }
    }

    @Test
    void loadStringsReadsFromDatabase() {
        LocalizationService service = new LocalizationService(db);
        service.loadStrings("en_US");
        assertEquals("Hello from DB", service.getString("welcome"));
        assertEquals("en_US", service.getCurrentLanguage());
    }

    @Test
    void getAvailableLanguagesReadsFromDatabase() {
        LocalizationService service = new LocalizationService(db);
        String[] langs = service.getAvailableLanguages();
        assertEquals(2, langs.length);
        assertTrue(java.util.Arrays.asList(langs).contains("en_US"));
        assertTrue(java.util.Arrays.asList(langs).contains("fi_FI"));
    }

    @Test
    void getLocalizedStringsReturnsCopy() {
        LocalizationService service = new LocalizationService(db);
        service.loadStrings("fi_FI");
        var map = service.getLocalizedStrings();
        map.clear();
        assertEquals("Hei", service.getString("welcome"));
    }
}
