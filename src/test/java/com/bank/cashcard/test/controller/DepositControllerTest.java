package com.bank.cashcard.test.controller;

import com.google.gson.Gson;
import com.bank.cashcard.controller.DepositController;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
  @author Akshay Parab
 */
@RunWith(SpringRunner.class)
@WebMvcTest(DepositController.class)
public class DepositControllerTest  {
	 @Autowired
	    protected MockMvc mvc;

	    @MockBean
	    protected AccountService accountService;

	    @MockBean
	    protected TransactionsService transactionsService;
	    
    @Test
    public void testMaxDepositForTheDay() throws Exception {
        AccountTransaction transaction = new AccountTransaction(TransactionType.DEPOSIT.getId(), 100000, new Date());
        AccountTransaction transaction2 = new AccountTransaction(TransactionType.DEPOSIT.getId(), 40000, new Date());

        List<AccountTransaction> list = new ArrayList<>();
        list.add(transaction);
        list.add(transaction2);

        UserTransaction userTransaction = new UserTransaction(15000); // 3rd deposit $15K
        Gson gson = new Gson();
        String json = gson.toJson(userTransaction);

        given(this.transactionsService.findByDateBetweenAndType(AccountUtils.getStartOfDay(new Date()),
                AccountUtils.getEndOfDay(new Date()), TransactionType.DEPOSIT.getId())).willReturn(list);
        this.mvc.perform(post("/deposit/").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(content().json("{\"success\":false,\"messages\":{\"message\":\"Deposit for the day should not be more than $150K\",\"title\":\"Error\"},\"errors\":{},\"data\":{},\"httpResponseCode\":406}"));
    }

}