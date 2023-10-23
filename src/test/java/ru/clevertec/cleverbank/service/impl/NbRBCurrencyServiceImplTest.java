package ru.clevertec.cleverbank.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.nbrbcurrency.NbRBCurrencyResponseTestBuilder;
import ru.clevertec.cleverbank.builder.nbrbcurrency.NbRBCurrencyTestBuilder;
import ru.clevertec.cleverbank.dao.NbRBCurrencyDAO;
import ru.clevertec.cleverbank.dto.nbrbcurrency.NbRBCurrencyResponse;
import ru.clevertec.cleverbank.exception.notfound.NbRBCurrencyNotFoundException;
import ru.clevertec.cleverbank.mapper.NbRBCurrencyMapper;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.NbRBCurrency;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class NbRBCurrencyServiceImplTest {

    @InjectMocks
    private NbRBCurrencyServiceImpl nbRBCurrencyService;
    @Mock
    private NbRBCurrencyDAO nbRBCurrencyDAO;
    @Mock
    private NbRBCurrencyMapper currencyMapper;

    @Nested
    class FindByCurrencyIdTest {

        @Test
        @DisplayName("test should throw NbRBCurrencyNotFoundException with expected message")
        void testShouldThrowNbRBCurrencyNotFoundExceptionWithExpectedMessage() {
            int currencyId = 102;
            String expectedMessage = "NbRBCurrency with currencyId " + currencyId + " is not found!";

            Exception exception = assertThrows(NbRBCurrencyNotFoundException.class,
                    () -> nbRBCurrencyService.findByCurrencyId(currencyId));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            NbRBCurrency expected = NbRBCurrencyTestBuilder.aNbRBCurrency().build();
            int currencyId = expected.getCurrencyId();

            doReturn(Optional.of(expected))
                    .when(nbRBCurrencyDAO)
                    .findByCurrencyId(currencyId);

            NbRBCurrency actual = nbRBCurrencyService.findByCurrencyId(currencyId);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class SaveTest {

        @Test
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            NbRBCurrency expected = NbRBCurrencyTestBuilder.aNbRBCurrency().build();
            NbRBCurrencyResponse response = NbRBCurrencyResponseTestBuilder.aNbRBCurrencyResponse().build();

            doReturn(expected)
                    .when(currencyMapper)
                    .fromResponse(response);
            doReturn(expected)
                    .when(nbRBCurrencyDAO)
                    .save(expected);

            NbRBCurrency actual = nbRBCurrencyService.save(response);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class ExchangeSumByCurrencyTest {

        @Test
        @DisplayName("test should return sum without changes")
        void testShouldReturnSumWithoutChanges() {
            Currency currencySender = Currency.EUR;
            Currency currencyRecipient = Currency.EUR;
            BigDecimal expected = BigDecimal.TEN;

            BigDecimal actual = nbRBCurrencyService.exchangeSumByCurrency(currencySender, currencyRecipient, expected);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return sum if currencyRecipient is BYN")
        void testShouldReturnSumIfCurrencyRecipientIsBYN() {
            Currency currencySender = Currency.EUR;
            Currency currencyRecipient = Currency.BYN;
            BigDecimal sum = BigDecimal.TEN;
            BigDecimal expected = BigDecimal.valueOf(34.78);
            NbRBCurrency nbRBCurrency = NbRBCurrencyTestBuilder.aNbRBCurrency().build();

            doReturn(Optional.of(nbRBCurrency))
                    .when(nbRBCurrencyDAO)
                    .findByCurrencyId(currencySender.getCode());

            BigDecimal actual = nbRBCurrencyService.exchangeSumByCurrency(currencySender, currencyRecipient, sum);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return sum if currencySender is BYN")
        void testShouldReturnSumIfCurrencySenderIsBYN() {
            Currency currencySender = Currency.BYN;
            Currency currencyRecipient = Currency.EUR;
            BigDecimal sum = BigDecimal.TEN;
            BigDecimal expected = BigDecimal.valueOf(2.88);
            NbRBCurrency nbRBCurrency = NbRBCurrencyTestBuilder.aNbRBCurrency().build();

            doReturn(Optional.of(nbRBCurrency))
                    .when(nbRBCurrencyDAO)
                    .findByCurrencyId(currencyRecipient.getCode());

            BigDecimal actual = nbRBCurrencyService.exchangeSumByCurrency(currencySender, currencyRecipient, sum);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return sum if currencySender and currencyRecipient is not BYN")
        void testShouldReturnSumIfCurrencySenderAndCurrencyRecipientIsNotBYN() {
            Currency currencySender = Currency.RUB;
            Currency currencyRecipient = Currency.EUR;
            BigDecimal sum = BigDecimal.valueOf(1000);
            BigDecimal expected = BigDecimal.valueOf(9.73);
            NbRBCurrency nbRBCurrencySender = NbRBCurrencyTestBuilder.aNbRBCurrency()
                    .withCurrencyId(currencySender.getCode())
                    .withCurrency(currencySender)
                    .withScale(100)
                    .withRate(BigDecimal.valueOf(3.3817))
                    .build();
            NbRBCurrency nbRBCurrencyRecipient = NbRBCurrencyTestBuilder.aNbRBCurrency().build();

            doReturn(Optional.of(nbRBCurrencySender))
                    .when(nbRBCurrencyDAO)
                    .findByCurrencyId(currencySender.getCode());
            doReturn(Optional.of(nbRBCurrencyRecipient))
                    .when(nbRBCurrencyDAO)
                    .findByCurrencyId(currencyRecipient.getCode());

            BigDecimal actual = nbRBCurrencyService.exchangeSumByCurrency(currencySender, currencyRecipient, sum);

            assertThat(actual).isEqualTo(expected);
        }

    }

}
