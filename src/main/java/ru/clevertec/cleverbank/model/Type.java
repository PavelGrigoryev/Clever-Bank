package ru.clevertec.cleverbank.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Type {

    REPLENISHMENT("Пополнение"),
    WITHDRAWAL("Снятие"),
    TRANSFER("Перевод"),
    EXCHANGE("Обмен");

    private final String name;

}
