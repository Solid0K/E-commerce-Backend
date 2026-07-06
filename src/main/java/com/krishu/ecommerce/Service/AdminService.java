package com.krishu.ecommerce.Service;

import com.krishu.ecommerce.CustomExceptions.InsufficientStock;
import com.krishu.ecommerce.CustomExceptions.ProductNotFound;
import com.krishu.ecommerce.DTO.ProductRequest;
import com.krishu.ecommerce.DTO.ProductResponse;
import com.krishu.ecommerce.DTO.StockAdjustQuantity;
import com.krishu.ecommerce.Model.Product;
import com.krishu.ecommerce.Repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AdminService {

    private final ProductRepo productRepo;

    public AdminService(ProductRepo productRepo){
        this.productRepo=productRepo;
    }

    public ProductResponse addProduct(ProductRequest request) {
        Product product=new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrls(request.getImageUrls());
        product.setCurrency("INR");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdateAt(LocalDateTime.now());
        String categoryPrefix = request.getCategory().length() >= 3
                ? request.getCategory().substring(0, 3).toUpperCase()
                : request.getCategory().toUpperCase();
        String generatedSku = categoryPrefix + "-" + System.currentTimeMillis();
        product.setSku(generatedSku);
        productRepo.save(product);
        return mapToResponse(product);
    }

    public ProductResponse updateProduct(String id,ProductRequest request) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFound("Product not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrls(request.getImageUrls());
        product.setUpdateAt(LocalDateTime.now());
        productRepo.save(product);
        return mapToResponse(product);
    }

    private ProductResponse mapToResponse(Product product){
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

    public ProductResponse AdjustStock(String id, StockAdjustQuantity quantity) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFound("Product not found"));
        int newQuantity=product.getStockQuantity()+quantity.getQuantity();
        if(newQuantity<0){
            throw new InsufficientStock("Insufficient stock");
        }
        product.setStockQuantity(newQuantity);
        product.setUpdateAt(LocalDateTime.now());
        productRepo.save(product);
        return mapToResponse(product);
    }

    public ProductResponse softDelete(String id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFound("Product not found"));
        product.setActive(false);
        productRepo.save(product);
        return mapToResponse(product);
    }

    public ProductResponse absoluteDelete(String id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFound("Product not found"));
        productRepo.delete(product);
        return mapToResponse(product);
    }
}
