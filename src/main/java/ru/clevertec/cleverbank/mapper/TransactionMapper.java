package ru.clevertec.cleverbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.cleverbank.dto.TransactionResponse;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Mapper
public interface TransactionMapper {

    default Transaction createTransaction(Type type, String recipientsBank, String recipientsAccount, BigDecimal sum) {
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
    TransactionResponse createResponse(Transaction transaction, BigDecimal oldBalance, BigDecimal newBalance);

}
