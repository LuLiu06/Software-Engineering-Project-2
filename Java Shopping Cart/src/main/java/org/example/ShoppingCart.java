package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Shopping Cart class that handles item management and cost calculations.
 */
public class ShoppingCart {
    
    private final List<CartItem> items;
    
    public ShoppingCart() {
        this.items = new ArrayList<>();
    }
    
    /**
     * Adds an item to the shopping cart.
     * 
     * @param price the price per unit of the item
     * @param quantity the quantity of the item
     * @throws IllegalArgumentException if price or quantity is negative
     */
    public void addItem(double price, int quantity) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        items.add(new CartItem(price, quantity));
    }
    
    /**
     * Calculates the total cost for a single item.
     * 
     * @param price the price per unit
     * @param quantity the quantity
     * @return the total cost for the item (price × quantity)
     */
    public static double calculateItemCost(double price, int quantity) {
        if (price < 0 || quantity < 0) {
            throw new IllegalArgumentException("Price and quantity cannot be negative");
        }
        return price * quantity;
    }
    
    /**
     * Calculates the total cost of all items in the cart.
     * 
     * @return the total cost of the shopping cart
     */
    public double calculateTotalCost() {
        double total = 0;
        for (CartItem item : items) {
            total += calculateItemCost(item.getPrice(), item.getQuantity());
        }
        return total;
    }
    
    /**
     * Gets all items in the cart.
     * 
     * @return list of cart items
     */
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }
    
    /**
     * Gets the number of items in the cart.
     * 
     * @return number of items
     */
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * Clears all items from the cart.
     */
    public void clear() {
        items.clear();
    }
    
    /**
     * Inner class representing a cart item.
     */
    public static class CartItem {
        private final double price;
        private final int quantity;
        
        public CartItem(double price, int quantity) {
            this.price = price;
            this.quantity = quantity;
        }
        
        public double getPrice() {
            return price;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public double getTotalCost() {
            return price * quantity;
        }
    }
}
