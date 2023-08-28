package ru.clevertec.cleverbank.service.impl;

import ru.clevertec.cleverbank.dao.UserDAO;
import ru.clevertec.cleverbank.dao.impl.UserDAOImpl;
import ru.clevertec.cleverbank.exception.notfound.UserNotFoundException;
import ru.clevertec.cleverbank.model.User;
import ru.clevertec.cleverbank.service.UserService;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    public UserServiceImpl() {
        userDAO = new UserDAOImpl();
    }

    @Override
    public User findById(Long id) {
        return userDAO.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " is not found!"));
    }

}
