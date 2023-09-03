package ru.clevertec.cleverbank.util.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.dto.user.UserRequest;
import ru.clevertec.cleverbank.util.TestBuilder;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aUserRequest")
@With
public class UserRequestTestBuilder implements TestBuilder<UserRequest> {

    private String lastname = "Иванов";
    private String firstname = "Иван";
    private String surname = "Иванович";
    private String mobileNumber = "+7 (900) 123-45-67";

    @Override
    public UserRequest build() {
        return new UserRequest(lastname, firstname, surname, mobileNumber);
    }

}
