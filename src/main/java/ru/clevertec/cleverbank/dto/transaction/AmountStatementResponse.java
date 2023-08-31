package ru.clevertec.cleverbank.dto.transaction;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import ru.clevertec.cleverbank.dto.adapter.LocalDateAdapter;
import ru.clevertec.cleverbank.dto.adapter.LocalTimeAdapter;
import ru.clevertec.cleverbank.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record AmountStatementResponse(@SerializedName("bank_name")
                                      String bankName,

                                      String lastname,
                                      String firstname,
                                      String surname,

                                      @SerializedName("account_id")
                                      String accountId,

                                      Currency currency,

                                      @SerializedName("opening_date")
                                      @JsonAdapter(LocalDateAdapter.class)
                                      LocalDate openingDate,

                                      @JsonAdapter(LocalDateAdapter.class)
                                      LocalDate from,

                                      @JsonAdapter(LocalDateAdapter.class)
                                      LocalDate to,

                                      @SerializedName("formation_date")
                                      @JsonAdapter(LocalDateAdapter.class)
                                      LocalDate formationDate,

                                      @SerializedName("formation_time")
                                      @JsonAdapter(LocalTimeAdapter.class)
                                      LocalTime formationTime,

                                      BigDecimal balance,

                                      @SerializedName("spent_funds")
                                      BigDecimal spentFunds,

                                      @SerializedName("received_funds")
                                      BigDecimal receivedFunds) {
}
