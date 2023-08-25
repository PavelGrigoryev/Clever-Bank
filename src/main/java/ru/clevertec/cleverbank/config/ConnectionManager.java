package ru.clevertec.cleverbank.config;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.exception.internalservererror.JDBCConnectionException;
import ru.clevertec.cleverbank.util.YamlUtil;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@UtilityClass
public class ConnectionManager {

    private Connection connection;

    public Connection getJDBCConnection() {
        if (connection == null) {
            Map<String, String> postgresqlMap = new YamlUtil().getYamlMap().get("postgresql");
            String url = postgresqlMap.get("url");
            String user = postgresqlMap.get("user");
            String password = postgresqlMap.get("password");
            try {
                Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
                connection = DriverManager.getConnection(url, user, password);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException |
                     ClassNotFoundException | SQLException e) {
                log.error(e.getMessage());
                throw new JDBCConnectionException();
            }
        }
        return connection;
    }

}
