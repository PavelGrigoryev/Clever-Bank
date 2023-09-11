package ru.clevertec.cleverbank.dao;

import ru.clevertec.cleverbank.tables.pojos.Bank;

import java.util.List;
import java.util.Optional;

public interface BankDAO {

    Optional<Bank> findById(Long id);

    List<Bank> findAll();

    Bank save(Bank bank);

    Bank update(Bank bank);

    Optional<Bank> delete(Long id);

}
