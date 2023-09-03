package ru.clevertec.cleverbank.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.dao.AccountDAO;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.User;
import ru.clevertec.cleverbank.util.ConnectionManager;
import ru.clevertec.cleverbank.util.RandomStringGenerator;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class AccountDAOImpl implements AccountDAO {

    private final Connection connection;

    public AccountDAOImpl() {
        connection = ConnectionManager.getJDBCConnection();
    }

    /**
     * Находит счёт по его id и связанные с ним банк и юзера в базе данных и возвращает его в виде объекта Optional.
     *
     * @param id String, представляющая идентификатор счета
     * @return объект Optional, содержащий счет, если он найден, или пустой, если нет
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public Optional<Account> findById(String id) {
        String sql = """
                SELECT * FROM accounts a
                JOIN banks b ON b.id = a.bank_id
                JOIN users u ON u.id = a.user_id
                WHERE a.id = ?
                """;
        Optional<Account> account = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    account = Optional.of(getAccountFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return account;
    }

    /**
     * Находит все счета и связанные с ним банк и юзера в базе данных и возвращает их в виде списка объектов Account.
     *
     * @return список объектов Account, представляющих счета
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = """
                SELECT * FROM accounts a
                JOIN banks b ON b.id = a.bank_id
                JOIN users u ON u.id = a.user_id
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Account account = getAccountFromResultSet(resultSet);
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return accounts;
    }

    /**
     * Сохраняет счёт в базе данных и возвращает его в виде объекта Account.
     *
     * @param account объект Account, представляющий счёт для сохранения
     * @return объект Account, представляющий сохраненный счёт
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public Account save(Account account) {
        String sql = """
                INSERT INTO accounts (currency, balance, opening_date, closing_date, bank_id, user_id, id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setAccountValuesInStatement(preparedStatement, account);
            preparedStatement.setString(7, RandomStringGenerator.generateRandomString());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                String id = resultSet.getString(1);
                account.setId(id);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return account;
    }

    /**
     * Обновляет счёт в базе данных и возвращает его в виде объекта Account.
     *
     * @param account объект Account, представляющий счёт для обновления
     * @return объект Account, представляющий обновленный счёт
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public Account update(Account account) {
        String sql = """
                UPDATE accounts
                SET currency = ?, balance = ?, opening_date = ?, closing_date = ?, bank_id = ?, user_id = ?
                WHERE id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setAccountValuesInStatement(preparedStatement, account);
            preparedStatement.setString(7, account.getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                String id = resultSet.getString(1);
                account.setId(id);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return account;
    }

    /**
     * Удаляет счёт из базы данных по его id и возвращает его в виде объекта Optional.
     *
     * @param id String, представляющая идентификатор счёта для удаления
     * @return объект Optional, содержащий удаленный счет, если он найден, или пустой, если нет
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public Optional<Account> delete(String id) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        Optional<Account> account = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                account = Optional.of(Account.builder().id(resultSet.getString("id")).build());
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return account;
    }

    private Account getAccountFromResultSet(ResultSet resultSet) throws SQLException {
        Date closingDate = resultSet.getDate("closing_date");
        return Account.builder()
                .id(resultSet.getString("id"))
                .currency(Currency.valueOf(resultSet.getString("currency")))
                .balance(resultSet.getBigDecimal("balance"))
                .openingDate(resultSet.getDate("opening_date").toLocalDate())
                .closingDate(closingDate != null ? closingDate.toLocalDate() : null)
                .bank(Bank.builder()
                        .id(resultSet.getLong("bank_id"))
                        .name(resultSet.getString("name"))
                        .address(resultSet.getString("address"))
                        .phoneNumber(resultSet.getString("phone_number"))
                        .build())
                .user(User.builder()
                        .id(resultSet.getLong("user_id"))
                        .lastname(resultSet.getString("lastname"))
                        .firstname(resultSet.getString("firstname"))
                        .surname(resultSet.getString("surname"))
                        .registerDate(resultSet.getDate("register_date").toLocalDate())
                        .mobileNumber(resultSet.getString("mobile_number"))
                        .build())
                .build();
    }

    private void setAccountValuesInStatement(PreparedStatement preparedStatement, Account account) throws SQLException {
        preparedStatement.setString(1, String.valueOf(account.getCurrency()));
        preparedStatement.setBigDecimal(2, account.getBalance());
        preparedStatement.setObject(3, account.getOpeningDate());
        preparedStatement.setObject(4, account.getClosingDate());
        preparedStatement.setLong(5, account.getBank().getId());
        preparedStatement.setLong(6, account.getUser().getId());
    }

}
