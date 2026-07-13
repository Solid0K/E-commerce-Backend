package com.krishu.ecommerce.Controller;

import com.krishu.ecommerce.DTO.AdminOrderResponse;
import com.krishu.ecommerce.DTO.OrderStatusChange;
import com.krishu.ecommerce.Service.AdminOrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService){
        this.adminOrderService=adminOrderService;
    }

    @GetMapping()
    public ResponseEntity<Page<AdminOrderResponse>> getAllOrders(Pageable pageable){
        return ResponseEntity.ok(adminOrderService.getAllOrders(pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<AdminOrderResponse> getOrderById(@PathVariable String orderId){
        return ResponseEntity.ok(adminOrderService.getOrderById(orderId));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<AdminOrderResponse> updateOrderStatus(@PathVariable String orderId,@RequestBody OrderStatusChange request){
        return ResponseEntity.ok(adminOrderService.updateOrderStatus(orderId,request.getStatus()));
    }
}
