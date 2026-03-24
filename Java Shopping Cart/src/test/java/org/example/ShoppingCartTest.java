package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ShoppingCart class.
 */
@DisplayName("Shopping Cart Tests")
class ShoppingCartTest {
    
    private ShoppingCart cart;
    
    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }
    
    @Nested
    @DisplayName("Item Cost Calculation Tests")
    class ItemCostCalculationTests {
        
        @Test
        @DisplayName("Calculate item cost with valid price and quantity")
        void testCalculateItemCost() {
            double result = ShoppingCart.calculateItemCost(10.0, 5);
            assertEquals(50.0, result, 0.001);
        }
        
        @Test
        @DisplayName("Calculate item cost with zero price")
        void testCalculateItemCostZeroPrice() {
            double result = ShoppingCart.calculateItemCost(0.0, 5);
            assertEquals(0.0, result, 0.001);
        }
        
        @Test
        @DisplayName("Calculate item cost with zero quantity")
        void testCalculateItemCostZeroQuantity() {
            double result = ShoppingCart.calculateItemCost(10.0, 0);
            assertEquals(0.0, result, 0.001);
        }
        
        @Test
        @DisplayName("Calculate item cost with decimal price")
        void testCalculateItemCostDecimalPrice() {
            double result = ShoppingCart.calculateItemCost(9.99, 3);
            assertEquals(29.97, result, 0.001);
        }
        
        @Test
        @DisplayName("Calculate item cost throws exception for negative price")
        void testCalculateItemCostNegativePrice() {
            assertThrows(IllegalArgumentException.class, 
                () -> ShoppingCart.calculateItemCost(-10.0, 5));
        }
        
        @Test
        @DisplayName("Calculate item cost throws exception for negative quantity")
        void testCalculateItemCostNegativeQuantity() {
            assertThrows(IllegalArgumentException.class, 
                () -> ShoppingCart.calculateItemCost(10.0, -5));
        }
    }
    
    @Nested
    @DisplayName("Add Item Tests")
    class AddItemTests {
        
        @Test
        @DisplayName("Add single item to cart")
        void testAddSingleItem() {
            cart.addItem(10.0, 2);
            assertEquals(1, cart.getItemCount());
        }
        
        @Test
        @DisplayName("Add multiple items to cart")
        void testAddMultipleItems() {
            cart.addItem(10.0, 2);
            cart.addItem(20.0, 3);
            cart.addItem(5.0, 1);
            assertEquals(3, cart.getItemCount());
        }
        
        @Test
        @DisplayName("Add item with negative price throws exception")
        void testAddItemNegativePrice() {
            assertThrows(IllegalArgumentException.class, 
                () -> cart.addItem(-10.0, 2));
        }
        
        @Test
        @DisplayName("Add item with negative quantity throws exception")
        void testAddItemNegativeQuantity() {
            assertThrows(IllegalArgumentException.class, 
                () -> cart.addItem(10.0, -2));
        }
    }
    
    @Nested
    @DisplayName("Total Cost Calculation Tests")
    class TotalCostCalculationTests {
        
        @Test
        @DisplayName("Calculate total cost for single item")
        void testCalculateTotalCostSingleItem() {
            cart.addItem(10.0, 2);
            assertEquals(20.0, cart.calculateTotalCost(), 0.001);
        }
        
        @Test
        @DisplayName("Calculate total cost for multiple items")
        void testCalculateTotalCostMultipleItems() {
            cart.addItem(10.0, 2);  // 20.0
            cart.addItem(15.0, 3);  // 45.0
            cart.addItem(5.50, 4);  // 22.0
            assertEquals(87.0, cart.calculateTotalCost(), 0.001);
        }
        
        @Test
        @DisplayName("Calculate total cost for empty cart")
        void testCalculateTotalCostEmptyCart() {
            assertEquals(0.0, cart.calculateTotalCost(), 0.001);
        }
        
        @Test
        @DisplayName("Calculate total cost with decimal prices")
        void testCalculateTotalCostDecimalPrices() {
            cart.addItem(9.99, 1);
            cart.addItem(4.99, 2);
            assertEquals(19.97, cart.calculateTotalCost(), 0.001);
        }
    }
    
    @Nested
    @DisplayName("Cart Operations Tests")
    class CartOperationsTests {
        
        @Test
        @DisplayName("Clear cart removes all items")
        void testClearCart() {
            cart.addItem(10.0, 2);
            cart.addItem(20.0, 3);
            cart.clear();
            assertEquals(0, cart.getItemCount());
            assertEquals(0.0, cart.calculateTotalCost(), 0.001);
        }
        
        @Test
        @DisplayName("Get items returns copy of items list")
        void testGetItemsReturnsCopy() {
            cart.addItem(10.0, 2);
            var items = cart.getItems();
            items.clear();
            assertEquals(1, cart.getItemCount());
        }
        
        @Test
        @DisplayName("Empty cart has zero item count")
        void testEmptyCartItemCount() {
            assertEquals(0, cart.getItemCount());
        }
    }
    
    @Nested
    @DisplayName("CartItem Tests")
    class CartItemTests {
        
        @Test
        @DisplayName("CartItem stores price correctly")
        void testCartItemPrice() {
            ShoppingCart.CartItem item = new ShoppingCart.CartItem(25.99, 3);
            assertEquals(25.99, item.getPrice(), 0.001);
        }
        
        @Test
        @DisplayName("CartItem stores quantity correctly")
        void testCartItemQuantity() {
            ShoppingCart.CartItem item = new ShoppingCart.CartItem(25.99, 3);
            assertEquals(3, item.getQuantity());
        }
        
        @Test
        @DisplayName("CartItem calculates total cost correctly")
        void testCartItemTotalCost() {
            ShoppingCart.CartItem item = new ShoppingCart.CartItem(25.99, 3);
            assertEquals(77.97, item.getTotalCost(), 0.001);
        }
    }
}
