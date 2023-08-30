package ru.clevertec.cleverbank.dto.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.clevertec.cleverbank.exception.conflict.LocalDateParseException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        try {
            return LocalDate.parse(in.nextString());
        } catch (DateTimeParseException e) {
            throw new LocalDateParseException("Date is out of pattern: yyyy-MM-dd. Right example: 2023-08-30");
        }
    }

}
