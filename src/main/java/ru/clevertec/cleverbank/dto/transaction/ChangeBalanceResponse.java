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

public record ChangeBalanceResponse(@SerializedName("transaction_id")
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

                                    @SerializedName("account_recipient_id")
                                    String accountRecipientId,

                                    BigDecimal sum,

                                    @SerializedName("old_balance")
                                    BigDecimal oldBalance,

                                    @SerializedName("new_balance")
                                    BigDecimal newBalance) {
}
