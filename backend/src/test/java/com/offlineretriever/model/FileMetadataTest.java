package com.offlineretriever.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Test;

public class FileMetadataTest {

    @Test
    public void shouldStoreAndReturnMetadataFields() {

        LocalDateTime modifiedTime =
                LocalDateTime.of(2026, 7, 14, 22, 15);

        FileMetadata metadata = new FileMetadata(
                "sample.pdf",
                "pdf",
                27455L,
                modifiedTime,
                "C:\\files\\sample.pdf"
        );

        assertEquals("sample.pdf", metadata.getFileName());
        assertEquals("pdf", metadata.getFileType());
        assertEquals(27455L, metadata.getFileSize());
        assertEquals(modifiedTime, metadata.getLastModified());
        assertEquals("C:\\files\\sample.pdf", metadata.getFilePath());
    }

    @Test
    public void shouldGenerateReadableStringRepresentation() {

        LocalDateTime modifiedTime =
                LocalDateTime.of(2026, 7, 14, 22, 15);

        FileMetadata metadata = new FileMetadata(
                "sample.txt",
                "txt",
                107L,
                modifiedTime,
                "C:\\files\\sample.txt"
        );

        String result = metadata.toString();

        assertTrue(result.contains("sample.txt"));
        assertTrue(result.contains("txt"));
        assertTrue(result.contains("107"));
        assertTrue(result.contains("2026-07-14T22:15"));
        assertTrue(result.contains("C:\\files\\sample.txt"));
    }
}