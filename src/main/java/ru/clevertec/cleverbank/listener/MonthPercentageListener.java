package ru.clevertec.cleverbank.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.service.AccountService;
import ru.clevertec.cleverbank.service.impl.AccountServiceImpl;
import ru.clevertec.cleverbank.util.YamlUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@WebListener
public class MonthPercentageListener implements ServletContextListener {

    private final ExecutorService executor;
    private final ScheduledExecutorService scheduler;
    private final Lock lock;
    private final AccountService accountService;

    public MonthPercentageListener() {
        executor = Executors.newCachedThreadPool();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        lock = new ReentrantLock();
        accountService = new AccountServiceImpl();
    }

    /**
     * Переопределяет метод contextInitialized, чтобы запустить задачу по начислению процентов по счетам в конце
     * каждого месяца.
     *
     * @param sce объект ServletContextEvent, содержащий информацию о контексте сервлета
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Map<String, String> shedulerMap = new YamlUtil().getYamlMap().get("scheduler");
        String monthPercentage = shedulerMap.get("monthPercentage");
        long initialDelay = Long.parseLong(shedulerMap.get("initialDelay"));
        long period = Long.parseLong(shedulerMap.get("period"));
        Runnable task = () -> {
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.now();
            LocalTime lowerBound = LocalTime.of(23, 59, 29);
            LocalTime upperBound = LocalTime.of(23, 59, 59);
            boolean isLastDayOfMonth = currentDate.getDayOfMonth() == currentDate.lengthOfMonth();
            boolean isInInterval = currentTime.isAfter(lowerBound) && currentTime.isBefore(upperBound);
            log.info("Scheduled task isLastDayOfMonth={} , isInInterval={}", isLastDayOfMonth, isInInterval);
            if (isLastDayOfMonth && isInInterval) {
                List<Account> accounts = accountService.findAll();
                accounts.forEach(account -> executor.submit(() -> {
                    BigDecimal balance = account.getBalance();
                    BigDecimal rate = new BigDecimal(monthPercentage);
                    BigDecimal percentage = balance.multiply(rate).multiply(BigDecimal.valueOf(0.01))
                            .setScale(2, RoundingMode.DOWN);
                    balance = balance.add(percentage);
                    lock.lock();
                    accountService.updateBalance(account, balance);
                    lock.unlock();
                    log.info("Account {} has been credited with +{} at the end of month {}",
                            account.getId(), percentage, currentDate.getMonth());
                }));
            }
        };
        scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
    }

    /**
     * Переопределяет метод contextDestroyed, чтобы остановить пулы потоков.
     *
     * @param sce объект ServletContextEvent, содержащий информацию о контексте сервлета
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        executor.shutdown();
        scheduler.shutdown();
    }

}
