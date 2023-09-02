package ru.clevertec.cleverbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.cleverbank.dto.account.AccountRequest;
import ru.clevertec.cleverbank.dto.account.AccountResponse;
import ru.clevertec.cleverbank.model.Account;

import java.util.List;

@Mapper
public interface AccountMapper {

    AccountResponse toResponse(Account account);

    List<AccountResponse> toResponseList(List<Account> accounts);

    @Mapping(target = "user.id", source = "bankId")
    @Mapping(target = "bank.id", source = "userId")
    Account fromRequest(AccountRequest request);

}
