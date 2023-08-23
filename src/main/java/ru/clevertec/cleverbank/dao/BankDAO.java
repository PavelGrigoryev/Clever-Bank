package ru.clevertec.cleverbank.dao;

import ru.clevertec.cleverbank.model.Bank;

import java.util.Optional;

public interface BankDAO {

    Optional<Bank> findById(Long id);

}
