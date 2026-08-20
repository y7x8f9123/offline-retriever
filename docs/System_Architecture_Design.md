# System Architecture Design Document

## 1. Introduction

This document describes the final architecture of the Offline Accessible Multimodal Local Content Retrieval System.

The system is an offline-first Windows desktop application designed to index and semantically search local text documents and images.

The final implementation combines:

- Flutter Windows desktop user interface
- Java backend layer
- Local Python FastAPI retrieval service
- BERT text embedding model
- MobileCLIP image and text embedding model
- ChromaDB persistent vector storage

The system supports:

- Local file indexing
- TXT, PDF, and DOCX text extraction
- JPG, JPEG, and PNG image indexing
- BERT-based semantic text embeddings
- MobileCLIP-based image and text embeddings
- Long-document chunking
- Semantic vector search
- File-level result aggregation
- Multimodal result ranking
- Persistent local vector storage
- Local retrieval without a remote semantic-search API after setup
- Accessibility-focused Windows desktop interaction

The architecture follows a modular design so that parsing, embedding, storage, retrieval, and user-interface components can be maintained independently.

---

## 2. System Overview

The system processes local documents and images through two related semantic retrieval pipelines.

### 2.1 Text Document Workflow

For TXT, PDF, and DOCX files:

1. The user selects a local file through the Flutter interface or Java CLI.
2. The Java backend identifies the file type.
3. Textual content is extracted.
4. Extracted text is sent to the local FastAPI retrieval service.
5. Long documents are divided into overlapping chunks.
6. BERT generates an embedding for each chunk.
7. Embeddings and metadata are stored in the text ChromaDB collection.
8. During search, the query is embedded using the same BERT-based text embedding pipeline.
9. ChromaDB retrieves semantically similar chunks.
10. Chunk-level results are aggregated to file level.
11. Ranked document results are returned to the application.

### 2.2 Image Workflow

For JPG, JPEG, and PNG files:

1. The selected image path is routed to the local retrieval service.
2. MobileCLIP generates an image embedding.
3. The image embedding and metadata are stored in the image ChromaDB collection.
4. During search, MobileCLIP converts the text query into a compatible text embedding.
5. ChromaDB retrieves relevant images using cosine similarity.
6. Image similarity scores are calibrated.
7. Image and text results are combined into a unified ranking.

### 2.3 Supported File Formats

| Content Type | Supported Formats |
|---|---|
| Text documents | TXT, PDF, DOCX |
| Images | JPG, JPEG, PNG |

All indexed metadata and vector embeddings are stored locally.

The retrieval service listens only on:

```text
127.0.0.1:8765
```

during normal operation.

---

## 3. Overall Architecture

The system uses a layered architecture.

```text
+------------------------------------------------------+
|                  Flutter Frontend                    |
|                  Windows Desktop                     |
|                                                      |
| Search UI | File Selection | Results | Library       |
| Settings  | Accessibility Features                   |
+--------------------------+---------------------------+
                           |
                           v
+------------------------------------------------------+
|                   Java Backend                       |
|                                                      |
| FileScanner | ParserFactory | Parsers | Metadata     |
| BackendCli  | Retrieval Integration                  |
+--------------------------+---------------------------+
                           |
                    Local HTTP API
                    127.0.0.1:8765
                           |
                           v
+------------------------------------------------------+
|               Python FastAPI Service                 |
|                                                      |
| Indexing | Search | Chunking | Aggregation           |
| Multimodal Score Calibration                         |
+-------------------+------------------+---------------+
                    |                  |
                    v                  v
          +----------------+   +----------------+
          |      BERT      |   |   MobileCLIP   |
          | Text Embedding |   | Image / Text   |
          +-------+--------+   +-------+--------+
                  |                    |
                  +---------+----------+
                            |
                            v
                  +--------------------+
                  |      ChromaDB      |
                  |                    |
                  | Text Collection    |
                  | Image Collection   |
                  +--------------------+
```

This separation allows user-interface logic, document processing, machine-learning inference, and vector storage to evolve independently.

---

## 4. Flutter Frontend Layer

Flutter provides the user-facing Windows desktop interface.

Its responsibilities include:

- Accepting natural-language search queries
- Allowing users to select supported local files
- Displaying semantic search results
- Displaying indexed files
- Opening original local files
- Presenting errors and status information
- Providing settings
- Supporting keyboard navigation
- Supporting High Contrast Mode
- Supporting Dynamic Font Scaling
- Providing semantic accessibility labels

The frontend does not directly perform machine-learning inference or vector storage.

Those responsibilities are delegated to the backend retrieval layers.

---

## 5. Java Backend Layer

