package com.krishu.ecommerce.Service;

import com.krishu.ecommerce.CustomExceptions.InsufficientStock;
import com.krishu.ecommerce.CustomExceptions.ProductNotFound;
import com.krishu.ecommerce.CustomExceptions.UserNotFound;
import com.krishu.ecommerce.DTO.CartItemRequest;
import com.krishu.ecommerce.DTO.CartItemResponse;
import com.krishu.ecommerce.DTO.CartResponse;
import com.krishu.ecommerce.Model.Cart;
import com.krishu.ecommerce.Model.CartItem;
import com.krishu.ecommerce.Model.Product;
import com.krishu.ecommerce.Model.User;
import com.krishu.ecommerce.Repository.CartRepo;
import com.krishu.ecommerce.Repository.ProductRepo;
import com.krishu.ecommerce.Repository.UserRepo;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepo cartRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;

    public CartService(CartRepo cartRepo, UserRepo userRepo, ProductRepo productRepo){
        this.cartRepo=cartRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    private Cart getOrCreateCart(String userId){
        return cartRepo.findByUserId(userId).orElseGet(()->{
            Cart cart=new Cart();
            cart.setUserId(userId);
            cart.setItems(new ArrayList<>());
            cart.setUpdateAt(LocalDateTime.now());
            return cartRepo.save(cart);
        });
    }

    private CartResponse mapToCartResponse(Cart cart){
        List<CartItemResponse> items=cart.getItems().stream().map(this::mapToCartItemResponse).toList();
        BigDecimal totalAmount=items.stream().map(CartItemResponse::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponse cartResponse=new CartResponse();
        cartResponse.setItems(items);
        cartResponse.setTotalAmount(totalAmount);
        return cartResponse;
    }

    private CartItemResponse mapToCartItemResponse(CartItem cartItem){
        Product product=productRepo.findById(cartItem.getProductId())
                .orElseThrow(()->new ProductNotFound("Product not found"));
        CartItemResponse cartItemResponse=new CartItemResponse();
        cartItemResponse.setProductId(product.getId());
        cartItemResponse.setName(product.getName());
        cartItemResponse.setQuantity(cartItem.getQuantity());
        cartItemResponse.setUnitPrice(product.getPrice());
        cartItemResponse.setSubtotal(cartItemResponse.getUnitPrice().multiply(BigDecimal.valueOf(cartItemResponse.getQuantity())));
        return cartItemResponse;
    }

    public CartResponse getCart(Authentication authentication) {
        String email=authentication.getName();
        User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFound("User not found"));
        Cart userCart=getOrCreateCart(user.getId());
        return mapToCartResponse(userCart);
    }

    public CartResponse addToCart(Authentication authentication, @Valid CartItemRequest request) {
        String email=authentication.getName();
        User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFound("User not found"));
        Cart userCart=getOrCreateCart(user.getId());
        Product product = productRepo.findById(request.getProductId())
                .filter(Product::isActive)
                .orElseThrow(() -> new ProductNotFound("Product not found"));
        CartItem foundItem=userCart.getItems().stream().filter(item->item.getProductId().equals(request.getProductId())).findFirst().orElse(null);
        int currentQuantityInCart = (foundItem != null) ? foundItem.getQuantity() : 0;
        int newTotalQuantity = currentQuantityInCart + request.getQuantity();
        if (newTotalQuantity > product.getStockQuantity()) {
            throw new InsufficientStock("Dont have Sufficient Stocks");
        }
        if(foundItem==null){
            CartItem newItem=new CartItem();
            newItem.setProductId(request.getProductId());
            newItem.setQuantity(newTotalQuantity);
            userCart.getItems().add(newItem);
        }else{
            foundItem.setQuantity(newTotalQuantity);
        }
        userCart.setUpdateAt(LocalDateTime.now());
        cartRepo.save(userCart);
        return mapToCartResponse(userCart);
    }

    public CartResponse updateAItem(Authentication authentication, String productId, @Valid CartItemRequest request) {
        String email=authentication.getName();
        User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFound("User not found"));
        Cart userCart=getOrCreateCart(user.getId());
        Product product = productRepo.findById(productId)
                .filter(Product::isActive)
                .orElseThrow(() -> new ProductNotFound("Product not found"));
        if(request.getQuantity()>product.getStockQuantity()){
            throw new InsufficientStock("Dont have Sufficient Stocks");
        }
        CartItem cartProduct=userCart.getItems().stream().filter(item->item.getProductId().equals(productId)).findFirst().orElse(null);
        if(cartProduct==null){
            throw new ProductNotFound("Product not found");
        }
        cartProduct.setQuantity(request.getQuantity());
        userCart.setUpdateAt(LocalDateTime.now());
        cartRepo.save(userCart);
        return mapToCartResponse(userCart);
    }
}
