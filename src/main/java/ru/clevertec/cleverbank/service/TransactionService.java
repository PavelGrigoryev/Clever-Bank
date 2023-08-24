package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.ChangeBalanceRequest;
import ru.clevertec.cleverbank.dto.TransactionResponse;
import ru.clevertec.cleverbank.dto.TransferBalanceRequest;

public interface TransactionService {

    TransactionResponse changeBalance(ChangeBalanceRequest request);

    TransactionResponse transferBalance(TransferBalanceRequest request);

}
