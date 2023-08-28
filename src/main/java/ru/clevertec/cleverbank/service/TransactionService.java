package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;

import java.sql.SQLException;

public interface TransactionService {

    ChangeBalanceResponse changeBalance(ChangeBalanceRequest request);

    TransferBalanceResponse transferBalance(TransferBalanceRequest request) throws SQLException;

}
