package ru.clevertec.cleverbank.dao;

import ru.clevertec.cleverbank.model.User;

import java.util.Optional;

public interface UserDAO {

    Optional<User> findById(Long id);

}
