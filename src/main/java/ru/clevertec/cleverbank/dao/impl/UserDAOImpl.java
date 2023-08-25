package ru.clevertec.cleverbank.dao.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.config.ConnectionManager;
import ru.clevertec.cleverbank.dao.UserDAO;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
public class UserDAOImpl implements UserDAO {

    private final Connection connection;

    public UserDAOImpl() {
        connection = ConnectionManager.getJDBCConnection();
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Optional<User> user = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = Optional.of(getUserFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return user;
    }

    private User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .lastname(resultSet.getString("lastname"))
                .firstname(resultSet.getString("firstname"))
                .surname(resultSet.getString("surname"))
                .birthdate(resultSet.getDate("birthdate").toLocalDate())
                .mobileNumber(resultSet.getString("mobile_number"))
                .build();
    }

    private void setUserValuesInStatement(PreparedStatement preparedStatement, User user) throws SQLException {
        preparedStatement.setString(1, user.getLastname());
        preparedStatement.setString(2, user.getFirstname());
        preparedStatement.setString(3, user.getSurname());
        preparedStatement.setObject(4, user.getBirthdate());
        preparedStatement.setString(5, user.getMobileNumber());
    }

}
