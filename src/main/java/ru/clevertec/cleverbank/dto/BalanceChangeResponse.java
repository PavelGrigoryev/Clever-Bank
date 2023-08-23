package ru.clevertec.cleverbank.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import ru.clevertec.cleverbank.adapter.LocalDateAdapter;
import ru.clevertec.cleverbank.adapter.LocalTimeAdapter;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record BalanceChangeResponse(@SerializedName("transaction_id")
                                    Long transactionId,

                                    @JsonAdapter(LocalDateAdapter.class)
                                    LocalDate date,

                                    @JsonAdapter(LocalTimeAdapter.class)
                                    LocalTime time,

                                    Type type,

                                    @SerializedName("recipient_bank_name")
                                    String recipientBankName,

                                    @SerializedName("recipient_account_id")
                                    String recipientAccountId,

                                    BigDecimal money,

                                    @SerializedName("old_balance")
                                    BigDecimal oldBalance,

                                    @SerializedName("new_balance")
                                    BigDecimal newBalance) {
}
