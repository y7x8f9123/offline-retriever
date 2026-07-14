package com.offlineretriever.factory;

import com.offlineretriever.parser.DocumentParser;
import com.offlineretriever.parser.Parser;
import com.offlineretriever.parser.TextParser;
import com.offlineretriever.parser.ImageParser;

public class ParserFactory {

    public static Parser getParser(String fileName) {

        String lower = fileName.toLowerCase();

        if (lower.endsWith(".txt")) {
            return new TextParser();
        }

        if (lower.endsWith(".pdf") || lower.endsWith(".docx")) {
            return new DocumentParser();
        }

        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")) {
            return new ImageParser();
        }

        return null;
    }
}