package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceRequest;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;

import java.sql.SQLException;
import java.util.List;

public interface TransactionService {

    ChangeBalanceResponse changeBalance(ChangeBalanceRequest request);

    TransferBalanceResponse transferBalance(TransferBalanceRequest request) throws SQLException;

    TransactionStatementResponse findAllByPeriodOfDateAndAccountId(TransactionStatementRequest request);

    TransactionResponse findById(Long id);

    List<TransactionResponse> findAllBySendersAccountId(String id);

    List<TransactionResponse> findAllByRecipientAccountId(String id);

}
