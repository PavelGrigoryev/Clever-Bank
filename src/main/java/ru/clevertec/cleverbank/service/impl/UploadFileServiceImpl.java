package ru.clevertec.cleverbank.service.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.exception.internalservererror.UploadFileException;
import ru.clevertec.cleverbank.service.UploadFileService;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class UploadFileServiceImpl implements UploadFileService {

    @Override
    public void uploadCheck(String check) {
        Path path = Paths.get(findPaths("BankCheck.txt"));
        writeFile(check, path);
    }

    @Override
    public void uploadStatement(String statement) {
        Path path = Paths.get(findPaths("TransactionStatement.txt"));
        writeFile(statement, path);
    }

    @Override
    public void uploadAmount(String amount) {
        Path path = Paths.get(findPaths("AmountStatement.txt"));
        writeFile(amount, path);
    }

    private static void writeFile(String statement, Path path) {
        log.info("File download link: {}", path);
        try {
            Files.write(path, statement.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UploadFileException("Sorry! We got Server upload file problems");
        }
    }

    private static String findPaths(String fileName) {
        URL pdfURL = UploadFileServiceImpl.class.getResource("/check");
        if (pdfURL == null) {
            throw new UploadFileException("Can not find a way to upload a txt file");
        }
        return URLDecoder.decode(pdfURL.getPath(), StandardCharsets.UTF_8)
                .concat(fileName)
                .substring(1);
    }

}
