package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.nbrbcurrency.NbRBCurrencyResponse;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.NbRBCurrency;

import java.math.BigDecimal;

public interface NbRBCurrencyService {

    NbRBCurrency findByCurrencyId(Integer currencyId);

    NbRBCurrency save(NbRBCurrencyResponse response);

    BigDecimal exchangeSumByCurrency(Currency currencySender, Currency currencyRecipient, BigDecimal sum);

}
