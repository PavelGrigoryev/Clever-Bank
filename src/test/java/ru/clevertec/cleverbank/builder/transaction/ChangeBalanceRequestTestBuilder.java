package ru.clevertec.cleverbank.builder.transaction;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceRequest;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.builder.TestBuilder;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aChangeBalanceRequest")
@With
public class ChangeBalanceRequestTestBuilder implements TestBuilder<ChangeBalanceRequest> {

    private String accountSenderId = "5X92 ISKH ZUAT 2YF5 D0A9 C2Z4 7UIZ";
    private String accountRecipientId = "55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9";
    private BigDecimal sum = BigDecimal.valueOf(2000);
    private Type type = Type.REPLENISHMENT;

    @Override
    public ChangeBalanceRequest build() {
        return new ChangeBalanceRequest(accountSenderId, accountRecipientId, sum, type);
    }

}
