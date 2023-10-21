package ru.clevertec.cleverbank.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.dao.NbRBCurrencyDAO;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.NbRBCurrency;
import ru.clevertec.cleverbank.util.HikariConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class NbRBCurrencyDAOImpl implements NbRBCurrencyDAO {

    private final Connection connection;

    public NbRBCurrencyDAOImpl() {
        connection = HikariConnectionManager.getConnection();
    }

    /**
     * Находит курс валюты НБ РБ по ее currencyId в базе данных и возвращает ее в виде объекта Optional.
     *
     * @param currencyId Integer, представляющее идентификатор курса по НБ РБ
     * @return объект Optional, содержащий курс, если он найден, или пустой, если нет
     * @throws JDBCConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public Optional<NbRBCurrency> findByCurrencyId(Integer currencyId) {
        String sql = """
                SELECT n.* FROM nb_rb_currency n
                JOIN (SELECT currency_id, max(update_date) AS max_date
                FROM nb_rb_currency
                GROUP BY currency_id) m
                ON n.currency_id = m.currency_id AND n.update_date = m.max_date
                WHERE n.currency_id = ?
                LIMIT 1;
                """;
        Optional<NbRBCurrency> nbRBCurrency = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, currencyId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    nbRBCurrency = Optional.of(getNbRBCurrencyFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new JDBCConnectionException();
        }
        return nbRBCurrency;
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

    private NbRBCurrency getNbRBCurrencyFromResultSet(ResultSet resultSet) throws SQLException {
        return NbRBCurrency.builder()
                .id(resultSet.getLong("id"))
                .currencyId(resultSet.getInt("currency_id"))
                .currency(Currency.valueOf(resultSet.getString("currency")))
                .scale(resultSet.getInt("scale"))
                .rate(resultSet.getBigDecimal("rate"))
                .updateDate(resultSet.getDate("update_date").toLocalDate())
                .build();
    }

    private void setNbRBCurrencyValuesInStatement(PreparedStatement preparedStatement, NbRBCurrency nbRBCurrency) throws SQLException {
        preparedStatement.setInt(1, nbRBCurrency.getCurrencyId());
        preparedStatement.setString(2, String.valueOf(nbRBCurrency.getCurrency()));
        preparedStatement.setInt(3, nbRBCurrency.getScale());
        preparedStatement.setBigDecimal(4, nbRBCurrency.getRate());
        preparedStatement.setObject(5, nbRBCurrency.getUpdateDate());
    }

}
