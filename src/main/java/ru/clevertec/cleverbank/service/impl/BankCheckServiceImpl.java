package ru.clevertec.cleverbank.service.impl;

import ru.clevertec.cleverbank.dto.ChangeBalanceResponse;
import ru.clevertec.cleverbank.service.BankCheckService;

import java.time.format.DateTimeFormatter;

public class BankCheckServiceImpl implements BankCheckService {

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
                response.sum(),
                response.currency(),
                repeat);
    }

}
