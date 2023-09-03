package ru.clevertec.cleverbank.service.impl;

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
import ru.clevertec.cleverbank.dao.UserDAO;
import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.user.UserRequest;
import ru.clevertec.cleverbank.dto.user.UserResponse;
import ru.clevertec.cleverbank.exception.badrequest.UniquePhoneNumberException;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.exception.notfound.UserNotFoundException;
import ru.clevertec.cleverbank.mapper.UserMapper;
import ru.clevertec.cleverbank.model.User;
import ru.clevertec.cleverbank.util.user.UserRequestTestBuilder;
import ru.clevertec.cleverbank.util.user.UserResponseTestBuilder;
import ru.clevertec.cleverbank.util.user.UserTestBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserDAO userDAO;
    @Mock
    private UserMapper userMapper;
    @Captor
    private ArgumentCaptor<User> captor;

    @Nested
    class FindByIdTest {

        @Test
        void testShouldThrowUserNotFoundExceptionWithExpectedMessage() {
            long id = 1L;
            String expectedMessage = "User with ID " + id + " is not found!";

            Exception exception = assertThrows(UserNotFoundException.class, () -> userService.findByIdResponse(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

        @Test
        void testShouldReturnExpectedResponse() {
            UserResponse expected = UserResponseTestBuilder.aUserResponse().build();
            User user = UserTestBuilder.aUser().build();
            long id = expected.id();

            doReturn(expected)
                    .when(userMapper)
                    .toResponse(user);
            doReturn(Optional.of(user))
                    .when(userDAO)
                    .findById(id);

            UserResponse actual = userService.findByIdResponse(id);

            assertThat(actual).isEqualTo(expected);
        }

    }

    @Nested
    class FindAllTest {

        @Test
        void testShouldReturnListOfSizeOne() {
            UserResponse response = UserResponseTestBuilder.aUserResponse().build();
            User user = UserTestBuilder.aUser().build();
            int expectedSize = 1;

            doReturn(List.of(response))
                    .when(userMapper)
                    .toResponseList(List.of(user));
            doReturn(List.of(user))
                    .when(userDAO)
                    .findAll();

            List<UserResponse> actual = userService.findAll();

            assertThat(actual).hasSize(expectedSize);
        }

        @Test
        void testShouldReturnListThatContainsExpectedResponse() {
            UserResponse expected = UserResponseTestBuilder.aUserResponse().build();
            User user = UserTestBuilder.aUser().build();

            doReturn(List.of(expected))
                    .when(userMapper)
                    .toResponseList(List.of(user));
            doReturn(List.of(user))
                    .when(userDAO)
                    .findAll();

            List<UserResponse> actual = userService.findAll();

            assertThat(actual.get(0)).isEqualTo(expected);
        }

        @Test
        void testShouldReturnEmptyList() {
            doReturn(List.of())
                    .when(userDAO)
                    .findAll();

            List<UserResponse> actual = userService.findAll();

            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class SaveTest {

        @ParameterizedTest(name = "{arguments} test")
        @MethodSource("ru.clevertec.cleverbank.service.impl.UserServiceImplTest#getArgumentsForSaveTest")
        void testShouldCaptureValue(User expected) {
            UserRequest request = UserRequestTestBuilder.aUserRequest().build();
            UserResponse response = UserResponseTestBuilder.aUserResponse()
                    .withId(expected.getId())
                    .withLastname(expected.getLastname())
                    .build();

            doReturn(expected)
                    .when(userMapper)
                    .fromRequest(request);
            doReturn(expected)
                    .when(userDAO)
                    .save(expected);
            doReturn(response)
                    .when(userMapper)
                    .toResponse(expected);

            userService.save(request);
            verify(userDAO).save(captor.capture());

            User userCaptor = captor.getValue();
            assertThat(userCaptor).isEqualTo(expected);
        }

        @Test
        void testShouldThrowUniquePhoneNumberExceptionWithExpectedMessage() {
            String mobileNumber = "+7 (900) 123-45-67";
            String expectedMessage = "User with phone number " + mobileNumber + " is already exist";
            User user = UserTestBuilder.aUser().build();
            UserRequest request = UserRequestTestBuilder.aUserRequest().build();

            doReturn(user)
                    .when(userMapper)
                    .fromRequest(request);
            doThrow(new JDBCConnectionException())
                    .when(userDAO)
                    .save(user);

            Exception exception = assertThrows(UniquePhoneNumberException.class, () -> userService.save(request));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

    }

    @Nested
    class UpdateTest {

        @Test
        void testShouldReturnUpdatedResponse() {
            User user = UserTestBuilder.aUser().build();
            UserRequest request = UserRequestTestBuilder.aUserRequest().build();
            UserResponse expected = UserResponseTestBuilder.aUserResponse().build();

            doReturn(Optional.of(user))
                    .when(userDAO)
                    .findById(user.getId());
            doReturn(user)
                    .when(userMapper)
                    .fromRequest(request);
            doReturn(user)
                    .when(userDAO)
                    .update(user);
            doReturn(expected)
                    .when(userMapper)
                    .toResponse(user);

            UserResponse actual = userService.update(user.getId(), request);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void testShouldThrowUniquePhoneNumberExceptionWithExpectedMessage() {
            String mobileNumber = "+7 (900) 123-45-67";
            String expectedMessage = "User with phone number " + mobileNumber + " is already exist";
            User user = UserTestBuilder.aUser().build();
            UserRequest request = UserRequestTestBuilder.aUserRequest().build();

            doReturn(Optional.of(user))
                    .when(userDAO)
                    .findById(user.getId());
            doReturn(user)
                    .when(userMapper)
                    .fromRequest(request);
            doThrow(new JDBCConnectionException())
                    .when(userDAO)
                    .update(user);

            Exception exception = assertThrows(UniquePhoneNumberException.class, () -> userService.update(1L, request));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

    }

    @Nested
    class DeleteTest {

        @Test
        void testShouldReturnExpectedResponse() {
            User user = UserTestBuilder.aUser().build();
            DeleteResponse expected = new DeleteResponse("User with ID " + user.getId() + " was successfully deleted");

            doReturn(Optional.of(user))
                    .when(userDAO)
                    .delete(user.getId());

            DeleteResponse actual = userService.delete(user.getId());

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void testShouldThrowUserNotFoundExceptionWithExpectedMessage() {
            long id = 1L;
            String expectedMessage = "No User with ID " + id + " to delete";

            Exception exception = assertThrows(UserNotFoundException.class, () -> userService.delete(id));
            String actualMessage = exception.getMessage();

            assertThat(actualMessage).isEqualTo(expectedMessage);
        }

    }

    private static Stream<Arguments> getArgumentsForSaveTest() {
        return Stream.of(Arguments.of(UserTestBuilder.aUser().build()),
                Arguments.of(UserTestBuilder.aUser()
                        .withId(2L)
                        .withLastname("Сидоров")
                        .build()),
                Arguments.of(UserTestBuilder.aUser()
                        .withId(3L)
                        .withFirstname("Петя")
                        .build()));
    }

}