package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.TransferBalanceResponse;

public interface CheckService {

     String createChangeBalanceCheck(ChangeBalanceResponse response);

     String createTransferBalanceCheck(TransferBalanceResponse response);

}
