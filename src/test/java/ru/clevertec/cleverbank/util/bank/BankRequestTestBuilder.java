package ru.clevertec.cleverbank.util.bank;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.clevertec.cleverbank.dto.bank.BankRequest;
import ru.clevertec.cleverbank.util.TestBuilder;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aBankRequest")
@With
public class BankRequestTestBuilder implements TestBuilder<BankRequest> {

    private String name = "Клевер-Банк";
    private String address = "ул. Тверская, 25";
    private String phoneNumber = "+7 (495) 222-22-22";

    @Override
    public BankRequest build() {
        return new BankRequest(name, address, phoneNumber);
    }

}
