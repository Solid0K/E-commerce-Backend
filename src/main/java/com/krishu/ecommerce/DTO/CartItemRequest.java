package com.krishu.ecommerce.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class CartItemRequest {
    private String productId;
    @Positive
    private int quantity;

    public CartItemRequest(){}

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
