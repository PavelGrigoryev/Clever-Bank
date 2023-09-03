package ru.clevertec.cleverbank.util;

import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

@Getter
public class YamlUtil {

    private final Map<String, Map<String, String>> yamlMap;

    /**
     * Конструктор класса, который загружает файл application.yaml из ресурсов и парсит его в карту с помощью класса Yaml.
     */
    public YamlUtil() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("application.yaml");
        yamlMap = yaml.load(inputStream);
    }

}
