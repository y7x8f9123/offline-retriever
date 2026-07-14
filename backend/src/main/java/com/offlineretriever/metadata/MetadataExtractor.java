package com.offlineretriever.metadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.offlineretriever.model.FileMetadata;

public class MetadataExtractor {

    public FileMetadata extract(Path filePath) {

        try {
            BasicFileAttributes attributes =
                    Files.readAttributes(filePath, BasicFileAttributes.class);

            String fileName = filePath.getFileName().toString();
            String fileType = getExtension(fileName);
            long fileSize = attributes.size();

            LocalDateTime lastModified = LocalDateTime.ofInstant(
                    attributes.lastModifiedTime().toInstant(),
                    ZoneId.systemDefault()
            );

            return new FileMetadata(
                    fileName,
                    fileType,
                    fileSize,
                    lastModified,
                    filePath.toAbsolutePath().toString()
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to extract metadata from: " + filePath,
                    e
            );
        }
    }

    private String getExtension(String fileName) {

        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1).toLowerCase();
    }
}