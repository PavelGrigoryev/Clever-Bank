package ru.clevertec.cleverbank.builder.transaction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.builder.TestBuilder;
import ru.clevertec.cleverbank.dto.transaction.TransactionRequest;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aTransactionRequest")
@With
public class TransactionRequestTestBuilder implements TestBuilder<TransactionRequest> {

    private String accountSenderId = "5X92 ISKH ZUAT 2YF5 D0A9 C2Z4 7UIZ";
    private String accountRecipientId = "BL7U 2IQC IB7Y 3Q0F ZSSW KZOE YRI6";
    private BigDecimal sum = BigDecimal.valueOf(2000);
    private Type type = Type.REPLENISHMENT;

    @Override
    public TransactionRequest build() {
        return new TransactionRequest(accountSenderId, accountRecipientId, sum, type);
    }

}
