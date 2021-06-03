package com.bank.cashcard.test.controller;

import com.google.gson.Gson;
import com.bank.cashcard.controller.WithdrawalController;
import com.bank.cashcard.entity.Account;
import com.bank.cashcard.entity.AccountTransaction;
import com.bank.cashcard.entity.TransactionType;
import com.bank.cashcard.entity.UserTransaction;
import com.bank.cashcard.service.AccountService;
import com.bank.cashcard.service.TransactionsService;
import com.bank.cashcard.util.AccountUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 @author Akshay Parab
 */
@RunWith(SpringRunner.class)
@WebMvcTest(WithdrawalController.class)
public class WithdrawalControllerTest {
	
	 @Autowired
	    protected MockMvc mvc;

	    @MockBean
	    protected AccountService accountService;

	    @MockBean
	    protected TransactionsService transactionsService;
	    
    @Test
    public void testWithdrawalExceedsCurrentBalance() throws Exception {

        UserTransaction userTransaction = new UserTransaction(50000);
        Gson gson = new Gson();
        String json = gson.toJson(userTransaction);

        given(this.accountService.findById(1L)).willReturn(Optional.of(new Account(40000)));

        this.mvc.perform(post("/withdrawal/").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotAcceptable()).andExpect(content().json("{\"success\":false,\"messages\":{\"message\":\"You have insufficient funds\",\"title\":\"Error\"},\"errors\":{},\"data\":{},\"httpResponseCode\":406}"));

    }

    @Test
    public void testMaxWithdrawalAmountPerTheDay() throws Exception {

        AccountTransaction transaction = new AccountTransaction(TransactionType.WITHDRAWAL.getId(), 40000, new Date());
        AccountTransaction transaction2 = new AccountTransaction(TransactionType.WITHDRAWAL.getId(), 20000, new Date());

        List<AccountTransaction> list = new ArrayList<>();
        list.add(transaction);
        list.add(transaction2);

        UserTransaction userTransaction = new UserTransaction(50000);
        Gson gson = new Gson();
        String json = gson.toJson(userTransaction);

        given(this.accountService.findById(1L)).willReturn(Optional.of(new Account(400000)));

        given(this.transactionsService.findByDateBetweenAndType(AccountUtils.getStartOfDay(new Date()),
                AccountUtils.getEndOfDay(new Date()), TransactionType.WITHDRAWAL.getId())).willReturn(list);

        this.mvc.perform(post("/withdrawal/").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotAcceptable()).andExpect(content().json("{\"success\":false,\"messages\":{\"message\":\"Withdrawal per day should not be more than 50000\",\"title\":\"Error\"},\"errors\":{},\"data\":{},\"httpResponseCode\":406}"));

    }
    
    @Test
    public void testMaxTrasactionPerTheDay() throws Exception {

        AccountTransaction transaction = new AccountTransaction(TransactionType.WITHDRAWAL.getId(), 20000, new Date());
        AccountTransaction transaction2 = new AccountTransaction(TransactionType.WITHDRAWAL.getId(), 20000, new Date());
        AccountTransaction transaction3 = new AccountTransaction(TransactionType.WITHDRAWAL.getId(), 10000, new Date());
        AccountTransaction transaction4 = new AccountTransaction(TransactionType.WITHDRAWAL.getId(), 20000, new Date());
        
        List<AccountTransaction> list = new ArrayList<>();
        list.add(transaction);
        list.add(transaction2);
        list.add(transaction3);
        list.add(transaction4);

        UserTransaction userTransaction = new UserTransaction(80000);
        Gson gson = new Gson();
        String json = gson.toJson(userTransaction);

        given(this.accountService.findById(1L)).willReturn(Optional.of(new Account(400000)));

        given(this.transactionsService.findByDateBetweenAndType(AccountUtils.getStartOfDay(new Date()),
                AccountUtils.getEndOfDay(new Date()), TransactionType.WITHDRAWAL.getId())).willReturn(list);

        this.mvc.perform(post("/withdrawal/").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotAcceptable()).andExpect(content().json("{\"success\":false,\"messages\":{\"message\":\"Withdrawal per day should not be more than 3\",\"title\":\"Error\"},\"errors\":{},\"data\":{},\"httpResponseCode\":406}"));

    }

    @Test
    public void testMaxWithdrawalPerTransaction() throws Exception {

        AccountTransaction transaction = new AccountTransaction(TransactionType.WITHDRAWAL.getId(), 5000, new Date());
        AccountTransaction transaction2 = new AccountTransaction(TransactionType.WITHDRAWAL.getId(), 7500, new Date());

        List<AccountTransaction> list = new ArrayList<>();
        list.add(transaction);
        list.add(transaction2);

        UserTransaction userTransaction = new UserTransaction(25000);
        Gson gson = new Gson();
        String json = gson.toJson(userTransaction);

        given(this.accountService.findById(1L)).willReturn(Optional.of(new Account(400000)));

        given(this.transactionsService.findByDateBetweenAndType(AccountUtils.getStartOfDay(new Date()),
                AccountUtils.getEndOfDay(new Date()), TransactionType.WITHDRAWAL.getId())).willReturn(list);

        this.mvc.perform(post("/withdrawal/").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotAcceptable()).andExpect(content().json("{\"success\":false,\"messages\":{\"message\":\"Exceeded Maximum Withdrawal Per Transaction 20000\",\"title\":\"Error\"},\"errors\":{},\"data\":{},\"httpResponseCode\":406}"));

    }


    @Test
    public void testSuccessfulWithdrawal() throws Exception {

        AccountTransaction transaction = new AccountTransaction(TransactionType.WITHDRAWAL.getId(), 5000, new Date());
        AccountTransaction transaction2 = new AccountTransaction(TransactionType.WITHDRAWAL.getId(), 7500, new Date());

        List<AccountTransaction> list = new ArrayList<>();
        list.add(transaction);
        list.add(transaction2);

        UserTransaction userTransaction = new UserTransaction(1000);
        Gson gson = new Gson();
        String json = gson.toJson(userTransaction);

        given(this.accountService.findById(1L)).willReturn(Optional.of(new Account(70000)));

        given(this.transactionsService.findByDateBetweenAndType(AccountUtils.getStartOfDay(new Date()),
                AccountUtils.getEndOfDay(new Date()), TransactionType.WITHDRAWAL.getId())).willReturn(list);

        when(this.transactionsService.save(any(AccountTransaction.class))).thenReturn(transaction);
        when(this.accountService.save(any(Account.class))).thenReturn(new Account(400));

        this.mvc.perform(post("/withdrawal/").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(content().json("{\"success\":true,\"messages\":{\"message\":\"Withdrawal sucessfully Transacted\",\"title\":\"\"},\"errors\":{},\"data\":{},\"httpResponseCode\":200}"));

    }

}