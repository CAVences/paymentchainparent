package com.paymentchain.customer.controller;

import com.paymentchain.customer.entity.Customer;
import com.paymentchain.customer.exception.BusinessRuleException;
import com.paymentchain.customer.repository.CustomerRepository;
import com.paymentchain.customer.transactions.BusinessTransaction;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final Environment environment;
    private final BusinessTransaction businessTransaction;


    public CustomerController(CustomerRepository customerRepository, Environment environment, BusinessTransaction businessTransaction) {
        this.customerRepository = customerRepository;
        this.environment = environment;
        this.businessTransaction = businessTransaction;
    }

    @GetMapping("/check")
    public String checkCustomer() {
        return "Check Customer Successfully, " + environment.getProperty("custom.activeprofileName");
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Customer> customers = customerRepository.findAll();

        if(customers.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(customers);
        }
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
    public ResponseEntity<?> save(@RequestBody Customer customer) throws BusinessRuleException, UnknownHostException {
        Customer customerSave = businessTransaction.postCustomer(customer);
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
        Customer customer = businessTransaction.getByCode(code);

        if(customer == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(customer);
        }
    }


}
