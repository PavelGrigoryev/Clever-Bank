package ru.clevertec.cleverbank.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class RandomStringGenerator {

    public String generateRandomString() {
        SecureRandom random = new SecureRandom();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        return IntStream.range(0, 7)
                .mapToObj(i -> IntStream.range(0, 4)
                        .mapToObj(j -> chars.charAt(random.nextInt(chars.length())))
                        .map(Object::toString)
                        .collect(Collectors.joining()))
                .collect(Collectors.joining(" "))
                .trim();
    }

}
