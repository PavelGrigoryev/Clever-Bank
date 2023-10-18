package ru.clevertec.cleverbank.builder.account;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.dto.account.AccountRequest;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.builder.TestBuilder;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aAccountRequest")
@With
public class AccountRequestTestBuilder implements TestBuilder<AccountRequest> {

    private Currency currency = Currency.BYN;
    private BigDecimal balance = BigDecimal.valueOf(5000.00);
    private Long bankId = 1L;
    private Long userId = 1L;

    @Override
    public AccountRequest build() {
        return new AccountRequest(currency, balance, bankId, userId);
    }

}
