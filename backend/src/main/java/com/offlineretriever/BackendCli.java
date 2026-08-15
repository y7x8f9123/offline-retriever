package com.offlineretriever;

import com.offlineretriever.vector.SearchResult;

import java.io.File;
import java.util.List;

public class BackendCli {

    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println(
                    "Usage: java -jar backend.jar <query> <topK> <file1> [file2] ..."
            );
            System.exit(1);
        }

        try {
            String query = args[0];
            int topK = Integer.parseInt(args[1]);

            RetrievalPipeline pipeline = new RetrievalPipeline();

            // Index all files supplied by Flutter / command line.
            for (int i = 2; i < args.length; i++) {
                File file = new File(args[i]);

                if (!file.exists() || !file.isFile()) {
                    System.err.println("Skipping invalid file: " + args[i]);
                    continue;
                }

                pipeline.indexFile(file);
            }

            List<SearchResult> results = pipeline.search(query, topK);

            System.out.println(toJson(results));

        } catch (Exception e) {
            System.err.println("Backend error: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(2);
        }
    }

    private static String toJson(List<SearchResult> results) {

        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < results.size(); i++) {
            SearchResult result = results.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                    .append("\"fileName\":\"")
                    .append(escapeJson(
                            result.getRecord().getFileName()
                    ))
                    .append("\",")
                    .append("\"filePath\":\"")
                    .append(escapeJson(
                            result.getRecord().getFilePath()
                    ))
                    .append("\",")
                    .append("\"score\":")
                    .append(result.getSimilarityScore())
                    .append("}");
        }

        json.append("]");

        return json.toString();
    }

    private static String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}