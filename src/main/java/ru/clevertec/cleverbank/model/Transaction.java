package ru.clevertec.cleverbank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private Long id;
    private LocalDate date;
    private LocalTime time;
    private Type type;
    private Long bankSenderId;
    private Long bankRecipientId;
    private String accountSenderId;
    private String accountRecipientId;
    private BigDecimal sum;

}
