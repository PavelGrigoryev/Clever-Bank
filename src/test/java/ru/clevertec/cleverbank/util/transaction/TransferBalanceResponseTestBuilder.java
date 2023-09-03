package ru.clevertec.cleverbank.util.transaction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.util.TestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aTransferBalanceResponse")
@With
public class TransferBalanceResponseTestBuilder implements TestBuilder<TransferBalanceResponse> {

    private Long transactionId = 1L;
    private LocalDate date = LocalDate.of(2020, Month.APRIL, 12);
    private LocalTime time = LocalTime.of(14, 20, 33);
    private Currency currency = Currency.BYN;
    private Type type = Type.TRANSFER;
    private String bankSenderName = "Клевер-Банк";
    private String bankRecipientName = "Альфа-Банк";
    private String accountSenderId = "5X92 ISKH ZUAT 2YF5 D0A9 C2Z4 7UIZ";
    private String accountRecipientId = "BL7U 2IQC IB7Y 3Q0F ZSSW KZOE YRI6";
    private BigDecimal sum = BigDecimal.valueOf(2000);
    private BigDecimal senderOldBalance = BigDecimal.valueOf(1500);
    private BigDecimal senderNewBalance = sum.add(senderOldBalance);
    private BigDecimal recipientOldBalance = BigDecimal.valueOf(3000);
    private BigDecimal recipientNewBalance = recipientOldBalance.subtract(sum);

    @Override
    public TransferBalanceResponse build() {
        return new TransferBalanceResponse(transactionId, date, time, currency, type, bankSenderName, bankRecipientName,
                accountSenderId, accountRecipientId, sum, senderOldBalance, senderNewBalance, recipientOldBalance,
                recipientNewBalance);
    }

}
