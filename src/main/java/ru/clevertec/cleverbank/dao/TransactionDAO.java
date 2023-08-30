package ru.clevertec.cleverbank.dao;

import ru.clevertec.cleverbank.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionDAO {

    Optional<Transaction> findById(Long id);

    List<Transaction> findAllBySendersAccountId(String id);

    List<Transaction> findAllByRecipientAccountId(String id);

    Transaction save(Transaction transaction);

    List<Transaction> findAllByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id);

    BigDecimal findSumOfSpentFundsByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id);

    BigDecimal findSumOfReceivedFundsByPeriodOfDateAndAccountId(LocalDate from, LocalDate to, String id);

}
