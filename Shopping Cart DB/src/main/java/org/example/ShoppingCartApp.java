package org.example;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main application class for the Shopping Cart with Database Localization.
 */
public class ShoppingCartApp {

    private final Scanner scanner;
    private final PrintStream out;
    private final LocalizationService localizationService;
    private final CartService cartService;
    private final DatabaseConnection connectionProbe;
    private Map<String, String> messages;
    private String currentLanguage;

    public ShoppingCartApp() {
        this(System.in, UserOutput.utf8Stdout());
    }

    /**
     * Package-private constructor for tests (injected streams, services, and DB probe).
     */
    ShoppingCartApp(InputStream in, PrintStream out, LocalizationService localizationService,
                    CartService cartService, DatabaseConnection connectionProbe) {
        this.scanner = new Scanner(in, StandardCharsets.UTF_8);
        this.out = out;
        this.localizationService = localizationService;
        this.cartService = cartService;
        this.connectionProbe = connectionProbe;
        this.currentLanguage = "en_US";
        this.messages = localizationService.loadStrings(currentLanguage);
    }

    private ShoppingCartApp(InputStream in, PrintStream out) {
        this(in, out, DatabaseConnection.getInstance());
    }

    private ShoppingCartApp(InputStream in, PrintStream out, DatabaseConnection db) {
        this(in, out, new LocalizationService(db), new CartService(db, out), db);
    }

    /**
     * Maps menu choice (1–4) to locale code used in the database.
     */
    static String languageForChoice(int choice) {
        return switch (choice) {
            case 1 -> "en_US";
            case 2 -> "fi_FI";
            case 3 -> "sv_SE";
            case 4 -> "ja_JP";
            default -> "en_US";
        };
    }

    /**
     * Main entry point of the application.
     */
    public static void main(String[] args) {
        ShoppingCartApp app = new ShoppingCartApp();
        app.run();
    }

    /**
     * Runs the shopping cart application.
     */
    public void run() {
        out.println("Testing database connection...");
        if (!connectionProbe.testConnection()) {
            out.println("Warning: Could not connect to database. Using default messages.");
            out.println("Please ensure MySQL is running and the database is set up.");
            out.println("Set DB_PASSWORD (and DB_URL / DB_USER if needed) for your environment.");
            out.println();
        }

        selectLanguage();

        List<CartItem> cartItems = new ArrayList<>();

        out.println("\n" + messages.get("welcome"));
        out.println("=".repeat(50));

        out.print(messages.get("enter.item.count"));
        int itemCount = readPositiveInt();

        double totalCost = 0;

        for (int i = 1; i <= itemCount; i++) {
            out.println("\n" + messages.get("item") + " " + i + ":");
            out.println("-".repeat(25));

            out.print(messages.get("enter.price"));
            double price = readPositiveDouble();

            out.print(messages.get("enter.quantity"));
            int quantity = readPositiveInt();

            CartItem item = new CartItem(i, price, quantity);
            cartItems.add(item);

            out.println(messages.get("item.cost") + " " + String.format("%.2f", item.getSubtotal()));
            totalCost += item.getSubtotal();
        }

        out.println("\n" + "=".repeat(50));
        out.println(messages.get("total.cost") + " " + String.format("%.2f", totalCost));
        out.println("=".repeat(50));

        int cartId = cartService.saveCart(cartItems, totalCost, currentLanguage);
        if (cartId > 0) {
            out.println("\n" + messages.get("cart.saved") + " (ID: " + cartId + ")");

            cartService.printAllCartRecords();
            cartService.printCartItems(cartId);
        } else {
            out.println("\n" + messages.get("cart.save.error"));
        }

        out.println("\n" + messages.get("thank.you"));
    }

    /**
     * Prompts the user to select a language.
     */
    private void selectLanguage() {
        out.println("Select your language / Valitse kieli / Välj språk / 言語を選択:");
        out.println("1. English");
        out.println("2. Suomi (Finnish)");
        out.println("3. Svenska (Swedish)");
        out.println("4. 日本語 (Japanese)");
        out.print("Enter choice (1-4): ");

        int choice = readIntInRange(1, 4);
        currentLanguage = languageForChoice(choice);

        messages = localizationService.loadStrings(currentLanguage);
        out.println("Language set to: " + currentLanguage);
    }

    /**
     * Reads a positive integer from user input.
     */
    private int readPositiveInt() {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= 0) {
                    return value;
                }
                out.print(messages.get("error.positive.number"));
            } catch (NumberFormatException e) {
                out.print(messages.get("error.invalid.number"));
            }
        }
    }

    /**
     * Reads an integer within a specific range.
     */
    private int readIntInRange(int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                out.print("Please enter a number between " + min + " and " + max + ": ");
            } catch (NumberFormatException e) {
                out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    /**
     * Reads a positive double from user input.
     */
    private double readPositiveDouble() {
        while (true) {
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= 0) {
                    return value;
                }
                out.print(messages.get("error.positive.number"));
            } catch (NumberFormatException e) {
                out.print(messages.get("error.invalid.number"));
            }
        }
    }
}
