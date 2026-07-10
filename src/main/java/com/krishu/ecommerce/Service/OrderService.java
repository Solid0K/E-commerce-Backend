package com.krishu.ecommerce.Service;

import com.krishu.ecommerce.CustomExceptions.*;
import com.krishu.ecommerce.DTO.OrderItemResponse;
import com.krishu.ecommerce.DTO.OrderResponse;
import com.krishu.ecommerce.Model.*;
import com.krishu.ecommerce.OrderStatus;
import com.krishu.ecommerce.Repository.CartRepo;
import com.krishu.ecommerce.Repository.OrderRepo;
import com.krishu.ecommerce.Repository.ProductRepo;
import com.krishu.ecommerce.Repository.UserRepo;
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

    public OrderService(OrderRepo orderRepo, UserRepo userRepo, CartRepo cartRepo, ProductRepo productRepo){
        this.orderRepo=orderRepo;
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    public OrderResponse checkOut(Authentication authentication) {
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
        userCart.getItems().clear();
        userCart.setUpdateAt(LocalDateTime.now());
        cartRepo.save(userCart);
        return mapToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders(Authentication authentication) {
        String email=authentication.getName();
        User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFound("User not found"));
        List<Order> orders=orderRepo.findByUserId(user.getId());
        return orders.stream().map(this::mapToOrderResponse).toList();
    }

    public OrderResponse getOrderById(Authentication authentication, String orderId) {
        String email=authentication.getName();
        User user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFound("User not found"));
        List<Order> orders=orderRepo.findByUserId(user.getId());
        Order orderById=orders.stream().filter(order->order.getId()
                .equals(orderId)).findFirst().orElseThrow(()->new OrderNotFoundException("Order not found"));
        return mapToOrderResponse(orderById);
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
