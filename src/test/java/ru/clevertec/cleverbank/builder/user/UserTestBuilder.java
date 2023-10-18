package ru.clevertec.cleverbank.builder.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.model.User;
import ru.clevertec.cleverbank.builder.TestBuilder;

import java.time.LocalDate;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aUser")
@With
public class UserTestBuilder implements TestBuilder<User> {

    private Long id = 1L;
    private String lastname = "Иванов";
    private String firstname = "Иван";
    private String surname = "Иванович";
    private LocalDate registerDate = LocalDate.of(1990, Month.JANUARY, 1);
    private String mobileNumber = "+7 (900) 123-45-67";

    @Override
    public User build() {
        return User.builder()
                .id(id)
                .lastname(lastname)
                .firstname(firstname)
                .surname(surname)
                .registerDate(registerDate)
                .mobileNumber(mobileNumber)
                .build();
    }

}
