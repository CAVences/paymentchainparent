package com.paymentchain.customer.transactions;

import com.paymentchain.customer.entity.Customer;
import com.paymentchain.customer.entity.CustomerProduct;
import com.paymentchain.customer.exception.BusinessRuleException;
import com.paymentchain.customer.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class BusinessTransaction {

    private final WebClient productClient;   // WebClient ya configurado para productos
    private final WebClient transactionClient;
    private final CustomerRepository customerRepository;

    public BusinessTransaction(WebClient productClient, WebClient transactionClient, CustomerRepository customerRepository) {
        this.productClient = productClient;
        this.transactionClient = transactionClient;
        this.customerRepository = customerRepository;
    }

    // DTO para deserializar la respuesta del microservicio de productos
    public record ProductResponse(Long id, String name) {}

    // 🔍 Llamada al microservicio de productos para obtener el nombre
    private String getProductName(long id) throws UnknownHostException {
        String name = "";
        try {
            ProductResponse product = productClient
                    .get()
                    .uri("/products/{id}", id)
                    .retrieve()
                    .bodyToMono(ProductResponse.class)
                    .timeout(Duration.ofSeconds(3))
                    .block();

            if(product != null) {
                name = product.name();
            }

        } catch (WebClientResponseException ex) {
            if(ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return "";
            } else {
                throw new UnknownHostException(ex.getMessage());
            }
        }


        return name;
    }

    private List<?> getTransactionStatus(String accountNumber) {
        List<?> transactions = transactionClient
                .get()
                .uri("/transaction/customer/{account}", accountNumber)
                .retrieve()
                .bodyToMono(List.class)
                .block();

        if(transactions == null) {
            return Collections.emptyList();
        }

        return  transactions;
    }


    public Customer getByCode( String code) {
        Customer customer = customerRepository.findByCode(code);

        List<CustomerProduct> products =
                customer.getProducts() != null ? customer.getProducts() : Collections.emptyList();

        products.forEach(product -> {
                    try {
                        product.setProductName(getProductName(product.getProductId()));
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(BusinessTransaction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        );

        List<?> transactions = getTransactionStatus(customer.getAccountNumber());

        customer.setTransactions(transactions);

        customer.setProducts(products);
        return customer;
    }


    public Customer postCustomer(Customer customer) throws BusinessRuleException, UnknownHostException {
        if(customer.getProducts() != null) {
            for (CustomerProduct customerProduct : customer.getProducts()) {
                String productName = getProductName(customerProduct.getProductId());
                if (productName.isBlank()) {
                    throw new BusinessRuleException("1025", "Error validacion producto con id: " + customerProduct.getProductId(), HttpStatus.PRECONDITION_FAILED);
                } else {
                    customerProduct.setProductName(productName);
                }
            }
        }

        return customerRepository.save(customer);
    }

}
