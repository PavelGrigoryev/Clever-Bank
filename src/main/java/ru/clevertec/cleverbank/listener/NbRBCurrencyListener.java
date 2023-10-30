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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + code))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            int responseCode = httpResponse.statusCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String body = httpResponse.body();
                NbRBCurrencyResponse response = gson.fromJson(body, NbRBCurrencyResponse.class);
                lock.lock();
                NbRBCurrency saved = nbRBCurrencyService.save(response);
                lock.unlock();
                log.info("Saving currency on schedule:\n{}", saved);
            } else {
                log.error("GET request failed: {}", responseCode);
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
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