The Java backend coordinates local file processing and communication with the Python retrieval service.

### 5.1 FileScanner

`FileScanner` is responsible for discovering local files.

Responsibilities include:

- Traversing supported paths
- Identifying regular files
- Supplying local file paths for indexing
- Supporting batch file processing

---

### 5.2 Parser Abstraction

The parser layer provides a common abstraction for text extraction.

This separates file-format-specific parsing behaviour from the indexing workflow.

---

### 5.3 TextParser

`TextParser` processes:

```text
TXT
```

files.

Responsibilities include:

- Reading plain-text content
- Returning extracted text
- Handling file-reading failures

---

### 5.4 DocumentParser

`DocumentParser` handles document formats including:

```text
PDF
DOCX
```

Its primary responsibility is extracting textual content suitable for semantic embedding.

Text-based PDFs are supported.

Image-only or scanned PDFs may not contain searchable text because OCR is not part of the final implementation.

---

### 5.5 Image Routing

Image files are recognized as supported local content and routed through the image indexing pipeline.

Supported formats include:

```text
JPG
JPEG
PNG
```

Images are represented using MobileCLIP rather than BERT.

---

### 5.6 ParserFactory

`ParserFactory` separates file-type selection from parsing implementation.

Typical routing is:

| File Type | Processing Route |
|---|---|
| TXT | Text parser |
| PDF | Document parser |
| DOCX | Document parser |
| JPG | Image pipeline |
| JPEG | Image pipeline |
| PNG | Image pipeline |

This makes file-processing logic easier to extend and maintain.

---

### 5.7 Metadata

File metadata accompanies indexed content.

Typical metadata includes:

- File name
- File path
- File type
- File size
- Last modified time
- Content type

Text chunks also contain:

- File identifier
- Chunk index
- Total chunk count

Metadata allows search results to be mapped back to the original local source files.

---

## 6. Local Python Retrieval Service

The machine-learning retrieval subsystem is implemented as a local FastAPI service.

The service runs at:

```text
127.0.0.1:8765
```

It is bound to the loopback interface rather than a public network interface.

Main API operations include:

```text
GET  /health
GET  /files
POST /index-text
POST /index-image
POST /search
POST /delete
```

The Python service is responsible for:

- Machine-learning model loading
- BERT inference
- MobileCLIP inference
- Text chunking
- ChromaDB storage
- Semantic retrieval
- File-level aggregation
- Multimodal ranking

---

## 7. Text Embedding Architecture

Text semantic retrieval uses a BERT-based embedding model.

For document indexing:

1. Text is extracted from the source document.
2. Long content is divided into chunks.
3. Each chunk is passed through the BERT embedding engine.
4. A numerical vector is generated.
5. The vector and associated metadata are stored in ChromaDB.

During search:

1. The user query is embedded using the same BERT-based pipeline.
2. ChromaDB performs cosine-similarity retrieval.
3. Relevant chunks are returned.
4. Chunk-level results are aggregated.
5. The most relevant chunk represents the source file.

This allows semantic matching instead of requiring exact keyword matches.

---

## 8. Long-Document Chunking

Long documents are divided into overlapping chunks before embedding.

The current configuration is:

```text
Chunk size: 400 words
Chunk overlap: 50 words
```

The overlap helps preserve contextual information around chunk boundaries.

Each chunk receives:

- Unique vector-record identifier
- Original file identifier
- Chunk index
- Total chunk count
- Original file metadata

Each chunk is embedded and stored independently.

### 8.1 File-Level Aggregation

Although a document may create multiple stored vectors, users search for files rather than individual chunks.

After retrieval, chunk results are grouped using the original file identifier.

The highest-scoring chunk is used as the relevance score for that source document.

This prevents a single long file from appearing repeatedly in the final result list.

---

## 9. Image Embedding Architecture

Image semantic retrieval uses MobileCLIP.

During image indexing:

1. The local image is loaded.
2. MobileCLIP generates an image embedding.
3. File metadata and the embedding are stored in the image ChromaDB collection.

During search:

1. The natural-language query is converted into a MobileCLIP text embedding.
2. The text embedding is compared with stored image embeddings.
3. ChromaDB retrieves the most semantically related images.

Because MobileCLIP maps text and images into a compatible semantic space, images can be retrieved using natural-language descriptions.

---

## 10. ChromaDB Vector Storage

ChromaDB provides persistent local vector storage.

The application uses separate collections for text and image content:

```text
offline_retriever_text
offline_retriever_images
```

The text collection stores BERT vectors.

The image collection stores MobileCLIP vectors.

Cosine similarity is used for retrieval.

Stored information includes:

