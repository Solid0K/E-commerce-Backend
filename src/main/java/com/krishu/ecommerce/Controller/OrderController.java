package com.krishu.ecommerce.Controller;

import com.krishu.ecommerce.DTO.CheckOutResponse;
import com.krishu.ecommerce.DTO.OrderResponse;
import com.krishu.ecommerce.Service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<CheckOutResponse> checkOut(Authentication authentication){
        CheckOutResponse response = orderService.checkOut(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    public ResponseEntity<Page<OrderResponse>> getOrders(Authentication authentication, Pageable pageable){
        return ResponseEntity.ok(orderService.getAllOrders(authentication,pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(Authentication authentication,@PathVariable String orderId){
        return ResponseEntity.ok(orderService.getOrderById(authentication,orderId));
    }
}
