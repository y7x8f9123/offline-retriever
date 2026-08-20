package com.offlineretriever.parser;

import static org.junit.Assert.assertTrue;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Test;

public class DocumentParserTest {

    @Test
    public void shouldParsePdfContent() throws Exception {

        Path pdfPath = Files.createTempFile(
                "document-parser-test",
                ".pdf"
        );

        try {
            PDDocument document = new PDDocument();

            try {
                PDPage page = new PDPage();
                document.addPage(page);

                PDPageContentStream contentStream =
                        new PDPageContentStream(document, page);

                try {
                    contentStream.beginText();
                    contentStream.setFont(
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                        12
                     );
                    contentStream.newLineAtOffset(
                            50,
                            700
                    );
                    contentStream.showText(
                            "Offline Retrieval System"
                    );
                    contentStream.endText();

                } finally {
                    contentStream.close();
                }

                document.save(pdfPath.toFile());

            } finally {
                document.close();
            }

            DocumentParser parser =
                    new DocumentParser();

            String result =
                    parser.parse(pdfPath.toString());

            assertTrue(
                    result.contains(
                            "Offline Retrieval System"
                    )
            );

        } finally {
            Files.deleteIfExists(pdfPath);
        }
    }

    @Test
    public void shouldParseDocxContent() throws Exception {

        Path docxPath = Files.createTempFile(
                "document-parser-test",
                ".docx"
        );

        try {
            OutputStream output =
                    Files.newOutputStream(docxPath);

            ZipOutputStream zip =
                    new ZipOutputStream(output);

            try {

                String contentTypes =
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">" +
                        "<Default Extension=\"rels\" " +
                        "ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>" +
                        "<Default Extension=\"xml\" " +
                        "ContentType=\"application/xml\"/>" +
                        "<Override PartName=\"/word/document.xml\" " +
                        "ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>" +
                        "</Types>";

                addZipEntry(
                        zip,
                        "[Content_Types].xml",
                        contentTypes
                );

                String relationships =
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" +
                        "<Relationship Id=\"rId1\" " +
                        "Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" " +
                        "Target=\"word/document.xml\"/>" +
                        "</Relationships>";

                addZipEntry(
                        zip,
                        "_rels/.rels",
                        relationships
                );

                String documentXml =
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<w:document " +
                        "xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">" +
                        "<w:body>" +
                        "<w:p>" +
                        "<w:r>" +
                        "<w:t>Offline Retrieval System DOCX test document</w:t>" +
                        "</w:r>" +
                        "</w:p>" +
                        "</w:body>" +
                        "</w:document>";

                addZipEntry(
                        zip,
                        "word/document.xml",
                        documentXml
                );

            } finally {
                zip.close();
            }

            DocumentParser parser =
                    new DocumentParser();

            String result =
                    parser.parse(docxPath.toString());

            assertTrue(
                    result.contains(
                            "Offline Retrieval System"
                    )
            );

            assertTrue(
                    result.contains(
                            "DOCX test document"
                    )
            );

        } finally {
            Files.deleteIfExists(docxPath);
        }
    }

    @Test
    public void shouldReturnErrorForMissingDocument() {

        DocumentParser parser =
                new DocumentParser();

        String result =
                parser.parse(
                        "missing-document-"
                        + System.nanoTime()
                        + ".pdf"
                );

        assertTrue(
                result.startsWith(
                        "Error reading document:"
                )
        );
    }

    private static void addZipEntry(
            ZipOutputStream zip,
            String name,
            String content
    ) throws Exception {

        ZipEntry entry =
                new ZipEntry(name);

        zip.putNextEntry(entry);

        zip.write(
                content.getBytes(
                        StandardCharsets.UTF_8
                )
        );

        zip.closeEntry();
    }
}