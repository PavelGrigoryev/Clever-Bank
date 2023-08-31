package ru.clevertec.cleverbank.dao.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.dao.BankDAO;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BankDAOImpl implements BankDAO {

    private final Connection connection;

    public BankDAOImpl() {
        connection = ConnectionManager.getJDBCConnection();
    }

    /**
     * Находит банк по его идентификатору в базе данных и возвращает его в виде объекта Optional.
     *
     * @param id Long, представляющее идентификатор банка
     * @return объект Optional, содержащий банк, если он найден, или пустой, если нет
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
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
            throw new JDBCConnectionException();
        }
        return bank;
    }

    /**
     * Находит все банки в базе данных и возвращает их в виде списка объектов Bank.
     *
     * @return список объектов Bank, представляющих банки
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public List<Bank> findAll() {
        List<Bank> banks = new ArrayList<>();
        String sql = "SELECT * FROM banks";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Bank bank = getBankFromResultSet(resultSet);
                    banks.add(bank);
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return banks;
    }

    /**
     * Сохраняет банк в базе данных и возвращает его в виде объекта Bank.
     *
     * @param bank объект Bank, представляющий банк для сохранения
     * @return объект Bank, представляющий сохраненный банк
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public Bank save(Bank bank) {
        String sql = """
                INSERT INTO banks (name, address, phone_number)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setBankValuesInStatement(preparedStatement, bank);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                bank.setId(id);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return bank;
    }

    /**
     * Обновляет банк в базе данных и возвращает его в виде объекта Bank.
     *
     * @param bank объект Bank, представляющий банк для обновления
     * @return объект Bank, представляющий обновленный банк
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public Bank update(Bank bank) {
        String sql = """
                UPDATE banks
                SET name = ?, address = ?, phone_number = ?
                WHERE id = ?
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setBankValuesInStatement(preparedStatement, bank);
            preparedStatement.setLong(4, bank.getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getLong(1);
                bank.setId(id);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return bank;
    }

    /**
     * Удаляет банк из базы данных и связанные с ним счета по его id, и возвращает его в виде объекта Optional.
     *
     * @param id Long, представляющее идентификатор банка для удаления
     * @return объект Optional, содержащий удаленный банк, если он найден, или пустой, если нет
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public Optional<Bank> delete(Long id) {
        deleteAllBanksAccounts(id);
        String sql = "DELETE FROM banks WHERE id = ?";
        Optional<Bank> bank = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                bank = Optional.of(getBankFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return bank;
    }

    /**
     * Удаляет все счета, принадлежащие банку с заданным id, из базы данных.
     *
     * @param bankId Long, представляющее идентификатор банка, чьи счета нужно удалить
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    private void deleteAllBanksAccounts(Long bankId) {
        String sql = "DELETE FROM accounts WHERE bank_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, bankId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
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
