# Project Demo Script

## Offline Accessible Multimodal Local Content Retrieval System

**Target Duration:** Approximately 5 minutes  
**Platform:** Windows Desktop  
**Version:** Week 7 Prototype

---

## 1. Introduction

Hello, this is a demonstration of the Offline Accessible Multimodal Local Content Retrieval System.

The purpose of this project is to provide a local document retrieval application that allows users to search files through a simple and accessible Windows desktop interface.

The project focuses on three main goals:

- Offline-first local processing
- Local similarity-based document retrieval
- Accessibility

The current prototype consists of a Flutter desktop frontend and a Java-based local retrieval backend.

---

## 2. Application Overview

First, I will show the main application interface.

The application provides three main sections:

- File Library
- Search
- Settings

The normal workflow starts by importing local documents into the File Library.

The current prototype supports:

- TXT
- PDF
- DOCX

The application is designed to process these documents locally rather than uploading user content to an external retrieval service.

This local-processing approach supports both privacy and offline use.

---

## 3. File Import Demonstration

First, I will open the File Library.

The user can select the Import Files button and choose one or more supported documents from the local computer.

Imported files are displayed with:

- File name
- File type
- File size
- File-type icon

The application currently distinguishes between text documents, PDF documents, and Word documents.

Removing a file from the application library does not delete the original file from the user's computer.

---

## 4. Search Demonstration

Next, I will open the Search page.

The application displays how many local files are currently available for retrieval.

The user enters a text query and activates the Search button.

The current prototype supports both English and Chinese text queries.

If the user attempts to search without entering a query, the application provides a validation message instead of starting an invalid search.

When a valid query is submitted, the Flutter frontend sends the query and selected local file paths to the Java backend.

---

## 5. Frontend-Backend Integration

The Flutter frontend and Java backend are connected locally.

Flutter uses a retrieval service to start the packaged Java backend as a local process.

The main workflow is:

1. Flutter collects the search query.
2. Flutter collects the imported file paths.
3. Flutter starts the Java backend JAR.
4. The Java backend indexes the supplied files.
5. The backend performs local retrieval.
6. Ranked results are returned to Flutter as JSON.
7. Flutter displays the returned results.

This architecture does not require a remote retrieval server.

---

## 6. Retrieval Architecture

The Java backend is organized into independent modules.

The main retrieval workflow includes:

1. Parser selection
2. Content extraction
3. Text representation generation
4. Vector storage
5. Similarity calculation
6. Result ranking

`ParserFactory` selects the appropriate parser according to the file type.

TXT files are processed using `TextParser`.

PDF and DOCX files are processed using `DocumentParser`, which uses Apache Tika to extract textual content.

The current text representation is a lightweight 256-dimensional deterministic vector implementation.

It supports English tokens and Chinese character-based tokens.

The current prototype does not yet use a pretrained BERT model.

The backend uses cosine similarity to compare the query vector with indexed document vectors.

---

## 7. Search Results

The Search Results page displays the documents returned by the Java backend.

Each result contains:

- File name
- File type
- Similarity score
- Ranking position
- Open button

Results are ranked according to cosine similarity.

The similarity value is used as a ranking signal and should not be interpreted as a probability or percentage.

The interface also identifies TXT, PDF, and DOCX results with different file labels and icons.

---

## 8. Opening Retrieved Files

Each result includes an Open button.

When selected, the application checks whether the original local file still exists.

If the file exists, Windows opens it using the default application associated with that file type.

For example:

- TXT files can open in the configured text editor.
- PDF files can open in the configured PDF viewer.
- DOCX files can open in the configured document application.

This allows the user to move directly from retrieval results to the original local document.

---

## 9. Chinese Retrieval Demonstration

The current prototype also supports Chinese text retrieval.

For example, a Chinese PDF containing searchable text can be imported into the File Library.

The user can then enter a Chinese query.

The document text is extracted locally and processed by the current multilingual text-representation mechanism.

The relevant document can then appear in the ranked search results and can be opened directly from the application.

Scanned or image-only PDFs are outside the current prototype because OCR is not yet implemented.

---

## 10. Accessibility Demonstration

Accessibility is another major requirement of the project.

The current Flutter prototype includes four main accessibility features.

### Keyboard Navigation

The interface supports keyboard-only navigation.

Users can use:

- Tab to move forward
- Shift + Tab to move backward
- Enter or Space to activate controls

### High Contrast Mode

The Settings page provides High Contrast Mode.

When enabled, the interface changes immediately to provide stronger visual contrast.

### Dynamic Font Scaling

Users can select different font sizes.

The current interface provides:

- Small
- Medium
- Large
- Extra Large

The interface updates immediately when the setting changes.

### Screen Reader Support

Important interface elements include semantic labels to improve compatibility with assistive technologies.

These accessibility features support the WCAG 2.1 AA objectives of the project.

---

## 11. Testing and Quality Assurance

The project includes automated testing for both backend and frontend components.

The Java backend uses JUnit.

Tests cover major components including:

- File scanning
- Metadata extraction
- Parsers
- Text representation
- Vector storage
- Similarity calculation
- Retrieval
- Retrieval pipeline integration
- Chinese text handling
- Performance and stress scenarios

JaCoCo is used to evaluate backend test coverage.

The Flutter frontend includes widget testing for major interface behavior.

The current version has also been manually tested through the complete workflow from local file import to retrieval and file opening.

---

## 12. Security and Offline Design

The application is designed to operate locally.

Core retrieval does not require document content or search queries to be uploaded to an external cloud service.

The source code has also been reviewed for common network-related functionality and sensitive credential patterns.

The current Flutter-to-Java connection runs as a local process on the same machine.

This provides privacy benefits and allows the retrieval workflow to operate without continuous internet connectivity.

---

## 13. Open-Source Compliance

The project is released under the Apache License 2.0.

A LICENSE file is included in the project repository.

Major open-source dependencies include technologies such as:

- Apache Tika
- JUnit
- Flutter
- Cupertino Icons
- file_picker
- url_launcher

Third-party license and notice requirements are documented in the Open-Source Compliance Report.

---

## 14. Documentation

The project includes technical and user documentation.

Major documents include:

- Product Requirements Document
- System Architecture Design
- API Reference
- Maintenance Guide
- End-User Manual
- Accessibility User Guide
- Open-Source Compliance Report
- Demo Script

This documentation supports future development, maintenance, testing, and use of the system.

---

## 15. Current Prototype Limitations

The current version is a functional prototype.

Current limitations include:

- The text representation uses deterministic 256-dimensional hashing rather than BERT.
- The vector store is currently in memory.
- OCR is not implemented for scanned PDFs.
- Image retrieval is not yet integrated into the complete Flutter workflow.
- Retrieval quality is more dependent on token overlap than a pretrained semantic embedding model.

These limitations provide clear directions for future development.

---

## 16. Conclusion

This project demonstrates an offline-first and accessible approach to local document retrieval.

The current prototype combines:

- A Flutter Windows desktop interface
- A modular Java backend
- Local TXT, PDF, and DOCX processing
- English and Chinese text retrieval
- Local vector-based similarity search
- Flutter-to-Java integration
- Ranked search results
- Opening original local files
- Accessibility features
- Automated testing
- Open-source documentation and compliance

The modular design also provides a foundation for future improvements such as pretrained local embedding models, OCR, persistent vector storage, and image or multimodal retrieval.

Thank you for watching this demonstration.