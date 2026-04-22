package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartItemTest {

    @Test
    void constructorComputesSubtotal() {
        CartItem item = new CartItem(1, 10.5, 3);
        assertEquals(31.5, item.getSubtotal(), 0.001);
        assertEquals(1, item.getItemNumber());
        assertEquals(10.5, item.getPrice(), 0.001);
        assertEquals(3, item.getQuantity());
    }

    @Test
    void setPriceUpdatesSubtotal() {
        CartItem item = new CartItem(2, 5.0, 4);
        item.setPrice(6.0);
        assertEquals(24.0, item.getSubtotal(), 0.001);
    }

    @Test
    void setQuantityUpdatesSubtotal() {
        CartItem item = new CartItem(3, 4.0, 2);
        item.setQuantity(5);
        assertEquals(20.0, item.getSubtotal(), 0.001);
    }

    @Test
    void toStringContainsFields() {
        CartItem item = new CartItem(1, 2.0, 3);
        String s = item.toString();
        assertTrue(s.contains("Item 1"));
        assertTrue(s.contains("2.00"));
        assertTrue(s.contains("3"));
    }
}
