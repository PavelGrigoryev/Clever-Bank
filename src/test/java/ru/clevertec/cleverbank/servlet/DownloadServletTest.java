package ru.clevertec.cleverbank.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DownloadServletTest {

    @InjectMocks
    private DownloadServlet downloadServlet;
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private ServletOutputStream outputStream;

    @Test
    @SneakyThrows
    @DisplayName("test doGet should capture expected values")
    void testDoGetShouldCaptureExpectedValues() {
        String file = "BankCheck.txt";
        ArgumentCaptor<String> headerNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentTypeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> characterEncodingCaptor = ArgumentCaptor.forClass(String.class);
        String expectedHeaderName = "Content-Disposition";
        String expectedHeaderValue = "attachment; filename=\"%s\"".formatted(file);
        String expectedContentType = "text/plain";
        String expectedCharacterEncoding = "UTF-8";

        doReturn(file)
                .when(req)
                .getParameter("file");
        doReturn(outputStream)
                .when(resp)
                .getOutputStream();

        downloadServlet.doGet(req, resp);

        verify(resp).setHeader(headerNameCaptor.capture(), headerValueCaptor.capture());
        verify(resp).setContentType(contentTypeCaptor.capture());
        verify(resp).setCharacterEncoding(characterEncodingCaptor.capture());

        String actualHeaderName = headerNameCaptor.getValue();
        String actualHeaderValue = headerValueCaptor.getValue();
        String actualContentType = contentTypeCaptor.getValue();
        String actualCharacterEncoding = characterEncodingCaptor.getValue();

        assertAll(
                () -> assertThat(actualHeaderName).isEqualTo(expectedHeaderName),
                () -> assertThat(actualHeaderValue).isEqualTo(expectedHeaderValue),
                () -> assertThat(actualContentType).isEqualTo(expectedContentType),
                () -> assertThat(actualCharacterEncoding).isEqualTo(expectedCharacterEncoding)
        );
    }

}
