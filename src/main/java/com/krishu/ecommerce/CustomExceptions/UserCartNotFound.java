package com.krishu.ecommerce.CustomExceptions;

public class UserCartNotFound extends RuntimeException {
    public UserCartNotFound(String message) {
        super(message);
    }
}
