package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.TransferBalanceRequest;
import ru.clevertec.cleverbank.dto.TransferBalanceResponse;

import java.sql.SQLException;

public interface TransactionService {

    ChangeBalanceResponse changeBalance(ChangeBalanceRequest request);

    TransferBalanceResponse transferBalance(TransferBalanceRequest request) throws SQLException;

}
