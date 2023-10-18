package ru.clevertec.cleverbank.builder.account;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.dto.account.AccountResponse;
import ru.clevertec.cleverbank.dto.bank.BankResponse;
import ru.clevertec.cleverbank.dto.user.UserResponse;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.builder.TestBuilder;
import ru.clevertec.cleverbank.builder.bank.BankResponseTestBuilder;
import ru.clevertec.cleverbank.builder.user.UserResponseTestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aAccountResponse")
@With
public class AccountResponseTestBuilder implements TestBuilder<AccountResponse> {

    private String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";
    private Currency currency = Currency.BYN;
    private BigDecimal balance = BigDecimal.valueOf(5000.00);
    private LocalDate openingDate = LocalDate.of(2020, Month.MARCH, 1);
    private LocalDate closingDate = null;
    private BankResponse bank = BankResponseTestBuilder.aBankResponse().build();
    private UserResponse user = UserResponseTestBuilder.aUserResponse().build();

    @Override
    public AccountResponse build() {
        return new AccountResponse(id, currency, balance, openingDate, closingDate, bank, user);
    }

}
