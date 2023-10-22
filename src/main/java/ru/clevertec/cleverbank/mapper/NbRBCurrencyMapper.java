package ru.clevertec.cleverbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.cleverbank.dto.nbrbcurrency.NbRBCurrencyResponse;
import ru.clevertec.cleverbank.model.NbRBCurrency;

import java.time.LocalDateTime;

@Mapper(imports = LocalDateTime.class)
public interface NbRBCurrencyMapper {

    NbRBCurrencyResponse toResponse(NbRBCurrency nbRBCurrency);

    @Mapping(target = "updateDate", expression = "java(LocalDateTime.now())")
    NbRBCurrency fromResponse(NbRBCurrencyResponse response);

}
