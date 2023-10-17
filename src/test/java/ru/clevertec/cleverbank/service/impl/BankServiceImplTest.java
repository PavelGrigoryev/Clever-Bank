package ru.clevertec.cleverbank.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.dao.BankDAO;
import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.bank.BankRequest;
import ru.clevertec.cleverbank.dto.bank.BankResponse;
import ru.clevertec.cleverbank.exception.badrequest.UniquePhoneNumberException;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.exception.notfound.BankNotFoundException;
import ru.clevertec.cleverbank.mapper.BankMapper;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.util.bank.BankRequestTestBuilder;
import ru.clevertec.cleverbank.util.bank.BankResponseTestBuilder;
import ru.clevertec.cleverbank.util.bank.BankTestBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankServiceImplTest {

    @InjectMocks
    private BankServiceImpl bankService;
    @Mock
    private BankDAO bankDAO;
    @Mock
    private BankMapper bankMapper;
    @Captor
    private ArgumentCaptor<Bank> captor;

    @Nested
    class FindByIdTest {

        @Test
        @DisplayName("test should throw BankNotFoundException with expected message")
        void testShouldThrowBankNotFoundExceptionWithExpectedMessage() {
            long id = 1L;
            String expectedMessage = "Bank with ID " + id + " is not found!";

            Exception exception = assertThrows(BankNotFoundException.class, () -> bankService.findByIdResponse(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            BankResponse expected = BankResponseTestBuilder.aBankResponse().build();
            Bank bank = BankTestBuilder.aBank().build();
            long id = expected.id();

            doReturn(expected)
                    .when(bankMapper)
                    .toResponse(bank);
            doReturn(Optional.of(bank))
                    .when(bankDAO)
                    .findById(id);

            BankResponse actual = bankService.findByIdResponse(id);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class FindAllTest {

        @Test
        @DisplayName("test should return list of size one")
        void testShouldReturnListOfSizeOne() {
            BankResponse response = BankResponseTestBuilder.aBankResponse().build();
            Bank bank = BankTestBuilder.aBank().build();
            int expectedSize = 1;

            doReturn(List.of(response))
                    .when(bankMapper)
                    .toResponseList(List.of(bank));
            doReturn(List.of(bank))
                    .when(bankDAO)
                    .findAll();

            List<BankResponse> actual = bankService.findAll();

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        @DisplayName("test should return list that contains expected response")
        void testShouldReturnListThatContainsExpectedResponse() {
            BankResponse expected = BankResponseTestBuilder.aBankResponse().build();
            Bank bank = BankTestBuilder.aBank().build();

            doReturn(List.of(expected))
                    .when(bankMapper)
                    .toResponseList(List.of(bank));
            doReturn(List.of(bank))
                    .when(bankDAO)
                    .findAll();

            List<BankResponse> actual = bankService.findAll();

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return empty list")
        void testShouldReturnEmptyList() {
            doReturn(List.of())
                    .when(bankDAO)
                    .findAll();

            List<BankResponse> actual = bankService.findAll();

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class SaveTest {

        @ParameterizedTest(name = "{arguments} test")
        @MethodSource("ru.clevertec.cleverbank.service.impl.BankServiceImplTest#getArgumentsForSaveTest")
        @DisplayName("test should capture value and return expected response")
        void testShouldCaptureValue(Bank expected) {
            BankRequest request = BankRequestTestBuilder.aBankRequest().build();
            BankResponse response = BankResponseTestBuilder.aBankResponse()
                    .withId(expected.getId())
                    .withName(expected.getName())
                    .build();

            doReturn(expected)
                    .when(bankMapper)
                    .fromRequest(request);
            doReturn(expected)
                    .when(bankDAO)
                    .save(expected);
            doReturn(response)
                    .when(bankMapper)
                    .toResponse(expected);

            bankService.save(request);
            verify(bankDAO).save(captor.capture());

            Bank bankCaptor = captor.getValue();
            assertThat(bankCaptor).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should throw UniquePhoneNumberException with expected message")
        void testShouldThrowUniquePhoneNumberExceptionWithExpectedMessage() {
            String phoneNumber = "+7 (495) 222-22-22";
            String expectedMessage = "Bank with phone number " + phoneNumber + " is already exist";
            Bank bank = BankTestBuilder.aBank().build();
            BankRequest request = BankRequestTestBuilder.aBankRequest().build();

            doReturn(bank)
                    .when(bankMapper)
                    .fromRequest(request);
            doThrow(new JDBCConnectionException())
                    .when(bankDAO)
                    .save(bank);

            Exception exception = assertThrows(UniquePhoneNumberException.class, () -> bankService.save(request));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

    }

    @Nested
    class UpdateTest {

        @Test
        @DisplayName("test should return updated response")
        void testShouldReturnUpdatedResponse() {
            Bank bank = BankTestBuilder.aBank().build();
            BankRequest request = BankRequestTestBuilder.aBankRequest().build();
            BankResponse expected = BankResponseTestBuilder.aBankResponse().build();

            doReturn(Optional.of(bank))
                    .when(bankDAO)
                    .findById(bank.getId());
            doReturn(bank)
                    .when(bankMapper)
                    .fromRequest(request);
            doReturn(bank)
                    .when(bankDAO)
                    .update(bank);
            doReturn(expected)
                    .when(bankMapper)
                    .toResponse(bank);

            BankResponse actual = bankService.update(bank.getId(), request);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should throw UniquePhoneNumberException with expected message")
        void testShouldThrowUniquePhoneNumberExceptionWithExpectedMessage() {
            String phoneNumber = "+7 (495) 222-22-22";
            String expectedMessage = "Bank with phone number " + phoneNumber + " is already exist";
            Bank bank = BankTestBuilder.aBank().build();
            BankRequest request = BankRequestTestBuilder.aBankRequest().build();

            doReturn(Optional.of(bank))
                    .when(bankDAO)
                    .findById(bank.getId());
            doReturn(bank)
                    .when(bankMapper)
                    .fromRequest(request);
            doThrow(new JDBCConnectionException())
                    .when(bankDAO)
                    .update(bank);

            Exception exception = assertThrows(UniquePhoneNumberException.class, () -> bankService.update(1L, request));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

    }

    @Nested
    class DeleteTest {

        @Test
        @DisplayName("test should return expected response")
        void testShouldReturnExpectedResponse() {
            Bank bank = BankTestBuilder.aBank().build();
            DeleteResponse expected = new DeleteResponse("Bank with ID " + bank.getId() + " was successfully deleted");

            doReturn(Optional.of(bank))
                    .when(bankDAO)
                    .delete(bank.getId());

            DeleteResponse actual = bankService.delete(bank.getId());

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should throw BankNotFoundException with expected message")
        void testShouldThrowBankNotFoundExceptionWithExpectedMessage() {
            long id = 1L;
            String expectedMessage = "No Bank with ID " + id + " to delete";

            Exception exception = assertThrows(BankNotFoundException.class, () -> bankService.delete(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

    }

    private static Stream<Arguments> getArgumentsForSaveTest() {
        return Stream.of(Arguments.of(BankTestBuilder.aBank().build()),
                Arguments.of(BankTestBuilder.aBank()
                        .withId(2L)
                        .withName("Новый Банк")
                        .build()),
                Arguments.of(BankTestBuilder.aBank()
                        .withId(3L)
                        .withName("Старый Банк")
                        .build()));
    }

}