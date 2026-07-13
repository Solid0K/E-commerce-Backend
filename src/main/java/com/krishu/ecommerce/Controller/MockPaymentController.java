package com.krishu.ecommerce.Controller;

import com.krishu.ecommerce.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mock-payment")
public class MockPaymentController {

    private final OrderService orderService;

    public MockPaymentController(OrderService orderService){
        this.orderService=orderService;
    }

    @PostMapping("/{paymentIntentId}/simulate")
    public ResponseEntity<String> simulatePayment(@PathVariable String paymentIntentId, @RequestParam boolean success){
        if(success){
            orderService.markOrderPaid(paymentIntentId);
        }else{
            orderService.markOrderFailed(paymentIntentId);
        }
        return ResponseEntity.ok("Simulated: " + (success ? "payment succeeded" : "payment failed"));
    }
}
