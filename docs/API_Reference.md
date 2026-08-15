# API Reference

## 1. Overview

This document describes the main backend APIs of the Offline Accessible Multimodal Local Content Retrieval System.

The backend is implemented in Java and organized into independent modules for file scanning, metadata extraction, file parsing, embedding generation, vector storage, similarity calculation, and local retrieval.

The current desktop prototype integrates the Java backend with the Flutter frontend through a local command-line interface. Search queries and selected file paths are passed to the packaged Java backend, and ranked retrieval results are returned to Flutter as JSON.

The main packages are:

* `com.offlineretriever`
* `com.offlineretriever.embedding`
* `com.offlineretriever.factory`
* `com.offlineretriever.io`
* `com.offlineretriever.metadata`
* `com.offlineretriever.model`
* `com.offlineretriever.parser`
* `com.offlineretriever.vector`

---

# 2. Core Retrieval Pipeline

## 2.1 RetrievalPipeline

**Package**

```java
com.offlineretriever
```

`RetrievalPipeline` provides the main integration layer between parsing, embedding, vector storage, and retrieval.

### Constructor

```java
public RetrievalPipeline()
```

Creates a new retrieval pipeline and initializes the components required for indexing and local similarity search.

### indexFile

```java
public void indexFile(File file) throws IOException
```

Indexes a local file into the retrieval system.

The method selects and uses the appropriate parser, extracts textual content, generates an embedding representation, and adds the resulting vector record to the local vector store.

**Parameters**

* `file` – local file to be indexed.

**Throws**

* `IOException` – if the file cannot be accessed or processed.

### search

```java
public List<SearchResult> search(String query, int topK)
```

Performs local retrieval using a text query.

The query is converted into a vector representation and compared against indexed vector records. The most relevant results are returned according to cosine similarity scores.

**Parameters**

* `query` – search query entered by the user.
* `topK` – maximum number of search results to return.

**Returns**

A list of `SearchResult` objects ranked by similarity.

### getVectorStore

```java
public VectorStore getVectorStore()
```

Returns the vector store used by the retrieval pipeline.

**Returns**

The current `VectorStore` instance.

---

# 3. Embedding Module

## 3.1 EmbeddingEngine

**Package**

```java
com.offlineretriever.embedding
```

`EmbeddingEngine<T>` defines the common interface used by embedding implementations.

It provides a standardized abstraction for converting input data into numerical vector representations.

The current backend implementation uses `TextEmbeddingEngine` for textual content.

---

## 3.2 TextEmbeddingEngine

**Package**

```java
com.offlineretriever.embedding
```

`TextEmbeddingEngine` implements:

```java
EmbeddingEngine<String>
```

It converts textual input into a floating-point vector that can be stored and compared during retrieval.

The current prototype generates a 256-dimensional local vector representation using deterministic token hashing.

English alphanumeric tokens are supported together with Chinese character-based tokens. Chinese text is represented using individual Chinese characters and two-character combinations, allowing both English and Chinese document content to participate in local retrieval.

The current prototype uses this lightweight deterministic embedding mechanism rather than a pretrained BERT model. This keeps the current retrieval workflow fully local and simple to execute during prototype testing.

### embed

```java
public float[] embed(String input)
```

Generates a 256-dimensional vector representation for the supplied text.

**Parameters**

* `input` – text to be embedded.

**Returns**

A `float[]` containing the generated vector representation.

**Throws**

* `IllegalArgumentException` – if the supplied text is null or empty.

---

# 4. File I/O Module

## 4.1 FileScanner

**Package**

```java
com.offlineretriever.io
```

`FileScanner` is responsible for discovering files in a local folder.

### scan

```java
public List<Path> scan(String folderPath)
```

Scans the specified folder and returns discovered file paths.

**Parameters**

* `folderPath` – path of the local folder to scan.

**Returns**

A list of `Path` objects representing discovered files.

---

# 5. Metadata Module

## 5.1 MetadataExtractor

**Package**

```java
com.offlineretriever.metadata
```

`MetadataExtractor` obtains metadata from local files before or during ingestion.

### extract

```java
public FileMetadata extract(Path filePath)
```

Extracts metadata from the specified file.

**Parameters**

* `filePath` – path of the file.

**Returns**

A `FileMetadata` object containing information about the file.

---

# 6. Metadata Model

## 6.1 FileMetadata

**Package**

```java
com.offlineretriever.model
```

`FileMetadata` stores descriptive information about a local file.

The model contains:

