package com.krishu.ecommerce.DTO;

import com.krishu.ecommerce.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderStatusChange {
    @NotNull
    private OrderStatus status;

    public OrderStatusChange(){}

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
