package ru.clevertec.cleverbank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private String id;
    private Currency currency;
    private BigDecimal balance;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private Bank bank;
    private User user;

}
