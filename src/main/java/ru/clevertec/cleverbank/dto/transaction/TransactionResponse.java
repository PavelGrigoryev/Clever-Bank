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

                                  @SerializedName("senders_bank")
                                  String sendersBank,

                                  @SerializedName("recipients_bank")
                                  String recipientsBank,

                                  @SerializedName("senders_account")
                                  String sendersAccount,

                                  @SerializedName("recipients_account")
                                  String recipientsAccount,

                                  BigDecimal sum) {
}
