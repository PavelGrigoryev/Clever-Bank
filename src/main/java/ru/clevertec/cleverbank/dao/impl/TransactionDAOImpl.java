package ru.clevertec.cleverbank.dao.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.config.ConnectionManager;
import ru.clevertec.cleverbank.dao.TransactionDAO;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class TransactionDAOImpl implements TransactionDAO {

    private final Connection connection;

    public TransactionDAOImpl() {
        connection = ConnectionManager.getJDBCConnection();
    }

    @Override
    public Transaction save(Transaction transaction) {
        String sql = """
                INSERT INTO transactions
                (date, time, type, senders_bank, recipients_bank, senders_account, recipients_account, sum)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setTransactionValuesInStatement(preparedStatement, transaction);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                transaction.setId(id);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return transaction;
    }

    private Transaction getTransactionFromResultSet(ResultSet resultSet) throws SQLException {
        return Transaction.builder()
                .id(resultSet.getLong("id"))
                .date(resultSet.getDate("date").toLocalDate())
                .time(resultSet.getTime("time").toLocalTime())
                .type(Type.valueOf(resultSet.getString("type")))
                .sendersBank(resultSet.getString("senders_bank"))
                .recipientsBank(resultSet.getString("recipients_bank"))
                .sendersAccount(resultSet.getString("senders_account"))
                .recipientsAccount(resultSet.getString("recipients_account"))
                .sum(new BigDecimal(resultSet.getString("sum")))
                .build();
    }

    private void setTransactionValuesInStatement(PreparedStatement preparedStatement, Transaction transaction) throws SQLException {
        preparedStatement.setObject(1, transaction.getDate());
        preparedStatement.setObject(2, transaction.getTime());
        preparedStatement.setString(3, String.valueOf(transaction.getType()));
        preparedStatement.setString(4, transaction.getSendersBank());
        preparedStatement.setString(5, transaction.getRecipientsBank());
        preparedStatement.setString(6, transaction.getSendersAccount());
        preparedStatement.setString(7, transaction.getRecipientsAccount());
        preparedStatement.setBigDecimal(8, transaction.getSum());
    }

}
