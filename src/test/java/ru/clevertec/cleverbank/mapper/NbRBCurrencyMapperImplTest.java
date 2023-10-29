package ru.clevertec.cleverbank.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.nbrbcurrency.NbRBCurrencyResponseTestBuilder;
import ru.clevertec.cleverbank.builder.nbrbcurrency.NbRBCurrencyTestBuilder;
import ru.clevertec.cleverbank.dto.nbrbcurrency.NbRBCurrencyResponse;
import ru.clevertec.cleverbank.model.NbRBCurrency;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NbRBCurrencyMapperImplTest {

    @Spy
    private NbRBCurrencyMapperImpl nbRBCurrencyMapper;

    @Nested
    class ToResponseTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            NbRBCurrency nbRBCurrency = NbRBCurrencyTestBuilder.aNbRBCurrency().build();
            NbRBCurrencyResponse expected = NbRBCurrencyResponseTestBuilder.aNbRBCurrencyResponse().build();

            NbRBCurrencyResponse actual = nbRBCurrencyMapper.toResponse(nbRBCurrency);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            NbRBCurrencyResponse actual = nbRBCurrencyMapper.toResponse(null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class FromResponseTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            NbRBCurrency expected = NbRBCurrencyTestBuilder.aNbRBCurrency()
                    .withId(null)
                    .withUpdateDate(null)
                    .build();
            NbRBCurrencyResponse response = NbRBCurrencyResponseTestBuilder.aNbRBCurrencyResponse().build();

            NbRBCurrency actual = nbRBCurrencyMapper.fromResponse(response);
            actual.setUpdateDate(null);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            NbRBCurrency actual = nbRBCurrencyMapper.fromResponse(null);

            assertThat(actual).isNull();
        }

    }

}
