package ru.clevertec.cleverbank.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.bank.BankRequestTestBuilder;
import ru.clevertec.cleverbank.builder.bank.BankResponseTestBuilder;
import ru.clevertec.cleverbank.builder.bank.BankTestBuilder;
import ru.clevertec.cleverbank.dto.bank.BankRequest;
import ru.clevertec.cleverbank.dto.bank.BankResponse;
import ru.clevertec.cleverbank.model.Bank;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BankMapperImplTest {

    @Spy
    private BankMapperImpl bankMapper;

    @Nested
    class ToResponseTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            Bank bank = BankTestBuilder.aBank().build();
            BankResponse expected = BankResponseTestBuilder.aBankResponse().build();

            BankResponse actual = bankMapper.toResponse(bank);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            BankResponse actual = bankMapper.toResponse(null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class ToResponseListTest {

        @Test
        @DisplayName("test should return expected list")
        void testShouldReturnExpectedList() {
            List<Bank> banks = List.of(BankTestBuilder.aBank().build());
            List<BankResponse> expectedList = List.of(BankResponseTestBuilder.aBankResponse().build());

            List<BankResponse> actualList = bankMapper.toResponseList(banks);

            assertThat(actualList).isEqualTo(expectedList);
        }

        @Test
        @DisplayName("test should return empty list")
        void testShouldReturnEmptyList() {
            List<BankResponse> actual = bankMapper.toResponseList(List.of());

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            List<BankResponse> actual = bankMapper.toResponseList(null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class FromSaveRequestTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            BankRequest request = BankRequestTestBuilder.aBankRequest().build();
            Bank expected = BankTestBuilder.aBank()
                    .withId(null)
                    .build();

            Bank actual = bankMapper.fromSaveRequest(request);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            Bank actual = bankMapper.fromSaveRequest(null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class FromUpdateRequestTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            BankRequest request = BankRequestTestBuilder.aBankRequest().build();
            Bank expected = BankTestBuilder.aBank().build();

            Bank actual = bankMapper.fromUpdateRequest(request, expected.getId());

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            Bank actual = bankMapper.fromUpdateRequest(null, null);

            assertThat(actual).isNull();
        }

    }

}
