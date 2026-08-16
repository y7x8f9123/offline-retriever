package com.offlineretriever.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ChromaBridgeClient {

    private final Gson gson = new Gson();
    private final Path bridgePath;

    public ChromaBridgeClient() {
        this.bridgePath = findBridgePath();
    }

    public void indexText(
            File file,
            String content
    ) throws IOException {

        List<String> command = new ArrayList<>();

        command.add("python");
        command.add(bridgePath.toString());
        command.add("index-text");

        command.add("--file-name");
        command.add(file.getName());

        command.add("--file-path");
        command.add(file.getAbsolutePath());

        command.add("--file-type");
        command.add(getExtension(file.getName()));

        command.add("--file-size");
        command.add(String.valueOf(file.length()));

        command.add("--last-modified");
        command.add(String.valueOf(file.lastModified()));

        runWithInput(
                command,
                content
        );
    }

    public List<BridgeSearchResult> searchText(
            String query,
            int topK
    ) throws IOException {

        List<String> command = new ArrayList<>();

        command.add("python");
        command.add(bridgePath.toString());
        command.add("search-text");

        command.add("--top-k");
        command.add(String.valueOf(topK));

        String json = runWithInput(
                command,
                query
        );

        Type type =
                new TypeToken<List<BridgeSearchResult>>() {
                }.getType();

        List<BridgeSearchResult> results =
                gson.fromJson(json, type);

        return results == null
                ? new ArrayList<>()
                : results;
    }

    public List<BridgeIndexedFile> listFiles()
            throws IOException {

        List<String> command = new ArrayList<>();

        command.add("python");
        command.add(bridgePath.toString());
        command.add("list");

        String json = runWithoutInput(command);

        Type type =
                new TypeToken<List<BridgeIndexedFile>>() {
                }.getType();

        List<BridgeIndexedFile> files =
                gson.fromJson(json, type);

        if (files == null) {
            return new ArrayList<>();
        }

        for (BridgeIndexedFile file : files) {

            boolean exists =
                    file.filePath != null &&
                    new File(file.filePath).isFile();

            file.exists = exists;
        }

        return files;
    }

    public void deleteFile(String id)
            throws IOException {

        List<String> command = new ArrayList<>();

        command.add("python");
        command.add(bridgePath.toString());
        command.add("delete");
        command.add("--id");
        command.add(id);

        runWithoutInput(command);
    }

    private String runWithInput(
            List<String> command,
            String input
    ) throws IOException {

        ProcessBuilder builder =
                new ProcessBuilder(command);

        /*
         * Keep Python model progress output away
         * from stdout so stdout remains valid JSON.
         */
        builder.redirectError(
                ProcessBuilder.Redirect.INHERIT
        );

        Process process = builder.start();

        try (
                BufferedWriter writer =
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        process.getOutputStream(),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {
            writer.write(input);
        }

        String stdout;

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        process.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {
            stdout = reader.lines()
                    .reduce(
                            "",
                            (a, b) -> a + b
                    );
        }

        waitForProcess(process);

        if (stdout.trim().isEmpty()) {
            throw new IOException(
                    "Python bridge returned no output."
            );
        }

        return stdout.trim();
    }

    private String runWithoutInput(
            List<String> command
    ) throws IOException {

        ProcessBuilder builder =
                new ProcessBuilder(command);

        builder.redirectError(
                ProcessBuilder.Redirect.INHERIT
        );

        Process process = builder.start();

        String stdout;

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        process.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {
            stdout = reader.lines()
                    .reduce(
                            "",
                            (a, b) -> a + b
                    );
        }

        waitForProcess(process);

        if (stdout.trim().isEmpty()) {
            throw new IOException(
                    "Python bridge returned no output."
            );
        }

        return stdout.trim();
    }

    private void waitForProcess(
            Process process
    ) throws IOException {

        try {
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IOException(
                        "Python bridge failed with exit code " +
                        exitCode
                );
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IOException(
                    "Python bridge was interrupted.",
                    e
            );
        }
    }

    private Path findBridgePath() {

        Path current =
                Paths.get(
                        System.getProperty("user.dir")
                ).toAbsolutePath();

        Path candidate = current;

        for (int i = 0; i < 5; i++) {

            Path bridge =
                    candidate.resolve(
                            Paths.get(
                                    "scripts",
                                    "storage",
                                    "chroma_bridge.py"
                            )
                    );

            if (Files.exists(bridge)) {
                return bridge.normalize();
            }

            candidate = candidate.getParent();

            if (candidate == null) {
                break;
            }
        }

        throw new IllegalStateException(
                "Could not locate " +
                "scripts/storage/chroma_bridge.py"
        );
    }

    private String getExtension(
            String fileName
    ) {

        int index = fileName.lastIndexOf('.');

        if (
                index < 0 ||
                index == fileName.length() - 1
        ) {
            return "";
        }

        return fileName
                .substring(index + 1)
                .toLowerCase();
    }

    public static class BridgeSearchResult {

        private String id;
        private String fileName;
        private String filePath;
        private String fileType;
        private double score;

        public String getId() {
            return id;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getFileType() {
            return fileType;
        }

        public double getScore() {
            return score;
        }
    }

    public static class BridgeIndexedFile {

        private String id;
        private String fileName;
        private String filePath;
        private String fileType;
        private long fileSize;
        private double lastModified;
        private String contentType;
        private boolean exists;

        public String getId() {
            return id;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getFileType() {
            return fileType;
        }

        public long getFileSize() {
            return fileSize;
        }

        public double getLastModified() {
            return lastModified;
        }

        public String getContentType() {
            return contentType;
        }

        public boolean exists() {
            return exists;
        }
    }
}