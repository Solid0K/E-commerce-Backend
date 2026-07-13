package com.krishu.ecommerce.Service;

import com.krishu.ecommerce.Model.MockPaymentIntent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class MockPaymentGateway {

    public MockPaymentIntent createMockPaymentIntent(BigDecimal amount, String orderId){
        MockPaymentIntent intent=new MockPaymentIntent();
        intent.setId("mock_pi_" + UUID.randomUUID());
        intent.setClientSecret("mock_secret_" + UUID.randomUUID());
        intent.setAmount(amount);
        intent.setOrderId(orderId);
        intent.setStatus("PENDING");
        return intent;
    }
}
