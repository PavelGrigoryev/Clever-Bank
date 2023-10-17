package ru.clevertec.cleverbank.service;

import java.nio.file.Path;

public interface UploadFileService {

    Path uploadCheck(String check);

    Path uploadStatement(String statement);

    Path uploadAmount(String amount);

}
