package com.offlineretriever.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public class FileScannerTest {

    @Test
    public void shouldScanFolder() throws Exception {

        Path tempDir = Files.createTempDirectory(
                "file-scanner-test"
        );

        try {
            Path txt = Files.createFile(
                    tempDir.resolve("sample.txt")
            );

            Path pdf = Files.createFile(
                    tempDir.resolve("sample.pdf")
            );

            Path docx = Files.createFile(
                    tempDir.resolve("sample.docx")
            );

            FileScanner scanner = new FileScanner();

            List<Path> files = scanner.scan(
                    tempDir.toString()
            );

            assertEquals(3, files.size());
            assertTrue(files.contains(txt));
            assertTrue(files.contains(pdf));
            assertTrue(files.contains(docx));

        } finally {
            Files.walk(tempDir)
                    .sorted((a, b) ->
                            b.getNameCount()
                                    - a.getNameCount())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception ignored) {
                        }
                    });
        }
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowErrorForMissingFolder() {

        FileScanner scanner = new FileScanner();

        scanner.scan(
                "missing-folder-" + System.nanoTime()
        );
    }
}