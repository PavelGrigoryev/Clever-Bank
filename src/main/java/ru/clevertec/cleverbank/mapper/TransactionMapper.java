package ru.clevertec.cleverbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.cleverbank.dto.transaction.AmountStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.ExchangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatement;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementRequest;
import ru.clevertec.cleverbank.dto.transaction.TransactionStatementResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;
import ru.clevertec.cleverbank.model.User;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface TransactionMapper {

    @Mapping(target = "accountSenderId", source = "request.accountSenderId")
    @Mapping(target = "accountRecipientId", source = "request.accountRecipientId")
    @Mapping(target = "sumSender", source = "request.sum")
    @Mapping(target = "sumRecipient", source = "request.sum")
    @Mapping(target = "date", expression = "java(LocalDate.now())")
    @Mapping(target = "time", expression = "java(LocalTime.now())")
    Transaction toChangeTransaction(Type type,
                                    Long bankRecipientId,
                                    Long bankSenderId,
                                    TransactionRequest request);

    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "sum", source = "transaction.sumSender")
    ChangeBalanceResponse toChangeResponse(Transaction transaction,
                                           String bankSenderName,
                                           String bankRecipientName,
                                           Currency currency,
                                           BigDecimal oldBalance,
                                           BigDecimal newBalance);

    @Mapping(target = "sumSender", source = "sum")
    @Mapping(target = "sumRecipient", source = "sum")
    @Mapping(target = "date", expression = "java(LocalDate.now())")
    @Mapping(target = "time", expression = "java(LocalTime.now())")
    Transaction toTransferTransaction(Type type,
                                      Long bankSenderId,
                                      Long bankRecipientId,
                                      String accountSenderId,
                                      String accountRecipientId,
                                      BigDecimal sum);

    @Mapping(target = "sum", source = "transaction.sumSender")
    @Mapping(target = "transactionId", source = "transaction.id")
    TransferBalanceResponse toTransferResponse(Transaction transaction,
                                               Currency currency,
                                               String bankSenderName,
                                               String bankRecipientName,
                                               BigDecimal senderOldBalance,
                                               BigDecimal senderNewBalance,
                                               BigDecimal recipientOldBalance,
                                               BigDecimal recipientNewBalance);

    @Mapping(target = "date", expression = "java(LocalDate.now())")
    @Mapping(target = "time", expression = "java(LocalTime.now())")
    Transaction toExchangeTransaction(Type type,
                                      Long bankSenderId,
                                      Long bankRecipientId,
                                      String accountSenderId,
                                      String accountRecipientId,
                                      BigDecimal sumSender,
                                      BigDecimal sumRecipient);

    @Mapping(target = "transactionId", source = "transaction.id")
    ExchangeBalanceResponse toExchangeResponse(Transaction transaction,
                                               Currency currencySender,
                                               Currency currencyRecipient,
                                               String bankSenderName,
                                               String bankRecipientName,
                                               BigDecimal senderOldBalance,
                                               BigDecimal senderNewBalance,
                                               BigDecimal recipientOldBalance,
                                               BigDecimal recipientNewBalance);

    TransactionResponse toResponse(Transaction transaction);

    List<TransactionResponse> toResponseList(List<Transaction> transactions);

    @Mapping(target = "formationDate", expression = "java(LocalDate.now())")
    @Mapping(target = "formationTime", expression = "java(LocalTime.now())")
    TransactionStatementResponse toStatementResponse(String bankName,
                                                     User user,
                                                     Account account,
                                                     TransactionStatementRequest request,
                                                     List<TransactionStatement> transactions);

    @Mapping(target = "formationDate", expression = "java(LocalDate.now())")
    @Mapping(target = "formationTime", expression = "java(LocalTime.now())")
    AmountStatementResponse toAmountResponse(String bankName,
                                             User user,
                                             Account account,
                                             TransactionStatementRequest request,
                                             BigDecimal spentFunds,
                                             BigDecimal receivedFunds);

}
