package ru.clevertec.cleverbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.cleverbank.dto.bank.BankRequest;
import ru.clevertec.cleverbank.dto.bank.BankResponse;
import ru.clevertec.cleverbank.model.Bank;

import java.util.List;

@Mapper
public interface BankMapper {

    BankResponse toResponse(Bank bank);

    List<BankResponse> toResponseList(List<Bank> banks);

    Bank fromSaveRequest(BankRequest request);

    @Mapping(target = "id", source = "id")
    Bank fromUpdateRequest(BankRequest request, Long id);

}
