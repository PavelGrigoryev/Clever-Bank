package ru.clevertec.cleverbank.service.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.service.CheckService;

import java.time.format.DateTimeFormatter;

@Slf4j
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
                | Банк отправителя: %39s |
                | Банк получателя: %40s |
                | Счет получателя: %40s |
                | Сумма: %46s %s |
                %s
                """.formatted("\n",
                repeat,
                "Банковский чек", "|",
                response.transactionId(),
                response.date(), response.time().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                response.type().getName(),
                response.senderBankName(),
                response.recipientBankName(),
                response.recipientAccountId(),
                response.type() == Type.WITHDRAWAL ? "-" + response.sum() : "" + response.sum(), response.currency(),
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
                "Банковский чек", "|",
                response.transactionId(),
                response.date(), response.time().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                response.type().getName(),
                response.senderBankName(),
                response.recipientBankName(),
                response.senderAccountId(),
                response.recipientAccountId(),
                response.sum(), response.currency(),
                repeat);
    }

    @Override
    public String createTransactionStatement(TransactionStatementResponse response) {
        String line = "|";
        StringBuilder result = new StringBuilder();
        response.transactions()
                .forEach(transaction -> result.append("%s %4s %-15s от %-10s %9s %s %s%s"
                        .formatted(transaction.date(), line, transaction.type().getName(), transaction.userLastname(),
                                line, transaction.type() == Type.WITHDRAWAL ? "-" + transaction.sum() : "" + transaction.sum(),
                                response.currency(), "\n")));
        result.deleteCharAt(result.length() - 1);
        return """
                %s%36s
                %38s
                Клиент %25s %s %s %s
                Счет %27s %s
                Валюта %25s %s
                Дата открытия %18s %s
                Период %25s %s - %s
                Дата и время формирования %6s %s,  %s
                Остаток %24s %s %s
                %8s %6s %20s %18s %8s
                %s
                %s
                """.formatted("\n",
                "Выписка",
                response.bankName(),
                line, response.lastname(), response.firstname(), response.surname(),
                line, response.accountId(),
                line, response.currency(),
                line, response.openingDate(),
                line, response.from(), response.to(),
                line, response.formationDate(), response.formationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                line, response.balance(), response.currency(),
                "Дата", line, "Примечание", line, "Сумма",
                "-".repeat(70),
                result);
    }

}
