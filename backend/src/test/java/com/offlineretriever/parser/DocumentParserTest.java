package com.offlineretriever.parser;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DocumentParserTest {

    @Test
    public void shouldParsePdfContent() {

        DocumentParser parser = new DocumentParser();

        String result = parser.parse("sample.pdf");

        assertTrue(result.contains("Offline Retrieval System"));
        assertTrue(result.contains("Apache Tika successfully extracted this text"));
    }

    @Test
    public void shouldParseDocxContent() {

        DocumentParser parser = new DocumentParser();

        String result = parser.parse("sample.docx");

        assertTrue(result.contains("Offline Retrieval System"));
        assertTrue(result.contains("DOCX test document"));
    }

    @Test
    public void shouldReturnErrorForMissingDocument() {

        DocumentParser parser = new DocumentParser();

        String result = parser.parse("missing-document.pdf");

        assertTrue(result.startsWith("Error reading document:"));
    }
}