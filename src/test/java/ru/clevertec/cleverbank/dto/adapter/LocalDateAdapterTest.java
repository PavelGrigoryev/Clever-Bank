package ru.clevertec.cleverbank.dto.adapter;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.exception.conflict.LocalDateParseException;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LocalDateAdapterTest {

    @InjectMocks
    private LocalDateAdapter localDateAdapter;
    @Mock
    private JsonWriter out;
    @Mock
    private JsonReader in;
    @Captor
    private ArgumentCaptor<String> captor;

    @Test
    @SneakyThrows
    @DisplayName("test write should capture expected LocalDate")
    void testWriteShouldCaptureExpectedLocalDate() {
        LocalDate expectedDate = LocalDate.of(2023, Month.JULY, 15);

        doReturn(out)
                .when(out)
                .value(expectedDate.toString());

        localDateAdapter.write(out, expectedDate);

        verify(out).value(captor.capture());

        String actualDate = captor.getValue();

        assertThat(actualDate).isEqualTo(expectedDate.toString());
    }

    @Test
    @SneakyThrows
    @DisplayName("test read should return expected LocalDate")
    void testReadShouldReturnExpectedLocalDate() {
        LocalDate expectedDate = LocalDate.of(2023, Month.JULY, 15);

        doReturn(expectedDate.toString())
                .when(in)
                .nextString();

        LocalDate actualDate = localDateAdapter.read(in);

        assertThat(actualDate).isEqualTo(expectedDate);
    }

    @Test
    @SneakyThrows
    @DisplayName("test read should throw LocalDateParseException with expected message")
    void testReadShouldThrowLocalDateParseException() {
        String expectedMessage = "Date is out of pattern: yyyy-MM-dd. Right example: 2023-08-30";

        doReturn("999")
                .when(in)
                .nextString();

        Exception exception = assertThrows(LocalDateParseException.class, () -> localDateAdapter.read(in));
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

}
