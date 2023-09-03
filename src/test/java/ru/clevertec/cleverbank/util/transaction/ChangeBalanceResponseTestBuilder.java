package ru.clevertec.cleverbank.util.transaction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.util.TestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aChangeBalanceResponse")
@With
public class ChangeBalanceResponseTestBuilder implements TestBuilder<ChangeBalanceResponse> {

    private Long transactionId = 1L;
    private LocalDate date = LocalDate.of(2020, Month.APRIL, 12);
    private LocalTime time = LocalTime.of(14, 20, 33);
    private Currency currency = Currency.BYN;
    private Type type = Type.REPLENISHMENT;
    private String bankSenderName = "Клевер-Банк";
    private String bankRecipientName = "Альфа-Банк";
    private String accountRecipientId = "5X92 ISKH ZUAT 2YF5 D0A9 C2Z4 7UIZ";
    private BigDecimal sum = BigDecimal.valueOf(2000);
    private BigDecimal oldBalance = BigDecimal.valueOf(1500);
    private BigDecimal newBalance = sum.add(oldBalance);

    @Override
    public ChangeBalanceResponse build() {
        return new ChangeBalanceResponse(transactionId, date, time, currency, type, bankSenderName, bankRecipientName,
                accountRecipientId, sum, oldBalance, newBalance);
    }

}
