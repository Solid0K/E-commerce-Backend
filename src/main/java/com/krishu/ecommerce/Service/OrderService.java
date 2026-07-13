package com.krishu.ecommerce.Service;

import com.krishu.ecommerce.CustomExceptions.*;
import com.krishu.ecommerce.DTO.CheckOutResponse;
import com.krishu.ecommerce.DTO.OrderItemResponse;
import com.krishu.ecommerce.DTO.OrderResponse;
import com.krishu.ecommerce.Model.*;
import com.krishu.ecommerce.OrderStatus;
import com.krishu.ecommerce.Repository.CartRepo;
import com.krishu.ecommerce.Repository.OrderRepo;
import com.krishu.ecommerce.Repository.ProductRepo;
import com.krishu.ecommerce.Repository.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    private final MockPaymentGateway mockPaymentGateway;

    public OrderService(OrderRepo orderRepo, UserRepo userRepo, CartRepo cartRepo, ProductRepo productRepo, MockPaymentGateway mockPaymentGateway){
        this.orderRepo=orderRepo;
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.mockPaymentGateway = mockPaymentGateway;
    }

    public CheckOutResponse checkOut(Authentication authentication) {
        String email=authentication.getName();
        User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFound("User not found"));
        Cart userCart=cartRepo.findByUserId(user.getId()).orElseThrow(()->new UserCartNotFound("Cart not found"));

        if(userCart.getItems().isEmpty()){
            throw new CartEmptyException("Cart is Empty");
        }

        List<OrderItem> orderItems=new ArrayList<>();
        BigDecimal totalAmount= BigDecimal.ZERO;

        for(CartItem item:userCart.getItems()){
            Product product=productRepo.findById(item.getProductId()).orElseThrow(()-> new ProductNotFound("Product not found"));
            if(item.getQuantity()>product.getStockQuantity()){
                throw new InsufficientStock("Insufficient Stock");
            }
            OrderItem orderItem=new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItems.add(orderItem);
            totalAmount=totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        Order order=new Order();
        order.setUserId(user.getId());
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.Pending);
        order.setCreatedAt(LocalDateTime.now());
        Order savedOrder=orderRepo.save(order);

        MockPaymentIntent intent=mockPaymentGateway.createMockPaymentIntent(savedOrder.getTotalAmount(),savedOrder.getId());
        savedOrder.setStripePaymentIntentId(intent.getId());
        Order savedOrderV2=orderRepo.save(savedOrder);

        userCart.getItems().clear();
        userCart.setUpdateAt(LocalDateTime.now());
        cartRepo.save(userCart);

        CheckOutResponse checkOutResponse=new CheckOutResponse();
        checkOutResponse.setOrder(mapToOrderResponse(savedOrderV2));
        checkOutResponse.setClientSecret(intent.getClientSecret());
        checkOutResponse.setPaymentIntentId(intent.getId());
        return checkOutResponse;
    }

    public Page<OrderResponse> getAllOrders(Authentication authentication, Pageable pageable) {
        String email=authentication.getName();
        User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFound("User not found"));
        Page<Order> orders=orderRepo.findByUserId(user.getId(),pageable);
        return orders.map(this::mapToOrderResponse);
    }

    public OrderResponse getOrderById(Authentication authentication, String orderId) {
        String email=authentication.getName();
        User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFound("User not found"));
        List<Order> orders=orderRepo.findByUserId(user.getId());
        Order orderById=orders.stream().filter(order->order.getId()
                .equals(orderId)).findFirst().orElseThrow(()->new OrderNotFoundException("Order not found"));
        return mapToOrderResponse(orderById);
    }

    public void markOrderPaid(String paymentIntentId){
        Order order=orderRepo.findByStripePaymentIntentId(paymentIntentId).orElseThrow(()->new OrderNotFoundException("Order not Found"));
        order.setStatus(OrderStatus.Paid);
        orderRepo.save(order);

        for(OrderItem item:order.getItems()){
            Product product=productRepo.findById(item.getProductId()).orElseThrow(()->new ProductNotFound("Product not found"));
            product.setStockQuantity(product.getStockQuantity()-item.getQuantity());
            productRepo.save(product);
        }
    }

    public void markOrderFailed(String paymentIntentId){
        Order order=orderRepo.findByStripePaymentIntentId(paymentIntentId).orElseThrow(()->new OrderNotFoundException("Order not Found"));
        order.setStatus(OrderStatus.Failed);
        orderRepo.save(order);
    }

    private OrderResponse mapToOrderResponse(Order order){
        List<OrderItemResponse> items=order.getItems().stream().map(product->{
            OrderItemResponse orderItem=new OrderItemResponse();
            orderItem.setProductId(product.getProductId());
            orderItem.setProductName(product.getProductName());
            orderItem.setQuantity(product.getQuantity());
            orderItem.setUnitPrice(product.getUnitPrice());
            orderItem.setSubTotal(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            return orderItem;
        }).toList();

        OrderResponse orderResponse=new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setItems(items);
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setCreatedAt(order.getCreatedAt());
        return orderResponse;
    }
}
