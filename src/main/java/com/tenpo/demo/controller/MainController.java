package com.tenpo.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tenpo.demo.entity.Transaction;
import com.tenpo.demo.repositories.TransactionRepository;

@RestController
@RequestMapping(path = "/api/transactions")
public class MainController {

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping
    public Object getTransaction(@RequestParam(name = "id", required = false) Optional<Integer> idTransaction) {
        if (!idTransaction.isPresent()) {
            return transactionRepository.findAll();
        }
        return transactionRepository.findById(idTransaction.get()).orElse(null);
    }

    @PostMapping
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        transactionRepository.save(transaction);
        return transaction;
    }

    @PutMapping
    public Transaction updateTransaction(@RequestParam(name = "id") Integer idTransaction,
            @RequestBody Transaction transaction) {
        transactionRepository.findById(idTransaction).ifPresent(t -> {
            t.setAmount(transaction.getAmount());
            t.setTrade(transaction.getTrade());
            t.setUserName(transaction.getUserName());
            transactionRepository.save(t);
        });
        return transaction;
    }

    @DeleteMapping
    public void deleteTransaction(@RequestParam(name = "id") Integer idTransaction) {
        transactionRepository.deleteById(idTransaction);
    }

}