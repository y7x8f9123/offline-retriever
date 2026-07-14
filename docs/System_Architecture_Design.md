# System Architecture Design Document

## 1. Introduction

This document describes the overall architecture of the Offline Accessible Multimodal Local Content Retrieval System. The system follows a modular design approach to improve maintainability, scalability, and testability. Each functional component is developed independently while communicating through well-defined interfaces.

The current implementation focuses on local file scanning, metadata extraction, and document parsing for multiple file formats. The architecture is designed to support future semantic search and vector database integration.

---

## 2. System Overview

The backend system processes local files through several independent modules. The workflow consists of:

1. Scan local folders.
2. Detect supported file types.
3. Select the appropriate parser.
4. Extract document content.
5. Extract file metadata.
6. Return structured information for later indexing.

The current supported file formats include:

- TXT
- PDF
- DOCX
- PNG
- JPG
- JPEG

---

## 3. Architecture Design

The backend follows a layered modular architecture.

```
                 +----------------------+
                 |      App.java        |
                 +----------+-----------+
                            |
                            |
                 +----------v-----------+
                 |    FileScanner       |
                 +----------+-----------+
                            |
                     List<Path>
                            |
                 +----------v-----------+
                 |    ParserFactory     |
                 +----------+-----------+
                            |
          +-----------------+-----------------+
          |                 |                 |
          v                 v                 v
   TextParser      DocumentParser      ImageParser
          |                 |                 |
          +-----------------+-----------------+
                            |
                            v
                   Extract Document Text
                            |
                 +----------v-----------+
                 | MetadataExtractor    |
                 +----------+-----------+
                            |
                            v
                     FileMetadata
```

---

## 4. Module Description

### 4.1 App

App.java is the program entry point. It coordinates all modules by scanning files, selecting parsers, extracting metadata, and displaying parsing results.

Responsibilities:

- Initialize system components
- Scan local files
- Invoke parsers
- Display extracted content

---

### 4.2 FileScanner

The FileScanner module searches a specified directory and returns all regular files.

Responsibilities:

- Traverse directories
- Ignore non-file entries
- Return a list of file paths

---

### 4.3 Parser Interface

Parser defines a common interface for all document parsers.

```java
String parse(String filePath);
```

This design allows additional parsers to be added without modifying existing modules.

---

### 4.4 TextParser

TextParser processes plain text files.

Responsibilities:

- Read TXT files
- Return file content as String

---

### 4.5 DocumentParser

DocumentParser uses Apache Tika to parse office documents.

Supported formats:

- PDF
- DOCX

Responsibilities:

- Parse document contents
- Handle parsing exceptions

---

### 4.6 ImageParser

ImageParser also uses Apache Tika to process image files.

Supported formats:

- PNG
- JPG
- JPEG

Responsibilities:

- Read image metadata or embedded text
- Handle parsing errors

---

### 4.7 ParserFactory

ParserFactory implements the Factory Design Pattern.

Responsibilities:

- Determine parser based on file extension
- Create parser instances
- Hide parser creation logic from clients

Supported mapping:

| File Type | Parser |
|-----------|--------|
| txt | TextParser |
| pdf | DocumentParser |
| docx | DocumentParser |
| png | ImageParser |
| jpg | ImageParser |
| jpeg | ImageParser |

---

### 4.8 MetadataExtractor

MetadataExtractor retrieves file information using Java NIO.

Extracted information includes:

- File name
- File type
- File size
- Last modified time
- Absolute file path

---

### 4.9 FileMetadata

FileMetadata is a simple model class used to store metadata extracted from files.

Fields:

- fileName
- fileType
- fileSize
- lastModified
- filePath

---

## 5. Design Principles

The project follows several software engineering principles.

### Modular Design

Each module has a single responsibility.

### Factory Pattern

ParserFactory separates parser creation from parser usage.

### Interface-Oriented Programming

All parsers implement the Parser interface, allowing future extensions without changing existing code.

### Extensibility

New parsers (such as HTMLParser or XMLParser) can be added by implementing the Parser interface and updating ParserFactory.

---

## 6. Testing Strategy

Unit tests were implemented using JUnit.

The following modules have corresponding unit tests:

- TextParser
- DocumentParser
- ImageParser
- ParserFactory
- FileScanner
- MetadataExtractor
- FileMetadata

JaCoCo was used to measure code coverage.

Current code coverage:

- Instruction Coverage: **83%**

This exceeds the project requirement of 80%.

---

## 7. Future Improvements

The current implementation serves as the foundation of the Offline Accessible Multimodal Local Content Retrieval System.

Future development will include:

- PDF image extraction
- OCR support
- Vector embedding generation
- Local vector database integration
- Semantic search
- Flutter frontend
- Accessibility optimization

---

## 8. Conclusion

The current backend architecture successfully implements modular file scanning, parsing, and metadata extraction. The use of interfaces, factory pattern, and independent modules improves maintainability and scalability while providing a solid foundation for future multimodal retrieval functions.