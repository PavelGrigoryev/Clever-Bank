package ru.clevertec.cleverbank.builder.nbrbcurrency;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.builder.TestBuilder;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.NbRBCurrency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aNbRBCurrency")
@With
public class NbRBCurrencyTestBuilder implements TestBuilder<NbRBCurrency> {

    private Long id = 1L;
    private Integer currencyId = Currency.EUR.getCode();
    private Currency currency = Currency.EUR;
    private Integer scale = 1;
    private BigDecimal rate = BigDecimal.valueOf(3.4773);
    private LocalDateTime updateDate = LocalDateTime.of(2023, Month.JULY, 30, 6, 11);

    @Override
    public NbRBCurrency build() {
        return NbRBCurrency.builder()
                .id(id)
                .currencyId(currencyId)
                .currency(currency)
                .scale(scale)
                .rate(rate)
                .updateDate(updateDate)
                .build();
    }

}
