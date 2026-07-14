package com.offlineretriever.model;

import java.time.LocalDateTime;

public class FileMetadata {

    private final String fileName;
    private final String fileType;
    private final long fileSize;
    private final LocalDateTime lastModified;
    private final String filePath;

    public FileMetadata(
            String fileName,
            String fileType,
            long fileSize,
            LocalDateTime lastModified,
            String filePath) {

        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public String toString() {
        return "FileMetadata{" +
                "fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", lastModified=" + lastModified +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}