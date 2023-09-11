package ru.clevertec.cleverbank.util.account;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.tables.pojos.Account;
import ru.clevertec.cleverbank.util.TestBuilder;

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
    private Long bankId = 2L;
    private Long userId = 6L;

    @Override
    public Account build() {
        return new Account(id, currency.toString(), balance, openingDate, closingDate, bankId, userId);
    }

}
