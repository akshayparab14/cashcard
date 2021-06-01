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

/**
  @author Akshay Parab
 */
@RestController
@RequestMapping(value = Constants.WITHDRAW)
@Api(value=Constants.WITHDRAW ,tags= {"Withdraw Operations"},produces ="application/json")
public class WithdrawalController {

	private static final Logger logger=Logger.getLogger(WithdrawalController.class);
	@Autowired
    AccountService accountService;

    @Autowired
    TransactionsService transactionsService;
	
    @ApiOperation(value="This API is used to balance from account")

    @PostMapping("/")
    public ResponseEntity<?> withDraw(@RequestBody UserTransaction userTransaction,@RequestParam(name = "accountId") long accountId) {

        StandardJsonResponse jsonResponse = new StandardJsonResponseImpl();
        HashMap<String, Object> responseData = new HashMap<>();
        logger.debug("Start : withDraw");
        try {
            double total = 0;

            // check balance
            double balance = accountService.findById(accountId).get().getAmount();
            if (userTransaction.getAmount() > balance) {
                jsonResponse.setSuccess(false, "Error", "You have insufficient funds");
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(jsonResponse);
            }


            // check maximum limit withdrawal for the day has been reached
            List<AccountTransaction> withdrawals = transactionsService.findByDateBetweenAndType(AccountUtils.getStartOfDay(new Date()),
                    AccountUtils.getEndOfDay(new Date()), TransactionType.WITHDRAWAL.getId());

            if (withdrawals.size() > 0) {
                for (AccountTransaction accountTransaction : withdrawals) {
                    total += accountTransaction.getAmount();
                }
                if (total + userTransaction.getAmount() > Constants.MAX_WITHDRAWAL_PER_DAY) {
                    jsonResponse.setSuccess(false, "Error", "Withdrawal per day should not be more than " + Constants.MAX_WITHDRAWAL_PER_DAY);
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(jsonResponse);
                }
            }

            // Check whether the amount being withdrawn exceeds the MAX_WITHDRAWAL_PER_TRANSACTION
            if (userTransaction.getAmount() > Constants.MAX_WITHDRAWAL_PER_TRANSACTION) {
                jsonResponse.setSuccess(false, "Error", "Exceeded Maximum Withdrawal Per Transaction "+Constants.MAX_WITHDRAWAL_PER_TRANSACTION);
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(jsonResponse);
            }

            // check whether transactions exceeds the max allowed per day
            if (withdrawals.size() < Constants.MAX_WITHDRAWAL_TRANSACTIONS_PER_DAY) {
                AccountTransaction accountTransaction = new AccountTransaction(TransactionType.WITHDRAWAL.getId(), userTransaction.getAmount(), new Date());
                double amount = transactionsService.save(accountTransaction).getAmount();

                Account account = accountService.findById(accountId).get();
                double newBalance = account.getAmount() - amount;
                account.setAmount(newBalance);
                accountService.save(account);

                jsonResponse.setSuccess(true, "", "Withdrawal sucessfully Transacted");
                jsonResponse.setHttpResponseCode(HttpStatus.OK);

            } else {
                jsonResponse.setSuccess(false, "Error", "Maximum Withdrawal transactions for the day Exceeded");
                jsonResponse.setHttpResponseCode(HttpStatus.NOT_ACCEPTABLE);
            }

        } catch (Exception e) {
        	logger.error("Error Occurred --> ", e);
            responseData.put("Error"+ e, accountId);
            jsonResponse.setData(responseData);
            jsonResponse.setSuccess(false, StandardJsonResponse.DEFAULT_MSG_TITLE_VALUE, StandardJsonResponse.DEFAULT_MSG_NAME_VALUE);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonResponse);
        }
        logger.debug("End : withDraw");
        return jsonResponse != null ? ResponseEntity.status(HttpStatus.OK).body(jsonResponse)
				: ResponseEntity.status(HttpStatus.NO_CONTENT).body("Data not Found!!");
    }

}
