package ru.clevertec.cleverbank.dto.transaction;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public record TransferBalanceRequest(@SerializedName("sender_account_id")
                                     String senderAccountId,

                                     @SerializedName("recipient_account_id")
                                     String recipientAccountId,

                                     BigDecimal sum) {
}
