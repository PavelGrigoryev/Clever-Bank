package ru.clevertec.cleverbank.dto.transaction;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import ru.clevertec.cleverbank.dto.adapter.LocalDateAdapter;
import ru.clevertec.cleverbank.dto.adapter.LocalTimeAdapter;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record TransactionResponse(Long id,

                                  @JsonAdapter(LocalDateAdapter.class)
                                  LocalDate date,

                                  @JsonAdapter(LocalTimeAdapter.class)
                                  LocalTime time,

                                  Type type,

                                  @SerializedName("bank_sender_id")
                                  Long bankSenderId,

                                  @SerializedName("bank_recipient_id")
                                  Long bankRecipientId,

                                  @SerializedName("account_sender_id")
                                  String accountSenderId,

                                  @SerializedName("account_recipient_id")
                                  String accountRecipientId,

                                  @SerializedName("sum_sender")
                                  BigDecimal sumSender,

                                  @SerializedName("sum_recipient")
                                  BigDecimal sumRecipient) {
}
