package com.krishu.ecommerce.Controller;

import com.krishu.ecommerce.DTO.OrderResponse;
import com.krishu.ecommerce.Service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService=orderService;
    }

    @PostMapping("/checkOut")
    public ResponseEntity<OrderResponse> checkOut(Authentication authentication){
        OrderResponse response = orderService.checkOut(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    public ResponseEntity<List<OrderResponse>> getOrders(Authentication authentication){
        return ResponseEntity.ok(orderService.getAllOrders(authentication));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(Authentication authentication,@PathVariable String orderId){
        return ResponseEntity.ok(orderService.getOrderById(authentication,orderId));
    }
}
