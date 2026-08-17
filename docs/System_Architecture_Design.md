# System Architecture Design Document

## 1. Introduction

This document describes the architecture of the Offline Accessible Multimodal Local Content Retrieval System. The system is designed as an offline-first, modular application capable of indexing and semantically searching local text documents and images.

The current implementation combines a Flutter user interface, a Java backend layer, a local Python retrieval service, machine-learning embedding models, and ChromaDB persistent vector storage.

The system currently supports:

- Local file indexing
- TXT, PDF, and DOCX text extraction
- JPG, JPEG, and PNG image indexing
- BERT-based text embeddings
- MobileCLIP-based image and text embeddings
- Long-document chunking
- Semantic vector search
- Multimodal result ranking
- Persistent local vector storage using ChromaDB
- Local retrieval without external network communication after required models are available

The architecture follows a modular design so that parsing, embedding, storage, retrieval, and user-interface components can be maintained independently.

---

## 2. System Overview

The system processes local files through a multimodal retrieval pipeline.

### 2.1 Text Document Workflow

For text documents, the workflow is:

1. The user selects or indexes a local file through the Flutter interface or Java CLI.
2. The Java backend detects the file type and extracts textual content.
3. The extracted text is sent to the local FastAPI retrieval service.
4. Long documents are divided into overlapping text chunks.
5. BERT generates an embedding for each chunk.
6. Embeddings and metadata are stored locally in ChromaDB.
7. During search, the query is embedded using BERT.
8. ChromaDB retrieves the most similar chunks using cosine similarity.
9. Chunk-level results are aggregated back into file-level results.
10. Ranked results are returned to the frontend.

### 2.2 Image Workflow

For images, the workflow is:

1. The image path is sent to the local retrieval service.
2. MobileCLIP generates an image embedding.
3. The embedding and file metadata are stored in a separate ChromaDB collection.
4. During search, MobileCLIP converts the text query into the same embedding space.
5. ChromaDB retrieves relevant images using cosine similarity.
6. Image scores are calibrated before being combined with text results.

### 2.3 Supported File Formats

| Content Type | Supported Formats |
|---|---|
| Text documents | TXT, PDF, DOCX |
| Images | PNG, JPG, JPEG |

All indexed metadata and vector embeddings are stored locally. The retrieval service listens only on the loopback interface at `127.0.0.1`.

---

## 3. Overall Architecture

The system uses a layered architecture consisting of the user-interface layer, Java application layer, Python retrieval service, machine-learning models, and persistent vector storage.

