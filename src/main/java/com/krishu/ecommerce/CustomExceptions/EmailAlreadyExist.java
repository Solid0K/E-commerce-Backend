package com.krishu.ecommerce.CustomExceptions;

public class EmailAlreadyExist extends RuntimeException{
    public EmailAlreadyExist(String email){
        super("Email already exist "+email);
    }
}
