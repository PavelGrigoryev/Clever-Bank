package ru.clevertec.cleverbank.exception.handler;

import java.util.List;

public record ValidationResponse(List<Violation> violations) {
}