```text
+------------------------------------------------------+
|                  Flutter Frontend                    |
|                                                      |
|   Search UI | File Selection | Results | Library     |
+--------------------------+---------------------------+
                           |
                           v
+------------------------------------------------------+
|                   Java Backend                       |
|                                                      |
| FileScanner | ParserFactory | Parsers | Metadata     |
| RetrievalPipeline | Local Retrieval Client           |
+--------------------------+---------------------------+
                           |
                    Local HTTP API
                    127.0.0.1:8765
                           |
                           v
+------------------------------------------------------+
|               Python FastAPI Service                 |
|                                                      |
| Indexing | Search | Chunking | Result Aggregation    |
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

This separation allows the user interface, file-processing logic, machine-learning components, and vector database to evolve independently.

---

## 4. Flutter Frontend Layer

Flutter provides the user-facing interface of the application.

Its responsibilities include:

- Accepting search queries
- Allowing users to select local files
- Displaying semantic search results
- Displaying indexed files
- Providing file-opening operations
- Presenting errors and status information
- Supporting accessible keyboard and interface interaction

Flutter communicates with the Java backend rather than directly implementing embedding or vector-storage logic.

Using Flutter also provides a common UI codebase for supported desktop platforms.

---

## 5. Java Backend Layer

The Java backend coordinates local file processing and communication with the Python retrieval service.

### 5.1 FileScanner

`FileScanner` discovers files from local paths and directories.

Responsibilities include:

- Traversing local directories
- Identifying regular files
- Providing file paths for indexing
- Supporting batch file processing

### 5.2 Parser Interface

The parser interface provides a common abstraction for supported document parsers.

This allows file-format-specific parsing implementations to be separated from the main retrieval pipeline.

### 5.3 TextParser

`TextParser` processes plain TXT documents.

Responsibilities include:

- Reading text files
- Returning extracted textual content
- Handling file-reading errors

### 5.4 DocumentParser

`DocumentParser` processes document formats including:

- PDF
- DOCX

Its main responsibility is extracting text that can later be converted into semantic embeddings.

### 5.5 ImageParser

Image files are recognized as supported local content and are routed through the image indexing pipeline.

Supported image formats include:

- PNG
- JPG
- JPEG

Unlike text documents, images are semantically represented using MobileCLIP rather than BERT text embeddings.

### 5.6 ParserFactory

`ParserFactory` applies the Factory Design Pattern to select an appropriate parser according to file extension.

Typical mappings include:

| File Type | Processing Route |
|---|---|
| TXT | Text parser |
| PDF | Document parser |
| DOCX | Document parser |
| PNG | Image pipeline |
| JPG | Image pipeline |
| JPEG | Image pipeline |

This design keeps file-type selection logic separate from the rest of the indexing pipeline.

### 5.7 Metadata

File metadata is maintained together with indexed content.

Metadata includes information such as:

- File name
- File path
- File type
- File size
- Last modified time
- Content type

This metadata allows semantic search results to be mapped back to the original local files.

---

## 6. Local Python Retrieval Service

The machine-learning retrieval subsystem is implemented as a local Python service using FastAPI.

The service runs locally at:

```text
127.0.0.1:8765
```

It does not need to expose the retrieval API to an external network.

The main API operations include:

- Health checking
- Text indexing
- Image indexing
- Semantic search
- Indexed-file listing
- File deletion

The service acts as the bridge between the Java application and the Python-based embedding and vector-storage components.

---

## 7. Text Embedding Architecture

Text semantic retrieval uses a BERT-based embedding model.

For each text input:

1. Text is extracted from the source file.
2. Long content is divided into smaller chunks.
3. Each chunk is processed by the BERT embedding engine.
4. A numerical embedding vector is generated.
5. The embedding is stored in the text collection in ChromaDB.

During search:

1. The user's query is processed by the same text embedding engine.
2. A query embedding is generated.
3. ChromaDB performs cosine-similarity retrieval.
4. Relevant document chunks are returned.
5. Chunk-level results are aggregated to file-level results.

This architecture allows semantic matching rather than relying only on exact keyword matching.

---

## 8. Long-Document Chunking

Long-document chunking is used to avoid losing information from documents that exceed the practical input length of the text embedding model.

The current chunking configuration uses:

```text
Chunk size:    400 words
Chunk overlap: 50 words
```

The overlap preserves contextual information around chunk boundaries.

Each chunk receives:

- A unique record identifier
- The original file identifier
- A chunk index
- The total chunk count
- Original file metadata

Each chunk is embedded independently and stored as a separate vector record.

### File-Level Aggregation

Although long documents may generate multiple vector records, users search for files rather than individual chunks.

Therefore, after ChromaDB retrieves relevant chunks, the system groups results using the original file identifier.

The highest-scoring matching chunk represents the relevance score of the corresponding file.

This prevents a single long document from appearing repeatedly in the final result list.

---

## 9. Image Embedding Architecture

Image semantic retrieval uses MobileCLIP.

During image indexing:

1. The image file is loaded locally.
2. MobileCLIP generates an image embedding.
3. File metadata and the embedding are stored in the image collection in ChromaDB.

During search:

1. The user's text query is converted into a MobileCLIP text embedding.
2. The query embedding is compared with stored image embeddings.
3. Relevant images are retrieved using cosine similarity.

Because MobileCLIP maps text and images into a compatible embedding space, the system can retrieve images using natural-language text queries.

---

## 10. ChromaDB Vector Storage

ChromaDB provides persistent local vector storage.

The database is stored inside the local project environment and does not require a remote database server.

Two collections are maintained:

```text
offline_retriever_text
offline_retriever_images
```

The text collection stores BERT embeddings for document chunks.

The image collection stores MobileCLIP image embeddings.

Both collections use cosine distance for semantic similarity retrieval.

The database stores:

- Vector embeddings
- Record identifiers
- File identifiers
- File metadata
- Chunk metadata where applicable
- Text chunk content where applicable

Persistent storage means indexed files do not need to be completely re-indexed every time the application starts.

---

## 11. Multimodal Result Fusion

Text and image results originate from different embedding models:

- BERT for text documents
- MobileCLIP for images

Their raw cosine similarity scores are not guaranteed to have identical score distributions.

Directly combining the two raw score sets can therefore produce unbalanced rankings.

To reduce this problem, the current implementation applies a calibration factor to image similarity scores before combining the two modalities.

The current calibration is:

```text
Text score  = raw BERT cosine similarity
Image score = raw MobileCLIP cosine similarity × 1.25
```

The calibration factor was selected through local retrieval testing using queries representing text-oriented, image-oriented, and general semantic searches.

After calibration:

1. Text results are aggregated to file level.
2. Image scores are calibrated.
3. Text and image results are combined.
4. Results are sorted by the final score.
5. The requested top-K results are returned.

This provides a simple and transparent multimodal ranking strategy while preserving the original semantic similarity information.

---

## 12. Search Pipeline

The complete semantic search workflow is:

```text
                     User Query
                         |
                         v
                +----------------+
                | Java / Flutter |
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

