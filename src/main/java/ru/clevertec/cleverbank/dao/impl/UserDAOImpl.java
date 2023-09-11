package ru.clevertec.cleverbank.dao.impl;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import ru.clevertec.cleverbank.dao.UserDAO;
import ru.clevertec.cleverbank.exception.badrequest.UniquePhoneNumberException;
import ru.clevertec.cleverbank.tables.pojos.User;
import ru.clevertec.cleverbank.util.ConnectionManager;

import java.util.List;
import java.util.Optional;

import static ru.clevertec.cleverbank.Tables.ACCOUNT;
import static ru.clevertec.cleverbank.Tables.USER;

@AllArgsConstructor
public class UserDAOImpl implements UserDAO {

    private final DSLContext dslContext;

    public UserDAOImpl() {
        dslContext = DSL.using(ConnectionManager.getJDBCConnection());
    }

    /**
     * Находит пользователя по его id в базе данных и возвращает его в виде объекта Optional.
     *
     * @param id Long, представляющее идентификатор пользователя
     * @return объект Optional, содержащий пользователя, если он найден, или пустой, если нет
     */
    @Override
    public Optional<User> findById(Long id) {
        return dslContext.fetchOptional(USER, USER.ID.eq(id))
                .map(usersRecord -> usersRecord.into(User.class));
    }

    /**
     * Находит всех пользователей в базе данных и возвращает их в виде списка объектов User.
     *
     * @return список объектов User, представляющих пользователей
     */
    @Override
    public List<User> findAll() {
        return dslContext.selectFrom(USER)
                .fetchInto(User.class);
    }

    /**
     * Сохраняет пользователя в базе данных и возвращает его в виде объекта User.
     *
     * @param user объект User, представляющий пользователя для сохранения
     * @return объект User, представляющий сохраненного пользователя
     * @throws UniquePhoneNumberException если заданный телефон не уникальный
     */
    @Override
    public User save(User user) {
        return dslContext.insertInto(USER)
                .set(USER.LASTNAME, user.getLastname())
                .set(USER.FIRSTNAME, user.getFirstname())
                .set(USER.SURNAME, user.getSurname())
                .set(USER.REGISTER_DATE, user.getRegisterDate())
                .set(USER.MOBILE_NUMBER, user.getMobileNumber())
                .returning()
                .fetchOptional()
                .map(userRecord -> userRecord.into(User.class))
                .orElseThrow(() -> new UniquePhoneNumberException("User with phone number " + user.getMobileNumber()
                                                                  + " is already exist"));
    }

    /**
     * Обновляет пользователя в базе данных и возвращает его в виде объекта User.
     *
     * @param user объект User, представляющий пользователя для обновления
     * @return объект User, представляющий обновленного пользователя
     * @throws UniquePhoneNumberException если заданный телефон не уникальный
     */
    @Override
    public User update(User user) {
        return dslContext.update(USER)
                .set(USER.LASTNAME, user.getLastname())
                .set(USER.FIRSTNAME, user.getFirstname())
                .set(USER.SURNAME, user.getSurname())
                .set(USER.REGISTER_DATE, user.getRegisterDate())
                .set(USER.MOBILE_NUMBER, user.getMobileNumber())
                .where(USER.ID.eq(user.getId()))
                .returning()
                .fetchOptional()
                .map(userRecord -> userRecord.into(User.class))
                .orElseThrow(() -> new UniquePhoneNumberException("User with phone number " + user.getMobileNumber()
                                                                  + " is already exist"));
    }

    /**
     * Удаляет пользователя из базы данных и связанные с ним счета по его id и возвращает его в виде объекта Optional.
     *
     * @param id Long, представляющее идентификатор пользователя для удаления
     * @return объект Optional, содержащий удаленного пользователя, если он найден, или пустой, если нет
     */
    @Override
    public Optional<User> delete(Long id) {
        deleteAllUsersAccounts(id);
        return dslContext.deleteFrom(USER)
                .where(USER.ID.eq(id))
                .returning()
                .fetchOptional()
                .map(userRecord -> userRecord.into(User.class));
    }

    /**
     * Удаляет все счета, принадлежащие пользователю с заданным id, из базы данных.
     *
     * @param userId Long, представляющее идентификатор пользователя, чьи счета нужно удалить
     */
    private void deleteAllUsersAccounts(Long userId) {
        dslContext.deleteFrom(ACCOUNT)
                .where(ACCOUNT.USER_ID.eq(userId))
                .execute();
    }

}
