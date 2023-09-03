package ru.clevertec.cleverbank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String lastname;
    private String firstname;
    private String surname;
    private LocalDate registerDate;
    private String mobileNumber;

}
