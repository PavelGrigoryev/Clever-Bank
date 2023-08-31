package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.transaction.AmountStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;

public interface CheckService {

     String createChangeBalanceCheck(ChangeBalanceResponse response);

     String createTransferBalanceCheck(TransferBalanceResponse response);

     String createTransactionStatement(TransactionStatementResponse response);

     String createAmountStatement(AmountStatementResponse response);

}
