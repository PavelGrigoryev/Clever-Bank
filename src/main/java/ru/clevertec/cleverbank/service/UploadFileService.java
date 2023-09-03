package ru.clevertec.cleverbank.service;

public interface UploadFileService {

    void uploadCheck(String check);

    void uploadStatement(String statement);

    void  uploadAmount(String amount);

}
