package com.paymentchain.customer.controller;

import com.paymentchain.customer.entity.Customer;
import com.paymentchain.customer.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    final
    CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping()
    public ResponseEntity<?> findAll() {
        List<Customer> customers = customerRepository.findAll();
        return ResponseEntity.ok().body(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isPresent()) {
            return  ResponseEntity.ok().body(customer);
        }  else {
            return  ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Customer customer) {
        Customer customerSave = customerRepository.save(customer);
        return ResponseEntity.ok().body(customerSave);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody Customer customer) {
        Optional<Customer> customerBd = customerRepository.findById(id);
        if(customerBd.isPresent()) {
            Customer updatedCustomer = customerBd.get();
            updatedCustomer.setFirstName(customer.getFirstName());
            updatedCustomer.setLastName(customer.getLastName());
            updatedCustomer.setPhoneNumber(customer.getPhoneNumber());
            customerRepository.save(updatedCustomer);
            return ResponseEntity.ok().body(updatedCustomer);
        } else  {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        customerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
