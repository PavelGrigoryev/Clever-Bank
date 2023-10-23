package ru.clevertec.cleverbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.cleverbank.dto.user.UserRequest;
import ru.clevertec.cleverbank.dto.user.UserResponse;
import ru.clevertec.cleverbank.model.User;

import java.time.LocalDate;
import java.util.List;

@Mapper(imports = LocalDate.class)
public interface UserMapper {

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    @Mapping(target = "registerDate", expression = "java(LocalDate.now())")
    User fromSaveRequest(UserRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "registerDate", source = "registerDate")
    User fromUpdateRequest(UserRequest request, Long id, LocalDate registerDate);

}
