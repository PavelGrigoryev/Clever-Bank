package ru.clevertec.cleverbank.service.impl;

import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import ru.clevertec.cleverbank.aspect.annotation.ServiceLoggable;
import ru.clevertec.cleverbank.dao.UserDAO;
import ru.clevertec.cleverbank.dao.impl.UserDAOImpl;
import ru.clevertec.cleverbank.dto.DeleteResponse;
import ru.clevertec.cleverbank.dto.user.UserRequest;
import ru.clevertec.cleverbank.dto.user.UserResponse;
import ru.clevertec.cleverbank.exception.badrequest.UniquePhoneNumberException;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.exception.notfound.UserNotFoundException;
import ru.clevertec.cleverbank.mapper.UserMapper;
import ru.clevertec.cleverbank.model.User;
import ru.clevertec.cleverbank.service.UserService;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final UserMapper userMapper;

    public UserServiceImpl() {
        userDAO = new UserDAOImpl();
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    /**
     * Реализует метод findById, который возвращает пользователя по его id.
     *
     * @param id Long, представляющее id пользователя
     * @return объект User, представляющий пользователя с заданным id
     * @throws UserNotFoundException если пользователь с заданным id не найден в базе данных
     */
    @Override
    public User findById(Long id) {
        return userDAO.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " is not found!"));
    }

    /**
     * Реализует метод findByIdResponse, который возвращает ответ с данными о пользователе по его id.
     *
     * @param id Long, представляющее id пользователя
     * @return объект UserResponse, представляющий ответ с данными о пользователе с заданным id
     * @throws UserNotFoundException если пользователь с заданным id не найден в базе данных
     */
    @Override
    @ServiceLoggable
    public UserResponse findByIdResponse(Long id) {
        return userMapper.toResponse(findById(id));
    }

    /**
     * Реализует метод findAll, который возвращает список всех пользователей из базы данных.
     *
     * @return список объектов UserResponse, представляющих ответы со всеми данными о пользователях из базы данных
     */
    @Override
    public List<UserResponse> findAll() {
        return userMapper.toResponseList(userDAO.findAll());
    }

    /**
     * Реализует метод save, который сохраняет нового пользователя в базу данных по данным из запроса.
     *
     * @param request объект UserRequest, представляющий запрос с данными для создания нового пользователя
     * @return объект UserResponse, представляющий ответ с данными о созданном пользователе
     * @throws UniquePhoneNumberException если заданный телефон не уникальный
     */
    @Override
    @ServiceLoggable
    public UserResponse save(UserRequest request) {
        User user = userMapper.fromRequest(request);
        user.setRegisterDate(LocalDate.now());
        User savedUser;
        try {
            savedUser = userDAO.save(user);
        } catch (JDBCConnectionException e) {
            throw new UniquePhoneNumberException("User with phone number " + request.mobileNumber() + " is already exist");
        }
        return userMapper.toResponse(savedUser);
    }

    /**
     * Реализует метод update, который обновляет пользователя в базе данных по его id и данным из запроса.
     *
     * @param id      Long, представляющее id пользователя
     * @param request объект UserRequest, представляющий запрос с данными для обновления пользователя
     * @return объект UserResponse, представляющий ответ с данными об обновленном пользователе
     * @throws UserNotFoundException      если пользователь с заданным id не найден в базе данных
     * @throws UniquePhoneNumberException если заданный телефон не уникальный
     */
    @Override
    @ServiceLoggable
    public UserResponse update(Long id, UserRequest request) {
        User userById = findById(id);
        User user = userMapper.fromRequest(request);
        user.setId(userById.getId());
        user.setRegisterDate(userById.getRegisterDate());
        User updatedUser;
        try {
            updatedUser = userDAO.update(user);
        } catch (JDBCConnectionException e) {
            throw new UniquePhoneNumberException("User with phone number " + request.mobileNumber() + " is already exist");
        }
        return userMapper.toResponse(updatedUser);
    }

    /**
     * Реализует метод delete, который удаляет пользователя из базы данных по его id.
     *
     * @param id Long, представляющее id пользователя
     * @return объект DeleteResponse, представляющий ответ с сообщением об успешном удалении пользователя
     * @throws UserNotFoundException если нет пользователя с заданным id для удаления из базы данных
     */
    @Override
    @ServiceLoggable
    public DeleteResponse delete(Long id) {
        return userDAO.delete(id)
                .map(user -> new DeleteResponse("User with ID " + id + " was successfully deleted"))
                .orElseThrow(() -> new UserNotFoundException("No User with ID " + id + " to delete"));
    }

}
