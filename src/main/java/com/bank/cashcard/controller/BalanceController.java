package com.bank.cashcard.controller;

import com.bank.cashcard.constant.Constants;
import com.bank.cashcard.entity.Account;
import com.bank.cashcard.service.AccountService;
import com.bank.cashcard.service.TransactionsService;
import com.bank.cashcard.util.StandardJsonResponse;
import com.bank.cashcard.util.StandardJsonResponseImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Optional;

/**
 @author Akshay Parab
 */
@RestController
@RequestMapping(value = Constants.BALANCE)
@Api(value=Constants.BALANCE ,tags= {"Balance Operations"},produces ="application/json")
public class BalanceController {
	private static final Logger logger=Logger.getLogger(BalanceController.class);
	@Autowired
    AccountService accountService;

    @Autowired
    TransactionsService transactionsService;
	
    @ApiOperation(value="This API is used to balance from account")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<?> getBalance(@RequestParam(name = "accountId") long accountId) {

        StandardJsonResponse jsonResponse = new StandardJsonResponseImpl();
        HashMap<String, Object> responseData = new HashMap<>();
        logger.debug("Start : getBalance");
        try {
            Optional<Account> account = Optional.of(accountService.findById(accountId).get());

            if (account.isPresent()) {
            	responseData.put("balance",  account.get().getAmount());
            	responseData.put("accountID",  accountId);
                jsonResponse.setSuccess(true,  "", "Balance Retrived");
                jsonResponse.setData(responseData);
            } else {
                jsonResponse.setSuccess(false, "Resource not found", StandardJsonResponse.RESOURCE_NOT_FOUND_MSG);
                jsonResponse.setHttpResponseCode(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error Occurred --> ", e);
            responseData.put("Error"+ e, accountId);
            jsonResponse.setData(responseData);
            jsonResponse.setSuccess(false, StandardJsonResponse.DEFAULT_MSG_TITLE_VALUE, StandardJsonResponse.DEFAULT_MSG_NAME_VALUE);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonResponse);
        }
        logger.debug("End : getBalance");
        return jsonResponse != null ? ResponseEntity.status(HttpStatus.OK).body(jsonResponse)
				: ResponseEntity.status(HttpStatus.NO_CONTENT).body("Data not Found!!");
    }

}
