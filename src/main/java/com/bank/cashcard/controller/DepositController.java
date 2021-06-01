package com.bank.cashcard.controller;

import com.bank.cashcard.constant.Constants;
import com.bank.cashcard.entity.Account;
import com.bank.cashcard.entity.AccountTransaction;
import com.bank.cashcard.entity.TransactionType;
import com.bank.cashcard.entity.UserTransaction;
import com.bank.cashcard.service.AccountService;
import com.bank.cashcard.service.TransactionsService;
import com.bank.cashcard.util.AccountUtils;
import com.bank.cashcard.util.StandardJsonResponse;
import com.bank.cashcard.util.StandardJsonResponseImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 @author Akshay Parab
 */
@RestController
@RequestMapping(value = Constants.DEPOSIT)
@Api(value=Constants.DEPOSIT ,tags= {"Deposit Operations"},produces ="application/json")
public class DepositController {
	private static final Logger logger=Logger.getLogger(DepositController.class);
	@Autowired
    AccountService accountService;

    @Autowired
    TransactionsService transactionsService;
	
    @ApiOperation(value="This API is used to add deposit into account")
    @PostMapping("/")
    public ResponseEntity<?> deposit(@RequestBody UserTransaction userTransaction,@RequestParam(name = "accountId") long accountId) {
        StandardJsonResponse jsonResponse = new StandardJsonResponseImpl();
        HashMap<String, Object> responseData = new HashMap<>();
        logger.debug("Start : deposit");
        try {
            double total = 0;
            // check maximum limit deposit for the day has been reached
            List<AccountTransaction> deposits = transactionsService.findByDateBetweenAndType(AccountUtils.getStartOfDay(new Date()),
                    AccountUtils.getEndOfDay(new Date()), TransactionType.DEPOSIT.getId());

            if (deposits.size() > 0) {
                for (AccountTransaction accountTransaction : deposits) {
                    total += accountTransaction.getAmount();
                }
                if (total + userTransaction.getAmount() > Constants.MAX_DEPOSIT_PER_DAY) {
                    jsonResponse.setSuccess(false, "Error", "Deposit for the day should not be more than " + Constants.MAX_DEPOSIT_PER_DAY);
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(jsonResponse);
                }
            }

            // Check whether the amount being deposited exceeds the MAX_DEPOSIT_PER_TRANSACTION
            if (userTransaction.getAmount() > Constants.MAX_DEPOSIT_PER_TRANSACTION) {
                jsonResponse.setSuccess(false, "Error", "Deposit per transaction should not be more than " +Constants.MAX_DEPOSIT_PER_TRANSACTION);
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(jsonResponse);
            }

            // check whether transactions exceeds the max allowed per day
            if (deposits.size() < Constants.MAX_DEPOSIT_TRANSACTIONS_PER_DAY) {
                AccountTransaction accountTransaction = new AccountTransaction(TransactionType.DEPOSIT.getId(), userTransaction.getAmount(), new Date());
                double amount = transactionsService.save(accountTransaction).getAmount();

                Optional<Account> account = accountService.findById(accountId);
                double newBalance = account.get().getAmount() + amount;
                account.get().setAmount(newBalance);

                Account account1 = account.get();
                accountService.save(account1);

                jsonResponse.setSuccess(true, "", "Deposit sucessfully Transacted");
                jsonResponse.setHttpResponseCode(HttpStatus.OK);

            } else {
                jsonResponse.setSuccess(false, "Error", "maximum transactions for the day Exceeded");
                jsonResponse.setHttpResponseCode(HttpStatus.NOT_ACCEPTABLE);
            }

        } catch (Exception e) {
        	logger.error("Error Occurred --> ", e);
            responseData.put("Error"+ e, accountId);
            jsonResponse.setData(responseData);
            jsonResponse.setSuccess(false, StandardJsonResponse.DEFAULT_MSG_TITLE_VALUE, StandardJsonResponse.DEFAULT_MSG_NAME_VALUE);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonResponse);
        }
        logger.debug("End : deposit");
        return jsonResponse != null ? ResponseEntity.status(HttpStatus.OK).body(jsonResponse)
				: ResponseEntity.status(HttpStatus.NO_CONTENT).body("Data not Found!!");
    }

}
