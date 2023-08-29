package ru.clevertec.cleverbank.service.impl;

import org.mapstruct.factory.Mappers;
import ru.clevertec.cleverbank.dao.UserDAO;
import ru.clevertec.cleverbank.dao.impl.UserDAOImpl;
import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.user.UserRequest;
import ru.clevertec.cleverbank.dto.user.UserResponse;
import ru.clevertec.cleverbank.exception.badrequest.UniquePhoneNumberException;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.exception.notfound.UserNotFoundException;
import ru.clevertec.cleverbank.mapper.UserMapper;
import ru.clevertec.cleverbank.model.User;
import ru.clevertec.cleverbank.service.UserService;

import java.time.LocalDate;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final UserMapper userMapper;

    public UserServiceImpl() {
        userDAO = new UserDAOImpl();
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Override
    public User findById(Long id) {
        return userDAO.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " is not found!"));
    }

    @Override
    public UserResponse findByIdResponse(Long id) {
        return userMapper.toResponse(findById(id));
    }

    @Override
    public List<UserResponse> findAll() {
        return userMapper.toResponseList(userDAO.findAll());
    }

    @Override
    public UserResponse save(UserRequest request) {
        User user = userMapper.fromRequest(request);
        user.setRegisterDate(LocalDate.now());
        User savedUser;
        try {
            savedUser = userDAO.save(user);
        } catch (JDBCConnectionException e) {
            throw new UniquePhoneNumberException("User with phone number " + request.mobileNumber() + " is already exist");
        }
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        User userById = findById(id);
        User user = userMapper.fromRequest(request);
        user.setId(userById.getId());
        user.setRegisterDate(userById.getRegisterDate());
        User updatedUser = userDAO.update(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public DeleteResponse delete(Long id) {
        return userDAO.delete(id)
                .map(user -> new DeleteResponse("User with ID " + id + " was successfully deleted"))
                .orElseThrow(() -> new UserNotFoundException("No User with ID " + id + " to delete"));
    }

}
