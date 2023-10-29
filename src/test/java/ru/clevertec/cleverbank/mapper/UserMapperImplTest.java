package ru.clevertec.cleverbank.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.user.UserRequestTestBuilder;
import ru.clevertec.cleverbank.builder.user.UserResponseTestBuilder;
import ru.clevertec.cleverbank.builder.user.UserTestBuilder;
import ru.clevertec.cleverbank.dto.user.UserRequest;
import ru.clevertec.cleverbank.dto.user.UserResponse;
import ru.clevertec.cleverbank.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserMapperImplTest {

    @InjectMocks
    private UserMapperImpl userMapper;

    @Nested
    class ToResponseTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            User user = UserTestBuilder.aUser().build();
            UserResponse expected = UserResponseTestBuilder.aUserResponse().build();

            UserResponse actual = userMapper.toResponse(user);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            UserResponse actual = userMapper.toResponse(null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class ToResponseListTest {

        @Test
        @DisplayName("test should return expected list")
        void testShouldReturnExpectedList() {
            List<User> users = List.of(UserTestBuilder.aUser().build());
            List<UserResponse> expectedList = List.of(UserResponseTestBuilder.aUserResponse().build());

            List<UserResponse> actualList = userMapper.toResponseList(users);

            assertThat(actualList).isEqualTo(expectedList);
        }

        @Test
        @DisplayName("test should return empty list")
        void testShouldReturnEmptyList() {
            List<UserResponse> actual = userMapper.toResponseList(List.of());

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            List<UserResponse> actual = userMapper.toResponseList(null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class FromSaveRequestTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            UserRequest request = UserRequestTestBuilder.aUserRequest().build();
            User expected = UserTestBuilder.aUser()
                    .withId(null)
                    .withRegisterDate(LocalDate.now())
                    .build();

            User actual = userMapper.fromSaveRequest(request);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            User actual = userMapper.fromSaveRequest(null);

            assertThat(actual).isNull();
        }

    }

    @Nested
    class FromUpdateRequestTest {

        @Test
        @DisplayName("test should return expected value")
        void testShouldReturnExpectedValue() {
            UserRequest request = UserRequestTestBuilder.aUserRequest().build();
            User expected = UserTestBuilder.aUser().build();

            User actual = userMapper.fromUpdateRequest(request, expected.getId(), expected.getRegisterDate());

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should return null")
        void testShouldReturnNull() {
            User actual = userMapper.fromUpdateRequest(null, null, null);

            assertThat(actual).isNull();
        }

    }

}
