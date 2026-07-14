package com.offlineretriever.parser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ImageParserTest {

    @Test
    public void shouldParseImageFile() {

        ImageParser parser = new ImageParser();

        String result = parser.parse("sample.png");

        // 图片可能没有文字，但不应该返回 null
        assertNotNull(result);
    }

    @Test
    public void shouldReturnErrorForMissingImage() {

        ImageParser parser = new ImageParser();

        String result = parser.parse("missing-image.png");

        assertTrue(result.startsWith("Error reading image:"));
    }
}