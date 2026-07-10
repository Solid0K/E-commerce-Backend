package com.krishu.ecommerce.CustomExceptions;

public class CartEmptyException extends RuntimeException {
    public CartEmptyException(String message) {
        super(message);
    }
}
