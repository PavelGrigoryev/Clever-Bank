package ru.clevertec.cleverbank.dao.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.config.ConnectionManager;
import ru.clevertec.cleverbank.dao.AccountDAO;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Currency;
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
public class AccountDAOImpl implements AccountDAO {

    private final Connection connection;

    public AccountDAOImpl() {
        connection = ConnectionManager.getJDBCConnection();
    }

    @Override
    public Optional<Account> findById(String id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
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

    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
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

    @Override
    public Optional<Account> delete(String id) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        Optional<Account> account = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                account = Optional.of(getAccountFromResultSet(resultSet));
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
                .bankId(resultSet.getLong("bank_id"))
                .userId(resultSet.getLong("user_id"))
                .build();
    }

    private void setAccountValuesInStatement(PreparedStatement preparedStatement, Account account) throws SQLException {
        preparedStatement.setString(1, String.valueOf(account.getCurrency()));
        preparedStatement.setBigDecimal(2, account.getBalance());
        preparedStatement.setObject(3, account.getOpeningDate());
        preparedStatement.setObject(4, account.getClosingDate());
        preparedStatement.setLong(5, account.getBankId());
        preparedStatement.setLong(6, account.getUserId());
    }

}
