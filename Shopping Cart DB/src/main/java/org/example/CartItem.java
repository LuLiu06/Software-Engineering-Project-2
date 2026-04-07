package org.example;

/**
 * Represents an item in the shopping cart.
 */
public class CartItem {
    
    private int itemNumber;
    private double price;
    private int quantity;
    private double subtotal;
    
    public CartItem(int itemNumber, double price, int quantity) {
        this.itemNumber = itemNumber;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = price * quantity;
    }
    
    public int getItemNumber() {
        return itemNumber;
    }
    
    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
        this.subtotal = this.price * this.quantity;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = this.price * this.quantity;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    @Override
    public String toString() {
        return String.format("Item %d: Price=%.2f, Quantity=%d, Subtotal=%.2f",
                itemNumber, price, quantity, subtotal);
    }
}
