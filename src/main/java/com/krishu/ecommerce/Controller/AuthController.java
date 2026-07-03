package com.krishu.ecommerce.Controller;

import com.krishu.ecommerce.DTO.LoginRequest;
import com.krishu.ecommerce.DTO.RegisterRequest;
import com.krishu.ecommerce.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService){
        this.authService=authService;
    }

    @PostMapping("/signup")
    public void registerUser(@RequestBody RegisterRequest request){
        authService.registerUser(request);
    }

    @GetMapping("/signin")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.verifyUser(loginRequest));
    }
}
