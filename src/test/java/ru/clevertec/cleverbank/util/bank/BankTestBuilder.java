package ru.clevertec.cleverbank.util.bank;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.model.Bank;
import ru.clevertec.cleverbank.util.TestBuilder;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aBank")
@With
public class BankTestBuilder implements TestBuilder<Bank> {

    private Long id = 1L;
    private String name = "Клевер-Банк";
    private String address = "ул. Тверская, 25";
    private String phoneNumber = "+7 (495) 222-22-22";

    @Override
    public Bank build() {
        return Bank.builder()
                .id(id)
                .name(name)
                .address(address)
                .phoneNumber(phoneNumber)
                .build();
    }

}
