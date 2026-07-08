package com.krishu.ecommerce.CustomExceptions;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(String productId) {
        super("Product " + productId + " is not in your cart");
    }
}
