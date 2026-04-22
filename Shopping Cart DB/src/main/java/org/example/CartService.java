package org.example;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for saving shopping cart records to the database.
 */
public class CartService {

    private static final Logger LOG = Logger.getLogger(CartService.class.getName());

    private final DatabaseConnection dbConnection;
    private final PrintStream userOut;

    public CartService() {
        this(DatabaseConnection.getInstance());
    }

    public CartService(DatabaseConnection dbConnection) {
        this(dbConnection, UserOutput.utf8Stdout());
    }

    public CartService(DatabaseConnection dbConnection, PrintStream userOut) {
        this.dbConnection = dbConnection;
        this.userOut = userOut;
    }

    /**
     * Saves a shopping cart record and its items to the database.
     *
     * @param items list of cart items
     * @param totalCost total cost of the cart
     * @param language current language code
     * @return the generated cart record ID, or -1 if failed
     */
    public int saveCart(List<CartItem> items, double totalCost, String language) {
        Connection conn = null;
        int cartRecordId = -1;

        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            cartRecordId = saveCartRecord(conn, items.size(), totalCost, language);

            if (cartRecordId > 0) {
                saveCartItems(conn, cartRecordId, items);
                conn.commit();
            } else {
                conn.rollback();
            }

        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Error saving cart", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOG.log(Level.WARNING, "Error rolling back transaction", ex);
                }
            }
            cartRecordId = -1;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOG.log(Level.WARNING, "Error closing connection", e);
                }
            }
        }

        return cartRecordId;
    }

    /**
     * Saves the main cart record.
     */
    private int saveCartRecord(Connection conn, int totalItems, double totalCost, String language)
            throws SQLException {

        String sql = "INSERT INTO cart_records (total_items, total_cost, language) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, totalItems);
            stmt.setDouble(2, totalCost);
            stmt.setString(3, language);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }

        return -1;
    }

    /**
     * Saves individual cart items linked to the cart record.
     */
    private void saveCartItems(Connection conn, int cartRecordId, List<CartItem> items)
            throws SQLException {

        String sql = "INSERT INTO cart_items (cart_record_id, item_number, price, quantity, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cartRecordId);
            for (CartItem item : items) {
                stmt.setInt(2, item.getItemNumber());
                stmt.setDouble(3, item.getPrice());
                stmt.setInt(4, item.getQuantity());
                stmt.setDouble(5, item.getSubtotal());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    /**
     * Gets all cart records from the database.
     */
    public void printAllCartRecords() {
        String sql = "SELECT id, total_items, total_cost, language, created_at FROM cart_records ORDER BY created_at DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            userOut.println("\n=== Cart Records ===");
            userOut.printf("%-5s %-12s %-12s %-10s %-20s%n",
                    "ID", "Total Items", "Total Cost", "Language", "Created At");
            userOut.println("-".repeat(60));

            while (rs.next()) {
                userOut.printf("%-5d %-12d %-12.2f %-10s %-20s%n",
                        rs.getInt("id"),
                        rs.getInt("total_items"),
                        rs.getDouble("total_cost"),
                        rs.getString("language"),
                        rs.getTimestamp("created_at"));
            }

        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Error fetching cart records", e);
        }
    }

    /**
     * Gets cart items for a specific cart record.
     */
    public void printCartItems(int cartRecordId) {
        String sql = "SELECT id, item_number, price, quantity, subtotal FROM cart_items WHERE cart_record_id = ? "
                + "ORDER BY item_number";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartRecordId);

            try (ResultSet rs = stmt.executeQuery()) {
                userOut.println("\n=== Cart Items for Record #" + cartRecordId + " ===");
                userOut.printf("%-5s %-12s %-10s %-10s %-10s%n",
                        "ID", "Item Number", "Price", "Quantity", "Subtotal");
                userOut.println("-".repeat(50));

                while (rs.next()) {
                    userOut.printf("%-5d %-12d %-10.2f %-10d %-10.2f%n",
                            rs.getInt("id"),
                            rs.getInt("item_number"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getDouble("subtotal"));
                }
            }

        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Error fetching cart items", e);
        }
    }
}
