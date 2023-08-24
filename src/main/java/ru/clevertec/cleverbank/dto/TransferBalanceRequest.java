package ru.clevertec.cleverbank.dto;

import com.google.gson.annotations.SerializedName;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;

public record TransferBalanceRequest(@SerializedName("sender_account_id")
                                     String senderAccountId,

                                     @SerializedName("recipient_account_id")
                                     String recipientAccountId,

                                     BigDecimal sum,
                                     Type type) {
}
