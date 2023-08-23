package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.BalanceChangeRequest;
import ru.clevertec.cleverbank.dto.BalanceChangeResponse;

public interface AccountService {

    BalanceChangeResponse replenish(BalanceChangeRequest request);

    BalanceChangeResponse withdraw(BalanceChangeRequest request);

}
