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
    public void testFindOne() {
    	double amt=400;
        this.entityManager.persist(new Account(amt));
        this.accountService.findAll();
        Account account = this.accountService.findById(2L).get(); // ID 2 because default account 1 is created whenever the program runs
        assertEquals(account.getAmount(),amt);
//        assertThat(account.getAmount()).isEqualTo(400.0);

    }

}