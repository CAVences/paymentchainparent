package com.paymentchain.transaction.controller;

import com.paymentchain.transaction.entity.Transaction;
import com.paymentchain.transaction.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllTransactions(){
        List<Transaction> transactions = transactionRepository.findAll();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/customer/{account}")
    public ResponseEntity<?> getTransactionByAccount(@PathVariable String account){
        List<Transaction> transaction = transactionRepository.findByAccount(account);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction){
        Transaction savedTransaction = transactionRepository.save(transaction);
        return ResponseEntity.ok(savedTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody Transaction transactionDetails){
        Optional<Transaction> transaction = transactionRepository.findById(id);

        if(transaction.isPresent()){
            Transaction transactionUpdate = transaction.get();
            transactionUpdate.setReference(transactionDetails.getReference());
            transactionUpdate.setAccount(transactionDetails.getAccount());
            transactionUpdate.setLocalDateTime(transactionDetails.getLocalDateTime());
            transactionUpdate.setAmount(transactionDetails.getAmount());
            transactionUpdate.setFee(transactionDetails.getFee());
            return ResponseEntity.ok(transactionRepository.save(transactionUpdate));
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id){
        transactionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
