package ru.clevertec.cleverbank.dto.user;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import ru.clevertec.cleverbank.dto.adapter.LocalDateAdapter;

import java.time.LocalDate;

public record UserResponse(Long id,
                           String lastname,
                           String firstname,
                           String surname,

                           @JsonAdapter(LocalDateAdapter.class)
                           @SerializedName("register_date")
                           LocalDate registerDate,

                           @SerializedName("mobile_number")
                           String mobileNumber) {
}
