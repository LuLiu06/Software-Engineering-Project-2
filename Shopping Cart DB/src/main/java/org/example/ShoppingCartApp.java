package org.example;

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
    private Map<String, String> messages;
    private String currentLanguage;
    
    public ShoppingCartApp() {
        this.scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        this.out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        this.localizationService = new LocalizationService();
        this.cartService = new CartService();
        this.currentLanguage = "en_US";
        this.messages = localizationService.loadStrings(currentLanguage);
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
        if (!DatabaseConnection.getInstance().testConnection()) {
            out.println("Warning: Could not connect to database. Using default messages.");
            out.println("Please ensure MySQL is running and the database is set up.");
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
        
        switch (choice) {
            case 1:
                currentLanguage = "en_US";
                break;
            case 2:
                currentLanguage = "fi_FI";
                break;
            case 3:
                currentLanguage = "sv_SE";
                break;
            case 4:
                currentLanguage = "ja_JP";
                break;
            default:
                currentLanguage = "en_US";
        }
        
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
