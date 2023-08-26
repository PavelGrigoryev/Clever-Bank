package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.ChangeBalanceResponse;

public interface BankCheckService {

     String createChangeBalanceCheck(ChangeBalanceResponse response);

}
