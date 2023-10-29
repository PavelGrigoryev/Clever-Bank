package ru.clevertec.cleverbank.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class YamlUtilTest {

    @Spy
    private YamlUtil yamlUtil;

    @Test
    @DisplayName("test should return Map of Postgresql params")
    void testShouldReturnMapOfPostgresqlParams() {
        Map<String, String> postgresqlMap = yamlUtil.getYamlMap().get("postgresql");
        String expectedUrl = "jdbc:postgresql://localhost:5432/clever_bank";
        String expectedMaximumPoolSize = "30";

        String actualUrl = postgresqlMap.get("url");
        String actualUser = postgresqlMap.get("user");
        String actualPassword = postgresqlMap.get("password");
        String actualMaximumPoolSize = postgresqlMap.get("maximumPoolSize");

        assertAll(
                () -> assertThat(actualUrl).isEqualTo(expectedUrl),
                () -> assertThat(actualUser).isNotEmpty(),
                () -> assertThat(actualPassword).isNotEmpty(),
                () -> assertThat(actualMaximumPoolSize).isEqualTo(expectedMaximumPoolSize)
        );
    }

    @Test
    @DisplayName("test should return Map of Mongo params")
    void testShouldReturnMapOfMongoParams() {
        Map<String, String> mongoMap = yamlUtil.getYamlMap().get("mongo");
        String expectedClient = "mongodb://localhost:27017";
        String expectedDb = "currency_db";
        String expectedCollection = "nb_rb_currency";

        String actualClient = mongoMap.get("client");
        String actualDb = mongoMap.get("db");
        String actualCollection = mongoMap.get("collection");

        assertAll(
                () -> assertThat(actualClient).isEqualTo(expectedClient),
                () -> assertThat(actualDb).isEqualTo(expectedDb),
                () -> assertThat(actualCollection).isEqualTo(expectedCollection)
        );
    }

    @Test
    @DisplayName("test should return Map of scheduler params")
    void testShouldReturnMapOfSchedulerParams() {
        Map<String, String> shedulerMap = new YamlUtil().getYamlMap().get("scheduler");
        String expectedMonthPercentage = "1.0";
        String expectedInitialDelay = "10";
        String expectedPeriod = "30";

        String actualMonthPercentage = shedulerMap.get("monthPercentage");
        String actualInitialDelay = shedulerMap.get("initialDelay");
        String actualPeriod = shedulerMap.get("period");

        assertAll(
                () -> assertThat(actualMonthPercentage).isEqualTo(expectedMonthPercentage),
                () -> assertThat(actualInitialDelay).isEqualTo(expectedInitialDelay),
                () -> assertThat(actualPeriod).isEqualTo(expectedPeriod)
        );
    }

    @Test
    @DisplayName("test should return Map of NbRBScheduler params")
    void testShouldReturnMapOfNbRBSchedulerParams() {
        Map<String, String> shedulerMap = new YamlUtil().getYamlMap().get("NbRBScheduler");
        String expectedUrl = "https://api.nbrb.by/exrates/rates/";
        String expectedInitialDelay = "6";
        String expectedPeriod = "86400";

        String actualUrl = shedulerMap.get("url");
        String actualInitialDelay = shedulerMap.get("initialDelay");
        String actualPeriod = shedulerMap.get("period");

        assertAll(
                () -> assertThat(actualUrl).isEqualTo(expectedUrl),
                () -> assertThat(actualInitialDelay).isEqualTo(expectedInitialDelay),
                () -> assertThat(actualPeriod).isEqualTo(expectedPeriod)
        );
    }

}
