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

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LocalTimeAdapterTest {

    @InjectMocks
    private LocalTimeAdapter localTimeAdapter;
    @Mock
    private JsonWriter out;
    @Mock
    private JsonReader in;
    @Captor
    private ArgumentCaptor<String> captor;

    @Test
    @SneakyThrows
    @DisplayName("test write should capture expected LocalTime")
    void testWriteShouldCaptureExpectedLocalDate() {
        LocalTime expectedTime = LocalTime.of(14, 30, 33);

        doReturn(out)
                .when(out)
                .value(expectedTime.toString());

        localTimeAdapter.write(out, expectedTime);

        verify(out).value(captor.capture());

        String actualTime = captor.getValue();

        assertThat(actualTime).isEqualTo(expectedTime.toString());
    }

    @Test
    @SneakyThrows
    @DisplayName("test read should return expected LocalDate")
    void testReadShouldReturnExpectedLocalDate() {
        LocalTime expectedTime = LocalTime.of(14, 30, 33);

        doReturn(expectedTime.toString())
                .when(in)
                .nextString();

        LocalTime actualTime = localTimeAdapter.read(in);

        assertThat(actualTime).isEqualTo(expectedTime);
    }

}
