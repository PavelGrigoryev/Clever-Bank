# Clever-Bank application

## Автор: [Grigoryev Pavel](https://pavelgrigoryev.github.io/GrigoryevPavel/)

### Технологии, которые я использовал на проекте:

* Java 17
* Gradle 8.0.2
* Lombok plugin 8.2.2
* Servlet API 6.0.0
* Postgresql 42.6.0
* Mongodb-driver-sync 4.11.0
* HikariCP 5.0.1
* Gson 2.10.1
* Slf4j-API 2.0.7
* Logback logger 1.4.11
* SnakeYaml 2.1
* Liquibase 4.23.1
* Mapstruct 1.5.3.Final
* AspectJ 1.9.20
* Junit 5.10.0
* AssertJ 3.24.2
* Mockito 5.5.0
* MemoryFileSystem 2.6.1

### Инструкция для запуска приложения локально:

1. У вас должна быть
   установлена [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html),
   [Tomcat 10.1](https://tomcat.apache.org/download-10.cgi), [Intellij IDEA Ultimate](https://www.jetbrains.com/idea/download/),
   [Postgresql](https://www.postgresql.org/download/) и [MongoDB](https://www.mongodb.com/) (P.S: Postgresql и MongoDB
   можно развернуть в докере).
2. В Postgresql нужно создать базу данных. Как пример: "clever_bank" . Sql: CREATE DATABASE clever_bank
3. В [application.yaml](src/main/resources/application.yaml) в строчке №3 введите ваш username для Postgresql, в строчке
   №4 введите ваш password для Postgresql.
4. В настройках идеи Run -> Edit Configurations... вы должны поставить Tomcat 10.1. И в графе Deployment
   очистить Application context.
5. При запуске приложения Liquibase сам создаст таблицы и наполнит их дефолтными значениями.
6. И запустится два scheduler:
    * Первый будет регулярно, по расписанию (раз в полминуты), проверять, нужно ли начислять проценты на остаток
      счета в конце месяца.
    * Второй будет подключаться раз в 24 часа к api НБ РБ и получать актуальный курс валют.
7. Приложение готово к использованию.

### Http Запросы

* [transactions.http](src/main/resources/http/transactions.http) для транзакций
* [accounts.http](src/main/resources/http/accounts.http) для счетов
* [banks.http](src/main/resources/http/banks.http) для банков
* [users.http](src/main/resources/http/users.http) для пользователей
* [download.http](src/main/resources/http/dowload.http) для скачивания чеков и выписок по транзакциям

### Unit тесты

1. Написано 369 unit тестов
2. Покрытие классов = 93%, методов = 88%, линий = 91%
3. Вы можете запустить тесты для этого проекта, выполнив в корне проекта: `./gradlew test`

## Функциональность

В сумме приложение умеет:

***

### DownloadServlet

***

#### GET загрузить txt файл с банковскими чеками

Request body:

* file = название файла для скачивания
    * BankCheck.txt - для чеков снятия, пополнения, переводов и обмена
    * TransactionStatement.txt - для выписок по транзакциям пользователей за период времени
    * AmountStatement.txt - для выписок о количестве потраченных и полученных средств за период времени

Response Status 200:

### Банковский чек

```text
-------------------------------------------------------------
|                       Банковский чек                      |
| Чек:                                                   11 |
| 2023-09-01                                       21:52:18 |
| Тип транзакции:                                Пополнение |
| Банк отправителя:                             Клевер-Банк |
| Банк получателя:                           Россельхозбанк |
| Счет получателя:       0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4 |
| Сумма:                                           1200 BYN |
-------------------------------------------------------------

-------------------------------------------------------------
|                       Банковский чек                      |
| Чек:                                                   12 |
| 2023-09-01                                       21:52:21 |
| Тип транзакции:                                   Перевод |
| Банк отправителя:                          Россельхозбанк |
| Банк получателя:                              Клевер-Банк |
| Счет отправителя:      0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4 |
| Счет получателя:       G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q |
| Сумма:                                           1200 BYN |
-------------------------------------------------------------

-------------------------------------------------------------
|                       Банковский чек                      |
| Чек:                                                   13 |
| 2023-10-24                                       17:39:48 |
| Тип транзакции:                                    Снятие |
| Банк отправителя:                             Клевер-Банк |
| Банк получателя:                           Россельхозбанк |
| Счет получателя:       0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4 |
| Сумма:                                          -1200 BYN |
-------------------------------------------------------------

-------------------------------------------------------------
|                       Банковский чек                      |
| Чек:                                                   14 |
| 2023-10-24                                       17:39:51 |
| Тип транзакции:                                     Обмен |
| Банк отправителя:                             Клевер-Банк |
| Банк получателя:                               Альфа-Банк |
| Счет отправителя:      G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q |
| Счет получателя:       FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62 |
| Сумма отправителя:                                 10 BYN |
| Сумма получателя:                                2.93 EUR |
-------------------------------------------------------------
```

### Выписка по транзакциям

```text

                             Выписка
                           Клевер-Банк
Клиент                         | Зайцева Елена Евгеньевна
Счет                           | G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q
Валюта                         | BYN
Дата открытия                  | 2017-03-01
Период                         | 2023-09-01 - 2023-09-01
Дата и время формирования      | 2023-09-01,  21:51:31
Остаток                        | 11700.00 BYN
    Дата      |           Примечание                  |    Сумма
----------------------------------------------------------------------
2023-09-01    | Пополнение      от Зайцева            | 1200 BYN
2023-09-01    | Пополнение      от Зайцева            | 1200 BYN
2023-09-01    | Перевод         от Петров             | 1200 BYN
2023-09-01    | Перевод         от Петров             | 1200 BYN
2023-09-01    | Перевод         от Петров             | 1200 BYN
2023-09-01    | Перевод         от Петров             | 1200 BYN
2023-09-01    | Перевод         от Петров             | 1200 BYN
2023-09-01    | Перевод         от Петров             | 1200 BYN
2023-09-01    | Пополнение      от Зайцева            | 1200 BYN
2023-09-01    | Снятие          от Зайцева            | -1200 BYN

```

### Информация о количестве потраченных и полученных средств

```text

                  Выписка по деньгам
                     Клевер-Банк
Клиент                         | Зайцева Елена Евгеньевна
Счет                           | G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q
Валюта                         | BYN
Дата открытия                  | 2017-03-01
Период                         | 2023-09-01 - 2023-09-01
Дата и время формирования      | 2023-09-01,  21:52:30
Остаток                        | 12900.00 BYN
              Приход      |     Уход
          --------------------------------
                9600      |        -1200
                
```

***

### TransactionServlet

***

#### POST пополнения и снятия средств со счета

Request:

* account_sender_id = номер счёта отправителя
* account_recipient_id = номер счёта получателя
* sum = сумма денег
* type = тип, может быть REPLENISHMENT(Пополнение) либо WITHDRAWAL(снятие)

```json
{
  "account_sender_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
  "account_recipient_id": "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4",
  "sum": 1200,
  "type": "REPLENISHMENT"
}
```

Response Status 201:

```json
{
  "transaction_id": 1,
  "date": "2023-09-01",
  "time": "21:19:28",
  "currency": "BYN",
  "type": "REPLENISHMENT",
  "bank_sender_name": "Клевер-Банк",
  "bank_recipient_name": "Россельхозбанк",
  "account_recipient_id": "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4",
  "sum": 1200,
  "old_balance": 5000.00,
  "new_balance": 6200.00
}
```

Response Status 400:

```json
{
  "exception": "Insufficient funds in the account! You want to withdrawal/transfer 7401, but you have only 7400.00"
}
```

Response Status 404:

```json
{
  "exception": "Account with ID G5QZ 6B43 A6XG AHNK CO6S PSO6 718 is not found!"
}
```

Response Status 409:

```json
{
  "violations": [
    {
      "fieldName": "type",
      "exception": "Available types are: REPLENISHMENT or WITHDRAWAL"
    },
    {
      "fieldName": "sum",
      "exception": "Field must be grater than 0"
    }
  ]
}
```

#### PUT перевод с одного счёта на другой

Request:

* account_sender_id = номер счёта отправителя
* account_recipient_id = номер счёта получателя
* sum = сумма денег
* type = тип должен может быть TRANSFER(Перевод)

```json
{
  "account_sender_id": "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4",
  "account_recipient_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
  "sum": 1200,
  "type": "TRANSFER"
}
```

Response Status 201:

```json
{
  "transaction_id": 3,
  "date": "2023-09-01",
  "time": "21:27:55",
  "currency": "BYN",
  "type": "TRANSFER",
  "bank_sender_name": "Россельхозбанк",
  "bank_recipient_name": "Клевер-Банк",
  "account_sender_id": "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4",
  "account_recipient_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
  "sum": 1200,
  "sender_old_balance": 7400.00,
  "sender_new_balance": 6200.00,
  "recipient_old_balance": 4500.00,
  "recipient_new_balance": 5700.00
}
```

Response Status 409:

```json
{
  "violations": [
    {
      "fieldName": "sum",
      "exception": "Field must be grater than 0"
    }
  ]
}
```

Response Status 500:

```json
{
  "exception": "Transaction rollback, cause: Insufficient funds in the account! You want to change balance 1200, but you have only 200.00"
}
```

#### PUT перевод с одного счёта на другой с обменом валют по курсу НБ РБ

Request:

* account_sender_id = номер счёта отправителя
* account_recipient_id = номер счёта получателя
* sum = сумма денег
* type = тип должен может быть EXCHANGE(Обмен)

```json
{
  "account_sender_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
  "account_recipient_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
  "sum": 10,
  "type": "EXCHANGE"
}
```

Response Status 201:

```json
{
  "transaction_id": 11,
  "date": "2023-10-24",
  "time": "17:28:46",
  "currency_sender": "BYN",
  "currency_recipient": "EUR",
  "type": "EXCHANGE",
  "bank_sender_name": "Клевер-Банк",
  "bank_recipient_name": "Альфа-Банк",
  "account_sender_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
  "account_recipient_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
  "sum_sender": 10,
  "sum_recipient": 2.93,
  "sender_old_balance": 6910.00,
  "sender_new_balance": 6900.00,
  "recipient_old_balance": 3533.33,
  "recipient_new_balance": 3536.26
}
```

Response Status 409:

```json
{
  "violations": [
    {
      "fieldName": "type",
      "exception": "Available types are: TRANSFER or EXCHANGE"
    },
    {
      "fieldName": "sum",
      "exception": "Field must be greater than 0"
    }
  ]
}
```

Response Status 500:

```json
{
  "exception": "Transaction rollback, cause: Insufficient funds in the account! You want to change balance 1200, but you have only 200.00"
}
```

#### POST формирования выписки по транзакциям пользователя за период времени

Request:

* from = с какой даты формировать выписку
* to = по какую дату
* account_id = номер счёта

```json
{
  "from": "2023-09-01",
  "to": "2023-09-01",
  "account_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q"
}
```

Response Status 201:

```json
{
  "bank_name": "Клевер-Банк",
  "lastname": "Зайцева",
  "firstname": "Елена",
  "surname": "Евгеньевна",
  "account_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
  "currency": "BYN",
  "opening_date": "2017-03-01",
  "from": "2023-09-01",
  "to": "2023-09-01",
  "formation_date": "2023-09-01",
  "formation_time": "21:34:12",
  "balance": 11700.00,
  "transactions": [
    {
      "date": "2023-09-01",
      "type": "REPLENISHMENT",
      "user_lastname": "Зайцева",
      "sum": 1200
    },
    {
      "date": "2023-09-01",
      "type": "TRANSFER",
      "user_lastname": "Петров",
      "sum": 1200
    }
  ]
}
```

Response Status 404:

```json
{
  "exception": "Account with ID G5QZ 643 A6XG AHNK CO6S PSO6 718Q is not found!"
}
```

Response Status 409:

```json
{
  "exception": "Date is out of pattern: yyyy-MM-dd. Right example: 2023-08-30"
}
```

#### PUT информация о количестве потраченных и полученных средств за период времени

Request:

* from = с какой даты формировать выписку
* to = по какую дату
* account_id = номер счёта

Response Status 201:

```json
{
  "bank_name": "Клевер-Банк",
  "lastname": "Зайцева",
  "firstname": "Елена",
  "surname": "Евгеньевна",
  "account_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
  "currency": "BYN",
  "opening_date": "2017-03-01",
  "from": "2023-09-01",
  "to": "2023-09-01",
  "formation_date": "2023-09-01",
  "formation_time": "21:40:08",
  "balance": 11700.00,
  "spent_funds": 1200,
  "received_funds": 8400
}
```

Response Status 404:

```json
{
  "exception": "Account with ID G5QZ 643 A6XG AHNK CO6S PSO6 718Q is not found!"
}
```

Response Status 409:

```json
{
  "exception": "Date is out of pattern: yyyy-MM-dd. Right example: 2023-08-30"
}
```

#### GET найти транзакцию по id

Request param:

* id = идентификатор транзакции

Response Status 200:

```json
{
  "id": 2,
  "date": "2023-10-24",
  "time": "16:51:23",
  "type": "TRANSFER",
  "bank_sender_id": 6,
  "bank_recipient_id": 1,
  "account_sender_id": "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4",
  "account_recipient_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
  "sum_sender": 1200,
  "sum_recipient": 1200
}
```

Response Status 404:

```json
{
  "exception": "Transaction with ID 20 is not found!"
}
```

#### GET найти все транзакции по account_sender_id

Request param:

* account_sender_id = id отправителя

Response Status 200:

```json
[
  {
    "id": 1,
    "date": "2023-10-24",
    "time": "16:51:21",
    "type": "REPLENISHMENT",
    "bank_sender_id": 1,
    "bank_recipient_id": 6,
    "account_sender_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
    "account_recipient_id": "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4",
    "sum_sender": 1200,
    "sum_recipient": 1200
  },
  {
    "id": 3,
    "date": "2023-10-24",
    "time": "16:51:25",
    "type": "EXCHANGE",
    "bank_sender_id": 1,
    "bank_recipient_id": 3,
    "account_sender_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
    "account_recipient_id": "FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62",
    "sum_sender": 10,
    "sum_recipient": 2.93
  }
]
```

#### GET найти все транзакции по account_recipient_id

Request param:

* account_recipient_id = id получателя

Response Status 200:

```json
[
  {
    "id": 8,
    "date": "2023-10-24",
    "time": "17:24:08",
    "type": "WITHDRAWAL",
    "bank_sender_id": 1,
    "bank_recipient_id": 6,
    "account_sender_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
    "account_recipient_id": "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4",
    "sum_sender": 1200,
    "sum_recipient": 1200
  },
  {
    "id": 9,
    "date": "2023-10-24",
    "time": "17:24:16",
    "type": "REPLENISHMENT",
    "bank_sender_id": 1,
    "bank_recipient_id": 6,
    "account_sender_id": "G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q",
    "account_recipient_id": "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4",
    "sum_sender": 1200,
    "sum_recipient": 1200
  }
]
```

***

### AccountServlet

***

#### GET найти счёт по id

Request param:

* id = идентификатор счёта

Response Status 200:

```json
{
  "id": "0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4",
  "currency": "BYN",
  "balance": 5000.00,
  "opening_date": "2020-03-01",
  "bank": {
    "id": 6,
    "name": "Россельхозбанк",
    "address": "ул. Кутузовский проспект, 20",
    "phone_number": "+7 (495) 111-11-11"
  },
  "user": {
    "id": 2,
    "lastname": "Петров",
    "firstname": "Петр",
    "surname": "Петрович",
    "register_date": "1991-02-02",
    "mobile_number": "+7 (901) 234-56-78"
  }
}
```

Response Status 404:

```json
{
  "exception": "Account with ID 0J2O 6O3P 1CUB VZUT 91SJ X3FU MU4 is not found!"
}
```

#### GET найти все счета

Response Status 200:

```json
[
  {
    "id": "RSK9 NQIW GVZY ODR9 0ZS3 NA6N 9HNJ",
    "currency": "USD",
    "balance": 0,
    "opening_date": "2020-02-01",
    "closing_date": "2020-12-31",
    "bank": {
      "id": 2,
      "name": "Сбербанк",
      "address": "ул. Ленина, 1",
      "phone_number": "+7 (495) 555-55-55"
    },
    "user": {
      "id": 1,
      "lastname": "Иванов",
      "firstname": "Иван",
      "surname": "Иванович",
      "register_date": "1990-01-01",
      "mobile_number": "+7 (900) 123-45-67"
    }
  },
  {
    "id": "AS12 ASDG 1200 2132 ASDA 353A 2132",
    "currency": "RUB",
    "balance": 10000.00,
    "opening_date": "2020-01-01",
    "bank": {
      "id": 1,
      "name": "Клевер-Банк",
      "address": "ул. Тверская, 25",
      "phone_number": "+7 (495) 222-22-22"
    },
    "user": {
      "id": 1,
      "lastname": "Иванов",
      "firstname": "Иван",
      "surname": "Иванович",
      "register_date": "1990-01-01",
      "mobile_number": "+7 (900) 123-45-67"
    }
  }
]
```

#### POST открыть счёт

Request:

* currency = валюта счёта
* balance = начальный баланс
* bank_id = id банка на котором нужно открыть счёт
* user_id = id пользователя, который хочет открыть счёт

```json
{
  "currency": "USD",
  "balance": 1100.50,
  "bank_id": 5,
  "user_id": 3
}
```

Response Status 201:

```json
{
  "id": "1W4L JUDP JPY7 KT9W BCIP 1IN3 JPHI",
  "currency": "USD",
  "balance": 1100.50,
  "opening_date": "2023-09-02",
  "bank": {
    "id": 5,
    "name": "Газпромбанк",
    "address": "ул. Новый Арбат, 15",
    "phone_number": "+7 (495) 999-99-99"
  },
  "user": {
    "id": 3,
    "lastname": "Сидоров",
    "firstname": "Сидор",
    "surname": "Сидорович",
    "register_date": "1992-03-03",
    "mobile_number": "+7 (902) 345-67-89"
  }
}
```

Response Status 404:

```json
{
  "exception": "Bank with ID 9 is not found!"
}
```

Response Status 409:

```json
{
  "violations": [
    {
      "fieldName": "currency",
      "exception": "Available currencies are: BYN, RUB, USD or EUR"
    },
    {
      "fieldName": "balance",
      "exception": "Field must be grater than 0"
    }
  ]
}
```

#### PUT закрыть счёт по id, баланс становится 0

Request param:

* id = идентификатор счёта

Response Status 201:

```json
{
  "id": "1W4L JUDP JPY7 KT9W BCIP 1IN3 JPHI",
  "currency": "USD",
  "balance": 0,
  "opening_date": "2023-09-02",
  "closing_date": "2023-09-02",
  "bank": {
    "id": 5,
    "name": "Газпромбанк",
    "address": "ул. Новый Арбат, 15",
    "phone_number": "+7 (495) 999-99-99"
  },
  "user": {
    "id": 3,
    "lastname": "Сидоров",
    "firstname": "Сидор",
    "surname": "Сидорович",
    "register_date": "1992-03-03",
    "mobile_number": "+7 (902) 345-67-89"
  }
}
```

Response Status 404:

```json
{
  "exception": "Account with ID 0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR is not found!"
}
```

#### DELETE удалить счёт по id

Request param:

* id = идентификатор счёта

Response Status 200:

```json
{
  "message": "Account with ID EHZ9 Z3I4 1IUK 277J 85OA Q2BH GDOK was successfully deleted"
}
```

Response Status 404:

```json
{
  "exception": "Account with ID 0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR is not found!"
}
```

***

### BankServlet

***

#### GET найти банк по id

Request param:

* id = идентификатор банка

Response Status 200:

```json
{
  "id": 1,
  "name": "Клевер-Банк",
  "address": "ул. Тверская, 25",
  "phone_number": "+7 (495) 222-22-22"
}
```

Response Status 404:

```json
{
  "exception": "Bank with ID 77 is not found!"
}
```

#### GET найти все банки

Response Status 200:

```json
[
  {
    "id": 1,
    "name": "Клевер-Банк",
    "address": "ул. Тверская, 25",
    "phone_number": "+7 (495) 222-22-22"
  },
  {
    "id": 2,
    "name": "Сбербанк",
    "address": "ул. Ленина, 1",
    "phone_number": "+7 (495) 555-55-55"
  },
  {
    "id": 3,
    "name": "Альфа-Банк",
    "address": "пр. Мира, 10",
    "phone_number": "+7 (495) 777-77-77"
  },
  {
    "id": 4,
    "name": "ВТБ",
    "address": "ул. Садовая, 5",
    "phone_number": "+7 (495) 888-88-88"
  },
  {
    "id": 5,
    "name": "Газпромбанк",
    "address": "ул. Новый Арбат, 15",
    "phone_number": "+7 (495) 999-99-99"
  },
  {
    "id": 6,
    "name": "Россельхозбанк",
    "address": "ул. Кутузовский проспект, 20",
    "phone_number": "+7 (495) 111-11-11"
  }
]
```

#### POST сохранить банк

Request:

* name = название банка
* address = адрес
* phone_number = телефон

```json
{
  "name": "Супер-Банк",
  "address": "ул. Гвардейская, 9",
  "phone_number": "+7 (495) 123-45-67"
}
```

Response Status 201:

```json
{
  "id": 7,
  "name": "Супер-Банк",
  "address": "ул. Гвардейская, 9",
  "phone_number": "+7 (495) 123-45-67"
}
```

Response Status 400:

```json
{
  "exception": "Bank with phone number +7 (495) 123-45-67 is already exist"
}
```

Response Status 409:

```json
{
  "violations": [
    {
      "fieldName": "name",
      "exception": "Field is out of pattern: ^[a-zA-Zа-яА-ЯёЁ @_-]+$"
    },
    {
      "fieldName": "address",
      "exception": "Field is out of pattern: ^[a-zA-Zа-яА-ЯёЁ0-9 .,-]+$"
    },
    {
      "fieldName": "phone_number",
      "exception": "Field is out of pattern: ^\\+\\d{1,3} \\(\\d{1,3}\\) \\d{3}-\\d{2}-\\d{2}$"
    }
  ]
}
```

#### PUT обновить банк по id

Request param:

* id = идентификатор банка

Request body:

* name = название банка
* address = адрес
* phone_number = телефон

```json
{
  "name": "Биг-Банк",
  "address": "ул. Амнезии, 17",
  "phone_number": "+7 (495) 321-54-76"
}
```

Response Status 201:

```json
{
  "id": 7,
  "name": "Биг-Банк",
  "address": "ул. Амнезии, 17",
  "phone_number": "+7 (495) 321-54-76"
}
```

Response Status 400:

```json
{
  "exception": "Bank with phone number +7 (495) 888-88-88 is already exist"
}
```

Response Status 404:

```json
{
  "exception": "Bank with ID 12 is not found!"
}
```

#### DELETE удалить банк по id (также удаляет все его счета)

Request param:

* id = идентификатор банка

Response Status 200:

```json
{
  "message": "Bank with ID 7 was successfully deleted"
}
```

Response Status 404:

```json
{
  "exception": "No Bank with ID 8 to delete"
}
```

***

### UserServlet

***

#### GET найти пользователя по id

Request param:

* id = идентификатор пользователя

Response Status 200:

```json
{
  "id": 14,
  "lastname": "Зайцева",
  "firstname": "Елена",
  "surname": "Евгеньевна",
  "register_date": "1991-02-14",
  "mobile_number": "+7 (913) 456-78-90"
}
```

Response Status 404:

```json
{
  "exception": "User with ID 25 is not found!"
}
```

#### GET найти всех пользователей

Response Status 200:

```json
[
  {
    "id": 1,
    "lastname": "Иванов",
    "firstname": "Иван",
    "surname": "Иванович",
    "register_date": "1990-01-01",
    "mobile_number": "+7 (900) 123-45-67"
  },
  {
    "id": 2,
    "lastname": "Петров",
    "firstname": "Петр",
    "surname": "Петрович",
    "register_date": "1991-02-02",
    "mobile_number": "+7 (901) 234-56-78"
  },
  {
    "id": 3,
    "lastname": "Сидоров",
    "firstname": "Сидор",
    "surname": "Сидорович",
    "register_date": "1992-03-03",
    "mobile_number": "+7 (902) 345-67-89"
  }
]
```

#### POST сохранить пользователя

Request:

* lastname = фамилия
* firstname = имя
* surname = отчество
* mobile_number = телефон

```json
{
  "lastname": "Алексеев",
  "firstname": "Алексей",
  "surname": "Алексеевич",
  "mobile_number": "+7 (495) 555-11-94"
}
```

Response Status 201:

```json
{
  "id": 25,
  "lastname": "Алексеев",
  "firstname": "Алексей",
  "surname": "Алексеевич",
  "register_date": "2023-09-01",
  "mobile_number": "+7 (495) 555-11-94"
}
```

Response Status 400:

```json
{
  "exception": "User with phone number +7 (495) 555-11-94 is already exist"
}
```

Response Status 409:

```json
{
  "violations": [
    {
      "fieldName": "mobile_number",
      "exception": "Field is out of pattern: ^\\+\\d{1,3} \\(\\d{1,3}\\) \\d{3}-\\d{2}-\\d{2}$"
    }
  ]
}
```

#### PUT обновить пользователя по id

Request param:

* id = идентификатор пользователя

Request body:

* lastname = фамилия
* firstname = имя
* surname = отчество
* mobile_number = телефон

```json
{
  "lastname": "Сидорович",
  "firstname": "Сидр",
  "surname": "Сидорович",
  "mobile_number": "+7 (495) 444-45-66"
}
```

Response Status 201:

```json
{
  "id": 25,
  "lastname": "Сидорович",
  "firstname": "Сидр",
  "surname": "Сидорович",
  "register_date": "2023-09-01",
  "mobile_number": "+7 (495) 444-45-66"
}
```

Response Status 400:

```json
{
  "exception": "User with phone number +7 (910) 123-45-67 is already exist"
}
```

Response Status 404:

```json
{
  "exception": "User with ID 26 is not found!"
}
```

#### DELETE удалить пользователя по id (так же удаляет все его счета)

Request param:

* id = идентификатор пользователя

Response Status 200:

```json
{
  "message": "User with ID 25 was successfully deleted"
}
```

Response Status 404:

```json
{
  "exception": "No User with ID 25 to delete"
}
```