## 13. File Identification and Deletion

Files are assigned deterministic identifiers based on their normalized local paths.

A SHA-256 hash is used to generate the file identifier.

For chunked text documents, each vector record additionally receives a chunk-specific identifier.

This design allows:

- Multiple chunks to reference the same source file
- File-level result aggregation
- Reliable deletion of indexed content
- Stable identification of local files

Deleting a file from the index removes its associated vector records from local storage.

---

## 14. Offline-First Design

Offline operation is a central design requirement.

The retrieval pipeline performs the following operations locally:

- File parsing
- Metadata extraction
- BERT inference
- MobileCLIP inference
- Vector storage
- Vector similarity search
- Result ranking

The FastAPI service is bound to the local loopback interface rather than an external host.

Once the required machine-learning models and dependencies are installed locally, semantic retrieval does not require an external network connection.

This design improves:

- Privacy
- Data ownership
- Availability without Internet access
- Protection of sensitive local documents

---

## 15. Error Handling

The system performs validation at multiple layers.

Examples include:

- Rejecting unsupported file types
- Rejecting empty text content
- Detecting missing files
- Preventing empty search queries
- Handling parser failures
- Handling retrieval-service errors
- Checking whether indexed files still exist locally

The local service returns appropriate HTTP error responses for invalid indexing and search operations.

---

## 16. Testing and Validation

The project uses automated and manual testing to validate core functionality.

### Backend Testing

JUnit is used for Java backend testing.

JaCoCo is used to measure Java test coverage.

Core functional modules achieved high test coverage, with the vector retrieval package reaching approximately 98% instruction coverage and the major functional modules achieving approximately 93–100%.

The overall backend instruction coverage is approximately 84%, with application entry points and demonstration classes accounting for much of the uncovered code.

### Frontend Testing

Flutter tests are used to validate user-interface behaviour and important interaction flows.

### Retrieval Testing

Manual integration tests have been performed for:

- TXT indexing
- PDF indexing
- DOCX indexing
- JPG/PNG image indexing
- Text semantic search
- Image semantic search
- Multimodal retrieval
- Long-document chunking
- File-level chunk aggregation
- Persistent ChromaDB storage
- Offline operation

---

## 17. Scalability Validation

The local retrieval architecture was tested with 1,000 generated text files.

