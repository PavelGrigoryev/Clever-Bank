package ru.clevertec.cleverbank.dao.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.config.ConnectionManager;
import ru.clevertec.cleverbank.dao.BankDAO;
import ru.clevertec.cleverbank.model.Bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
public class BankDAOImpl implements BankDAO {

    private final Connection connection;

    public BankDAOImpl() {
        connection = ConnectionManager.getJDBCConnection();
    }

    @Override
    public Optional<Bank> findById(Long id) {
        String sql = "SELECT * FROM banks WHERE id = ?";
        Optional<Bank> bank = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    bank = Optional.of(getBankFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return bank;
    }

    private Bank getBankFromResultSet(ResultSet resultSet) throws SQLException {
        return Bank.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .address(resultSet.getString("address"))
                .phoneNumber(resultSet.getString("phone_number"))
                .build();
    }

    private void setBankValuesInStatement(PreparedStatement preparedStatement, Bank bank) throws SQLException {
        preparedStatement.setString(1, bank.getName());
        preparedStatement.setString(2, bank.getAddress());
        preparedStatement.setString(3, bank.getPhoneNumber());
    }

}
