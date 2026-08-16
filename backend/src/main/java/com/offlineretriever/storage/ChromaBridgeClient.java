package com.offlineretriever.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChromaBridgeClient {

    private static final String BASE_URL =
            "http://127.0.0.1:8765";

    private static final int STARTUP_WAIT_ATTEMPTS =
            60;

    private static final long STARTUP_WAIT_MS =
            500;

    private final Gson gson =
            new Gson();

    private final HttpClient httpClient =
        HttpClient.newBuilder()
                .version(
                        HttpClient.Version.HTTP_1_1
                )
                .connectTimeout(
                        Duration.ofSeconds(3)
                )
                .build();

    public ChromaBridgeClient() {
        ensureServerRunning();
    }

    public void indexText(
            File file,
            String content
    ) throws IOException {

        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "file_path",
                file.getAbsolutePath()
        );

        body.put(
                "content",
                content
        );

        postJson(
                "/index-text",
                gson.toJson(body)
        );
    }

    public void indexImage(
            File file
    ) throws IOException {

        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "file_path",
                file.getAbsolutePath()
        );

        postJson(
                "/index-image",
                gson.toJson(body)
        );
    }

    public List<BridgeSearchResult> searchAll(
            String query,
            int topK
    ) throws IOException {

        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "query",
                query
        );

        body.put(
                "top_k",
                topK
        );

        String json =
                postJson(
                        "/search",
                        gson.toJson(body)
                );

        Type type =
                new TypeToken<
                        List<BridgeSearchResult>
                        >() {
                }.getType();

        List<BridgeSearchResult> results =
                gson.fromJson(
                        json,
                        type
                );

        return results == null
                ? new ArrayList<>()
                : results;
    }

    public List<BridgeIndexedFile> listFiles()
            throws IOException {

        String json =
                getJson(
                        "/files"
                );

        Type type =
                new TypeToken<
                        List<BridgeIndexedFile>
                        >() {
                }.getType();

        List<BridgeIndexedFile> files =
                gson.fromJson(
                        json,
                        type
                );

        return files == null
                ? new ArrayList<>()
                : files;
    }

    public void deleteFile(
            String id
    ) throws IOException {

        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "id",
                id
        );

        postJson(
                "/delete",
                gson.toJson(body)
        );
    }

    private String getJson(
            String endpoint
    ) throws IOException {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL
                                        + endpoint
                                )
                        )
                        .timeout(
                                Duration.ofSeconds(10)
                        )
                        .GET()
                        .build();

        return sendRequest(
                request
        );
    }

    private String postJson(
            String endpoint,
            String json
    ) throws IOException {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL
                                        + endpoint
                                )
                        )
                        .timeout(
                                Duration.ofSeconds(60)
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(
                                                json,
                                                StandardCharsets.UTF_8
                                        )
                        )
                        .build();

        return sendRequest(
                request
        );
    }

    private String sendRequest(
            HttpRequest request
    ) throws IOException {

        try {

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers
                                    .ofString(
                                            StandardCharsets.UTF_8
                                    )
                    );

            int status =
                    response.statusCode();

            if (
                    status < 200
                    || status >= 300
            ) {

                throw new IOException(
                        "Local retrieval service returned HTTP "
                        + status
                        + ": "
                        + response.body()
                );
            }

            return response.body();

        } catch (
                InterruptedException e
        ) {

            Thread
                    .currentThread()
                    .interrupt();

            throw new IOException(
                    "Local retrieval service request was interrupted.",
                    e
            );
        }
    }

    private void ensureServerRunning() {

        if (isServerReady()) {
            return;
        }

        startServer();

        for (
                int i = 0;
                i < STARTUP_WAIT_ATTEMPTS;
                i++
        ) {

            if (isServerReady()) {
                return;
            }

            try {
                Thread.sleep(
                        STARTUP_WAIT_MS
                );

            } catch (
                    InterruptedException e
            ) {

                Thread
                        .currentThread()
                        .interrupt();

                throw new IllegalStateException(
                        "Interrupted while waiting for local retrieval service.",
                        e
                );
            }
        }

        throw new IllegalStateException(
                "Local retrieval service did not become ready."
        );
    }

    private boolean isServerReady() {

        try {

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            BASE_URL
                                            + "/health"
                                    )
                            )
                            .timeout(
                                    Duration.ofSeconds(2)
                            )
                            .GET()
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers
                                    .ofString()
                    );

            return response.statusCode()
                    == 200;

        } catch (
                Exception e
        ) {

            return false;
        }
    }

    private void startServer() {

        Path serverPath =
                findServerPath();

        try {

            ProcessBuilder builder =
                    new ProcessBuilder(
                            "python",
                            serverPath.toString()
                    );

            Path projectRoot =
                    serverPath
                            .getParent()
                            .getParent()
                            .getParent();

            builder.directory(
                    projectRoot.toFile()
            );

            /*
             * Prevent the Python process from blocking
             * because stdout/stderr buffers fill up.
             */
            builder.redirectOutput(
                ProcessBuilder.Redirect.DISCARD
            );

            builder.redirectError(
                ProcessBuilder.Redirect.DISCARD
            );

            builder.start();

        } catch (
                IOException e
        ) {

            throw new IllegalStateException(
                    "Could not start local retrieval service.",
                    e
            );
        }
    }

    private Path findServerPath() {

        Path current =
                Paths.get(
                        System.getProperty(
                                "user.dir"
                        )
                ).toAbsolutePath();

        Path candidate =
                current;

        for (
                int i = 0;
                i < 5;
                i++
        ) {

            Path server =
                    candidate.resolve(
                            Paths.get(
                                    "scripts",
                                    "service",
                                    "retrieval_server.py"
                            )
                    );

            if (
                    Files.exists(
                            server
                    )
            ) {

                return server.normalize();
            }

            candidate =
                    candidate.getParent();

            if (
                    candidate == null
            ) {
                break;
            }
        }

        throw new IllegalStateException(
                "Could not locate "
                + "scripts/service/retrieval_server.py"
        );
    }

    public static class BridgeSearchResult {

        private String id;
        private String fileName;
        private String filePath;
        private String fileType;
        private String contentType;
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

        public String getContentType() {
            return contentType;
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