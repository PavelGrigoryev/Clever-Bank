package ru.clevertec.cleverbank.dao;

import ru.clevertec.cleverbank.tables.pojos.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    Optional<User> findById(Long id);

    List<User> findAll();

    User save(User user);

    User update(User user);

    Optional<User> delete(Long id);

}
