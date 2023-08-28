package ru.clevertec.cleverbank.dao;

import ru.clevertec.cleverbank.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionDAO {

    Optional<Transaction> findById(Long id);

    List<Transaction> findAllBySendersAccountId(String id);

    List<Transaction> findAllByRecipientAccountId(String id);

    Transaction save(Transaction transaction);

}
