package ru.clevertec.cleverbank.dto.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeAdapter extends TypeAdapter<LocalTime> {

    /**
     * Записывает объект LocalTime в JSON-формате в выходной поток.
     *
     * @param out   выходной поток для записи JSON-данных
     * @param value объект LocalTime, который нужно записать
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    public void write(JsonWriter out, LocalTime value) throws IOException {
        out.value(value.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    /**
     * Считывает объект LocalTime из JSON-формата из входного потока.
     *
     * @param in входной поток для чтения JSON-данных
     * @return объект LocalTime, полученный из JSON-данных
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    public LocalTime read(JsonReader in) throws IOException {
        return LocalTime.parse(in.nextString());
    }

}
