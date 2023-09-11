package ru.clevertec.cleverbank.service.impl;

import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import ru.clevertec.cleverbank.aspect.annotation.ServiceLoggable;
import ru.clevertec.cleverbank.dao.BankDAO;
import ru.clevertec.cleverbank.dao.impl.BankDAOImpl;
import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.bank.BankRequest;
import ru.clevertec.cleverbank.dto.bank.BankResponse;
import ru.clevertec.cleverbank.exception.notfound.BankNotFoundException;
import ru.clevertec.cleverbank.mapper.BankMapper;
import ru.clevertec.cleverbank.service.BankService;
import ru.clevertec.cleverbank.tables.pojos.Bank;

import java.util.List;

@AllArgsConstructor
public class BankServiceImpl implements BankService {

    private final BankDAO bankDAO;
    private final BankMapper bankMapper;

    public BankServiceImpl() {
        bankDAO = new BankDAOImpl();
        bankMapper = Mappers.getMapper(BankMapper.class);

    }

    /**
     * Реализует метод findById, который возвращает банк по его id.
     *
     * @param id Long, представляющее id банка
     * @return объект Bank, представляющий банк с заданным id
     * @throws BankNotFoundException если банк с заданным id не найден в базе данных
     */
    @Override
    public Bank findById(Long id) {
        return bankDAO.findById(id)
                .orElseThrow(() -> new BankNotFoundException("Bank with ID " + id + " is not found!"));
    }

    /**
     * Реализует метод findByIdResponse, который возвращает ответ с данными о банке по его id.
     *
     * @param id Long, представляющее id банка
     * @return объект BankResponse, представляющий ответ с данными о банке с заданным id
     * @throws BankNotFoundException если банк с заданным id не найден в базе данных
     */
    @Override
    @ServiceLoggable
    public BankResponse findByIdResponse(Long id) {
        return bankMapper.toResponse(findById(id));
    }

    /**
     * Реализует метод findAll, который возвращает список всех банков из базы данных.
     *
     * @return список объектов BankResponse, представляющих ответы со всеми данными о банках из базы данных
     */
    @Override
    public List<BankResponse> findAll() {
        return bankMapper.toResponseList(bankDAO.findAll());
    }

    /**
     * Реализует метод save, который сохраняет новый банк в базу данных по данным из запроса.
     *
     * @param request объект BankRequest, представляющий запрос с данными для создания нового банка
     * @return объект BankResponse, представляющий ответ с данными о созданном банке
     */
    @Override
    @ServiceLoggable
    public BankResponse save(BankRequest request) {
        Bank bank = bankMapper.fromRequest(request);
        return bankMapper.toResponse(bankDAO.save(bank));
    }

    /**
     * Реализует метод update, который обновляет банк в базе данных по его id и данным из запроса.
     *
     * @param id      Long, представляющее id банка
     * @param request объект BankRequest, представляющий запрос с данными для обновления банка
     * @return объект BankResponse, представляющий ответ с данными об обновленном банке
     * @throws BankNotFoundException если банк с заданным id не найден в базе данных
     */
    @Override
    @ServiceLoggable
    public BankResponse update(Long id, BankRequest request) {
        findById(id);
        Bank bank = bankMapper.fromRequest(request);
        bank.setId(id);
        return bankMapper.toResponse(bankDAO.update(bank));
    }

    /**
     * Реализует метод delete, который удаляет банк из базы данных по его id.
     *
     * @param id Long, представляющее id банка
     * @return объект DeleteResponse, представляющий ответ с сообщением об успешном удалении банка
     * @throws BankNotFoundException если нет банка с заданным id для удаления из базы данных
     */
    @Override
    @ServiceLoggable
    public DeleteResponse delete(Long id) {
        return bankDAO.delete(id)
                .map(bank -> new DeleteResponse("Bank with ID " + id + " was successfully deleted"))
                .orElseThrow(() -> new BankNotFoundException("No Bank with ID " + id + " to delete"));
    }

}
