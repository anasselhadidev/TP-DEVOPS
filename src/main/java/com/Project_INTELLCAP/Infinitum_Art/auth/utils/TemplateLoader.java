package com.Project_INTELLCAP.Infinitum_Art.auth.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TemplateLoader {

    public static String loadTemplate(String fileName, Map<String, String> variables) {
        try (InputStream inputStream = TemplateLoader.class
                .getClassLoader()
                .getResourceAsStream("templates/" + fileName)) {

            if (inputStream == null) {
                throw new FileNotFoundException("Template file not found: " + fileName);
            }

            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            for (Map.Entry<String, String> entry : variables.entrySet()) {
                content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            return content;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template", e);
        }
    }
}
