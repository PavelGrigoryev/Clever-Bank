package ru.clevertec.cleverbank.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.transaction.AmountStatementResponseTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.ChangeBalanceResponseTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.ExchangeBalanceResponseTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransactionStatementResponseTestBuilder;
import ru.clevertec.cleverbank.builder.transaction.TransferBalanceResponseTestBuilder;
import ru.clevertec.cleverbank.dto.transaction.AmountStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.ExchangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CheckServiceImplTest {

    @Spy
    private CheckServiceImpl checkService;

    @Test
    @DisplayName("test createChangeBalanceCheck should return expected string")
    void testCreateChangeBalanceCheck() {
        ChangeBalanceResponse response = ChangeBalanceResponseTestBuilder.aChangeBalanceResponse().build();
        String expected = """
                                
                -------------------------------------------------------------
                |                       Банковский чек                      |
                | Чек:                                                    1 |
                | 2020-04-12                                       14:20:33 |
                | Тип транзакции:                                Пополнение |
                | Банк отправителя:                             Клевер-Банк |
                | Банк получателя:                               Альфа-Банк |
                | Счет получателя:       5X92 ISKH ZUAT 2YF5 D0A9 C2Z4 7UIZ |
                | Сумма:                                           2000 BYN |
                -------------------------------------------------------------
                """;

        String actual = checkService.createChangeBalanceCheck(response);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test createTransferBalanceCheck should return expected string")
    void testCreateTransferBalanceCheck() {
        TransferBalanceResponse response = TransferBalanceResponseTestBuilder.aTransferBalanceResponse().build();
        String expected = """
                                
                -------------------------------------------------------------
                |                       Банковский чек                      |
                | Чек:                                                    1 |
                | 2020-04-12                                       14:20:33 |
                | Тип транзакции:                                   Перевод |
                | Банк отправителя:                             Клевер-Банк |
                | Банк получателя:                               Альфа-Банк |
                | Счет отправителя:      5X92 ISKH ZUAT 2YF5 D0A9 C2Z4 7UIZ |
                | Счет получателя:       BL7U 2IQC IB7Y 3Q0F ZSSW KZOE YRI6 |
                | Сумма:                                           2000 BYN |
                -------------------------------------------------------------
                """;

        String actual = checkService.createTransferBalanceCheck(response);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test createExchangeBalanceCheck should return expected string")
    void testCreateExchangeBalanceCheck() {
        ExchangeBalanceResponse response = ExchangeBalanceResponseTestBuilder.aExchangeBalanceResponse().build();
        String expected = """
                                
                -------------------------------------------------------------
                |                       Банковский чек                      |
                | Чек:                                                    1 |
                | 2020-04-12                                       14:20:33 |
                | Тип транзакции:                                     Обмен |
                | Банк отправителя:                             Клевер-Банк |
                | Банк получателя:                               Альфа-Банк |
                | Счет отправителя:      G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q |
                | Счет получателя:       FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62 |
                | Сумма отправителя:                                 10 BYN |
                | Сумма получателя:                                2.89 EUR |
                -------------------------------------------------------------
                """;

        String actual = checkService.createExchangeBalanceCheck(response);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test createTransactionStatement should return expected string")
    void testCreateTransactionStatement() {
        TransactionStatementResponse response = TransactionStatementResponseTestBuilder.aTransactionStatementResponse().build();
        String expected = """
                                
                                             Выписка
                                           Клевер-Банк
                Клиент                         | Иванов Иван Иванович
                Счет                           | 5X92 ISKH ZUAT 2YF5 D0A9 C2Z4 7UIZ
                Валюта                         | BYN
                Дата открытия                  | 2020-03-01
                Период                         | 2020-04-12 - 2020-05-12
                Дата и время формирования      | 2023-09-02,  15:30:45
                Остаток                        | 3000 BYN
                    Дата      |           Примечание                  |    Сумма
                ----------------------------------------------------------------------
                2020-04-12    | Перевод         от Иванов             | -2000 BYN
                """;

        String actual = checkService.createTransactionStatement(response);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test createAmountStatement should return expected string")
    void testCreateAmountStatement() {
        AmountStatementResponse response = AmountStatementResponseTestBuilder.aAmountStatementResponse().build();
        String expected = """
                                
                                  Выписка по деньгам
                                     Клевер-Банк
                Клиент                         | Иванов Иван Иванович
                Счет                           | 5X92 ISKH ZUAT 2YF5 D0A9 C2Z4 7UIZ
                Валюта                         | BYN
                Дата открытия                  | 2020-03-01
                Период                         | 2020-04-12 - 2020-05-12
                Дата и время формирования      | 2023-09-02,  15:30:45
                Остаток                        | 3000 BYN
                              Приход      |     Уход
                          --------------------------------
                                   1      |          -10
                """;

        String actual = checkService.createAmountStatement(response);

        assertThat(actual).isEqualTo(expected);
    }

}
