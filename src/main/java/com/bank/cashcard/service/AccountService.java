package com.bank.cashcard.service;

import com.bank.cashcard.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
  @author Akshay Parab
 */
@Repository
public interface AccountService extends CrudRepository<Account, Long> {
}