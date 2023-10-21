package ru.clevertec.cleverbank.dao;

import ru.clevertec.cleverbank.model.NbRBCurrency;

import java.util.Optional;

public interface NbRBCurrencyDAO {

    Optional<NbRBCurrency> findByCurrencyId(Integer currencyId);

    NbRBCurrency save(NbRBCurrency nbRBCurrency);

}
