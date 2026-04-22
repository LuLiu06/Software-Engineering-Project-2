package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Targets JaCoCo branch gaps in {@link CartService#saveCart} and {@code saveCartRecord}
 * (paths that plain H2 integration tests rarely hit).
 */
class CartServiceBranchCoverageTest {

    @AfterEach
    void tearDown() {
        DatabaseConnection.clearInstanceForTests();
    }

    @Test
    void saveCartWhenCartRecordInsertAffectsZeroRowsRollsBackAndReturnsMinusOne() throws Exception {
        String url = memUrl("zeroaff");
        initSchema(url);
        DatabaseConnection db = databaseWithCartRecordStub(url, CartInsertBehavior.ZERO_AFFECTED_ROWS);
        CartService service = new CartService(db, UserOutput.utf8Stdout());
        int id = service.saveCart(List.of(new CartItem(1, 1.0, 1)), 1.0, "en_US");
        assertEquals(-1, id);
        try (Connection check = DriverManager.getConnection(url, "sa", "")) {
            assertNoCartRows(check);
        }
    }

    @Test
    void saveCartWhenNoGeneratedKeyRollsBackAndReturnsMinusOne() throws Exception {
        String url = memUrl("nokeys");
        initSchema(url);
        DatabaseConnection db = databaseWithCartRecordStub(url, CartInsertBehavior.ONE_ROW_BUT_EMPTY_KEYS);
        CartService service = new CartService(db, UserOutput.utf8Stdout());
        int id = service.saveCart(List.of(new CartItem(1, 2.0, 1)), 2.0, "sv_SE");
        assertEquals(-1, id);
        try (Connection check = DriverManager.getConnection(url, "sa", "")) {
            assertNoCartRows(check);
        }
    }

    @Test
    void saveCartWhenGetConnectionFailsReturnsMinusOneWithoutRollbackBranch() {
        DatabaseConnection failing = new DatabaseConnection("jdbc:h2:mem:unused", "sa", "") {
            @Override
            public Connection getConnection() throws SQLException {
                throw new SQLException("connection refused for test");
            }
        };
        CartService service = new CartService(failing, UserOutput.utf8Stdout());
        int id = service.saveCart(List.of(new CartItem(1, 1.0, 1)), 1.0, "en_US");
        assertEquals(-1, id);
    }

    @Test
    void defaultConstructorDelegatesToSingletonBoundViaReflection() throws Exception {
        String url = memUrl("defctor");
        initSchema(url);
        try {
            bindSingletonInstance(new DatabaseConnection(url, "sa", ""));
            CartService service = new CartService();
            int id = service.saveCart(List.of(new CartItem(1, 4.0, 2)), 8.0, "ja_JP");
            assertTrue(id > 0);
        } finally {
            DatabaseConnection.clearInstanceForTests();
        }
    }

    private static void bindSingletonInstance(DatabaseConnection db) throws Exception {
        Field f = DatabaseConnection.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, db);
    }

    private static String memUrl(String label) {
        return "jdbc:h2:mem:" + label + "_" + System.nanoTime() + ";MODE=MySQL;DB_CLOSE_DELAY=-1";
    }

    private static void initSchema(String url) throws SQLException {
        try (Connection c = DriverManager.getConnection(url, "sa", "")) {
            H2TestSchema.createTables(c);
        }
    }

    private static void assertNoCartRows(Connection c) throws SQLException {
        try (var st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) AS n FROM cart_records")) {
            assertTrue(rs.next());
            assertEquals(0, rs.getInt("n"));
        }
    }

    private enum CartInsertBehavior {
        ZERO_AFFECTED_ROWS,
        ONE_ROW_BUT_EMPTY_KEYS
    }

    private static DatabaseConnection databaseWithCartRecordStub(String url, CartInsertBehavior behavior) {
        return new DatabaseConnection(url, "sa", "") {
            @Override
            public Connection getConnection() throws SQLException {
                Connection raw = DriverManager.getConnection(url, "sa", "");
                return proxyConnection(raw, behavior);
            }
        };
    }

    private static Connection proxyConnection(Connection real, CartInsertBehavior behavior) {
        return (Connection) java.lang.reflect.Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[] {Connection.class},
                (proxy, method, args) -> {
                    if ("prepareStatement".equals(method.getName())
                            && args != null
                            && args.length >= 2
                            && args[0] instanceof String sql
                            && sql.contains("INSERT INTO cart_records")
                            && args[1] instanceof Number) {
                        PreparedStatement realPs = (PreparedStatement) method.invoke(real, args);
                        return wrapCartRecordInsertStatement(realPs, behavior);
                    }
                    return method.invoke(real, args);
                });
    }

    private static PreparedStatement wrapCartRecordInsertStatement(
            PreparedStatement realPs,
            CartInsertBehavior behavior) {
        return (PreparedStatement) java.lang.reflect.Proxy.newProxyInstance(
                PreparedStatement.class.getClassLoader(),
                new Class<?>[] {PreparedStatement.class},
                (p, method, args) -> {
                    String name = method.getName();
                    if ("executeUpdate".equals(name)) {
                        if (behavior == CartInsertBehavior.ZERO_AFFECTED_ROWS) {
                            return 0;
                        }
                        return 1;
                    }
                    if ("getGeneratedKeys".equals(name)) {
                        if (behavior == CartInsertBehavior.ONE_ROW_BUT_EMPTY_KEYS) {
                            return emptyResultSet();
                        }
                        return method.invoke(realPs, args);
                    }
                    return method.invoke(realPs, args);
                });
    }

    private static ResultSet emptyResultSet() {
        return (ResultSet) java.lang.reflect.Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class<?>[] {ResultSet.class},
                (p, method, args) -> switch (method.getName()) {
                    case "next" -> false;
                    case "wasNull" -> false;
                    case "close" -> null;
                    case "getStatement" -> null;
                    default -> throw new UnsupportedOperationException("Unexpected ResultSet." + method.getName());
                });
    }
}
