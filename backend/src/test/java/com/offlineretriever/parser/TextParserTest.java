package com.offlineretriever.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class TextParserTest {

    @Test
    public void shouldReadTextFileContent() throws IOException {

        Path tempFile = Files.createTempFile("text-parser-test", ".txt");
        Files.writeString(tempFile, "Hello from TextParser");

        TextParser parser = new TextParser();

        String result = parser.parse(tempFile.toString());

        assertEquals("Hello from TextParser", result);

        Files.deleteIfExists(tempFile);
    }

    @Test
    public void shouldReturnErrorForMissingFile() {

        TextParser parser = new TextParser();

        String result = parser.parse("missing-file.txt");

        assertTrue(result.startsWith("Error reading file:"));
    }
}