- Vector embeddings
- Record identifiers
- File identifiers
- File metadata
- Chunk metadata where applicable
- Text chunk content where applicable

Persistent storage allows indexed content to remain available across application sessions.

Runtime database files are treated as local application data and are not intended to be committed to the public source repository.

---

## 11. Multimodal Result Fusion

Text and image results originate from different embedding models:

```text
Text  → BERT
Image → MobileCLIP
```

The raw similarity distributions of these models are not identical.

Directly combining their raw scores may therefore create unbalanced rankings.

The final implementation applies image score calibration:

```text
Text score  = raw BERT cosine similarity
Image score = raw MobileCLIP cosine similarity × 1.25
```

Current configuration:

```text
IMAGE_SCORE_CALIBRATION = 1.25
```

The final ranking process is:

1. Retrieve text chunk candidates.
2. Aggregate text chunks to file level.
3. Retrieve image candidates.
4. Calibrate image scores.
5. Combine text and image results.
6. Sort by descending final score.
7. Return the requested top-K files.

This provides a simple and transparent multimodal fusion strategy.

---

## 12. Complete Search Pipeline

```text
                     User Query
                         |
                         v
                +----------------+
                | Flutter / Java |
                +-------+--------+
                        |
                        v
              +-------------------+
              |  FastAPI Search   |
              +---------+---------+
                        |
              +---------+---------+
              |                   |
              v                   v
      +---------------+   +---------------+
      | BERT Query    |   | MobileCLIP    |
      | Embedding     |   | Text Embedding|
      +-------+-------+   +-------+-------+
              |                   |
              v                   v
      +---------------+   +---------------+
      | Text ChromaDB |   | Image ChromaDB|
      +-------+-------+   +-------+-------+
              |                   |
              v                   |
      Chunk Aggregation           |
              |                   |
              +---------+---------+
                        |
                        v
               Score Calibration
                        |
                        v
                 Unified Ranking
                        |
                        v
                    Top-K Files
```

---

## 13. File Identification

Indexed files use deterministic identifiers based on normalized local paths.

Conceptually:

```text
SHA-256(normalized absolute file path)
```

For chunked text documents, each chunk receives a chunk-specific record identifier while retaining the original file identifier.

This supports:

- Stable local identification
- Grouping multiple chunks
- File-level aggregation
- Indexed-file deletion

---

## 14. Indexed-File Deletion

Deleting an indexed file removes its associated retrieval data from ChromaDB.

For text documents, all chunks associated with the original file identifier are removed.

For images, the corresponding image vector is removed.

Deleting an index entry does not delete the user's original file from the Windows file system.

---

## 15. Offline-First Architecture

Offline-first behaviour is a central design requirement.

The application performs the following locally:

- File parsing
- Metadata extraction
- Text chunking
- BERT inference
- MobileCLIP inference
- Vector storage
- Similarity retrieval
- Result aggregation
- Result ranking

The retrieval service is bound to:

```text
127.0.0.1
```

and is intended only for local application communication.

Initial software installation and model acquisition may require Internet access.

After required dependencies and models are available locally, normal semantic indexing and retrieval do not require a remote inference or search API.

---

## 16. Privacy and Local Data

The architecture is designed to keep normal retrieval data on the user's computer.

Local data may include:

- File paths
- File metadata
- Extracted text
- Vector embeddings
- ChromaDB records

Development or user-generated database contents should not be committed to a public repository.

No cloud semantic-search service is required by the final retrieval path.

---

## 17. Error Handling

Validation occurs across several layers.

Examples include:

- Unsupported file types
- Missing source files
- Empty extracted content
- Empty search queries
- Parser errors
- Retrieval-service startup problems
- Model initialization failures
- ChromaDB access failures
- Files moved after indexing

The FastAPI service returns HTTP errors for invalid operations.

The Java and Flutter layers should convert these failures into usable application feedback where possible.

---

## 18. Testing and Validation

The final system was validated using automated tests, integration tests, performance tests, and manual end-to-end testing.

### 18.1 Java Backend Testing

JUnit is used for Java backend testing.

JaCoCo is used for code-coverage measurement.

Final measured coverage includes:

```text
Overall backend instruction coverage: 61%
Overall backend branch coverage: 44%

Storage package: 63%
Vector package: 95%
Factory package: 93%
Metadata package: 97%
Embedding package: 100%
Parser package: 100%
Model package: 100%
I/O package: 100%
```

The overall percentage includes application entry points, CLI orchestration, service-management logic, and integration-oriented code.

Core reusable functional modules achieve substantially higher coverage.

### 18.2 Flutter Testing

Flutter tests validate important user-interface behaviours including:

- Search input
- Empty-query handling
- Navigation
- Result presentation
- User interaction

