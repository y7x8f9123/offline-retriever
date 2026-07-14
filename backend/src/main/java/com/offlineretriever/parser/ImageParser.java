package com.offlineretriever.parser;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public class ImageParser implements Parser {

    private final Tika tika = new Tika();

    @Override
    public String parse(String filePath) {

        try {
            return tika.parseToString(new File(filePath));
        } catch (IOException | TikaException e) {
            return "Error reading image: " + e.getMessage();
        }
    }
}