package ru.clevertec.cleverbank.listener;

import com.google.gson.Gson;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cleverbank.dto.nbrbcurrency.NbRBCurrencyResponse;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.NbRBCurrency;
import ru.clevertec.cleverbank.service.NbRBCurrencyService;
import ru.clevertec.cleverbank.service.impl.NbRBCurrencyServiceImpl;
import ru.clevertec.cleverbank.util.YamlUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@WebListener
public class NbRBCurrencyListener implements ServletContextListener {

    private final ScheduledExecutorService scheduler;
    private final Lock lock;
    private final NbRBCurrencyService nbRBCurrencyService;
    private final Gson gson;

    public NbRBCurrencyListener() {
        scheduler = Executors.newScheduledThreadPool(3);
        lock = new ReentrantLock();
        nbRBCurrencyService = new NbRBCurrencyServiceImpl();
        gson = new Gson();
    }

    /**
     * Переопределяет метод contextInitialized, чтобы запустить задачу по получению курса валюты по НБ РБ.
     *
     * @param sce объект ServletContextEvent, содержащий информацию о контексте сервлета
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Map<String, String> shedulerMap = new YamlUtil().getYamlMap().get("NbRBScheduler");
        String apiUrl = shedulerMap.get("url");
        long initialDelay = Long.parseLong(shedulerMap.get("initialDelay"));
        long period = Long.parseLong(shedulerMap.get("period"));

        List.of(Currency.RUB, Currency.USD, Currency.EUR)
                .forEach(currency -> {
                    Runnable task = () -> getCurrencyFromNbRB(apiUrl, currency.getCode());
                    scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
                });
    }

    private void getCurrencyFromNbRB(String apiUrl, Integer code) {
        try {
            URL url = new URL(apiUrl + code);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String result = readResponseFromNbRB(connection);
                NbRBCurrencyResponse response = gson.fromJson(result, NbRBCurrencyResponse.class);
                lock.lock();
                NbRBCurrency saved = nbRBCurrencyService.save(response);
                lock.unlock();
                log.info("Saving currency on schedule:\n{}", saved);
            } else {
                log.error("GET request failed: {}", responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String readResponseFromNbRB(HttpURLConnection connection) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }

    /**
     * Переопределяет метод contextDestroyed, чтобы остановить пулы потоков.
     *
     * @param sce объект ServletContextEvent, содержащий информацию о контексте сервлета
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        scheduler.shutdown();
    }

}
