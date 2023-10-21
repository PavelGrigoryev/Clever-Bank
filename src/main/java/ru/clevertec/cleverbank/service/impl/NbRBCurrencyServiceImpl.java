package ru.clevertec.cleverbank.service.impl;

import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import ru.clevertec.cleverbank.dao.NbRBCurrencyDAO;
import ru.clevertec.cleverbank.dao.impl.NbRBCurrencyDAOImpl;
import ru.clevertec.cleverbank.dto.nbrbcurrency.NbRBCurrencyResponse;
import ru.clevertec.cleverbank.exception.notfound.NbRBCurrencyNotFoundException;
import ru.clevertec.cleverbank.mapper.NbRBCurrencyMapper;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.NbRBCurrency;
import ru.clevertec.cleverbank.service.NbRBCurrencyService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@AllArgsConstructor
public class NbRBCurrencyServiceImpl implements NbRBCurrencyService {

    private final NbRBCurrencyDAO nbRBCurrencyDAO;
    private final NbRBCurrencyMapper currencyMapper;

    public NbRBCurrencyServiceImpl() {
        nbRBCurrencyDAO = new NbRBCurrencyDAOImpl();
        currencyMapper = Mappers.getMapper(NbRBCurrencyMapper.class);
    }

    /**
     * Реализует метод findByCurrencyId, который возвращает курс НБ РБ по его currencyId.
     *
     * @param currencyId Integer, представляющее currencyId курса
     * @return объект NbRBCurrency, представляющий курс с заданным currencyId
     * @throws NbRBCurrencyNotFoundException если курс с заданным currencyId не найден в базе данных
     */
    @Override
    public NbRBCurrency findByCurrencyId(Integer currencyId) {
        return nbRBCurrencyDAO.findByCurrencyId(currencyId)
                .orElseThrow(() -> new NbRBCurrencyNotFoundException("NbRBCurrency with currencyId " + currencyId
                                                                     + " is not found!"));
    }

    /**
     * Реализует метод save, который сохраняет курс в базу данных по данным из ответа.
     *
     * @param response объект NbRBCurrencyResponse, представляющий ответ с данными для создания нового курса
     * @return объект NbRBCurrency, представляющий объект с данными о созданном курсе
     */
    @Override
    public NbRBCurrency save(NbRBCurrencyResponse response) {
        return nbRBCurrencyDAO.save(currencyMapper.fromResponse(response));
    }

    /**
     * Этот метод рассчитывает сумму обмена валют, используя курсы НБ РБ. Иностранные валюты вначале переводятся в BYN,
     * затем переводятся в необходимую иностранную валюту.
     *
     * @param currencySender    валюта отправителя
     * @param currencyRecipient валюта получателя
     * @param sum               сумма для обмена валют
     * @return изменённая сумма по курсу НБ РБ
     * @throws NbRBCurrencyNotFoundException если курс с заданным currencyId не найден в базе данных
     */
    @Override
    public BigDecimal exchangeSumByCurrency(Currency currencySender, Currency currencyRecipient, BigDecimal sum) {
        if (currencySender.equals(currencyRecipient)) {
            return sum;
        } else if (currencyRecipient.equals(Currency.BYN)) {
            NbRBCurrency nbRBCurrency = findByCurrencyId(currencySender.getCode());
            return sum.multiply(nbRBCurrency.getRate())
                    .divide(BigDecimal.valueOf(nbRBCurrency.getScale()), 2, RoundingMode.UP);
        } else if (currencySender.equals(Currency.BYN)) {
            NbRBCurrency nbRBCurrency = findByCurrencyId(currencyRecipient.getCode());
            return sum.divide(nbRBCurrency.getRate(), 2, RoundingMode.UP)
                    .multiply(BigDecimal.valueOf(nbRBCurrency.getScale()));
        } else {
            NbRBCurrency nbRBCurrencySender = findByCurrencyId(currencySender.getCode());
            NbRBCurrency nbRBCurrencyRecipient = findByCurrencyId(currencyRecipient.getCode());
            return sum.multiply(nbRBCurrencySender.getRate())
                    .divide(BigDecimal.valueOf(nbRBCurrencySender.getScale()), 2, RoundingMode.UP)
                    .divide(nbRBCurrencyRecipient.getRate(), 2, RoundingMode.UP)
                    .multiply(BigDecimal.valueOf(nbRBCurrencyRecipient.getScale()))
                    .setScale(2, RoundingMode.UP);
        }
    }

}
