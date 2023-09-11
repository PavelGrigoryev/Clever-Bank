package ru.clevertec.cleverbank.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.cleverbank.dto.user.UserRequest;
import ru.clevertec.cleverbank.dto.user.UserResponse;
import ru.clevertec.cleverbank.tables.pojos.User;

import java.util.List;

@Mapper
public interface UserMapper {

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    User fromRequest(UserRequest request);

}
