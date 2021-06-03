package com.bank.cashcard.test.service;

import com.bank.cashcard.entity.Account;
import com.bank.cashcard.service.AccountService;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

/**
 @author Akshay Parab
 */
@RunWith(SpringRunner.class)
@DataJpaTest
class AccountServiceTests {

    @Autowired
    AccountService accountService;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCheckAmoutSuccess() {
    	double amt=400;
        this.entityManager.persist(new Account(amt));
        this.accountService.findAll();
        Account account = this.accountService.findById(2L).get(); 
        System.out.println(account.getAmount());
        assertEquals(account.getAmount(),amt);


    }
    
    @Test
    public void testCheckAmoutFailure() {
    	double amt=600;
        this.entityManager.persist(new Account(amt));
        this.accountService.findAll();
        Account account = this.accountService.findById(2L).get(); 
        assertNotEquals(account.getAmount(),amt);


    }

}