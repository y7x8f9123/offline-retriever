package com.offlineretriever.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextParser implements Parser {

    @Override
    public String parse(String filePath) {

        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }

    }

}