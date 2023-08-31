package ru.clevertec.cleverbank.service.impl;

import org.mapstruct.factory.Mappers;
import ru.clevertec.cleverbank.aspect.annotation.ServiceLoggable;
import ru.clevertec.cleverbank.dao.BankDAO;
import ru.clevertec.cleverbank.dao.impl.BankDAOImpl;
import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.bank.BankRequest;
import ru.clevertec.cleverbank.dto.bank.BankResponse;
import ru.clevertec.cleverbank.exception.badrequest.UniquePhoneNumberException;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.exception.notfound.BankNotFoundException;
import ru.clevertec.cleverbank.mapper.BankMapper;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.service.BankService;

import java.util.List;

public class BankServiceImpl implements BankService {

    private final BankDAO bankDAO;
    private final BankMapper bankMapper;

    public BankServiceImpl() {
        bankDAO = new BankDAOImpl();
        bankMapper = Mappers.getMapper(BankMapper.class);

    }

    @Override
    public Bank findById(Long id) {
        return bankDAO.findById(id)
                .orElseThrow(() -> new BankNotFoundException("Bank with ID " + id + " is not found!"));
    }

    @Override
    @ServiceLoggable
    public BankResponse findByIdResponse(Long id) {
        return bankMapper.toResponse(findById(id));
    }

    @Override
    public List<BankResponse> findAll() {
        return bankMapper.toResponseList(bankDAO.findAll());
    }

    @Override
    @ServiceLoggable
    public BankResponse save(BankRequest request) {
        Bank bank = bankMapper.fromRequest(request);
        Bank savedBank;
        try {
            savedBank = bankDAO.save(bank);
        } catch (JDBCConnectionException e) {
            throw new UniquePhoneNumberException("Bank with phone number " + request.phoneNumber() + " is already exist");
        }
        return bankMapper.toResponse(savedBank);
    }

    @Override
    @ServiceLoggable
    public BankResponse update(Long id, BankRequest request) {
        findById(id);
        Bank bank = bankMapper.fromRequest(request);
        bank.setId(id);
        Bank updatedBank = bankDAO.update(bank);
        return bankMapper.toResponse(updatedBank);
    }

    @Override
    @ServiceLoggable
    public DeleteResponse delete(Long id) {
        return bankDAO.delete(id)
                .map(bank -> new DeleteResponse("Bank with ID " + id + " was successfully deleted"))
                .orElseThrow(() -> new BankNotFoundException("No Bank with ID " + id + " to delete"));
    }

}
