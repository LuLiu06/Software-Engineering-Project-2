package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartServiceH2Test {

    private DatabaseConnection db;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() throws Exception {
        originalOut = System.out;
        String url = "jdbc:h2:mem:cartsvc;MODE=MySQL;DB_CLOSE_DELAY=-1";
        db = new DatabaseConnection(url, "sa", "");
        try (Connection c = db.getConnection()) {
            H2TestSchema.createTables(c);
        }
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void saveCartInsertsRecordAndItems() throws Exception {
        CartService service = new CartService(db);
        List<CartItem> items = List.of(
                new CartItem(1, 10.0, 2),
                new CartItem(2, 5.0, 1)
        );
        int id = service.saveCart(items, 25.0, "en_US");
        assertTrue(id > 0);

        try (Connection c = db.getConnection();
             var st = c.prepareStatement("SELECT total_items, total_cost, language FROM cart_records WHERE id = ?")) {
            st.setInt(1, id);
            var rs = st.executeQuery();
            assertTrue(rs.next());
            assertEquals(2, rs.getInt(1));
            assertEquals(25.0, rs.getDouble(2), 0.001);
            assertEquals("en_US", rs.getString(3));
        }
    }

    @Test
    void printAllCartRecordsAndPrintCartItemsWriteOutput() {
        CartService service = new CartService(db, UserOutput.utf8Stdout());
        service.saveCart(List.of(new CartItem(1, 3.0, 4)), 12.0, "fi_FI");

        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(capture, true, StandardCharsets.UTF_8);
        CartService reporting = new CartService(db, ps);

        reporting.printAllCartRecords();
        reporting.printCartItems(1);

        String text = capture.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Cart Records"));
        assertTrue(text.contains("Cart Items"));
        assertTrue(text.contains("12.00") || text.contains("12,00") || text.contains("12"));
    }

    @Test
    void saveCartReturnsMinusOneWhenTablesMissing() {
        String url = "jdbc:h2:mem:notables;MODE=MySQL;DB_CLOSE_DELAY=-1";
        DatabaseConnection bare = new DatabaseConnection(url, "sa", "");
        CartService service = new CartService(bare, UserOutput.utf8Stdout());
        int id = service.saveCart(List.of(new CartItem(1, 1.0, 1)), 1.0, "en_US");
        assertEquals(-1, id);
    }

    @Test
    void printMethodsHandleMissingTablesGracefully() {
        String url = "jdbc:h2:mem:notables2;MODE=MySQL;DB_CLOSE_DELAY=-1";
        DatabaseConnection bare = new DatabaseConnection(url, "sa", "");
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(capture, true, StandardCharsets.UTF_8);
        CartService service = new CartService(bare, ps);
        service.printAllCartRecords();
        service.printCartItems(1);
        assertTrue(capture.toString(StandardCharsets.UTF_8).isEmpty()
                || !capture.toString(StandardCharsets.UTF_8).contains("==="));
    }
}
