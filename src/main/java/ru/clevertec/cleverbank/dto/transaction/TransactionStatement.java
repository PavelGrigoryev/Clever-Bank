package ru.clevertec.cleverbank.dto.transaction;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import ru.clevertec.cleverbank.dto.adapter.LocalDateAdapter;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionStatement(@JsonAdapter(LocalDateAdapter.class)
                                   LocalDate date,

                                   Type type,

                                   @SerializedName("user_lastname")
                                   String userLastname,

                                   @SerializedName("sum_sender")
                                   BigDecimal sumSender,

                                   @SerializedName("sum_recipient")
                                   BigDecimal sumRecipient) {
}
