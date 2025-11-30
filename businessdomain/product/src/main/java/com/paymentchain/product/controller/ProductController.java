package com.paymentchain.product.controller;

import com.paymentchain.product.entity.Product;
import com.paymentchain.product.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {


    final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping()
    public ResponseEntity<?> getAllProducts(){
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok().body(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable long id){
        Optional<Product> product = productRepository.findById(id);

        if(product.isPresent()){
            return ResponseEntity.ok().body(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping()
    public ResponseEntity<?> saveProduct(@RequestBody Product product){
        productRepository.save(product);
        return ResponseEntity.ok().body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable long id, @RequestBody Product product){
        System.out.println(id);
        Optional<Product> productOptional = productRepository.findById(id);
        if(productOptional.isPresent()){
            Product updatedProduct = productOptional.get();
            updatedProduct.setName(product.getName());
            updatedProduct.setCode(product.getCode());
            productRepository.save(updatedProduct);
            return ResponseEntity.ok().body(updatedProduct);
        } else  {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
