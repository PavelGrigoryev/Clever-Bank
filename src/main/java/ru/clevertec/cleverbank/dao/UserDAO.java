package ru.clevertec.cleverbank.dao;

import ru.clevertec.cleverbank.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    Optional<User> findById(Long id);

    List<User> findAll();

    Optional<User> save(User user);

    Optional<User> update(User user);

    Optional<User> delete(Long id);

}
