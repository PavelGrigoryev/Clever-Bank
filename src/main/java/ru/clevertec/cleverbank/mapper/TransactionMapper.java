package ru.clevertec.cleverbank.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.cleverbank.dto.TransactionResponse;
import ru.clevertec.cleverbank.model.Transaction;
import ru.clevertec.cleverbank.model.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Mapper
public interface TransactionMapper {

    default Transaction toReplenishTransaction(String recipientsBank, String recipientsAccount, BigDecimal sum) {
        return Transaction.builder()
                .date(LocalDate.now())
                .time(LocalTime.now())
                .type(Type.REPLENISHMENT)
                .recipientsBank(recipientsBank)
                .recipientsAccount(recipientsAccount)
                .sum(sum)
                .build();
    }

    default TransactionResponse toReplenishResponse(Transaction transaction, BigDecimal oldBalance, BigDecimal newBalance) {
        return new TransactionResponse(transaction.getId(),
                transaction.getDate(),
                transaction.getTime().withNano(0),
                transaction.getType(),
                transaction.getRecipientsBank(),
                transaction.getRecipientsAccount(),
                transaction.getSum(),
                oldBalance,
                newBalance);
    }

}
