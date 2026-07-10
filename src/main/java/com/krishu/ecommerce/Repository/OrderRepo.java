package com.krishu.ecommerce.Repository;

import com.krishu.ecommerce.Model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends MongoRepository<Order,String> {
    List<Order> findByUserId(String userId);
}
