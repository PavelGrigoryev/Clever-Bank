package ru.clevertec.cleverbank.dto.transaction;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public record TransferBalanceRequest(@SerializedName("account_sender_id")
                                     String accountSenderId,

                                     @SerializedName("account_recipient_id")
                                     String accountRecipientId,

                                     BigDecimal sum) {
}
