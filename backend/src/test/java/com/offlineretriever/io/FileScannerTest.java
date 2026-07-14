package com.offlineretriever.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public class FileScannerTest {

    @Test
    public void shouldScanCurrentFolder() {

        FileScanner scanner = new FileScanner();

        List<Path> files = scanner.scan(".");

        assertFalse(files.isEmpty());

        boolean hasTxt = files.stream()
                .anyMatch(path -> path.getFileName().toString().equals("sample.txt"));

        boolean hasPdf = files.stream()
                .anyMatch(path -> path.getFileName().toString().equals("sample.pdf"));

        boolean hasDocx = files.stream()
                .anyMatch(path -> path.getFileName().toString().equals("sample.docx"));

        assertTrue(hasTxt);
        assertTrue(hasPdf);
        assertTrue(hasDocx);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowErrorForMissingFolder() {

        FileScanner scanner = new FileScanner();

        scanner.scan("missing-folder");
    }
}