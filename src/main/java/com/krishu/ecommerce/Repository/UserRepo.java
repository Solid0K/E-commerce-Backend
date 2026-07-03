package com.krishu.ecommerce.Repository;

import com.krishu.ecommerce.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo extends MongoRepository<User,String> {
    Optional<User> findByEmail(String email);
}
