package ru.clevertec.cleverbank.builder.transaction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.builder.TestBuilder;
import ru.clevertec.cleverbank.dto.transaction.ExchangeBalanceResponse;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aExchangeBalanceResponse")
@With
public class ExchangeBalanceResponseTestBuilder implements TestBuilder<ExchangeBalanceResponse> {

    private Long transactionId = 1L;
    private LocalDate date = LocalDate.of(2020, Month.APRIL, 12);
    private LocalTime time = LocalTime.of(14, 20, 33);
    private Currency currencySender = Currency.BYN;
    private Currency currencyRecipient = Currency.EUR;
    private Type type = Type.EXCHANGE;
    private String bankSenderName = "Клевер-Банк";
    private String bankRecipientName= "Альфа-Банк";
    private String accountSenderId = "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q";
    private String accountRecipientId = "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62";
    private BigDecimal sumSender = BigDecimal.TEN;
    private BigDecimal sumRecipient = BigDecimal.valueOf(2.89);
    private BigDecimal senderOldBalance = BigDecimal.valueOf(1500);
    private BigDecimal senderNewBalance = senderOldBalance.subtract(sumSender);
    private BigDecimal recipientOldBalance = BigDecimal.valueOf(3000);
    private BigDecimal recipientNewBalance = recipientOldBalance.add(sumRecipient);

    @Override
    public ExchangeBalanceResponse build() {
        return new ExchangeBalanceResponse(transactionId, date, time, currencySender, currencyRecipient, type,
                bankSenderName, bankRecipientName, accountSenderId, accountRecipientId, sumSender, sumRecipient,
                senderOldBalance, senderNewBalance, recipientOldBalance, recipientNewBalance);
    }

}
