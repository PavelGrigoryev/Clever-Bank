package ru.clevertec.cleverbank.service;

import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.user.UserRequest;
import ru.clevertec.cleverbank.dto.user.UserResponse;
import ru.clevertec.cleverbank.tables.pojos.User;

import java.util.List;

public interface UserService {

    User findById(Long id);

    UserResponse findByIdResponse(Long id);

    List<UserResponse> findAll();

    UserResponse save(UserRequest request);

    UserResponse update(Long id, UserRequest request);

    DeleteResponse delete(Long id);

}
