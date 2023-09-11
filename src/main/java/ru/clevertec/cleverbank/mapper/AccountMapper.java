package ru.clevertec.cleverbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.cleverbank.dto.account.AccountRequest;
import ru.clevertec.cleverbank.dto.account.AccountResponse;
import ru.clevertec.cleverbank.model.AccountData;
import ru.clevertec.cleverbank.tables.pojos.Account;

import java.util.List;

@Mapper
public interface AccountMapper {

    AccountResponse toResponse(AccountData accountData);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bankId", source = "bank.id")
    Account fromAccountData(AccountData accountData);

    List<AccountResponse> toResponseList(List<AccountData> accounts);

    Account fromRequest(AccountRequest request);

}
