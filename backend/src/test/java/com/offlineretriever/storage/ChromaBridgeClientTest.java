package com.offlineretriever.storage;

import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import javax.imageio.ImageIO;

import static org.junit.Assert.*;

public class ChromaBridgeClientTest {

    @Test
    public void testIndexTextAndListFiles() throws Exception {
        ChromaBridgeClient client =
                new ChromaBridgeClient();

        File tempFile =
                File.createTempFile(
                        "offline-retriever-",
                        ".txt"
                );

        tempFile.deleteOnExit();

        Files.writeString(
                tempFile.toPath(),
                "software engineering retrieval test",
                StandardCharsets.UTF_8
        );

        client.indexText(
                tempFile,
                "software engineering retrieval test"
        );

        List<ChromaBridgeClient.BridgeIndexedFile> files =
                client.listFiles();

        assertNotNull(files);

        ChromaBridgeClient.BridgeIndexedFile indexedFile =
                files.stream()
                        .filter(
                                file ->
                                        tempFile
                                                .getAbsolutePath()
                                                .equals(
                                                        file.getFilePath()
                                                )
                        )
                        .findFirst()
                        .orElseThrow();

        assertNotNull(
                indexedFile.getId()
        );

        assertNotNull(
                indexedFile.getFileName()
        );

        assertNotNull(
                indexedFile.getFilePath()
        );

        assertNotNull(
                indexedFile.getFileType()
        );

        assertTrue(
                indexedFile.getFileSize()
                >= 0
        );

        assertTrue(
                indexedFile.getLastModified()
                > 0
        );

        assertNotNull(
                indexedFile.getContentType()
        );

        indexedFile.exists();
    }

    @Test
    public void testSearchAll() throws Exception {
        ChromaBridgeClient client =
                new ChromaBridgeClient();

        List<ChromaBridgeClient.BridgeSearchResult> results =
                client.searchAll(
                        "software engineering",
                        5
                );

        assertNotNull(results);

        if (!results.isEmpty()) {
            ChromaBridgeClient.BridgeSearchResult result =
                    results.get(0);

            assertNotNull(
                    result.getId()
            );

            assertNotNull(
                    result.getFileName()
            );

            assertNotNull(
                    result.getFilePath()
            );

            assertNotNull(
                    result.getFileType()
            );

            assertNotNull(
                    result.getContentType()
            );

            assertTrue(
                    result.getScore()
                    >= 0
            );
        }
    }

    @Test
    public void testDeleteFile() throws Exception {
        ChromaBridgeClient client =
                new ChromaBridgeClient();

        File tempFile =
                File.createTempFile(
                        "offline-retriever-delete-",
                        ".txt"
                );

        tempFile.deleteOnExit();

        client.indexText(
                tempFile,
                "temporary delete test"
        );

        List<ChromaBridgeClient.BridgeIndexedFile> files =
                client.listFiles();

        ChromaBridgeClient.BridgeIndexedFile indexedFile =
                files.stream()
                        .filter(
                                file ->
                                        tempFile
                                                .getAbsolutePath()
                                                .equals(
                                                        file.getFilePath()
                                                )
                        )
                        .findFirst()
                        .orElseThrow();

        client.deleteFile(
                indexedFile.getId()
        );

        List<ChromaBridgeClient.BridgeIndexedFile> remaining =
                client.listFiles();

        boolean stillExists =
                remaining.stream()
                        .anyMatch(
                                file ->
                                        indexedFile
                                                .getId()
                                                .equals(
                                                        file.getId()
                                                )
                        );

        assertFalse(
                stillExists
        );
    }

    @Test
    public void testIndexImage() throws Exception {
        ChromaBridgeClient client =
                new ChromaBridgeClient();

        File tempImage =
                File.createTempFile(
                        "offline-retriever-image-",
                        ".png"
                );

        tempImage.deleteOnExit();

        BufferedImage image =
                new BufferedImage(
                        32,
                        32,
                        BufferedImage.TYPE_INT_RGB
                );

        ImageIO.write(
                image,
                "png",
                tempImage
        );

        client.indexImage(
                tempImage
        );

        List<ChromaBridgeClient.BridgeIndexedFile> files =
                client.listFiles();

        ChromaBridgeClient.BridgeIndexedFile indexedImage =
                files.stream()
                        .filter(
                                file ->
                                        tempImage
                                                .getAbsolutePath()
                                                .equals(
                                                        file.getFilePath()
                                                )
                        )
                        .findFirst()
                        .orElseThrow();

        assertNotNull(
                indexedImage.getId()
        );

        assertEquals(
                "png",
                indexedImage.getFileType()
        );

        assertEquals(
                "image",
                indexedImage.getContentType()
        );

        assertTrue(
                indexedImage.getFileSize()
                > 0
        );
    }
}