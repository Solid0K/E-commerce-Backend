package com.krishu.ecommerce;

import com.krishu.ecommerce.CustomExceptions.BadCredentials;
import com.krishu.ecommerce.CustomExceptions.EmailAlreadyExist;
import com.krishu.ecommerce.DTO.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExist.class)
    public ResponseEntity<ErrorResponse> emailExist(EmailAlreadyExist exp){
        ErrorResponse errorResponse=new ErrorResponse(409,exp.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(BadCredentials.class)
    public ResponseEntity<ErrorResponse> userNotFound(BadCredentials exp){
        ErrorResponse errorResponse=new ErrorResponse(401,exp.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
