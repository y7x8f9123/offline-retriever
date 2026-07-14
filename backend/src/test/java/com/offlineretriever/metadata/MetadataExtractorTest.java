package com.offlineretriever.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.offlineretriever.model.FileMetadata;

public class MetadataExtractorTest {

    @Test
    public void shouldExtractFileMetadata() throws IOException {

        Path tempFile = Files.createTempFile("metadata-test", ".txt");
        Files.writeString(tempFile, "Metadata Test");

        MetadataExtractor extractor = new MetadataExtractor();

        FileMetadata metadata = extractor.extract(tempFile);

        assertEquals(tempFile.getFileName().toString(), metadata.getFileName());
        assertEquals("txt", metadata.getFileType());
        assertTrue(metadata.getFileSize() > 0);
        assertTrue(metadata.getFilePath().contains(tempFile.getFileName().toString()));

        Files.deleteIfExists(tempFile);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionForMissingFile() {

        MetadataExtractor extractor = new MetadataExtractor();

        extractor.extract(Path.of("missing-file.txt"));
    }
}