package ru.clevertec.cleverbank.service.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UploadFileServiceImplTest {

    @Spy
    private UploadFileServiceImpl uploadFileService;

    @Test
    @SneakyThrows
    void testUploadCheck() {
        String check = "Check";
        URL url = UploadFileServiceImpl.class.getResource("/check");
        Path path = Paths.get(URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8)
                .concat("BankCheck.txt")
                .substring(1));

        uploadFileService.uploadCheck(check);

        assertThat(Files.exists(path)).isTrue();
    }

    @Test
    @SneakyThrows
    void testUploadStatement() {
        String statement = "Statement";
        URL url = UploadFileServiceImpl.class.getResource("/check");
        Path path = Paths.get(URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8)
                .concat("TransactionStatement.txt")
                .substring(1));

        uploadFileService.uploadStatement(statement);

        assertThat(Files.exists(path)).isTrue();
    }

    @Test
    @SneakyThrows
    void testUploadAmount() {
        String amount = "Amount";
        URL url = UploadFileServiceImpl.class.getResource("/check");
        Path path = Paths.get(URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8)
                .concat("AmountStatement.txt")
                .substring(1));

        uploadFileService.uploadAmount(amount);

        assertThat(Files.exists(path)).isTrue();
    }

}
