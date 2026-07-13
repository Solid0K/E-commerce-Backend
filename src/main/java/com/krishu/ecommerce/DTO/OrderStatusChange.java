package com.krishu.ecommerce.DTO;

import com.krishu.ecommerce.OrderStatus;

public class OrderStatusChange {
    private OrderStatus status;

    public OrderStatusChange(){}

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
