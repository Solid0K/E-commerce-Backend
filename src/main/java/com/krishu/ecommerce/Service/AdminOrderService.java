package com.krishu.ecommerce.Service;

import com.krishu.ecommerce.CustomExceptions.InvalidOrderStatusTransitionException;
import com.krishu.ecommerce.CustomExceptions.OrderNotFoundException;
import com.krishu.ecommerce.CustomExceptions.UserNotFound;
import com.krishu.ecommerce.DTO.AdminOrderResponse;
import com.krishu.ecommerce.DTO.OrderItemResponse;
import com.krishu.ecommerce.Model.Order;
import com.krishu.ecommerce.Model.User;
import com.krishu.ecommerce.OrderStatus;
import com.krishu.ecommerce.Repository.OrderRepo;
import com.krishu.ecommerce.Repository.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminOrderService {

    private final UserRepo userRepo;
    private final OrderRepo orderRepo;

    public AdminOrderService(UserRepo userRepo, OrderRepo orderRepo){
        this.userRepo=userRepo;
        this.orderRepo = orderRepo;
    }

    public Page<AdminOrderResponse> getAllOrders(Pageable pageable){
        Page<Order> orders = orderRepo.findAll(pageable);
        return orders.map(this::mapToAdminOrderResponse);
    }

    public AdminOrderResponse getOrderById(String orderId){
        Order order=orderRepo.findById(orderId).orElseThrow(()->new OrderNotFoundException("Order with id "+orderId+" not found"));
        return mapToAdminOrderResponse(order);
    }

    public AdminOrderResponse updateOrderStatus(String orderId, OrderStatus newStatus){
        Order order=orderRepo.findById(orderId).orElseThrow(()->new OrderNotFoundException("Order with id "+orderId+" not found"));
        if (order.getStatus() == OrderStatus.Pending && newStatus == OrderStatus.Shipped) {
            throw new InvalidOrderStatusTransitionException("Cannot ship an order that hasn't been paid");
        }
        order.setStatus(newStatus);
        Order newSavedOrder=orderRepo.save(order);
        return mapToAdminOrderResponse(newSavedOrder);
    }

    private AdminOrderResponse mapToAdminOrderResponse(Order order){
        List<OrderItemResponse> items=order.getItems().stream().map(product->{
            OrderItemResponse orderItem=new OrderItemResponse();
            orderItem.setProductId(product.getProductId());
            orderItem.setProductName(product.getProductName());
            orderItem.setQuantity(product.getQuantity());
            orderItem.setUnitPrice(product.getUnitPrice());
            orderItem.setSubTotal(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            return orderItem;
        }).toList();

        User user=userRepo.findById(order.getUserId()).orElseThrow(()-> new UserNotFound("Order User not found"));

        AdminOrderResponse adminOrderResponse=new AdminOrderResponse();
        adminOrderResponse.setOrderId(order.getId());
        adminOrderResponse.setUserId(order.getUserId());
        adminOrderResponse.setItems(items);
        adminOrderResponse.setUserEmail(user.getEmail());
        adminOrderResponse.setCreatedAt(LocalDateTime.now());
        adminOrderResponse.setStatus(order.getStatus());
        adminOrderResponse.setTotalAmount(order.getTotalAmount());
        adminOrderResponse.setPaymentIntentId(order.getStripePaymentIntentId());
        return adminOrderResponse;
    }
}