* File name
* File type
* File size
* Last modified time
* File path

### Constructor

```java
public FileMetadata(...)
```

Creates a metadata object containing information extracted from a local file.

### getFileName

```java
public String getFileName()
```

Returns the file name.

### getFileType

```java
public String getFileType()
```

Returns the file type.

### getFileSize

```java
public long getFileSize()
```

Returns the file size.

### getLastModified

```java
public LocalDateTime getLastModified()
```

Returns the last modification date and time.

### getFilePath

```java
public String getFilePath()
```

Returns the local file path.

### toString

```java
public String toString()
```

Returns a readable string representation of the metadata object.

---

# 7. Parsing Module

## 7.1 Parser

**Package**

```java
com.offlineretriever.parser
```

`Parser` defines the common parsing interface used by supported file parsers.

Concrete parser implementations currently include:

* `TextParser`
* `DocumentParser`
* `ImageParser`

These implementations allow different local file types to be handled through a consistent parsing abstraction.

The current end-to-end Flutter desktop workflow has been tested with TXT, PDF, and DOCX documents.

---

## 7.2 TextParser

**Package**

```java
com.offlineretriever.parser
```

`TextParser` implements the `Parser` interface and handles plain-text files.

### parse

```java
public String parse(String filePath)
```

Reads and extracts textual content from the supplied TXT file.

**Parameters**

* `filePath` – path of the file to parse.

**Returns**

Extracted content as a `String`.

---

## 7.3 DocumentParser

**Package**

```java
com.offlineretriever.parser
```

`DocumentParser` implements the `Parser` interface and provides text extraction support for document formats including PDF and DOCX.

The current implementation uses Apache Tika to extract textual content from supported local documents.

Extracted text is passed to the embedding and retrieval pipeline in the same way as plain-text content.

The prototype has also been manually tested with Chinese textual content extracted from PDF documents.

### parse

```java
public String parse(String filePath)
```

Processes the supplied document and extracts its textual content.

**Parameters**

* `filePath` – path of the document.

**Returns**

Extracted document content as a `String`.

---

## 7.4 ImageParser

**Package**

```java
com.offlineretriever.parser
```

`ImageParser` implements the `Parser` interface and provides the parser abstraction for image files.

### parse

```java
public String parse(String filePath)
```

Processes the supplied image file and returns its parsed representation.

**Parameters**

* `filePath` – path of the image file.

**Returns**

The parsed representation as a `String`.

Image parser support currently exists at the backend abstraction level. Image retrieval is not yet integrated into the current end-to-end Flutter desktop retrieval workflow.

---

# 8. Parser Factory

## 8.1 ParserFactory

**Package**

```java
com.offlineretriever.factory
```

`ParserFactory` selects the appropriate parser according to the supplied file name and extension.

### getParser

```java
public static Parser getParser(String fileName)
```

Returns a parser suitable for the specified file.

**Parameters**

* `fileName` – name of the file that needs to be parsed.

**Returns**

An implementation of the `Parser` interface.

Current parser mappings include:

* `.txt` → `TextParser`
* `.pdf` → `DocumentParser`
* `.docx` → `DocumentParser`
* `.jpg` → `ImageParser`
* `.jpeg` → `ImageParser`
* `.png` → `ImageParser`

The current Flutter file-import workflow exposes TXT, PDF, and DOCX documents. Image parser support exists at the backend abstraction level but is not yet integrated into the current end-to-end desktop retrieval workflow.

The factory separates parser-selection logic from the rest of the ingestion pipeline and makes it easier to extend support for additional file formats.

---

# 9. Vector Storage and Retrieval

## 9.1 VectorRecord

**Package**

```java
com.offlineretriever.vector
```

`VectorRecord` represents one indexed item stored in the vector retrieval layer.

It associates an identifier, file name, original local file path, and embedding vector with an indexed document.

The local file path is preserved so that the Flutter frontend can open a document returned by the retrieval process.

### Constructor

The current `VectorRecord` constructor initializes the identifying information, source file information, and vector representation required by the retrieval pipeline.

### getId

```java
public String getId()
```

Returns the record identifier.

### getFileName

```java
public String getFileName()
```

Returns the associated file name.

### getFilePath

```java
public String getFilePath()
```

Returns the original local path associated with the indexed file.

The path can be included in retrieval output so that the Flutter desktop interface can open the matching local document using the operating system's default application.

### getEmbedding

```java
public float[] getEmbedding()
```

Returns the embedding vector.

