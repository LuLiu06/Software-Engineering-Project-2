package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for fetching localized UI strings from the database.
 */
public class LocalizationService {
    
    private final DatabaseConnection dbConnection;
    private Map<String, String> localizedStrings;
    private String currentLanguage;
    
    public LocalizationService() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.localizedStrings = new HashMap<>();
        this.currentLanguage = "en_US";
    }
    
    public LocalizationService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.localizedStrings = new HashMap<>();
        this.currentLanguage = "en_US";
    }
    
    /**
     * Loads localized strings from the database for the specified language.
     * 
     * @param language the language code (e.g., "en_US", "fi_FI", "sv_SE", "ja_JP")
     * @return Map of key-value pairs for the localized strings
     */
    public Map<String, String> loadStrings(String language) {
        this.currentLanguage = language;
        this.localizedStrings = new HashMap<>();
        
        String sql = "SELECT `key`, value FROM localization_strings WHERE language = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, language);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getString("key");
                    String value = rs.getString("value");
                    localizedStrings.put(key, value);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading localization strings: " + e.getMessage());
            loadDefaultStrings();
        }
        
        if (localizedStrings.isEmpty()) {
            loadDefaultStrings();
        }
        
        return localizedStrings;
    }
    
    /**
     * Gets a localized string by key.
     * 
     * @param key the message key
     * @return the localized string, or the key if not found
     */
    public String getString(String key) {
        return localizedStrings.getOrDefault(key, key);
    }
    
    /**
     * Gets the current language.
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }
    
    /**
     * Gets all localized strings.
     */
    public Map<String, String> getLocalizedStrings() {
        return new HashMap<>(localizedStrings);
    }
    
    /**
     * Loads default English strings as fallback.
     */
    private void loadDefaultStrings() {
        localizedStrings.put("welcome", "Welcome to the Shopping Cart Application!");
        localizedStrings.put("enter.item.count", "Enter the number of items to purchase: ");
        localizedStrings.put("enter.price", "Enter the price for item: ");
        localizedStrings.put("enter.quantity", "Enter the quantity for item: ");
        localizedStrings.put("item", "Item");
        localizedStrings.put("item.cost", "Item cost:");
        localizedStrings.put("total.cost", "Total cost:");
        localizedStrings.put("thank.you", "Thank you for shopping with us!");
        localizedStrings.put("error.positive.number", "Please enter a positive number: ");
        localizedStrings.put("error.invalid.number", "Invalid input. Please enter a valid number: ");
        localizedStrings.put("cart.saved", "Shopping cart saved to database!");
        localizedStrings.put("cart.save.error", "Error saving cart to database.");
    }
    
    /**
     * Gets available languages from the database.
     * 
     * @return array of available language codes
     */
    public String[] getAvailableLanguages() {
        String sql = "SELECT DISTINCT language FROM localization_strings ORDER BY language";
        java.util.List<String> languages = new java.util.ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                languages.add(rs.getString("language"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching available languages: " + e.getMessage());
        }
        
        if (languages.isEmpty()) {
            return new String[]{"en_US", "fi_FI", "sv_SE", "ja_JP"};
        }
        
        return languages.toArray(new String[0]);
    }
}
