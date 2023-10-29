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
import ru.clevertec.cleverbank.util.HikariConnectionManager;

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
        try (JdbcConnection jdbcConnection = new JdbcConnection(HikariConnectionManager.getConnection());
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

}