Manual Windows desktop testing was also performed.

### 18.3 Integration Testing

Integration testing covered:

- Text indexing
- Semantic search
- File deletion
- Image indexing
- Java-to-Python communication
- ChromaDB persistence

### 18.4 Manual End-to-End Testing

The complete workflow was manually validated across:

```text
Flutter
   ↓
Java
   ↓
FastAPI
   ↓
BERT / MobileCLIP
   ↓
ChromaDB
```

---

## 19. Scalability Validation

The retrieval system was tested using 1,000 generated TXT files.

Observed results:

```text
Text records before test: 12
Text records after test: 1012
Stress-test files confirmed: 1000
```

Measured indexing batches included:

| Batch Size | Time |
|---:|---:|
| 200 files | 14.81 s |
| 300 files | 25.94 s |
| 450 files | 41.72 s |

The initial 50-file batch was used for functional validation and was not timed.

With more than 1,000 text records stored, an end-to-end semantic search completed in approximately:

```text
807 ms
```

for the query:

```text
software engineering
```

These measurements were obtained in the local development environment and should not be interpreted as hardware-independent benchmarks.

The validation demonstrates that the implemented pipeline can index and search at least 1,000 local text files without backend failure.

---

## 20. Accessibility Architecture

Accessibility is implemented primarily in the Flutter presentation layer.

The final interface includes:

- Keyboard navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic accessibility labels

Accessibility was treated as a core interface requirement.

The project uses WCAG 2.1 AA as a design objective.

Formal third-party accessibility certification was outside the scope of the eight-week project.

---

## 21. Windows Platform Scope

Flutter supports multiple desktop target platforms at the framework level.

However, the final project release scope is:

```text
Windows Desktop
```

The application was developed, tested, and functionally validated on Windows.

Linux and macOS runtime validation are outside the final release scope.

Generated Flutter platform files for other systems may exist as framework-generated project files, but they do not represent validated final release targets.

---

## 22. Design Principles

The architecture follows several software-engineering principles.

### 22.1 Modular Design

Parsing, metadata, embedding, storage, retrieval, and user-interface responsibilities are divided into separate components.

### 22.2 Separation of Concerns

Java handles local application and file-processing responsibilities.

Python handles machine-learning inference and vector retrieval.

Flutter handles user interaction and presentation.

### 22.3 Interface-Oriented Design

Parser abstractions allow new formats to be introduced without redesigning the complete retrieval pipeline.

### 22.4 Local-First Processing

User content and semantic retrieval data remain local during normal operation.

### 22.5 Persistent Storage

ChromaDB allows indexed vectors to survive application restarts.

### 22.6 Extensibility

The architecture can support future improvements such as:

- Additional document parsers
- OCR support
- Alternative embedding models
- Improved multimodal fusion
- Additional accessibility testing
- More advanced file-preview functionality
- Additional platform validation

These are extension opportunities rather than requirements of the final project release.

---

## 23. Known Architectural Limitations

The final architecture has several known limitations:

- OCR is not implemented for scanned or image-only PDFs.
- Cold model startup may take additional time.
- Retrieval performance depends on local hardware.
- BERT and MobileCLIP use separate embedding spaces and require score calibration for unified ranking.
- Moving or deleting a source file outside the application can invalidate its stored local path.
- The final validated release target is Windows.
- Extensive production-scale benchmarking was outside the project scope.
- Formal accessibility certification was outside the project scope.

These limitations do not prevent the main multimodal local retrieval workflow from operating.

---

## 24. Related Documentation

Detailed information is available in:

```text
README.md

docs/API_Reference.md
docs/Testing_Report.md
docs/Maintenance_Guide.md
docs/End_User_Manual.md
docs/Accessibility_User_Guide.md
docs/Open_Source_Compliance_Report.md
docs/Environment_Setup_Report.md
docs/Risk_Management_Plan.md
docs/PRD.md
```

The editable architecture diagram is stored as:

```text
docs/System_Architecture.drawio
```

---

## 25. Conclusion

The final system architecture combines a Flutter Windows desktop frontend, Java backend, local FastAPI retrieval service, BERT, MobileCLIP, and ChromaDB.

The architecture supports a complete local workflow for:

```text
file selection
      ↓
parsing / image routing
      ↓
embedding generation
      ↓
persistent vector storage
      ↓
semantic retrieval
      ↓
multimodal ranking
      ↓
desktop result presentation
```

The modular architecture separates user-interface, file-processing, machine-learning, and vector-storage responsibilities while maintaining an offline-first design.

The implemented architecture provides a stable basis for the final Windows release and for future extension of the local multimodal retrieval system.