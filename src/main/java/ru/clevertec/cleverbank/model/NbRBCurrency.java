package ru.clevertec.cleverbank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NbRBCurrency {

    private Long id;
    private Integer currencyId;
    private Currency currency;
    private Integer scale;
    private BigDecimal rate;
    private LocalDateTime updateDate;

}
