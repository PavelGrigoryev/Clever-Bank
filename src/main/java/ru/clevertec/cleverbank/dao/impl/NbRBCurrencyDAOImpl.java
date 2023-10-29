package ru.clevertec.cleverbank.dao.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.InsertOneResult;
import lombok.AllArgsConstructor;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.codecs.IntegerCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.types.ObjectId;
import ru.clevertec.cleverbank.dao.NbRBCurrencyDAO;
import ru.clevertec.cleverbank.exception.internalservererror.FailedConnectionException;
import ru.clevertec.cleverbank.model.NbRBCurrency;
import ru.clevertec.cleverbank.dao.codec.NbRBCurrencyCodec;
import ru.clevertec.cleverbank.util.MongoConnectionManager;

import java.util.Optional;

@AllArgsConstructor
public class NbRBCurrencyDAOImpl implements NbRBCurrencyDAO {

    private final MongoCollection<NbRBCurrency> mongoCollection;

    public NbRBCurrencyDAOImpl() {
        mongoCollection = MongoConnectionManager.getInstance(NbRBCurrency.class).getMongoCollection()
                .withCodecRegistry(CodecRegistries.fromCodecs(new NbRBCurrencyCodec(), new IntegerCodec()));
    }

    /**
     * Находит курс валюты НБ РБ по ее currencyId в базе данных и возвращает ее в виде объекта Optional.
     *
     * @param currencyId Integer, представляющее идентификатор курса по НБ РБ
     * @return объект Optional, содержащий курс, если он найден, или пустой, если нет
     */
    @Override
    public Optional<NbRBCurrency> findByCurrencyId(Integer currencyId) {
        return Optional.ofNullable(mongoCollection.find(Filters.eq("currency_id", currencyId))
                .sort(Sorts.descending("update_date"))
                .limit(1)
                .first());
    }

    /**
     * Сохраняет курс валюты по НБ РБ в базе данных и возвращает ее в виде объекта NbRBCurrency.
     *
     * @param nbRBCurrency объект NbRBCurrency, представляющий курс для сохранения
     * @return объект NbRBCurrency, представляющий сохраненный курс
     * @throws FailedConnectionException если произошла ошибка при работе с базой данных
     */
    @Override
    public NbRBCurrency save(NbRBCurrency nbRBCurrency) {
        ObjectId id = Optional.of(mongoCollection.insertOne(nbRBCurrency))
                .map(InsertOneResult::getInsertedId)
                .map(BsonValue::asObjectId)
                .map(BsonObjectId::getValue)
                .orElseThrow(() -> new FailedConnectionException("Failed to save " + nbRBCurrency));
        nbRBCurrency.setId(id);
        return nbRBCurrency;
    }

}
