package ru.clevertec.cleverbank.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EncodingFilterTest {

    @InjectMocks
    private EncodingFilter encodingFilter;
    @Mock
    private ServletRequest request;
    @Mock(extraInterfaces = HttpServletResponse.class)
    private ServletResponse response;
    @Mock
    private FilterChain chain;
    private HttpServletResponse resp;

    @BeforeEach
    void setUp() {
        resp = (HttpServletResponse) mockingDetails(response).getMock();
    }

    @Test
    @SneakyThrows
    @DisplayName("test doFilter should capture expected values")
    void testDoFilterShouldCaptureExpectedValues() {
        ArgumentCaptor<String> contentTypeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> characterEncodingCaptor = ArgumentCaptor.forClass(String.class);
        String expectedContentType = "application/json";
        String expectedCharacterEncoding = "UTF-8";

        encodingFilter.doFilter(request, response, chain);

        verify(resp).setContentType(contentTypeCaptor.capture());
        verify(resp).setCharacterEncoding(characterEncodingCaptor.capture());
        verify(chain).doFilter(request, response);

        String actualContentType = contentTypeCaptor.getValue();
        String actualCharacterEncoding = characterEncodingCaptor.getValue();

        assertAll(
                () -> assertThat(actualContentType).isEqualTo(expectedContentType),
                () -> assertThat(actualCharacterEncoding).isEqualTo(expectedCharacterEncoding)
        );
    }

}
