package ru.clevertec.cleverbank.builder.transaction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatement;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.builder.TestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aTransactionStatement")
@With
public class TransactionStatementTestBuilder implements TestBuilder<TransactionStatement> {

    private LocalDate date = LocalDate.of(2020, Month.APRIL, 12);
    private Type type = Type.TRANSFER;
    private String userLastname = "Иванов";
    private BigDecimal sumSender = BigDecimal.valueOf(2000);
    private BigDecimal sumRecipient = BigDecimal.valueOf(2000);

    @Override
    public TransactionStatement build() {
        return new TransactionStatement(date, type, userLastname, sumSender, sumRecipient);
    }

}