Before the stress test, the text vector collection contained 12 records.

After indexing 1,000 additional files, the collection contained:

```text
1,012 text records
```

The indexed-file API independently confirmed that all:

```text
1,000 stress-test files
```

were present.

No missing test files were observed during the validation.

Recorded indexing measurements included:

| Batch Size | Time |
|---:|---:|
| 200 files | 14.81 seconds |
| 300 files | 25.94 seconds |
| 450 files | 41.72 seconds |

The initial 50-file validation batch was used to verify the indexing workflow but was not timed.

With more than 1,000 text records stored, an end-to-end semantic search for `software engineering` completed in approximately:

```text
807 ms
```

This measurement includes Java CLI execution, local HTTP communication, query embedding, vector retrieval, multimodal processing, result ranking, and output handling.

The test demonstrates that the system can successfully index and search at least 1,000 local files without backend failure.

---

## 18. Design Principles

The architecture follows several software engineering principles.

### 18.1 Modular Design

Parsing, metadata extraction, embedding, storage, retrieval, and UI logic are separated into different components.

### 18.2 Separation of Concerns

Java handles application and file-processing logic, while Python handles machine-learning inference and vector retrieval.

### 18.3 Interface-Oriented Design

Parser interfaces allow additional document formats to be introduced without redesigning the complete retrieval pipeline.

### 18.4 Local-First Processing

User files and semantic vectors remain on the local machine during normal retrieval operations.

### 18.5 Extensibility

The modular architecture allows future improvements such as:

- Additional file parsers
- Alternative embedding models
- Improved multimodal fusion algorithms
- Additional accessibility features
- Application-internal file preview
- Further platform-specific validation

---

## 19. Cross-Platform Considerations

Flutter provides a shared application codebase that can target desktop platforms including Windows, macOS, and Linux.

The current implementation has been developed and functionally validated on Windows.

The architecture avoids unnecessary platform-specific dependencies where possible. However, full runtime validation on macOS and Linux requires access to those operating-system environments.

Therefore, Windows is currently the fully tested desktop environment, while macOS and Linux remain platform-validation targets.

---

## 20. Maintenance Considerations

Important configurable retrieval parameters include:

```text
CHUNK_SIZE = 400
CHUNK_OVERLAP = 50
CHUNK_SEARCH_MULTIPLIER = 5
IMAGE_SCORE_CALIBRATION = 1.25
```

These parameters should be changed carefully because they affect:

- Indexing cost
- Search quality
- Number of stored vectors
- Long-document retrieval
- Multimodal ranking behaviour

Changes to embedding models may require existing vectors to be re-indexed because embeddings generated by different models may not be directly compatible.

ChromaDB collections should also remain consistent with the embedding dimensions and similarity metrics used by the corresponding models.

---

## 21. Future Improvements

The core multimodal semantic retrieval architecture is now implemented.

Potential future improvements include:

- More advanced multimodal score normalization
- Additional Microsoft Office file formats
- In-application document and image preview
- Larger-scale performance testing
- Automated model installation and packaging
- Additional accessibility validation
- Full runtime validation on macOS and Linux
- More advanced chunking based on sentences or document structure

These improvements extend the existing architecture rather than representing missing core retrieval components.

---

## 22. Conclusion

The Offline Accessible Multimodal Local Content Retrieval System now implements a complete local semantic retrieval architecture.

The system integrates:

- Flutter for the user interface
- Java for application and file-processing logic
- FastAPI for the local retrieval service
- BERT for text semantic embeddings
- MobileCLIP for image and text-image retrieval
- ChromaDB for persistent local vector storage
- Long-document chunking and file-level aggregation
- Calibrated multimodal result ranking

The modular architecture separates user-interface, parsing, machine-learning, retrieval, and storage responsibilities while maintaining offline-first operation.

Testing has demonstrated successful multimodal retrieval, long-document indexing, persistent vector storage, and indexing of at least 1,000 local files.

This architecture provides a maintainable foundation for final project delivery and future extension.