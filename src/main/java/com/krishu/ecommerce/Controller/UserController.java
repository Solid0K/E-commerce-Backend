package com.krishu.ecommerce.Controller;

import com.krishu.ecommerce.DTO.ProductResponse;
import com.krishu.ecommerce.Service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponse>> getProducts(Pageable pageable,@RequestParam(required = false) String search){
        return ResponseEntity.ok(userService.getProducts(search,pageable));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getaProduct(@PathVariable String id){
        return ResponseEntity.ok(userService.getProduct(id));
    }
}
