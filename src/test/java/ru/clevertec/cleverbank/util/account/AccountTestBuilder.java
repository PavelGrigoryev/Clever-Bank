package ru.clevertec.cleverbank.util.account;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.User;
import ru.clevertec.cleverbank.util.TestBuilder;
import ru.clevertec.cleverbank.util.bank.BankTestBuilder;
import ru.clevertec.cleverbank.util.user.UserTestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aAccount")
@With
public class AccountTestBuilder implements TestBuilder<Account> {

    private String id = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";
    private Currency currency = Currency.BYN;
    private BigDecimal balance = BigDecimal.valueOf(5000.00);
    private LocalDate openingDate = LocalDate.of(2020, Month.MARCH, 1);
    private LocalDate closingDate = null;
    private Bank bank = BankTestBuilder.aBank().build();
    private User user = UserTestBuilder.aUser().build();

    @Override
    public Account build() {
        return Account.builder()
                .id(id)
                .currency(currency)
                .balance(balance)
                .openingDate(openingDate)
                .closingDate(closingDate)
                .bank(bank)
                .user(user)
                .build();
    }

}
