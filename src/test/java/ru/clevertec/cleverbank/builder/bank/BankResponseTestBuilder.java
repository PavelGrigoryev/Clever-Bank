package ru.clevertec.cleverbank.builder.bank;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.dto.bank.BankResponse;
import ru.clevertec.cleverbank.builder.TestBuilder;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aBankResponse")
@With
public class BankResponseTestBuilder implements TestBuilder<BankResponse> {

    private Long id = 1L;
    private String name = "Клевер-Банк";
    private String address = "ул. Тверская, 25";
    private String phoneNumber = "+7 (495) 222-22-22";

    @Override
    public BankResponse build() {
        return new BankResponse(id, name, address, phoneNumber);
    }

}
