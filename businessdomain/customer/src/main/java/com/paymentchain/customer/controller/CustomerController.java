package com.paymentchain.customer.controller;

import com.paymentchain.customer.entity.Customer;
import com.paymentchain.customer.entity.CustomerProduct;
import com.paymentchain.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final WebClient productClient;   // WebClient ya configurado para productos
    private final WebClient transactionClient;

    public CustomerController(CustomerRepository customerRepository,
                              @Qualifier("productClient") WebClient productClient, @Qualifier("transactionClient") WebClient transactionClient) {
        this.customerRepository = customerRepository;
        this.productClient = productClient; // viene del @Bean de WebClientConfig
        this.transactionClient = transactionClient;
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Customer> customers = customerRepository.findAll();
        return ResponseEntity.ok().body(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            return ResponseEntity.ok().body(customer.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Customer customer) {
        List<CustomerProduct> products =
                customer.getProducts() != null ? customer.getProducts() : Collections.emptyList();
        products.forEach(product -> product.setCustomer(customer));
        customer.setProducts(products);

        Customer customerSave = customerRepository.save(customer);
        return ResponseEntity.ok(customerSave);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody Customer customer) {
        Optional<Customer> customerBd = customerRepository.findById(id);
        if (customerBd.isPresent()) {
            Customer updatedCustomer = customerBd.get();
            updatedCustomer.setFirstName(customer.getFirstName());
            updatedCustomer.setLastName(customer.getLastName());
            updatedCustomer.setPhoneNumber(customer.getPhoneNumber());
            customerRepository.save(updatedCustomer);
            return ResponseEntity.ok().body(updatedCustomer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        customerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Endpoint que regresa el customer con los nombres de producto resueltos vía WebClient
    @GetMapping("/full")
    public ResponseEntity<?> getByCode(@RequestParam String code) {
        Customer customer = customerRepository.findByCode(code);

        if (customer == null) {
            return ResponseEntity.notFound().build();
        }

        List<CustomerProduct> products =
                customer.getProducts() != null ? customer.getProducts() : Collections.emptyList();

        products.forEach(product ->
                product.setProductName(getProductName(product.getProductId()))
        );

        List<?> transactions = getTransactionStatus(customer.getAccountNumber());

        customer.setTransactions(transactions);

        customer.setProducts(products);
        return ResponseEntity.ok(customer);
    }

    // DTO para deserializar la respuesta del microservicio de productos
    public record ProductResponse(Long id, String name) {}

    // 🔍 Llamada al microservicio de productos para obtener el nombre
    private String getProductName(long id) {
        ProductResponse product = productClient
                .get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .timeout(Duration.ofSeconds(3))
                .block();

        if (product == null || product.name() == null) {
            throw new IllegalStateException("No se pudo obtener el nombre del producto con id=" + id);
        }

        return product.name();
    }

    private List<?> getTransactionStatus(String accountNumber) {
        List<?> transactions = transactionClient
                .get()
                .uri("/customer/{account}", accountNumber)
                .retrieve()
                .bodyToMono(List.class)
                .block();

        if(transactions == null) {
            return Collections.emptyList();
        }

        return  transactions;
    }
}
