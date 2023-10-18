package ru.clevertec.cleverbank.builder.transaction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.builder.TestBuilder;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceRequest;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aTransferBalanceRequest")
@With
public class TransferBalanceRequestTestBuilder implements TestBuilder<TransferBalanceRequest> {

    private String accountSenderId = "5X92 ISKH ZUAT 2YF5 D0A9 C2Z4 7UIZ";
    private String accountRecipientId = "BL7U 2IQC IB7Y 3Q0F ZSSW KZOE YRI6";
    private BigDecimal sum = BigDecimal.valueOf(2000);

    @Override
    public TransferBalanceRequest build() {
        return new TransferBalanceRequest(accountSenderId, accountRecipientId, sum);
    }

}
