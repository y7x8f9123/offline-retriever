package com.offlineretriever;

import java.nio.file.Path;
import java.util.List;

import com.offlineretriever.factory.ParserFactory;
import com.offlineretriever.io.FileScanner;
import com.offlineretriever.metadata.MetadataExtractor;
import com.offlineretriever.model.FileMetadata;
import com.offlineretriever.parser.Parser;

public class App {

    public static void main(String[] args) {

        FileScanner scanner = new FileScanner();
        MetadataExtractor extractor = new MetadataExtractor();

        List<Path> files = scanner.scan(".");

        for (Path file : files) {

            Parser parser = ParserFactory.getParser(file.toString());

            if (parser == null) {
                continue;
            }

            FileMetadata metadata = extractor.extract(file);
            String content = parser.parse(file.toString());

            System.out.println("==============================");
            System.out.println(metadata);
            System.out.println(content);
        }
    }
}