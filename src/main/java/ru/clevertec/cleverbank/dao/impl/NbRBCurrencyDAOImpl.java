package ru.clevertec.cleverbank.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.dao.NbRBCurrencyDAO;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.NbRBCurrency;
import ru.clevertec.cleverbank.util.HikariConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@AllArgsConstructor
public class NbRBCurrencyDAOImpl implements NbRBCurrencyDAO {

    private final Connection connection;

    public NbRBCurrencyDAOImpl() {
        connection = HikariConnectionManager.getConnection();
    }

    /**
     * Сохраняет курс валюты по НБ РБ в базе данных и возвращает ее в виде объекта NbRBCurrency.
     *
     * @param nbRBCurrency объект NbRBCurrency, представляющий курс для сохранения
     * @return объект NbRBCurrency, представляющий сохраненный курс
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public NbRBCurrency save(NbRBCurrency nbRBCurrency) {
        String sql = """
                INSERT INTO nb_rb_currency
                (currency_id, currency, scale, rate, update_date)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setNbRBCurrencyValuesInStatement(preparedStatement, nbRBCurrency);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                nbRBCurrency.setId(id);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return nbRBCurrency;
    }

    private void setNbRBCurrencyValuesInStatement(PreparedStatement preparedStatement, NbRBCurrency nbRBCurrency) throws SQLException {
        preparedStatement.setInt(1, nbRBCurrency.getCurrencyId());
        preparedStatement.setString(2, nbRBCurrency.getCurrency());
        preparedStatement.setInt(3, nbRBCurrency.getScale());
        preparedStatement.setBigDecimal(4, nbRBCurrency.getRate());
        preparedStatement.setObject(5, nbRBCurrency.getUpdateDate());
    }

}
