package ru.clevertec.cleverbank.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.model.Account;
import ru.clevertec.cleverbank.service.AccountService;
import ru.clevertec.cleverbank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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

    @Override
    public void contextInitialized(ServletContextEvent sce) {
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
                    BigDecimal rate = new BigDecimal(1);
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
        scheduler.scheduleAtFixedRate(task, 10, 30, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        executor.shutdown();
        scheduler.shutdown();
    }

}
