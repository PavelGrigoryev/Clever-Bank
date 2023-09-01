package ru.clevertec.cleverbank.dto.transaction;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import ru.clevertec.cleverbank.dto.adapter.LocalDateAdapter;
import ru.clevertec.cleverbank.dto.adapter.LocalTimeAdapter;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record TransferBalanceResponse(@SerializedName("transaction_id")
                                      Long transactionId,

                                      @JsonAdapter(LocalDateAdapter.class)
                                      LocalDate date,

                                      @JsonAdapter(LocalTimeAdapter.class)
                                      LocalTime time,

                                      Currency currency,
                                      Type type,

                                      @SerializedName("bank_sender_name")
                                      String bankSenderName,

                                      @SerializedName("bank_recipient_name")
                                      String bankRecipientName,

                                      @SerializedName("account_sender_id")
                                      String accountSenderId,

                                      @SerializedName("account_recipient_id")
                                      String accountRecipientId,

                                      BigDecimal sum,

                                      @SerializedName("sender_old_balance")
                                      BigDecimal senderOldBalance,

                                      @SerializedName("sender_new_balance")
                                      BigDecimal senderNewBalance,

                                      @SerializedName("recipient_old_balance")
                                      BigDecimal recipientOldBalance,

                                      @SerializedName("recipient_new_balance")
                                      BigDecimal recipientNewBalance) {
}
