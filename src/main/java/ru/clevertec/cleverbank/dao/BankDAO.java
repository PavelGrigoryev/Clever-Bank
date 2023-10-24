package ru.clevertec.cleverbank.dao;

import ru.clevertec.cleverbank.model.Bank;

import java.util.List;
import java.util.Optional;

public interface BankDAO {

    Optional<Bank> findById(Long id);

    List<Bank> findAll();

    Optional<Bank> save(Bank bank);

    Optional<Bank> update(Bank bank);

    Optional<Bank> delete(Long id);

}
