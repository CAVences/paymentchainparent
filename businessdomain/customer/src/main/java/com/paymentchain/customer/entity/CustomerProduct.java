package com.paymentchain.customer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paymentchain.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CustomerProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long productId;

    @Transient
    private String productName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Customer.class)
    @JoinColumn(name = "customerId", nullable = true)
    private Customer customer;
}
