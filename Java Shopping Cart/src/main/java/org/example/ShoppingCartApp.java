package org.example;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Main application class for the Shopping Cart with localization support.
 */
public class ShoppingCartApp {
    
    private final Scanner scanner;
    private final PrintStream out;
    private ResourceBundle messages;
    private Locale currentLocale;
    
    public ShoppingCartApp() {
        this.scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        this.out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        this.currentLocale = Locale.US;
        this.messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);
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
        selectLanguage();
        
        ShoppingCart cart = new ShoppingCart();
        
        out.println("\n" + messages.getString("welcome"));
        out.println("=".repeat(40));
        
        out.print(messages.getString("enter.item.count"));
        int itemCount = readPositiveInt();
        
        for (int i = 1; i <= itemCount; i++) {
            out.println("\n" + messages.getString("item") + " " + i + ":");
            out.println("-".repeat(20));
            
            out.print(messages.getString("enter.price"));
            double price = readPositiveDouble();
            
            out.print(messages.getString("enter.quantity"));
            int quantity = readPositiveInt();
            
            cart.addItem(price, quantity);
            
            double itemCost = ShoppingCart.calculateItemCost(price, quantity);
            out.println(messages.getString("item.cost") + " " + formatCurrency(itemCost));
        }
        
        out.println("\n" + "=".repeat(40));
        out.println(messages.getString("total.cost") + " " + formatCurrency(cart.calculateTotalCost()));
        out.println("=".repeat(40));
        
        out.println("\n" + messages.getString("thank.you"));
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
                currentLocale = Locale.US;
                break;
            case 2:
                currentLocale = new Locale.Builder().setLanguage("fi").setRegion("FI").build();
                break;
            case 3:
                currentLocale = new Locale.Builder().setLanguage("sv").setRegion("SE").build();
                break;
            case 4:
                currentLocale = Locale.JAPAN;
                break;
            default:
                currentLocale = Locale.US;
        }
        
        messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);
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
                out.print(messages.getString("error.positive.number"));
            } catch (NumberFormatException e) {
                out.print(messages.getString("error.invalid.number"));
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
                out.print(messages.getString("error.positive.number"));
            } catch (NumberFormatException e) {
                out.print(messages.getString("error.invalid.number"));
            }
        }
    }
    
    /**
     * Formats a currency value.
     */
    private String formatCurrency(double value) {
        return String.format(currentLocale, "%.2f", value);
    }
}
