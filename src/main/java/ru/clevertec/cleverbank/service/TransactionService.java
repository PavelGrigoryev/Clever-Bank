package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.transaction.AmountStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.ExchangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;

import java.sql.SQLException;
import java.util.List;

public interface TransactionService {

    ChangeBalanceResponse changeBalance(TransactionRequest request);

    TransferBalanceResponse transferBalance(TransactionRequest request) throws SQLException;

    ExchangeBalanceResponse exchangeBalance(TransactionRequest request) throws SQLException;

    TransactionStatementResponse findAllByPeriodOfDateAndAccountId(TransactionStatementRequest request);

    AmountStatementResponse findSumOfFundsByPeriodOfDateAndAccountId(TransactionStatementRequest request);

    TransactionResponse findById(Long id);

    List<TransactionResponse> findAllBySendersAccountId(String id);

    List<TransactionResponse> findAllByRecipientAccountId(String id);

}
