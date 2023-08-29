package ru.clevertec.cleverbank.service.impl;

import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.service.CheckService;

import java.time.format.DateTimeFormatter;

public class CheckServiceImpl implements CheckService {

    @Override
    public String createChangeBalanceCheck(ChangeBalanceResponse response) {
        String repeat = "-".repeat(61);
        return """
                %s%s
                | %36s%23s
                | Чек: %52s |
                | %s %46s |
                | Тип транзакции: %41s |
                | Банк получателя: %40s |
                | Счет получателя: %40s |
                | Сумма: %46s %s |
                %s
                """.formatted("\n",
                repeat,
                "Банковский чек",
                "|",
                response.transactionId(),
                response.date(),
                response.time().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                response.type(),
                response.recipientBankName(),
                response.recipientAccountId(),
                response.type() == Type.WITHDRAWAL ? "-" + response.sum() : "+" + response.sum(),
                response.currency(),
                repeat);
    }

    @Override
    public String createTransferBalanceCheck(TransferBalanceResponse response) {
        String repeat = "-".repeat(61);
        return """
                %s%s
                | %36s%23s
                | Чек: %52s |
                | %s %46s |
                | Тип транзакции: %41s |
                | Банк отправителя: %39s |
                | Банк получателя: %40s |
                | Счет отправителя: %39s |
                | Счет получателя: %40s |
                | Сумма: %46s %s |
                %s
                """.formatted("\n",
                repeat,
                "Банковский чек",
                "|",
                response.transactionId(),
                response.date(),
                response.time().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                response.type(),
                response.senderBankName(),
                response.recipientBankName(),
                response.senderAccountId(),
                response.recipientAccountId(),
                response.sum(),
                response.currency(),
                repeat);
    }

}
