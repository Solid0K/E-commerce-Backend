package com.krishu.ecommerce.Controller;

import com.krishu.ecommerce.DTO.ProductRequest;
import com.krishu.ecommerce.DTO.ProductResponse;
import com.krishu.ecommerce.DTO.StockAdjustQuantity;
import com.krishu.ecommerce.Service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService){
        this.adminService=adminService;
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest request){
        return ResponseEntity.ok(adminService.addProduct(request));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String id, @RequestBody ProductRequest request){
        return ResponseEntity.ok(adminService.updateProduct(id,request));
    }

    @PatchMapping("/products/{id}/stock")
    public ResponseEntity<ProductResponse> adjustStock(@PathVariable String id,@RequestBody StockAdjustQuantity quantity){
        return ResponseEntity.ok(adminService.AdjustStock(id,quantity));
    }

    @DeleteMapping("/products/{id}/softDelete")
    public ResponseEntity<ProductResponse> softDelete(@PathVariable String id){
        return ResponseEntity.ok(adminService.softDelete(id));
    }

    @DeleteMapping("/products/{id}/absoluteDelete")
    public ResponseEntity<ProductResponse> absoluteDelete(@PathVariable String id){
        return ResponseEntity.ok(adminService.absoluteDelete(id));
    }


}
