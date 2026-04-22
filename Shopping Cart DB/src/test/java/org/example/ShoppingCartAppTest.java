package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
class ShoppingCartAppTest {

    private PrintStream originalOut;

    @BeforeEach
    void saveOut() {
        originalOut = System.out;
        DatabaseConnection.clearInstanceForTests();
    }

    @AfterEach
    void restoreOut() {
        System.setOut(originalOut);
    }

    @ParameterizedTest
    @CsvSource({"1,en_US", "2,fi_FI", "3,sv_SE", "4,ja_JP", "0,en_US", "9,en_US"})
    void languageForChoice(int choice, String expected) {
        assertEquals(expected, ShoppingCartApp.languageForChoice(choice));
    }

    @Test
    void runCompletesAndSavesWithH2() throws Exception {
        String url = "jdbc:h2:mem:appfull;MODE=MySQL;DB_CLOSE_DELAY=-1";
        DatabaseConnection db = new DatabaseConnection(url, "sa", "");
        try (var c = db.getConnection()) {
            H2TestSchema.createTables(c);
        }

        String input = "1\n1\n10.0\n2\n";
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bout, true, StandardCharsets.UTF_8);

        ShoppingCartApp app = new ShoppingCartApp(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                ps,
                new LocalizationService(db),
                new CartService(db, ps),
                db);
        app.run();

        String text = bout.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Total cost"));
        assertTrue(text.contains("20"));
        assertTrue(text.contains("saved") || text.contains("database"));
    }

    @Test
    void runWhenSaveFailsShowsLocalizedError() throws Exception {
        String url = "jdbc:h2:mem:appsavefail;MODE=MySQL;DB_CLOSE_DELAY=-1";
        DatabaseConnection db = new DatabaseConnection(url, "sa", "");
        try (Connection c = db.getConnection()) {
            H2TestSchema.createTables(c);
        }

        String input = "1\n1\n5.0\n2\n";
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bout, true, StandardCharsets.UTF_8);

        CartService failingCart = new CartService(db, ps) {
            @Override
            public int saveCart(List<CartItem> items, double totalCost, String language) {
                return -1;
            }
        };

        ShoppingCartApp app = new ShoppingCartApp(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                ps,
                new LocalizationService(db),
                failingCart,
                db);
        app.run();

        String text = bout.toString(StandardCharsets.UTF_8);
        assertTrue(text.toLowerCase().contains("error") || text.contains("database"));
    }

    @Test
    void runRetriesInvalidLanguageAndPriceInputs() throws Exception {
        String url = "jdbc:h2:mem:appretry;MODE=MySQL;DB_CLOSE_DELAY=-1";
        DatabaseConnection db = new DatabaseConnection(url, "sa", "");
        try (Connection c = db.getConnection()) {
            H2TestSchema.createTables(c);
        }

        String input = "9\nx\n3\n1\nx\n4.0\n2\n";
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bout, true, StandardCharsets.UTF_8);

        ShoppingCartApp app = new ShoppingCartApp(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                ps,
                new LocalizationService(db),
                new CartService(db, ps),
                db);
        app.run();

        String text = bout.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Total cost"));
        assertTrue(text.contains("8"));
    }
}
