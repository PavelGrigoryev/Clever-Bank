package ru.clevertec.cleverbank.builder.transaction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementRequest;
import ru.clevertec.cleverbank.builder.TestBuilder;

import java.time.LocalDate;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aTransactionStatementRequest")
@With
public class TransactionStatementRequestTestBuilder implements TestBuilder<TransactionStatementRequest> {

    private LocalDate from = LocalDate.of(2020, Month.APRIL, 12);
    private LocalDate to = LocalDate.of(2020, Month.MAY, 12);
    private String accountId = "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4";

    @Override
    public TransactionStatementRequest build() {
        return new TransactionStatementRequest(from, to, accountId);
    }

}
