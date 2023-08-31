package ru.clevertec.cleverbank.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import liquibase.command.CommandScope;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.util.YamlUtil;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@WebListener
public class LiquibaseInitializer implements ServletContextListener {

    private Database database;

    /**
     * Переопределяет метод contextInitialized, чтобы выполнить обновление базы данных с помощью Liquibase.
     *
     * @param sce объект ServletContextEvent, содержащий информацию о контексте сервлета
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (JdbcConnection jdbcConnection = new JdbcConnection(getDatabaseConnection());
             ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor()) {
            database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
            new CommandScope("update")
                    .addArgumentValue("changeLogFile", "db/changelog/db.changelog-master.yaml")
                    .addArgumentValue("resourceAccessor", classLoaderResourceAccessor)
                    .addArgumentValue("database", database)
                    .execute();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Переопределяет метод contextDestroyed, чтобы закрыть соединение с базой данных.
     *
     * @param sce объект ServletContextEvent, содержащий информацию о контексте сервлета
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (database != null) {
            try {
                database.close();
            } catch (DatabaseException e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * Возвращает объект Connection, представляющий соединение с базой данных PostgresSQL.
     *
     * @return объект Connection, представляющий соединение с базой данных PostgresSQL
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    private Connection getDatabaseConnection() throws SQLException {
        Map<String, String> postgresqlMap = new YamlUtil().getYamlMap().get("postgresql");
        String url = postgresqlMap.get("url");
        String user = postgresqlMap.get("user");
        String password = postgresqlMap.get("password");
        try {
            Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            log.error(e.getMessage());
        }
        return DriverManager.getConnection(url, user, password);
    }

}
