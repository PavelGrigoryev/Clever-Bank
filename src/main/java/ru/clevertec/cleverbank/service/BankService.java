package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.bank.BankRequest;
import ru.clevertec.cleverbank.dto.bank.BankResponse;
import ru.clevertec.cleverbank.tables.pojos.Bank;

import java.util.List;

public interface BankService {

    Bank findById(Long id);

    BankResponse findByIdResponse(Long id);

    List<BankResponse> findAll();

    BankResponse save(BankRequest request);

    BankResponse update(Long id, BankRequest request);

    DeleteResponse delete(Long id);

}
