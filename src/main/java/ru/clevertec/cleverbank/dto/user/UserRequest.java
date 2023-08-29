package ru.clevertec.cleverbank.dto.user;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import ru.clevertec.cleverbank.dto.adapter.LocalDateAdapter;

import java.time.LocalDate;

public record UserRequest(String lastname,
                          String firstname,
                          String surname,

                          @JsonAdapter(LocalDateAdapter.class)
                          LocalDate birthdate,

                          @SerializedName("mobile_number")
                          String mobileNumber) {
}
