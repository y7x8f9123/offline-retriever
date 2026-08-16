package com.offlineretriever;

import com.google.gson.Gson;

import com.offlineretriever.factory.ParserFactory;
import com.offlineretriever.parser.Parser;
import com.offlineretriever.storage.ChromaBridgeClient;
import com.offlineretriever.storage.ChromaBridgeClient.BridgeIndexedFile;
import com.offlineretriever.storage.ChromaBridgeClient.BridgeSearchResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BackendCli {

    private static final Gson GSON =
            new Gson();

    public static void main(String[] args) {

        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        try {

            ChromaBridgeClient chroma =
                    new ChromaBridgeClient();

            String command =
                    args[0].toLowerCase();

            switch (command) {

                case "index":
                    handleIndex(
                            chroma,
                            args
                    );
                    break;

                case "search":
                    handleSearch(
                            chroma,
                            args
                    );
                    break;

                case "list":
                    handleList(
                            chroma
                    );
                    break;

                case "delete":
                    handleDelete(
                            chroma,
                            args
                    );
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Unknown command: " +
                            command
                    );
            }

        } catch (Exception e) {

            System.err.println(
                    "Backend error: " +
                    e.getMessage()
            );

            e.printStackTrace(
                    System.err
            );

            System.exit(2);
        }
    }

    private static void handleIndex(
            ChromaBridgeClient chroma,
            String[] args
    ) throws Exception {

        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "Usage: index <file1> [file2] ..."
            );
        }

        List<String> indexed =
                new ArrayList<>();

        List<String> skipped =
                new ArrayList<>();

        for (int i = 1; i < args.length; i++) {

            File file =
                    new File(args[i]);

            if (
                    !file.exists() ||
                    !file.isFile()
            ) {
                skipped.add(args[i]);
                continue;
            }

            Parser parser =
                    ParserFactory.getParser(
                            file.getName()
                    );

            if (parser == null) {
                skipped.add(
                        file.getName()
                );
                continue;
            }

            String content =
                    parser.parse(
                            file.getAbsolutePath()
                    );

            if (
                    content == null ||
                    content.trim().isEmpty()
            ) {
                skipped.add(
                        file.getName()
                );
                continue;
            }

            chroma.indexText(
                    file,
                    content
            );

            indexed.add(
                    file.getName()
            );
        }

        IndexResponse response =
                new IndexResponse(
                        indexed,
                        skipped
                );

        System.out.println(
                GSON.toJson(response)
        );
    }

    private static void handleSearch(
            ChromaBridgeClient chroma,
            String[] args
    ) throws Exception {

        if (args.length < 3) {
            throw new IllegalArgumentException(
                    "Usage: search <query> <topK>"
            );
        }

        String query = args[1];

        int topK =
                Integer.parseInt(
                        args[2]
                );

        List<BridgeSearchResult> results =
                chroma.searchText(
                        query,
                        topK
                );

        System.out.println(
                GSON.toJson(results)
        );
    }

    private static void handleList(
            ChromaBridgeClient chroma
    ) throws Exception {

        List<BridgeIndexedFile> files =
                chroma.listFiles();

        System.out.println(
                GSON.toJson(files)
        );
    }

    private static void handleDelete(
            ChromaBridgeClient chroma,
            String[] args
    ) throws Exception {

        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "Usage: delete <id>"
            );
        }

        String id = args[1];

        chroma.deleteFile(id);

        System.out.println(
                GSON.toJson(
                        new DeleteResponse(
                                "ok",
                                id
                        )
                )
        );
    }

    private static void printUsage() {

        System.err.println(
                "Commands:"
        );

        System.err.println(
                "  index <file1> [file2] ..."
        );

        System.err.println(
                "  search <query> <topK>"
        );

        System.err.println(
                "  list"
        );

        System.err.println(
                "  delete <id>"
        );
    }

    private static class IndexResponse {

        private final List<String> indexed;
        private final List<String> skipped;

        private IndexResponse(
                List<String> indexed,
                List<String> skipped
        ) {
            this.indexed = indexed;
            this.skipped = skipped;
        }
    }

    private static class DeleteResponse {

        private final String status;
        private final String id;

        private DeleteResponse(
                String status,
                String id
        ) {
            this.status = status;
            this.id = id;
        }
    }
}