package com.bank.cashcard.test.service;

import com.bank.cashcard.entity.AccountTransaction;
import com.bank.cashcard.entity.TransactionType;
import com.bank.cashcard.service.TransactionsService;
import com.bank.cashcard.util.AccountUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

/**
 @author Akshay Parab
 */
@RunWith(SpringRunner.class)
@DataJpaTest
class TransactionsServiceTests {

    @Autowired
    TransactionsService transactionsService;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindByDateBetweenAndTypeSucccess() {
    	double amt=1000;
        this.entityManager.persist(new AccountTransaction(TransactionType.WITHDRAWAL.getId(), amt, new Date()));
        List<AccountTransaction> transactions = transactionsService.findByDateBetweenAndType(AccountUtils.getStartOfDay(new Date()), AccountUtils.getEndOfDay(new Date()), TransactionType.WITHDRAWAL.getId());
        assertEquals(transactions.get(0).getType(),TransactionType.WITHDRAWAL.getId());
        assertEquals(transactions.get(0).getAmount(),amt);
       
    }
    
    @Test
    public void testFindByDateBetweenAndTypeFailure() {
    	double amt=1000;
        this.entityManager.persist(new AccountTransaction(TransactionType.WITHDRAWAL.getId(), amt, new Date()));
        List<AccountTransaction> transactions = transactionsService.findByDateBetweenAndType(AccountUtils.getStartOfDay(new Date()), AccountUtils.getEndOfDay(new Date()), TransactionType.WITHDRAWAL.getId());
        assertEquals(transactions.get(0).getType(),TransactionType.WITHDRAWAL.getId());
        assertNotEquals(transactions.get(0).getAmount(),amt);
       
    }

}