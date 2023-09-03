package ru.clevertec.cleverbank.util.transaction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatement;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.util.TestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aTransactionStatementResponse")
@With
public class TransactionStatementResponseTestBuilder implements TestBuilder<TransactionStatementResponse> {

    private String bankName = "Клевер-Банк";
    private String lastname = "Иванов";
    private String firstname = "Иван";
    private String surname = "Иванович";
    private String accountId = "5X92 ISKH ZUAT 2YF5 D0A9 C2Z4 7UIZ";
    private Currency currency = Currency.BYN;
    private LocalDate openingDate = LocalDate.of(2020, Month.MARCH, 1);
    private LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
    private LocalDate to = LocalDate.of(2020, Month.MAY, 12);
    private LocalDate formationDate = LocalDate.of(2023, Month.SEPTEMBER, 2);
    private LocalTime formationTime = LocalTime.of(15, 30, 45);
    private BigDecimal balance = BigDecimal.valueOf(3000);
    private List<TransactionStatement> transactions = List.of(TransactionStatementTestBuilder.aTransactionStatement().build());

    @Override
    public TransactionStatementResponse build() {
        return new TransactionStatementResponse(bankName, lastname, firstname, surname, accountId, currency, openingDate,
                from, to, formationDate, formationTime, balance, transactions);
    }

}
