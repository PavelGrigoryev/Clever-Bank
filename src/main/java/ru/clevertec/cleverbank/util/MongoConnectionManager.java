package ru.clevertec.cleverbank.util;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MongoConnectionManager<D> {

    private static final Map<Class<?>, MongoConnectionManager<?>> INSTANCES = new HashMap<>();
    private final MongoCollection<D> mongoCollection;

    private MongoConnectionManager(Class<D> documentClass) {
        Map<String, String> mongoMap = new YamlUtil().getYamlMap().get("mongo");
        mongoCollection = MongoClients.create(mongoMap.get("client"))
                .getDatabase(mongoMap.get("db"))
                .getCollection(mongoMap.get("collection"), documentClass);
    }

    @SuppressWarnings("unchecked")
    public static <D> MongoConnectionManager<D> getInstance(Class<D> documentClass) {
        return (MongoConnectionManager<D>) INSTANCES.computeIfAbsent(documentClass, MongoConnectionManager::new);
    }

}
