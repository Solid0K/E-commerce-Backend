package com.krishu.ecommerce.Service;

import com.krishu.ecommerce.CustomExceptions.ProductNotFound;
import com.krishu.ecommerce.DTO.ProductResponse;
import com.krishu.ecommerce.Model.Product;
import com.krishu.ecommerce.Repository.ProductRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final ProductRepo productRepo;

    public UserService(ProductRepo productRepo){
        this.productRepo=productRepo;
    }

    public Page<ProductResponse> getProducts(Pageable pageable) {
        Page<Product> activeProducts = productRepo.findByIsActiveTrue(pageable);
        return activeProducts.map(this::ToResponse);
    }

    public ProductResponse getProduct(String id) {
        Product product = productRepo.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() -> new ProductNotFound("Product not found"));
        return ToResponse(product);
    }

    private ProductResponse ToResponse(Product product){
        ProductResponse productresponse=new ProductResponse();
        productresponse.setId(product.getId());
        productresponse.setName(product.getName());
        productresponse.setDescription(product.getDescription());
        productresponse.setCategory(product.getCategory());
        productresponse.setCurrency(product.getCurrency());
        productresponse.setCreatedAt(product.getCreatedAt());
        productresponse.setImageUrls(product.getImageUrls());
        productresponse.setPrice(product.getPrice());
        productresponse.setSku(product.getSku());
        productresponse.setStockQuantity(product.getStockQuantity());
        productresponse.setUpdateAt(product.getUpdateAt());
        return productresponse;
    }
}
