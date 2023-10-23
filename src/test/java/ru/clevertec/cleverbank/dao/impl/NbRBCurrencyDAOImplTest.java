package ru.clevertec.cleverbank.dao.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.nbrbcurrency.NbRBCurrencyTestBuilder;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.model.NbRBCurrency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class NbRBCurrencyDAOImplTest {

    @InjectMocks
    private NbRBCurrencyDAOImpl nbRBCurrencyDAO;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    @Nested
    class FindByCurrencyIdTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw JDBCConnectionException with expected message if there is no connection")
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = """
                    SELECT * FROM nb_rb_currency
                    WHERE currency_id = ?
                    ORDER BY update_date DESC
                    LIMIT 1;
                    """;
            int currencyId = 102;
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql);

            Exception exception = assertThrows(JDBCConnectionException.class,
                    () -> nbRBCurrencyDAO.findByCurrencyId(currencyId));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            String sql = """
                    SELECT * FROM nb_rb_currency
                    WHERE currency_id = ?
                    ORDER BY update_date DESC
                    LIMIT 1;
                    """;
            NbRBCurrency expected = NbRBCurrencyTestBuilder.aNbRBCurrency().build();
            int currencyId = expected.getCurrencyId();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql);
            doNothing()
                    .when(preparedStatement)
                    .setInt(1, currencyId);
            doReturn(resultSet)
                    .when(preparedStatement)
                    .executeQuery();
            doReturn(true)
                    .when(resultSet)
                    .next();
            getMockedNbRBCurrencyFromResultSet(expected);

            Optional<NbRBCurrency> nbRBCurrency = nbRBCurrencyDAO.findByCurrencyId(currencyId);

            nbRBCurrency.ifPresent(actual -> assertThat(actual).isEqualTo(expected));
        }

    }

    @Nested
    class SaveTest {

        @Test
        @SneakyThrows
        @DisplayName("test should throw JDBCConnectionException with expected message if there is no connection")
        void testShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
            String sql = """
                    INSERT INTO nb_rb_currency
                    (currency_id, currency, scale, rate, update_date)
                    VALUES (?, ?, ?, ?, ?)
                    """;
            NbRBCurrency nbRBCurrency = NbRBCurrencyTestBuilder.aNbRBCurrency().build();
            String expectedMessage = "Sorry! We got Server database connection problems";

            doThrow(new SQLException(expectedMessage))
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            Exception exception = assertThrows(JDBCConnectionException.class, () -> nbRBCurrencyDAO.save(nbRBCurrency));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @SneakyThrows
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            String sql = """
                    INSERT INTO nb_rb_currency
                    (currency_id, currency, scale, rate, update_date)
                    VALUES (?, ?, ?, ?, ?)
                    """;
            NbRBCurrency expected = NbRBCurrencyTestBuilder.aNbRBCurrency().build();

            doReturn(preparedStatement)
                    .when(connection)
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setMockedNbRBCurrencyInStatement(expected);
            doReturn(1)
                    .when(preparedStatement)
                    .executeUpdate();
            doReturn(resultSet)
                    .when(preparedStatement)
                    .getGeneratedKeys();
            doReturn(true)
                    .when(resultSet)
                    .next();
            doReturn(expected.getId())
                    .when(resultSet)
                    .getLong(1);

            NbRBCurrency actual = nbRBCurrencyDAO.save(expected);

            assertThat(actual).isEqualTo(expected);
        }

    }

    private void getMockedNbRBCurrencyFromResultSet(NbRBCurrency nbRBCurrency) throws SQLException {
        doReturn(nbRBCurrency.getId())
                .when(resultSet)
                .getLong("id");
        doReturn(nbRBCurrency.getCurrencyId())
                .when(resultSet)
                .getInt("currency_id");
        doReturn(nbRBCurrency.getCurrency().toString())
                .when(resultSet)
                .getString("currency");
        doReturn(nbRBCurrency.getScale())
                .when(resultSet)
                .getInt("scale");
        doReturn(nbRBCurrency.getRate())
                .when(resultSet)
                .getBigDecimal("rate");
        doReturn(Timestamp.valueOf(nbRBCurrency.getUpdateDate()))
                .when(resultSet)
                .getTimestamp("update_date");
    }

    private void setMockedNbRBCurrencyInStatement(NbRBCurrency nbRBCurrency) throws SQLException {
        doNothing()
                .when(preparedStatement)
                .setInt(1, nbRBCurrency.getCurrencyId());
        doNothing()
                .when(preparedStatement)
                .setString(2, String.valueOf(nbRBCurrency.getCurrency()));
        doNothing()
                .when(preparedStatement)
                .setInt(3, nbRBCurrency.getScale());
        doNothing()
                .when(preparedStatement)
                .setBigDecimal(4, nbRBCurrency.getRate());
        doNothing()
                .when(preparedStatement)
                .setObject(5, nbRBCurrency.getUpdateDate());
    }

}
