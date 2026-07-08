package com.krishu.ecommerce.Repository;

import com.krishu.ecommerce.Model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CartRepo extends MongoRepository<Cart,String> {
    Optional<Cart> findByUserId(String userId);
}
