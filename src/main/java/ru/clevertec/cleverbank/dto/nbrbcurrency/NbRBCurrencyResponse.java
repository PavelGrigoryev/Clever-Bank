package ru.clevertec.cleverbank.dto.nbrbcurrency;

import com.google.gson.annotations.SerializedName;
import ru.clevertec.cleverbank.model.Currency;

import java.math.BigDecimal;

public record NbRBCurrencyResponse(@SerializedName("Cur_ID")
                                   Integer currencyId,

                                   @SerializedName("Cur_Abbreviation")
                                   Currency currency,

                                   @SerializedName("Cur_Scale")
                                   Integer scale,

                                   @SerializedName("Cur_OfficialRate")
                                   BigDecimal rate) {
}
