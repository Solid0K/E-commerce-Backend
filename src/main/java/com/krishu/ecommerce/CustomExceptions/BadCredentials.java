package com.krishu.ecommerce.CustomExceptions;

public class BadCredentials extends RuntimeException {
    public BadCredentials(String message) {
        super(message);
    }
}
