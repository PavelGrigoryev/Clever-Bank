package ru.clevertec.cleverbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.cleverbank.dto.ChangeBalanceResponse;
import ru.clevertec.cleverbank.dto.TransferBalanceResponse;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Mapper
public interface TransactionMapper {

    default Transaction createChangeTransaction(Type type,
                                                String recipientsBank,
                                                String recipientsAccount,
                                                BigDecimal sum) {
        return Transaction.builder()
                .date(LocalDate.now())
                .time(LocalTime.now())
                .type(type)
                .recipientsBank(recipientsBank)
                .recipientsAccount(recipientsAccount)
                .sum(sum)
                .build();
    }

    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "recipientBankName", source = "transaction.recipientsBank")
    @Mapping(target = "recipientAccountId", source = "transaction.recipientsAccount")
    ChangeBalanceResponse createChangeResponse(Transaction transaction,
                                               Currency currency,
                                               BigDecimal oldBalance,
                                               BigDecimal newBalance);

    default Transaction createTransferTransaction(Type type,
                                                  String sendersBank,
                                                  String recipientsBank,
                                                  String sendersAccount,
                                                  String recipientsAccount,
                                                  BigDecimal sum) {
        return Transaction.builder()
                .date(LocalDate.now())
                .time(LocalTime.now())
                .type(type)
                .sendersBank(sendersBank)
                .recipientsBank(recipientsBank)
                .recipientsAccount(sendersAccount)
                .recipientsAccount(recipientsAccount)
                .sum(sum)
                .build();
    }

    @Mapping(target = "transactionId", source = "transaction.id")
    @Mapping(target = "senderBankName", source = "transaction.sendersBank")
    @Mapping(target = "recipientBankName", source = "transaction.recipientsBank")
    @Mapping(target = "senderAccountId", source = "transaction.sendersAccount")
    @Mapping(target = "recipientAccountId", source = "transaction.recipientsAccount")
    TransferBalanceResponse createTransferResponse(Transaction transaction,
                                                   Currency currency,
                                                   BigDecimal senderOldBalance,
                                                   BigDecimal senderNewBalance,
                                                   BigDecimal recipientOldBalance,
                                                   BigDecimal recipientNewBalance);

}
