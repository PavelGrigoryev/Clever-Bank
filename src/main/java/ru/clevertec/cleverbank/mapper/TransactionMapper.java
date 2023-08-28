package ru.clevertec.cleverbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.cleverbank.dto.transaction.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.transaction.TransactionResponse;
import ru.clevertec.cleverbank.dto.transaction.TransferBalanceResponse;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface TransactionMapper {

    @Mapping(target = "date", expression = "java(LocalDate.now())")
    @Mapping(target = "time", expression = "java(LocalTime.now())")
    Transaction toChangeTransaction(Type type,
                                    String recipientsBank,
                                    String recipientsAccount,
                                    BigDecimal sum);

    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "recipientBankName", source = "transaction.recipientsBank")
    @Mapping(target = "recipientAccountId", source = "transaction.recipientsAccount")
    ChangeBalanceResponse toChangeResponse(Transaction transaction,
                                           Currency currency,
                                           BigDecimal oldBalance,
                                           BigDecimal newBalance);

    @Mapping(target = "date", expression = "java(LocalDate.now())")
    @Mapping(target = "time", expression = "java(LocalTime.now())")
    Transaction toTransferTransaction(Type type,
                                      String sendersBank,
                                      String recipientsBank,
                                      String sendersAccount,
                                      String recipientsAccount,
                                      BigDecimal sum);

    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "senderBankName", source = "transaction.sendersBank")
    @Mapping(target = "recipientBankName", source = "transaction.recipientsBank")
    @Mapping(target = "senderAccountId", source = "transaction.sendersAccount")
    @Mapping(target = "recipientAccountId", source = "transaction.recipientsAccount")
    TransferBalanceResponse toTransferResponse(Transaction transaction,
                                               Currency currency,
                                               BigDecimal senderOldBalance,
                                               BigDecimal senderNewBalance,
                                               BigDecimal recipientOldBalance,
                                               BigDecimal recipientNewBalance);

    TransactionResponse toResponse(Transaction transaction);

    List<TransactionResponse> toResponseList(List<Transaction> transactions);

}
