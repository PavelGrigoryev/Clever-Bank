package ru.clevertec.cleverbank.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.dao.UserDAO;
import ru.clevertec.cleverbank.model.User;
import ru.clevertec.cleverbank.util.HikariConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class UserDAOImpl implements UserDAO {

    private final Connection connection;

    public UserDAOImpl() {
        connection = HikariConnectionManager.getConnection();
    }

    /**
     * Находит пользователя по его id в базе данных и возвращает его в виде объекта Optional.
     *
     * @param id Long, представляющее идентификатор пользователя
     * @return объект Optional, содержащий пользователя, если он найден, или пустой, если нет
     */
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
        }
        return user;
    }

    /**
     * Находит всех пользователей в базе данных и возвращает их в виде списка объектов User.
     *
     * @return список объектов User, представляющих пользователей
     */
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    User user = getUserFromResultSet(resultSet);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return users;
    }

    /**
     * Сохраняет пользователя в базе данных и возвращает его в виде объекта Optional.
     *
     * @param user объект User, представляющий пользователя для сохранения
     * @return объект Optional, представляющий сохраненного пользователя или пустой, если была SQLException
     */
    @Override
    public Optional<User> save(User user) {
        String sql = """
                INSERT INTO users (lastname, firstname, surname, register_date, mobile_number)
                VALUES (?, ?, ?, ?, ?)
                """;
        Optional<User> optionalUser = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setUserValuesInStatement(preparedStatement, user);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                user.setId(id);
                optionalUser = Optional.of(user);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return optionalUser;
    }

    /**
     * Обновляет пользователя в базе данных и возвращает его в виде объекта Optional.
     *
     * @param user объект User, представляющий пользователя для обновления
     * @return объект Optional, представляющий обновлённого пользователя или пустой, если была SQLException
     */
    @Override
    public Optional<User> update(User user) {
        String sql = """
                UPDATE users
                SET lastname = ?, firstname = ?, surname = ?, register_date = ?, mobile_number = ?
                WHERE id = ?
                """;
        Optional<User> optionalUser = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setUserValuesInStatement(preparedStatement, user);
            preparedStatement.setLong(6, user.getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getLong(1);
                user.setId(id);
                optionalUser = Optional.of(user);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return optionalUser;
    }

    /**
     * Удаляет пользователя из базы данных и связанные с ним счета по его id и возвращает его в виде объекта Optional.
     *
     * @param id Long, представляющее идентификатор пользователя для удаления
     * @return объект Optional, содержащий удаленного пользователя, если он найден, или пустой, если нет
     */
    @Override
    public Optional<User> delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        Optional<User> user = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            deleteAllUsersAccounts(id);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                user = Optional.of(getUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return user;
    }

    /**
     * Удаляет все счета, принадлежащие пользователю с заданным id, из базы данных.
     *
     * @param userId Long, представляющее идентификатор пользователя, чьи счета нужно удалить
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    private void deleteAllUsersAccounts(Long userId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.executeUpdate();
        }
    }

    private User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .lastname(resultSet.getString("lastname"))
                .firstname(resultSet.getString("firstname"))
                .surname(resultSet.getString("surname"))
                .registerDate(resultSet.getDate("register_date").toLocalDate())
                .mobileNumber(resultSet.getString("mobile_number"))
                .build();
    }

    private void setUserValuesInStatement(PreparedStatement preparedStatement, User user) throws SQLException {
        preparedStatement.setString(1, user.getLastname());
        preparedStatement.setString(2, user.getFirstname());
        preparedStatement.setString(3, user.getSurname());
        preparedStatement.setObject(4, user.getRegisterDate());
        preparedStatement.setString(5, user.getMobileNumber());
    }

}
