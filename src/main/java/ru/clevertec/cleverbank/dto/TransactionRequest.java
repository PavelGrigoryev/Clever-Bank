package ru.clevertec.cleverbank.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public record TransactionRequest(@SerializedName("account_recipient_id")
                                 String accountRecipientId,

                                 BigDecimal money) {
}
