package ru.clevertec.cleverbank.dao;

import ru.clevertec.cleverbank.model.Transaction;

public interface TransactionDAO {

    Transaction save(Transaction transaction);

}