### setId

```java
public void setId(String id)
```

Updates the record identifier.

### setFileName

```java
public void setFileName(String fileName)
```

Updates the associated file name.

### setFilePath

```java
public void setFilePath(String filePath)
```

Updates the associated local file path.

### setEmbedding

```java
public void setEmbedding(float[] embedding)
```

Updates the embedding vector.

### toString

```java
public String toString()
```

Returns a readable string representation of the vector record.

---

## 9.2 VectorStore

**Package**

```java
com.offlineretriever.vector
```

`VectorStore` manages the collection of indexed vector records used by the retrieval engine.

The current prototype uses an in-memory vector store.

### Constructor

```java
public VectorStore()
```

Creates an empty vector store.

### add

```java
public void add(VectorRecord record)
```

Adds a vector record to the store.

**Parameters**

* `record` – vector record to add.

### getAllRecords

```java
public List<VectorRecord> getAllRecords()
```

Returns all currently stored records.

### size

```java
public int size()
```

Returns the number of records currently stored.

### clear

```java
public void clear()
```

Removes all records from the vector store.

---

## 9.3 Retriever

**Package**

```java
com.offlineretriever.vector
```

`Retriever` performs similarity-based retrieval over records stored in a `VectorStore`.

### Constructor

```java
public Retriever(VectorStore vectorStore)
```

Creates a retriever connected to the specified vector store.

**Parameters**

* `vectorStore` – source vector store used for retrieval.

### search

```java
public List<SearchResult> search(float[] queryVector, int topK)
```

Searches the vector store using a query vector.

Records are compared against the query vector and ranked according to cosine similarity.

**Parameters**

* `queryVector` – vector representing the search query.
* `topK` – maximum number of results to return.

**Returns**

A ranked list of `SearchResult` objects.

---

## 9.4 SearchResult

**Package**

```java
com.offlineretriever.vector
```

`SearchResult` represents one result returned by the retrieval process.

Each result contains the matching vector record and its calculated similarity score.

### Constructor

```java
public SearchResult(VectorRecord record, double similarityScore)
```

**Parameters**

* `record` – matching vector record.
* `similarityScore` – calculated similarity score.

### getRecord

```java
public VectorRecord getRecord()
```

Returns the matching vector record.

### getSimilarityScore

```java
public double getSimilarityScore()
```

Returns the similarity score associated with the result.

### toString

```java
public String toString()
```

Returns a readable representation of the search result.

---

## 9.5 SimilarityCalculator

**Package**

```java
com.offlineretriever.vector
```

`SimilarityCalculator` provides similarity calculation utilities used by the retrieval module.

### cosineSimilarity

```java
public static double cosineSimilarity(float[] vectorA, float[] vectorB)
```

Calculates cosine similarity between two vectors.

**Parameters**

* `vectorA` – first vector.
* `vectorB` – second vector.

**Returns**

The cosine similarity value between the two vectors.

The method is used by the retrieval layer to determine how closely an indexed record matches a query representation.

---

# 10. Application Entry Points

## 10.1 App

**Package**

```java
com.offlineretriever
```

`App` provides a basic backend application entry point.

### main

```java
public static void main(String[] args)
```

Starts the application.

---

## 10.2 PipelineDemo

**Package**

```java
com.offlineretriever
```

`PipelineDemo` provides a demonstration entry point for the retrieval pipeline.

### main

```java
public static void main(String[] args) throws IOException
```

Runs a demonstration of the backend retrieval workflow.

---

## 10.3 BackendCli

**Package**

```java
com.offlineretriever
```

`BackendCli` provides the command-line bridge used by the Flutter desktop application to invoke the Java retrieval backend.

The backend is packaged as an executable JAR using Maven.

### main

```java
public static void main(String[] args)
```

The command-line interface accepts:

* A text search query
* A `topK` result limit
* One or more local file paths

The supplied files are indexed through the retrieval pipeline. The query is then processed and the ranked results are written to standard output as JSON.

A typical invocation follows the form:

```text
java -jar backend-1.0-SNAPSHOT.jar <query> <topK> <file1> [file2] ...
```

The JSON retrieval output provides information required by the Flutter frontend, including:

* File name
* Original local file path
* Similarity score

The Flutter `RetrievalService` launches the packaged backend JAR as a local process, supplies the selected files and query as command-line arguments, reads the JSON response, and converts the response into frontend retrieval result objects.

This provides an end-to-end local connection between the Flutter frontend and Java backend without requiring an external retrieval server.

