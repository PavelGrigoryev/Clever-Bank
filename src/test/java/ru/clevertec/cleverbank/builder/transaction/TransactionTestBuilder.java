package ru.clevertec.cleverbank.builder.transaction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.builder.TestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aTransaction")
@With
public class TransactionTestBuilder implements TestBuilder<Transaction> {

    private Long id = 1L;
    private LocalDate date = LocalDate.of(2020, Month.APRIL, 12);
    private LocalTime time = LocalTime.of(14, 20, 33);
    private Type type = Type.REPLENISHMENT;
    private Long bankSenderId = 11L;
    private Long bankRecipientId = 4L;
    private String accountSenderId = "5X92 ISKH ZUAT 2YF5 D0A9 C2Z4 7UIZ";
    private String accountRecipientId = "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9";
    private BigDecimal sumSender = BigDecimal.valueOf(2000);
    private BigDecimal sumRecipient = BigDecimal.valueOf(2000);

    @Override
    public Transaction build() {
        return Transaction.builder()
                .id(id)
                .date(date)
                .time(time)
                .type(type)
                .bankSenderId(bankSenderId)
                .bankRecipientId(bankRecipientId)
                .accountSenderId(accountSenderId)
                .accountRecipientId(accountRecipientId)
                .sumSender(sumSender)
                .sumRecipient(sumRecipient)
                .build();
    }

}
