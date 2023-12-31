package ru.clevertec.cleverbank.service.impl;

import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import ru.clevertec.cleverbank.aspect.annotation.ServiceLoggable;
import ru.clevertec.cleverbank.dao.TransactionDAO;
import ru.clevertec.cleverbank.dao.impl.TransactionDAOImpl;
import ru.clevertec.cleverbank.dto.transaction.AmountStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.ExchangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatement;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.exception.internalservererror.TransactionException;
import ru.clevertec.cleverbank.exception.notfound.TransactionNotFoundException;
import ru.clevertec.cleverbank.mapper.TransactionMapper;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.model.User;
import ru.clevertec.cleverbank.service.AccountService;
import ru.clevertec.cleverbank.service.CheckService;
import ru.clevertec.cleverbank.service.NbRBCurrencyService;
import ru.clevertec.cleverbank.service.TransactionService;
import ru.clevertec.cleverbank.service.UploadFileService;
import ru.clevertec.cleverbank.service.ValidationService;
import ru.clevertec.cleverbank.util.HikariConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountService accountService;
    private final TransactionDAO transactionDAO;
    private final TransactionMapper transactionMapper;
    private final CheckService checkService;
    private final UploadFileService uploadFileService;
    private final ValidationService validationService;
    private final NbRBCurrencyService nbRBCurrencyService;
    private final Connection connection;

    public TransactionServiceImpl() {
        accountService = new AccountServiceImpl();
        transactionDAO = new TransactionDAOImpl();
        transactionMapper = Mappers.getMapper(TransactionMapper.class);
        checkService = new CheckServiceImpl();
        uploadFileService = new UploadFileServiceImpl();
        validationService = new ValidationServiceImpl();
        connection = HikariConnectionManager.getConnection();
        nbRBCurrencyService = new NbRBCurrencyServiceImpl();
    }

    /**
     * Реализует метод changeBalance, который изменяет баланс счёта в базе данных по данным из запроса.
     *
     * @param request объект TransactionRequest, представляющий запрос с данными для изменения баланса счёта
     * @return объект ChangeBalanceResponse, представляющий ответ с данными об измененном балансе счёта
     */
    @Override
    @ServiceLoggable
    public ChangeBalanceResponse changeBalance(TransactionRequest request) {
        Account accountRecipient = accountService.findById(request.accountRecipientId());
        Account accountSender = accountService.findById(request.accountSenderId());
        validationService.validateAccountForClosingDate(accountRecipient.getClosingDate(), accountRecipient.getId());

        BigDecimal oldBalance = accountRecipient.getBalance();
        validationService.validateAccountForSufficientBalance(request.type(), request.sum(), oldBalance);
        BigDecimal newBalance = request.type() == Type.REPLENISHMENT
                ? oldBalance.add(request.sum())
                : oldBalance.subtract(request.sum());
        Account updatedAccount = accountService.updateBalance(accountRecipient, newBalance);

        Bank bankRecipient = accountRecipient.getBank();
        Bank bankSender = accountSender.getBank();

        Transaction transaction = transactionMapper
                .toChangeTransaction(request.type(), bankRecipient.getId(), bankSender.getId(), request);
        Transaction savedTransaction = transactionDAO.save(transaction);

        ChangeBalanceResponse response = transactionMapper
                .toChangeResponse(savedTransaction, bankSender.getName(), bankRecipient.getName(),
                        updatedAccount.getCurrency(), oldBalance, newBalance);
        String check = checkService.createChangeBalanceCheck(response);
        uploadFileService.uploadCheck(check);
        return response;
    }

    /**
     * Реализует метод transferBalance, который переводит средства между двумя счётами в базе данных по данным из запроса.
     *
     * @param request объект TransactionRequest, представляющий запрос с данными для перевода средств между счетами
     * @return объект TransferBalanceResponse, представляющий ответ с данными о переведенных средствах между счетами
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    @Override
    @ServiceLoggable
    public TransferBalanceResponse transferBalance(TransactionRequest request) throws SQLException {
        connection.setAutoCommit(false);
        try {
            Account accountSender = accountService.findById(request.accountSenderId());
            Account accountRecipient = accountService.findById(request.accountRecipientId());
            validationService.validateAccountForClosingDate(accountSender.getClosingDate(), accountSender.getId());
            validationService.validateAccountForClosingDate(accountRecipient.getClosingDate(), accountRecipient.getId());
            validationService.validateAccountForCurrency(accountSender.getCurrency(), accountRecipient.getCurrency());

            BigDecimal senderOldBalance = accountSender.getBalance();
            validationService.validateAccountForSufficientBalance(Type.TRANSFER, request.sum(), senderOldBalance);
            BigDecimal senderNewBalance = senderOldBalance.subtract(request.sum());
            Account updatedSenderAccount = accountService.updateBalance(accountSender, senderNewBalance);
            Bank bankSender = accountSender.getBank();

            BigDecimal recipientOldBalance = accountRecipient.getBalance();
            BigDecimal recipientNewBalance = recipientOldBalance.add(request.sum());
            Account updatedRecipientAccount = accountService.updateBalance(accountRecipient, recipientNewBalance);
            Bank bankRecipient = accountRecipient.getBank();

            Transaction transaction = transactionMapper.toTransferTransaction(Type.TRANSFER, bankSender.getId(),
                    bankRecipient.getId(), updatedSenderAccount.getId(), updatedRecipientAccount.getId(), request.sum());
            Transaction savedTransaction = transactionDAO.save(transaction);

            connection.commit();

            TransferBalanceResponse response = transactionMapper.toTransferResponse(savedTransaction,
                    accountSender.getCurrency(), bankSender.getName(), bankRecipient.getName(), senderOldBalance,
                    senderNewBalance, recipientOldBalance, recipientNewBalance);
            String check = checkService.createTransferBalanceCheck(response);
            uploadFileService.uploadCheck(check);
            return response;
        } catch (Exception e) {
            connection.rollback();
            throw new TransactionException("Transaction rollback, cause: " + e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Реализует метод exchangeBalance, который переводит средства между двумя счётами с обменом валют по курсу НБ РБ
     * в базе данных по данным из запроса.
     *
     * @param request объект TransactionRequest, представляющий запрос с данными для перевода средств между счетами
     * @return объект ExchangeBalanceResponse, представляющий ответ с данными о переведенных средствах между счетами
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    @Override
    @ServiceLoggable
    public ExchangeBalanceResponse exchangeBalance(TransactionRequest request) throws SQLException {
        connection.setAutoCommit(false);
        try {
            Account accountSender = accountService.findById(request.accountSenderId());
            BigDecimal senderOldBalance = accountSender.getBalance();
            validationService.validateAccountForClosingDate(accountSender.getClosingDate(), accountSender.getId());
            validationService.validateAccountForSufficientBalance(Type.EXCHANGE, request.sum(), senderOldBalance);
            Account accountRecipient = accountService.findById(request.accountRecipientId());
            BigDecimal recipientOldBalance = accountRecipient.getBalance();
            validationService.validateAccountForClosingDate(accountRecipient.getClosingDate(), accountRecipient.getId());
            BigDecimal exchangedSum = nbRBCurrencyService
                    .exchangeSumByCurrency(accountSender.getCurrency(), accountRecipient.getCurrency(), request.sum());
            Account updatedAccountSender = accountService
                    .updateBalance(accountSender, accountSender.getBalance().subtract(request.sum()));
            Account updatedAccountRecipient = accountService
                    .updateBalance(accountRecipient, accountRecipient.getBalance().add(exchangedSum));
            Transaction transaction = transactionMapper.toExchangeTransaction(Type.EXCHANGE, accountSender.getBank().getId(),
                    accountRecipient.getBank().getId(), accountSender.getId(), accountRecipient.getId(), request.sum(),
                    exchangedSum);
            Transaction savedTransaction = transactionDAO.save(transaction);
            connection.commit();
            ExchangeBalanceResponse response = transactionMapper.toExchangeResponse(savedTransaction,
                    accountSender.getCurrency(), accountRecipient.getCurrency(), accountSender.getBank().getName(),
                    accountRecipient.getBank().getName(), senderOldBalance, updatedAccountSender.getBalance(),
                    recipientOldBalance, updatedAccountRecipient.getBalance());
            String check = checkService.createExchangeBalanceCheck(response);
            uploadFileService.uploadCheck(check);
            return response;
        } catch (Exception e) {
            connection.rollback();
            throw new TransactionException("Transaction rollback, cause: " + e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Реализует метод findAllByPeriodOfDateAndAccountId, который формирует выписку по транзакциям счёта за определенный
     * период дат.
     *
     * @param request объект TransactionStatementRequest, представляющий запрос с данными о счёте и периоде дат
     * @return объект TransactionStatementResponse, представляющий ответ со списком транзакций по счёту за период дат
     * @throws TransactionNotFoundException если нет транзакций по счёту за период дат
     */
    @Override
    @ServiceLoggable
    public TransactionStatementResponse findAllByPeriodOfDateAndAccountId(TransactionStatementRequest request) {
        Account account = accountService.findById(request.accountId());
        Bank bank = account.getBank();
        User user = account.getUser();

        List<TransactionStatement> transactionStatements = transactionDAO
                .findAllByPeriodOfDateAndAccountId(request.from(), request.to(), account.getId());
        if (transactionStatements.isEmpty()) {
            throw new TransactionNotFoundException("It is not possible to create a transaction amount because" +
                                                   " you do not have any transactions for this period of time : from "
                                                   + request.from() + " to " + request.to());
        }

        TransactionStatementResponse response = transactionMapper
                .toStatementResponse(bank.getName(), user, account, request, transactionStatements);
        String statement = checkService.createTransactionStatement(response);
        uploadFileService.uploadStatement(statement);
        return response;
    }

    /**
     * Реализует метод findSumOfFundsByPeriodOfDateAndAccountId, который возвращает сумму потраченных и полученных
     * средств по счёту за определенный период дат.
     *
     * @param request объект TransactionStatementRequest, представляющий запрос с данными о счёте и периоде дат
     * @return объект AmountStatementResponse, представляющий ответ с суммой потраченных и полученных средств по счёту
     * за период дат
     * @throws TransactionNotFoundException если нет транзакций по счёту за период дат
     */
    @Override
    @ServiceLoggable
    public AmountStatementResponse findSumOfFundsByPeriodOfDateAndAccountId(TransactionStatementRequest request) {
        Account account = accountService.findById(request.accountId());
        Bank bank = account.getBank();
        User user = account.getUser();

        BigDecimal spentFunds = transactionDAO
                .findSumOfSpentFundsByPeriodOfDateAndAccountId(request.from(), request.to(), request.accountId());
        BigDecimal receivedFunds = transactionDAO
                .findSumOfReceivedFundsByPeriodOfDateAndAccountId(request.from(), request.to(), request.accountId());
        if (spentFunds == null && receivedFunds == null) {
            throw new TransactionNotFoundException("It is not possible to create a transaction amount because" +
                                                   " you do not have any transactions for this period of time : from "
                                                   + request.from() + " to " + request.to());
        }

        AmountStatementResponse response = transactionMapper
                .toAmountResponse(bank.getName(), user, account, request, spentFunds, receivedFunds);
        String amountStatement = checkService.createAmountStatement(response);
        uploadFileService.uploadAmount(amountStatement);
        return response;
    }

    /**
     * Реализует метод findById, который возвращает транзакцию по ее id.
     *
     * @param id Long, представляющее id транзакции
     * @return объект TransactionResponse, представляющий ответ с данными о транзакции
     * @throws TransactionNotFoundException если транзакция с заданным id не найдена в базе данных
     */
    @Override
    @ServiceLoggable
    public TransactionResponse findById(Long id) {
        return transactionDAO.findById(id)
                .map(transactionMapper::toResponse)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with ID " + id + " is not found!"));
    }

    /**
     * Реализует метод findAllBySendersAccountId, который возвращает список транзакций по id счёта отправителя.
     *
     * @param id String, представляющая id счёта отправителя
     * @return список объектов TransactionResponse, представляющих ответы с данными о транзакциях по счёту отправителя
     */
    @Override
    public List<TransactionResponse> findAllBySendersAccountId(String id) {
        return transactionMapper.toResponseList(transactionDAO.findAllBySendersAccountId(id));
    }

    /**
     * Реализует метод findAllByRecipientAccountId, который возвращает список транзакций по id счёта получателя.
     *
     * @param id String, представляющая id счёта получателя
     * @return список объектов TransactionResponse, представляющих ответы с данными о транзакциях по счёту получателя
     */
    @Override
    public List<TransactionResponse> findAllByRecipientAccountId(String id) {
        return transactionMapper.toResponseList(transactionDAO.findAllByRecipientAccountId(id));
    }

}
