package com.tenpo.demo.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.tenpo.demo.entity.Transaction;
import com.tenpo.demo.repositories.TransactionRepository;

@RestController
@RequestMapping(path = "/api/transactions")
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping
    public ResponseEntity<Iterable<Transaction>> getTransactions() {
        return ResponseEntity.ok(transactionRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable("id") Long idTransaction) {
        return transactionRepository.findById(idTransaction)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction) {
        logger.info("Adding transaction: {}", transaction);
        transactionRepository.save(transaction);
        return ResponseEntity.created(null).body(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable("id") Long idTransaction,
            @RequestBody Transaction transaction) {
        Optional<Transaction> updatedTransaction = transactionRepository.findById(idTransaction).map(t -> {
            t.setAmount(transaction.getAmount());
            t.setTrade(transaction.getTrade());
            t.setUserName(transaction.getUserName());
            return transactionRepository.save(t);
        });
        return updatedTransaction
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable("id") Long idTransaction) {
        transactionRepository.deleteById(idTransaction);
        return ResponseEntity.noContent().build();
    }

}