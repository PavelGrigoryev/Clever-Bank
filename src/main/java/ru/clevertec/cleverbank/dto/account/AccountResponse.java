package ru.clevertec.cleverbank.dto.account;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import ru.clevertec.cleverbank.dto.adapter.LocalDateAdapter;
import ru.clevertec.cleverbank.dto.bank.BankResponse;
import ru.clevertec.cleverbank.dto.user.UserResponse;
import ru.clevertec.cleverbank.model.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountResponse(String id,
                              Currency currency,
                              BigDecimal balance,

                              @JsonAdapter(LocalDateAdapter.class)
                              @SerializedName("opening_date")
                              LocalDate openingDate,

                              @JsonAdapter(LocalDateAdapter.class)
                              @SerializedName("closing_date")
                              LocalDate closingDate,

                              BankResponse bank,

                              UserResponse user) {
}
