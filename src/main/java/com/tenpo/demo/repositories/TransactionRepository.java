package com.tenpo.demo.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import com.tenpo.demo.entity.Transaction;

public interface TransactionRepository extends
        CrudRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

}
