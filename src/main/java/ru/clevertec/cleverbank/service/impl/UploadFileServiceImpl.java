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

    /**
     * Реализует метод uploadCheck, который загружает чек по транзакции в формате txt.
     *
     * @param check String, представляющая чек по транзакции
     * @return Path, представляющая путь к файлу
     */
    @Override
    public Path uploadCheck(String check) {
        Path path = Paths.get(findPaths("BankCheck.txt"));
        return writeFile(check, path);
    }

    /**
     * Реализует метод uploadStatement, который загружает выписку по транзакциям в формате txt.
     *
     * @param statement String, представляющая выписку по транзакциям
     * @return Path, представляющая путь к файлу
     */
    @Override
    public Path uploadStatement(String statement) {
        Path path = Paths.get(findPaths("TransactionStatement.txt"));
        return writeFile(statement, path);
    }

    /**
     * Реализует метод uploadAmount, который загружает выписку по суммам транзакций в формате txt.
     *
     * @param amount String, представляющая выписку по суммам транзакций
     * @return Path, представляющая путь к файлу
     */
    @Override
    public Path uploadAmount(String amount) {
        Path path = Paths.get(findPaths("AmountStatement.txt"));
        return writeFile(amount, path);
    }

    /**
     * Метод writeFile, который записывает строку в файл по заданному пути.
     *
     * @param file String, которую нужно записать в файл
     * @param path объект Path, представляющий путь к файлу на сервере
     * @return Path, представляющая путь к файлу
     */
    private static Path writeFile(String file, Path path) {
        log.info("File download link: {}", path);
        try {
            return Files.write(path, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UploadFileException("Sorry! We got Server upload file problems");
        }
    }

    /**
     * Метод findPaths, который возвращает путь к файлу на сервере по заданному названию файла.
     *
     * @param fileName String, представляющая название файла для загрузки
     * @return String, представляющая путь к файлу на сервере
     * @throws UploadFileException если не удается найти путь для загрузки txt файла
     */
    private static String findPaths(String fileName) {
        URL url = UploadFileServiceImpl.class.getResource("/check");
        if (url == null) {
            throw new UploadFileException("Can not find a way to upload a txt file");
        }
        return URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8)
                .concat(fileName)
                .substring(1);
    }

}
