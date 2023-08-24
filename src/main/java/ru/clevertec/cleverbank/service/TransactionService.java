package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.TransactionRequest;
import ru.clevertec.cleverbank.dto.TransactionResponse;

public interface TransactionService {

    TransactionResponse changeBalance(TransactionRequest request);

}
