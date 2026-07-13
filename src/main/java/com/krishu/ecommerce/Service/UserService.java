package com.krishu.ecommerce.Service;

import com.krishu.ecommerce.CustomExceptions.ProductNotFound;
import com.krishu.ecommerce.DTO.ProductResponse;
import com.krishu.ecommerce.Model.Product;
import com.krishu.ecommerce.Repository.ProductRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import java.util.List;


@Service
public class UserService {

    private final ProductRepo productRepo;
    private final MongoTemplate mongoTemplate;

    public UserService(ProductRepo productRepo, MongoTemplate mongoTemplate){
        this.productRepo=productRepo;
        this.mongoTemplate = mongoTemplate;
    }

    public Page<ProductResponse> getProducts(String search,Pageable pageable) {
        if(search!=null && !search.isBlank()){
            return searchHelper(search,pageable);
        }
        return productRepo.findByIsActiveTrue(pageable).map(this::ToResponse);
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

    private Page<ProductResponse> searchHelper(String search,Pageable pageable){
        Document searchStage = new Document("$search",
                new Document("index", "default")
                        .append("text", new Document("query", search)
                                .append("path", List.of("name", "description", "category"))));

        Document activeFilterStage = new Document("$match", new Document("isActive", true));

        Document skipStage = new Document("$skip", (long) pageable.getOffset());
        Document limitStage = new Document("$limit", pageable.getPageSize());

        Aggregation aggregation = Aggregation.newAggregation(
                context -> searchStage,
                context -> activeFilterStage,
                context -> skipStage,
                context -> limitStage
        );

        List<Product> results = mongoTemplate.aggregate(aggregation, "products", Product.class)
                .getMappedResults();

        Aggregation countAggregation = Aggregation.newAggregation(
                context -> searchStage,
                context -> activeFilterStage,
                context -> new Document("$count", "total")
        );

        List<Document> countResult = mongoTemplate.aggregate(countAggregation, "products", Document.class)
                .getMappedResults();
        long total = countResult.isEmpty() ? 0 : countResult.get(0).getInteger("total");

        List<ProductResponse> responses = results.stream().map(this::ToResponse).toList();
        return new PageImpl<>(responses, pageable, total);
    }
}
