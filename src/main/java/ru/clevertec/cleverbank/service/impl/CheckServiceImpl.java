package ru.clevertec.cleverbank.service.impl;

import ru.clevertec.cleverbank.dto.transaction.AmountStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.service.CheckService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class CheckServiceImpl implements CheckService {

    /**
     * Реализует метод createChangeBalanceCheck, который создает чек по операции изменения баланса счёта.
     *
     * @param response объект ChangeBalanceResponse, представляющий ответ с данными об измененном балансе счёта
     * @return String, представляющая чек по операции изменения баланса счёта
     */
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
                response.bankSenderName(),
                response.bankRecipientName(),
                response.accountRecipientId(),
                response.type() == Type.WITHDRAWAL ? "-" + response.sum() : "" + response.sum(), response.currency(),
                repeat);
    }

    /**
     * Реализует метод createTransferBalanceCheck, который создает чек по операции перевода средств между счетами.
     *
     * @param response объект TransferBalanceResponse, представляющий ответ с данными о переведенных средствах между счетами
     * @return String, представляющая чек по операции перевода средств между счетами
     */
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
                response.bankSenderName(),
                response.bankRecipientName(),
                response.accountSenderId(),
                response.accountRecipientId(),
                response.sum(), response.currency(),
                repeat);
    }

    /**
     * Реализует метод createTransactionStatement, который создает выписку по транзакциям по счёту за определенный
     * период дат.
     *
     * @param response объект TransactionStatementResponse, представляющий ответ со списком транзакций по счёту за период дат
     * @return String, представляющая выписку по транзакциям по счёту за период дат
     */
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

    /**
     * Реализует метод createAmountStatement, который создает выписку по суммам транзакций по счёту за определенный
     * период дат.
     *
     * @param response объект AmountStatementResponse, представляющий ответ с суммами потраченных и полученных средств
     *                 по счёту за период дат
     * @return String, представляющая выписку по суммам транзакций по счёту за период дат
     */
    @Override
    public String createAmountStatement(AmountStatementResponse response) {
        String line = "|";
        return """
                %s%36s
                %32s
                Клиент %25s %s %s %s
                Счет %27s %s
                Валюта %25s %s
                Дата открытия %18s %s
                Период %25s %s - %s
                Дата и время формирования %6s %s,  %s
                Остаток %24s %s %s
                %20s %6s %8s
                %42s
                %20s %6s %12s
                """.formatted("\n",
                "Выписка по деньгам",
                response.bankName(),
                line, response.lastname(), response.firstname(), response.surname(),
                line, response.accountId(),
                line, response.currency(),
                line, response.openingDate(),
                line, response.from(), response.to(),
                line, response.formationDate(), response.formationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                line, response.balance(), response.currency(),
                "Приход", line, "Уход",
                "-".repeat(32),
                response.receivedFunds() == null ? BigDecimal.ZERO : response.receivedFunds(), line,
                response.spentFunds() == null ? BigDecimal.ZERO : "-" + response.spentFunds());
    }

}
