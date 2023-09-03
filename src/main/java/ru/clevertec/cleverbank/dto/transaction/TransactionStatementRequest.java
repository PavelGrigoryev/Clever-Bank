package ru.clevertec.cleverbank.dto.transaction;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import ru.clevertec.cleverbank.dto.adapter.LocalDateAdapter;

import java.time.LocalDate;

public record TransactionStatementRequest(@JsonAdapter(LocalDateAdapter.class)
                                          LocalDate from,

                                          @JsonAdapter(LocalDateAdapter.class)
                                          LocalDate to,

                                          @SerializedName("account_id")
                                          String accountId) {
}
