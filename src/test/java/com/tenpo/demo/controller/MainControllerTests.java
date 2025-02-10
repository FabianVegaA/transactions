package com.tenpo.demo.controller;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import com.tenpo.demo.entity.Transaction;
import com.tenpo.demo.repositories.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class MainControllerTests {
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private MainController mainController;

    @Test
    public void testAddTransaction() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        Transaction transaction = new Transaction();
        transaction.setAmount(100);
        transaction.setTrade("Buy");
        transaction.setUserName("John");

        ResponseEntity<Transaction> response = mainController.addTransaction(transaction);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAmount()).isEqualTo(100);
        assertThat(response.getBody().getTrade()).isEqualTo("Buy");
        assertThat(response.getBody().getUserName()).isEqualTo("John");
    }

}