---

# 11. Flutter Integration

## 11.1 RetrievalService

The Flutter frontend uses `RetrievalService` as the integration layer between the desktop user interface and the Java backend.

The service performs the following operations:

1. Validates the user's search query.
2. Collects paths for files imported into the local file library.
3. Locates the packaged Java backend JAR.
4. Starts the Java backend as a local process.
5. Passes the query, `topK`, and file paths to `BackendCli`.
6. Reads the backend JSON output.
7. Converts returned JSON objects into `RetrievalResult` instances.
8. Passes the ranked results to the search results interface.

Backend diagnostic output is separated from the JSON result data during response processing so that frontend JSON decoding remains reliable.

---

## 11.2 RetrievalResult

`RetrievalResult` is the Flutter-side representation of a backend search result.

Each result contains:

* `fileName`
* `filePath`
* `score`

The `filePath` field allows the search results interface to locate and open the original local document.

---

## 11.3 Local File Opening

The Flutter results interface uses the returned local file path to open matching documents.

Before opening a result, the application verifies that the file still exists.

The `url_launcher` Flutter package is used to request that the operating system open the file with its associated external application.

For example:

* TXT files can be opened with the system text editor.
* PDF files can be opened with the configured PDF viewer.
* DOCX files can be opened with the configured document application.

---

# 12. Typical End-to-End Workflow

The current prototype supports the following end-to-end processing flow:

```text
Local TXT / PDF / DOCX File
        ↓
Flutter File Library
        ↓
RetrievalService
        ↓
BackendCli
        ↓
RetrievalPipeline
        ↓
ParserFactory
        ↓
TextParser / DocumentParser
        ↓
TextEmbeddingEngine
        ↓
256-dimensional local vector
        ↓
VectorRecord
        ↓
VectorStore
        ↓
Retriever
        ↓
Cosine Similarity
        ↓
SearchResult
        ↓
JSON Response
        ↓
RetrievalResult
        ↓
Flutter Search Results
        ↓
Open Original Local File
```

At application level, `RetrievalPipeline` coordinates the major backend indexing and search operations.

`BackendCli` exposes these operations to the Flutter application, while `RetrievalService` manages the frontend-to-backend process invocation and response conversion.

The complete retrieval workflow executes locally on the user's machine.

---

# 13. Supported Formats

The current Flutter desktop prototype supports importing and retrieving the following file formats:

| File Extension | Parser | Current End-to-End Support |
|---|---|---|
| `.txt` | `TextParser` | Yes |
| `.pdf` | `DocumentParser` | Yes |
| `.docx` | `DocumentParser` | Yes |
| `.jpg` | `ImageParser` | Backend abstraction only |
| `.jpeg` | `ImageParser` | Backend abstraction only |
| `.png` | `ImageParser` | Backend abstraction only |

Apache Tika is used by `DocumentParser` for document text extraction.

Text-based PDF and DOCX documents can therefore participate in the current retrieval workflow.

Image-only or scanned PDF documents may require OCR support, which is not part of the current prototype.

---

# 14. Current Prototype Limitations

The current implementation is a functional prototype and has several intentional limitations.

* Text embeddings use a deterministic 256-dimensional hashing approach rather than a pretrained language model.
* The current vector store is maintained in memory and is not persistent between backend process executions.
* TXT, PDF, and DOCX are integrated into the current Flutter retrieval workflow.
* Image parsing exists at the backend abstraction level but image-based retrieval is not yet integrated into the current desktop workflow.
* OCR for scanned or image-only PDF documents is not currently implemented.
* Retrieval quality is dependent on token overlap and the current lightweight vector representation.
* File opening depends on the operating system having an associated application for the selected file type.

These limitations keep the current prototype lightweight while leaving clear extension points for later development.

---

# 15. Extension Points

The modular API structure allows future implementations to extend the system without significantly changing the existing retrieval pipeline.

Potential extensions include:

* Replacement of the current lightweight text embedding implementation with BERT or another local pretrained embedding model.
* Integration of image embeddings such as MobileCLIP.
* End-to-end image retrieval support.
* OCR support for scanned documents.
* Persistent local vector storage.
* Additional parser implementations for new file formats.
* Additional ranking and filtering strategies.
* Additional metadata fields.
* Improved multilingual retrieval.
* Performance optimizations for larger local document libraries.

The use of interfaces, modular packages, separate data models, and the local frontend-backend bridge helps keep these future extensions isolated from the existing functionality.