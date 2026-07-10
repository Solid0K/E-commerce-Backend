package com.krishu.ecommerce.Controller;

import com.krishu.ecommerce.DTO.CartItemRequest;
import com.krishu.ecommerce.DTO.CartResponse;
import com.krishu.ecommerce.Service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService){
        this.cartService=cartService;
    }

    @GetMapping()
    public ResponseEntity<CartResponse> getCart(Authentication authentication){
        return ResponseEntity.ok(cartService.getCart(authentication));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addInCart(Authentication authentication, @Valid @RequestBody CartItemRequest request){
        return ResponseEntity.ok(cartService.addToCart(authentication,request));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateCart(Authentication authentication,@PathVariable String productId,@Valid @RequestBody CartItemRequest request){
        return ResponseEntity.ok(cartService.updateAItem(authentication,productId,request));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> deleteOneProduct(Authentication authentication,@PathVariable String productId){
        return ResponseEntity.ok(cartService.removeFromCart(authentication,productId));
    }

    @DeleteMapping()
    public void deleteCart(Authentication authentication){
        cartService.deleteCart(authentication);
    }
}
