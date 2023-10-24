package ru.clevertec.cleverbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.cleverbank.dto.account.AccountRequest;
import ru.clevertec.cleverbank.dto.account.AccountResponse;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper(imports = {LocalDate.class, BigDecimal.class})
public interface AccountMapper {

    AccountResponse toResponse(Account account);

    List<AccountResponse> toResponseList(List<Account> accounts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "openingDate", expression = "java(LocalDate.now())")
    Account fromSaveRequest(AccountRequest request, User user, Bank bank);

    @Mapping(target = "closingDate", expression = "java(LocalDate.now())")
    @Mapping(target = "balance", expression = "java(BigDecimal.ZERO)")
    Account fromCloseRequest(Account account);

}
