package ru.clevertec.cleverbank.service.impl;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UploadFileServiceImplTest {

    @Spy
    private UploadFileServiceImpl uploadFileService;

    @Test
    @SneakyThrows
    @DisplayName("test uploadCheck method should save a file.txt")
    void testUploadCheck() {
        String check = "Hello, Check!";
        FileSystem fileSystem = MemoryFileSystemBuilder.newEmpty().build();

        Path memoryPath = fileSystem.getPath("memory.txt");
        Files.writeString(memoryPath, check);

        Path actualPath = uploadFileService.uploadCheck(check);

        assertThat(Files.exists(actualPath)).isTrue();
        assertThat(Files.readString(actualPath)).startsWith(Files.readString(memoryPath));

        fileSystem.close();
    }

    @Test
    @SneakyThrows
    @DisplayName("test uploadStatement method should save a file.txt")
    void testUploadStatement() {
        String statement = "Hello, Statement!";
        FileSystem fileSystem = MemoryFileSystemBuilder.newEmpty().build();

        Path memoryPath = fileSystem.getPath("memory.txt");
        Files.writeString(memoryPath, statement);

        Path actualPath = uploadFileService.uploadStatement(statement);

        assertThat(Files.exists(actualPath)).isTrue();
        assertThat(Files.readString(actualPath)).startsWith(Files.readString(memoryPath));

        fileSystem.close();
    }

    @Test
    @SneakyThrows
    @DisplayName("test uploadAmount method should save a file.txt")
    void testUploadAmount() {
        String amount = "Hello, Amount!";
        FileSystem fileSystem = MemoryFileSystemBuilder.newEmpty().build();

        Path memoryPath = fileSystem.getPath("memory.txt");
        Files.writeString(memoryPath, amount);

        Path actualPath = uploadFileService.uploadAmount(amount);

        assertThat(Files.exists(actualPath)).isTrue();
        assertThat(Files.readString(actualPath)).startsWith(Files.readString(memoryPath));

        fileSystem.close();
    }

}
