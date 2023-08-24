package ru.clevertec.cleverbank.service.impl;

import ru.clevertec.cleverbank.dao.BankDAO;
import ru.clevertec.cleverbank.dao.impl.BankDAOImpl;
import ru.clevertec.cleverbank.exception.BankNotFoundException;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.service.BankService;

public class BankServiceImpl implements BankService {

    private final BankDAO bankDAO;

    public BankServiceImpl() {
        bankDAO = new BankDAOImpl();
    }

    @Override
    public Bank findById(Long id) {
        return bankDAO.findById(id)
                .orElseThrow(() -> new BankNotFoundException("Bank with ID " + id + " is not found!"));
    }

}
