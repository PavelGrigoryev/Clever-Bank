package ru.clevertec.cleverbank.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.dao.BankDAO;
import ru.clevertec.cleverbank.model.Bank;
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
public class BankDAOImpl implements BankDAO {

    private final Connection connection;

    public BankDAOImpl() {
        connection = HikariConnectionManager.getConnection();
    }

    /**
     * Находит банк по его идентификатору в базе данных и возвращает его в виде объекта Optional.
     *
     * @param id Long, представляющее идентификатор банка
     * @return объект Optional, содержащий банк, если он найден, или пустой, если нет
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
        }
        return bank;
    }

    /**
     * Находит все банки в базе данных и возвращает их в виде списка объектов Bank.
     *
     * @return список объектов Bank, представляющих банки
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
        }
        return banks;
    }

    /**
     * Сохраняет банк в базе данных и возвращает его в виде объекта Optional.
     *
     * @param bank объект Bank, представляющий банк для сохранения
     * @return объект Optional, представляющий сохраненный банк или пустой, если была SQLException
     */
    @Override
    public Optional<Bank> save(Bank bank) {
        String sql = """
                INSERT INTO banks (name, address, phone_number)
                VALUES (?, ?, ?)
                """;
        Optional<Bank> bankOptional = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setBankValuesInStatement(preparedStatement, bank);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                bank.setId(id);
                bankOptional = Optional.of(bank);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return bankOptional;
    }

    /**
     * Обновляет банк в базе данных и возвращает его в виде объекта Bank.
     *
     * @param bank объект Bank, представляющий банк для обновления
     * @return объект Optional, представляющий обновлённый банк или пустой, если была SQLException
     */
    @Override
    public Optional<Bank> update(Bank bank) {
        String sql = """
                UPDATE banks
                SET name = ?, address = ?, phone_number = ?
                WHERE id = ?
                """;
        Optional<Bank> bankOptional = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setBankValuesInStatement(preparedStatement, bank);
            preparedStatement.setLong(4, bank.getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getLong(1);
                bank.setId(id);
                bankOptional = Optional.of(bank);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return bankOptional;
    }

    /**
     * Удаляет банк из базы данных и связанные с ним счета по его id, и возвращает его в виде объекта Optional.
     *
     * @param id Long, представляющее идентификатор банка для удаления
     * @return объект Optional, содержащий удаленный банк, если он найден, или пустой, если нет
     */
    @Override
    public Optional<Bank> delete(Long id) {
        String sql = "DELETE FROM banks WHERE id = ?";
        Optional<Bank> bank = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            deleteAllBanksAccounts(id);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                bank = Optional.of(getBankFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return bank;
    }

    /**
     * Удаляет все счета, принадлежащие банку с заданным id, из базы данных.
     *
     * @param bankId Long, представляющее идентификатор банка, чьи счета нужно удалить
     */
    private void deleteAllBanksAccounts(Long bankId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE bank_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, bankId);
            preparedStatement.executeUpdate();
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
