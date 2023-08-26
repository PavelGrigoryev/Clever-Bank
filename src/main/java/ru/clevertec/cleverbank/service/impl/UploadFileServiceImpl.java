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
        Path path = Paths.get(findPaths());
        log.info("File download link: {}", path);
        try {
            Files.write(path, check.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UploadFileException("Sorry! We got Server upload file problems");
        }
    }

    private static String findPaths() {
        URL pdfURL = UploadFileServiceImpl.class.getResource("/check");
        if (pdfURL == null) {
            throw new UploadFileException("Can not find a way to upload a txt file");
        }
        return URLDecoder.decode(pdfURL.getPath(), StandardCharsets.UTF_8)
                .concat("BankCheck.txt")
                .substring(1);
    }

}