package ru.clevertec.cleverbank.dao.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.config.ConnectionManager;
import ru.clevertec.cleverbank.dao.AccountDAO;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Currency;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        preparedStatement.setString(1, account.getId());
        preparedStatement.setString(2, String.valueOf(account.getCurrency()));
        preparedStatement.setBigDecimal(3, account.getBalance());
        preparedStatement.setObject(4, account.getOpeningDate());
        preparedStatement.setObject(5, account.getClosingDate());
        preparedStatement.setLong(6, account.getBankId());
        preparedStatement.setLong(7, account.getUserId());
    }

}
