package com.offlineretriever.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FileScanner {

    public List<Path> scan(String folderPath) {
        try {
            return Files.list(Path.of(folderPath))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan folder: " + folderPath, e);
        }
    }
}