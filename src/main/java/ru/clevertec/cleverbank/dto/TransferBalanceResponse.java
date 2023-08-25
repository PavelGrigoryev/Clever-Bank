package ru.clevertec.cleverbank.dto;

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

                                      @SerializedName("sender_bank_name")
                                      String senderBankName,

                                      @SerializedName("recipient_bank_name")
                                      String recipientBankName,

                                      @SerializedName("sender_account_id")
                                      String senderAccountId,

                                      @SerializedName("recipient_account_id")
                                      String recipientAccountId,

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
