package com.offlineretriever.factory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.offlineretriever.parser.DocumentParser;
import com.offlineretriever.parser.ImageParser;
import com.offlineretriever.parser.Parser;
import com.offlineretriever.parser.TextParser;

public class ParserFactoryTest {

    @Test
    public void shouldReturnTextParser() {
        Parser parser = ParserFactory.getParser("sample.txt");
        assertTrue(parser instanceof TextParser);
    }

    @Test
    public void shouldReturnDocumentParserForPdf() {
        Parser parser = ParserFactory.getParser("sample.pdf");
        assertTrue(parser instanceof DocumentParser);
    }

    @Test
    public void shouldReturnDocumentParserForDocx() {
        Parser parser = ParserFactory.getParser("sample.docx");
        assertTrue(parser instanceof DocumentParser);
    }

    @Test
    public void shouldReturnImageParser() {
        Parser parser = ParserFactory.getParser("sample.png");
        assertTrue(parser instanceof ImageParser);
    }

    @Test
    public void shouldReturnNullForUnsupportedFile() {
        Parser parser = ParserFactory.getParser("sample.xyz");
        assertNull(parser);
    }
}