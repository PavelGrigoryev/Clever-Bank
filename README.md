# Clever-Bank application

## Автор: [Grigoryev Pavel](https://pavelgrigoryev.github.io/GrigoryevPavel/)

### Технологии, которые я использовал на проекте:

* Java 17
* Gradle 8.0.2
* Lombok plugin 8.2.2
* AspectJ plugin 8.3
* Servlet API 6.0.0
* Postgresql 42.6.0
* Gson 2.10.1
* Slf4j-API 2.0.7
* Logback logger 1.4.11
* SnakeYaml 2.1
* Liquibase 4.23.1
* Mapstruct 1.5.3.Final
* Junit 5.10.0

### Инструкция для запуска приложения локально:

1. У вас должна быть
   установлена [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html),
   [Tomcat 10.1](https://tomcat.apache.org/download-10.cgi), [Intellij IDEA Ultimate](https://www.jetbrains.com/idea/download/)
   и [Postgresql](https://www.postgresql.org/download/) (P.S: Postgresql можно развернуть в докере).
2. В Postgresql нужно создать базу данных. Как пример: "clever_bank" . Sql: CREATE DATABASE clever_bank
3. В [application.yaml](src/main/resources/application.yaml) в строчке №3 введите ваш username для Postgresql, в строчке
   №4 введите ваш password для Postgresql.
4. В настройках идеи Run -> Edit Configurations... вы должны поставить Tomcat 10.1. И в графе Deployment
   очистить Application context.
5. При запуске приложения Liquibase сам создаст таблицы и наполнит их дефолтными значениями.
6. Приложение готово к использованию.

### Http Запросы

* [transactions.http](src/main/resources/http/transactions.http) для транзакций
* [accounts.http](src/main/resources/http/accounts.http) для счетов
* [banks.http](src/main/resources/http/banks.http) для банков
* [users.http](src/main/resources/http/users.http) для пользователей

## Функциональность

В сумме приложение умеет:

***

### TransactionServlet

***
