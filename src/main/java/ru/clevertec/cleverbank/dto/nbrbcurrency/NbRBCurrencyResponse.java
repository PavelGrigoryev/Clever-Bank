package ru.clevertec.cleverbank.dto.nbrbcurrency;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public record NbRBCurrencyResponse(@SerializedName("Cur_ID")
                                   Integer currencyId,

                                   @SerializedName("Cur_Abbreviation")
                                   String currency,

                                   @SerializedName("Cur_Scale")
                                   Integer scale,

                                   @SerializedName("Cur_OfficialRate")
                                   BigDecimal rate) {
}
