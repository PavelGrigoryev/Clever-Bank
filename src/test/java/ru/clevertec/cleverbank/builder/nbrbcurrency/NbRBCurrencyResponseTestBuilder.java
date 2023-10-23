package ru.clevertec.cleverbank.builder.nbrbcurrency;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.builder.TestBuilder;
import ru.clevertec.cleverbank.dto.nbrbcurrency.NbRBCurrencyResponse;
import ru.clevertec.cleverbank.model.Currency;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aNbRBCurrencyResponse")
@With
public class NbRBCurrencyResponseTestBuilder implements TestBuilder<NbRBCurrencyResponse> {

    private Integer currencyId = Currency.EUR.getCode();
    private Currency currency = Currency.EUR;
    private Integer scale = 1;
    private BigDecimal rate = BigDecimal.valueOf(3.4773);

    @Override
    public NbRBCurrencyResponse build() {
        return new NbRBCurrencyResponse(currencyId, currency, scale, rate);
    }

}
