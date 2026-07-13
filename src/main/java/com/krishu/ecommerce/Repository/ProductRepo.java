package com.krishu.ecommerce.Repository;

import com.krishu.ecommerce.Model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepo extends MongoRepository<Product,String> {
    Page<Product> findByIsActiveTrue(Pageable pageable);
}
