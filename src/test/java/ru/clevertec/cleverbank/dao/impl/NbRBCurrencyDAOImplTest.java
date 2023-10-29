package ru.clevertec.cleverbank.dao.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.InsertOneResult;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.nbrbcurrency.NbRBCurrencyTestBuilder;
import ru.clevertec.cleverbank.exception.internalservererror.FailedConnectionException;
import ru.clevertec.cleverbank.model.NbRBCurrency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class NbRBCurrencyDAOImplTest {

    @InjectMocks
    private NbRBCurrencyDAOImpl nbRBCurrencyDAO;
    @Mock
    private MongoCollection<NbRBCurrency> mongoCollection;
    @Mock
    private FindIterable<NbRBCurrency> findIterable;
    @Mock
    private InsertOneResult insertOneResult;
    @Mock
    private BsonValue bsonValue;
    @Mock
    private BsonObjectId bsonObjectId;

    @Test
    @DisplayName("test findByCurrencyId should return expected response")
    void testFindByCurrencyIdShouldReturnExpectedResponse() {
        NbRBCurrency expected = NbRBCurrencyTestBuilder.aNbRBCurrency().build();
        int currencyId = expected.getCurrencyId();

        doReturn(findIterable)
                .when(mongoCollection)
                .find(Filters.eq("currency_id", currencyId));
        doReturn(findIterable)
                .when(findIterable)
                .sort(Sorts.descending("update_date"));
        doReturn(findIterable)
                .when(findIterable)
                .limit(1);
        doReturn(expected)
                .when(findIterable)
                .first();

        nbRBCurrencyDAO.findByCurrencyId(currencyId)
                .ifPresent(actual -> assertThat(actual).isEqualTo(expected));
    }

    @Test
    @DisplayName("test save should return expected response")
    void testSaveShouldReturnExpectedResponse() {
        NbRBCurrency expected = NbRBCurrencyTestBuilder.aNbRBCurrency().build();

        doReturn(insertOneResult)
                .when(mongoCollection)
                .insertOne(expected);
        doReturn(bsonValue)
                .when(insertOneResult)
                .getInsertedId();
        doReturn(bsonObjectId)
                .when(bsonValue)
                .asObjectId();
        doReturn(expected.getId())
                .when(bsonObjectId)
                .getValue();

        NbRBCurrency actual = nbRBCurrencyDAO.save(expected);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test save should throw FailedConnectionException with expected message if there is no connection")
    void testSaveShouldThrowJDBCConnectionExceptionWithExpectedMessage() {
        NbRBCurrency nbRBCurrency = NbRBCurrencyTestBuilder.aNbRBCurrency().build();
        String expectedMessage = "Failed to save " + nbRBCurrency;

        doReturn(insertOneResult)
                .when(mongoCollection)
                .insertOne(nbRBCurrency);
        doReturn(bsonValue)
                .when(insertOneResult)
                .getInsertedId();
        doReturn(bsonObjectId)
                .when(bsonValue)
                .asObjectId();
        doReturn(null)
                .when(bsonObjectId)
                .getValue();

        Exception exception = assertThrows(FailedConnectionException.class, () -> nbRBCurrencyDAO.save(nbRBCurrency));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

}
