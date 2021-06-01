package com.bank.cashcard.service;

import com.bank.cashcard.entity.AccountTransaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
  @author Akshay Parab
 */
@Repository
public interface TransactionsService extends CrudRepository<AccountTransaction, Long> {

    List<AccountTransaction> findByDateBetweenAndType(Date StartOfDay, Date endOfDay, int type);

}
