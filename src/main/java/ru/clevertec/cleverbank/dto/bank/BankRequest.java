package ru.clevertec.cleverbank.dto.bank;

import com.google.gson.annotations.SerializedName;

public record BankRequest(String name,
                          String address,

                          @SerializedName("phone_number")
                          String phoneNumber) {
}
