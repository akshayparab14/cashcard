package com.bank.cashcard.test.controller;

import com.bank.cashcard.constant.Constants;
import com.bank.cashcard.controller.BalanceController;
import com.bank.cashcard.entity.Account;
import com.bank.cashcard.service.AccountService;
import com.bank.cashcard.service.TransactionsService;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
  @author Akshay Parab
 */
@RunWith(SpringRunner.class)
@WebMvcTest(BalanceController.class)
public class BalanceControllerTests {

	 @Autowired
	    protected MockMvc mvc;

	    @MockBean
	    protected AccountService accountService;

	    @MockBean
	    protected TransactionsService transactionsService;

    @Test
    public void testGetBalanceSuccess() throws Exception {
    	long accountId = 123;
        given(this.accountService.findById(accountId))
                .willReturn(Optional.of(new Account(400)));
        this.mvc.perform(get("/balance/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().json("{\"success\":true,\"messages\":{},\"errors\":{},\"data\":{\"balance\":\"400\"},\"httpResponseCode\":200}"));
    }
    @Test
    public void testGetBalanceAccountFailure() throws Exception {
    	long accountId = 67890;
        given(this.accountService.findById(accountId))
                .willReturn(Optional.of(new Account(12345)));
        this.mvc.perform(get("/balance/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()).andExpect(content().json("{\"success\":false,\"messages\":{\"message\":\"Resource not found The resource requested is not found. Please check your resource ID.\",\"title\":\"Internal Server Error\"},\"errors\":{},\"data\":{\"balance\":\"Errorjava.util.NoSuchElementException: No value present\"},\"httpResponseCode\":204}"));
    }
    
    @Test
    public void testGetBalanceFailure() throws Exception {
    	long accountId = 123;
        given(this.accountService.findById(accountId))
                .willReturn(Optional.of(new Account(4000)));
        this.mvc.perform(get("/balance/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().json("{\"success\":false,\"messages\":{\"message\":\"The server encountered an unexpected condition which prevented it from fulfilling the request.\",\"title\":\"Internal Server Error\"},\"errors\":{},\"data\":{\"balance\":\"Errorjava.util.NoSuchElementException: No value present\"},\"httpResponseCode\":500}"));
    }

}