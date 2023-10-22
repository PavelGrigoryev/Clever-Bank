package ru.clevertec.cleverbank.dto.transaction;

import com.google.gson.annotations.SerializedName;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;

public record TransactionRequest(@SerializedName("account_sender_id")
                                 String accountSenderId,

                                 @SerializedName("account_recipient_id")
                                 String accountRecipientId,

                                 BigDecimal sum,
                                 Type type) {
}
