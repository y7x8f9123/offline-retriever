# API Reference

## 1. Overview

This document describes the main application and retrieval APIs of the Offline Accessible Multimodal Local Content Retrieval System.

The current implementation uses three main layers:

- Flutter frontend
- Java backend and command-line bridge
- Local Python FastAPI retrieval service

The retrieval service integrates:

- BERT text embeddings
- MobileCLIP image/text embeddings
- ChromaDB persistent vector storage
- Long-document chunking
- File-level aggregation
- Multimodal score calibration

The local retrieval service listens on:

```text
http://127.0.0.1:8765
```

The system is designed for local execution and does not require a remote retrieval server during normal operation after the required models and dependencies are available locally.

---

# 2. Java Backend Entry Point

## 2.1 BackendCli

**Package**

```java
com.offlineretriever
```

`BackendCli` is the main command-line bridge used by the desktop application and for local testing.

The executable JAR accepts four main commands:

```text
index
search
list
delete
```

### Index Command

```text
java -jar backend-1.0-SNAPSHOT.jar index <file1> [file2] ...
```

Indexes one or more supported local files.

Supported document types include:

- TXT
- PDF
- DOCX

Supported image types include:

- PNG
- JPG
- JPEG

Text files are parsed in Java and then forwarded to the local Python retrieval service.

Images are forwarded directly to the image indexing endpoint.

Example:

```text
java -jar backend-1.0-SNAPSHOT.jar index report.pdf notes.txt image.png
```

Example response:

```json
{
  "indexed": [
    "report.pdf",
    "notes.txt",
    "image.png"
  ],
  "skipped": []
}
```

Files that do not exist, are unsupported, or cannot be parsed are added to the `skipped` list.

---

### Search Command

```text
java -jar backend-1.0-SNAPSHOT.jar search <query> <topK>
```

Performs semantic search across indexed text documents and images.

Example:

```text
java -jar backend-1.0-SNAPSHOT.jar search "software engineering" 5
```

Example result structure:

```json
[
  {
    "id": "record-id",
    "fileName": "example.txt",
    "filePath": "C:\\example\\example.txt",
    "fileType": "txt",
    "contentType": "text",
    "score": 0.42
  }
]
```

The final result score may represent:

- BERT cosine similarity for text results
- Calibrated MobileCLIP cosine similarity for image results

---

### List Command

```text
java -jar backend-1.0-SNAPSHOT.jar list
```

Returns indexed files stored in the local retrieval database.

Chunked text documents are returned once at file level rather than once per chunk.

---

### Delete Command

```text
java -jar backend-1.0-SNAPSHOT.jar delete <id>
```

Deletes an indexed file from the local vector database.

For chunked text documents, all associated chunk records are deleted using the original file identifier.

---

# 3. Local Retrieval Service

The local retrieval service is implemented using FastAPI.

Base address:

```text
http://127.0.0.1:8765
```

The main endpoints are:

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/health` | Check backend and model status |
| GET | `/files` | List indexed files |
| POST | `/index-text` | Index extracted text content |
| POST | `/index-image` | Index a local image |
| POST | `/search` | Perform multimodal semantic search |
| POST | `/delete` | Delete an indexed file |

---

# 4. GET /health

Checks whether the local retrieval service is running and whether the main machine-learning components are loaded.

## Request

```http
GET /health
```

## Response

Example:

```json
{
  "status": "ok",
  "text_records": 1012,
  "image_records": 2,
  "bert_loaded": true,
  "mobileclip_loaded": true
}
```

## Response Fields

### status

Service status.

Typical value:

```text
ok
```

### text_records

Number of records currently stored in the text ChromaDB collection.

Because long documents are chunked, this value represents vector records or chunks rather than strictly the number of source files.

### image_records

Number of indexed image records.

### bert_loaded

Indicates whether the BERT text embedding engine has loaded successfully.

### mobileclip_loaded

Indicates whether the MobileCLIP model has loaded successfully.

---

# 5. GET /files

Returns indexed files stored in the local vector database.

## Request

```http
GET /files
```

## Response

Example:

```json
[
  {
    "id": "file-id",
    "fileName": "report.pdf",
    "filePath": "C:\\documents\\report.pdf",
    "fileType": "pdf",
    "fileSize": 296037,
    "lastModified": 1782033288822.284,
    "contentType": "text",
    "chunkIndex": 0,
    "chunkCount": 7,
    "exists": true
  }
]
```

## Behaviour

For text documents split into multiple chunks, the endpoint returns only one file-level result.

The `exists` field is calculated at request time and indicates whether the original local file is still present.

---

# 6. POST /index-text

Indexes textual content extracted from a TXT, PDF, or DOCX file.

## Request

```http
POST /index-text
Content-Type: application/json
```

Request body:

```json
{
  "file_path": "C:\\documents\\report.pdf",
  "content": "Extracted document text..."
}
```

## Validation

The endpoint verifies:

- The file exists
- The content is not empty
- The file extension is supported

Supported extensions:

```text
txt
pdf
docx
```

Unsupported files return HTTP 400.

Missing files return HTTP 404.

---

## Long-Document Chunking

Text is divided using the current configuration:

```text
CHUNK_SIZE = 400 words
CHUNK_OVERLAP = 50 words
```

Each chunk is embedded independently.

The chunk identifier format is:

```text
<file_id>_chunk_<index>
```

Each chunk stores metadata including:

```text
fileId
chunkIndex
chunkCount
fileName
filePath
fileType
fileSize
lastModified
contentType
```

## Response

Example:

```json
{
  "status": "ok",
  "id": "file-id",
  "contentType": "text",
  "chunkCount": 7,
  "dimension": 768
}
```

The exact embedding dimension depends on the active BERT model.

---

# 7. POST /index-image

Indexes a local image using MobileCLIP.

## Request

```http
POST /index-image
Content-Type: application/json
```

Request body:

```json
{
  "file_path": "C:\\pictures\\example.png"
}
```

## Supported Formats

```text
jpg
jpeg
png
```

## Processing

The endpoint:

1. Verifies the file exists
2. Validates the extension
3. Generates a MobileCLIP image embedding
4. Builds file metadata
5. Stores the embedding in the image ChromaDB collection

## Response

Example:

```json
{
  "status": "ok",
  "id": "file-id",
  "contentType": "image",
  "dimension": 512
}
```

The exact embedding dimension depends on the active MobileCLIP model.

---

# 8. POST /search

Performs semantic search across both text documents and images.

## Request

```http
POST /search
Content-Type: application/json
```

Request body:

```json
{
  "query": "red image",
  "top_k": 5
}
```

## Validation

Empty queries return HTTP 400.

`top_k` is forced to a minimum value of 1.

---

## Text Search Flow

The text search pipeline performs:

1. BERT query embedding
2. ChromaDB similarity search
3. Retrieval of multiple chunk-level candidates
4. File-level aggregation
5. Selection of the highest-scoring chunk for each file

The number of chunk candidates is calculated using:

```text
chunk_search_k = top_k × CHUNK_SEARCH_MULTIPLIER
```

Current value:

```text
CHUNK_SEARCH_MULTIPLIER = 5
```

---

## Image Search Flow

If image records exist:

1. MobileCLIP converts the text query into an embedding
2. The image ChromaDB collection is searched
3. Image results are returned using cosine similarity
4. Image scores are calibrated before fusion

Current calibration:

```text
IMAGE_SCORE_CALIBRATION = 1.25
```

The final image score is:

```text
calibrated_score =
raw_mobileclip_similarity × 1.25
```

---

## Multimodal Fusion

Text and image results are combined after:

- Text chunk aggregation
- Image score calibration

The final combined list is sorted by descending score.

The requested top-K results are then returned.

## Response

Example:

```json
[
  {
    "id": "image-id",
    "recordId": "image-id",
    "fileName": "red.png",
    "filePath": "C:\\pictures\\red.png",
    "fileType": "png",
    "contentType": "image",
    "chunkIndex": -1,
    "rawScore": 0.31,
    "score": 0.39
  },
  {
    "id": "text-id",
    "recordId": "text-id_chunk_0",
    "fileName": "notes.txt",
    "filePath": "C:\\documents\\notes.txt",
    "fileType": "txt",
    "contentType": "text",
    "chunkIndex": 0,
    "rawScore": 0.38,
    "score": 0.38
  }
]
```

The Java bridge may expose only the fields required by the frontend.

---

# 9. POST /delete

Deletes an indexed file.

## Request

```http
POST /delete
Content-Type: application/json
```

Request body:

```json
{
  "id": "file-id"
}
```

## Behaviour

For text documents:

```text
all records where metadata.fileId == requested id
```

are removed.

For images:

```text
the image record with the matching id
```

is removed.

## Response

```json
{
  "status": "ok",
  "id": "file-id"
}
```

---

# 10. ChromaStore API

The Python storage layer is implemented by:

```python
ChromaStore
```

The database path is:

```text
<project-root>/chroma_db
```

Two persistent collections are used:

```text
offline_retriever_text
offline_retriever_images
```

Both collections use cosine similarity.

---

## 10.1 add_text_file

```python
add_text_file(
    record_id: str,
    embedding: list[float],
    metadata: dict[str, Any],
    document: str = "",
) -> None
```

Stores or updates one text vector record.

For chunked documents, each chunk uses a unique `record_id`.

---

## 10.2 add_image_file

```python
add_image_file(
    file_id: str,
    embedding: list[float],
    metadata: dict[str, Any],
) -> None
```

Stores or updates one image vector record.

---

## 10.3 search_text

```python
search_text(
    query_embedding: list[float],
    top_k: int = 5,
) -> dict
```

Queries the text ChromaDB collection.

Returns:

```text
ids
metadatas
distances
```

---

## 10.4 search_images

```python
search_images(
    query_embedding: list[float],
    top_k: int = 5,
) -> dict
```

Queries the image collection using an embedding generated from the text query.

---

## 10.5 delete_file

```python
delete_file(
    file_id: str,
) -> None
```

Deletes all text chunks associated with the supplied file ID and also attempts to delete a matching image record.

---

## 10.6 get_all_files

```python
get_all_files() -> list[dict]
```

Returns indexed files.

Text chunks are deduplicated using the original file ID so that one source document appears once.

---

## 10.7 text_count

```python
text_count() -> int
```

Returns the number of text vector records stored in ChromaDB.

This may exceed the number of text files because one long document may contain multiple chunks.

---

## 10.8 image_count

```python
image_count() -> int
```

Returns the number of indexed image records.

---

# 11. BERT Text Embedding API

The current main text retrieval pipeline uses the Python BERT embedding engine.

Typical operation:

```python
embedding = text_engine.embed(text)
```

The output is a numerical embedding suitable for cosine-similarity retrieval.

The same model is used for:

- Document chunks
- Search queries

This ensures documents and text queries are represented in a compatible semantic space.

---

# 12. MobileCLIP API

The image retrieval pipeline uses MobileCLIP.

## Image Embedding

```python
image_engine.embed_image(file_path)
```

Generates an image embedding.

## Text Embedding

```python
image_engine.embed_text(query)
```

Generates a text embedding in the MobileCLIP multimodal embedding space.

This allows natural-language text queries to retrieve semantically related images.

---

# 13. Java Parsing APIs

The Java parsing layer is still responsible for local text extraction.

## Parser

```java
String parse(String filePath);
```

Provides a shared abstraction for parser implementations.

---

## TextParser

Handles:

```text
.txt
```

and returns textual content.

---

## DocumentParser

Handles:

```text
.pdf
.docx
```

and extracts text from supported documents.

---

## ParserFactory

Typical mappings include:

| Extension | Processing |
|---|---|
| `.txt` | TextParser |
| `.pdf` | DocumentParser |
| `.docx` | DocumentParser |
| `.jpg` | Image indexing route |
| `.jpeg` | Image indexing route |
| `.png` | Image indexing route |

Image files are routed to MobileCLIP-based indexing instead of BERT text parsing in the current main indexing flow.

---

# 14. File Metadata

Indexed file metadata contains fields including:

```text
fileName
filePath
fileType
fileSize
lastModified
contentType
```

Chunked text records additionally include:

```text
fileId
chunkIndex
chunkCount
```

This metadata is used for:

- Displaying result information
- Opening local files
- File-level aggregation
- Deleting all chunks belonging to a document
- Checking whether indexed files still exist

---

# 15. File Identifier Generation

The retrieval service creates deterministic file identifiers using:

```text
SHA-256(normalized absolute file path)
```

Conceptually:

```python
normalized_path = resolved_path.lower()
file_id = sha256(normalized_path)
```

This provides a stable local identifier for a given path.

Chunk identifiers are generated using:

```text
<file_id>_chunk_<chunk_index>
```

---

# 16. Similarity Scoring

Both ChromaDB collections use cosine space.

The returned ChromaDB distance is converted to similarity using:

```text
similarity = 1.0 - distance
```

For text:

```text
final score = raw BERT similarity
```

For images:

```text
final score =
raw MobileCLIP similarity × 1.25
```

Negative similarity values are valid and indicate low semantic similarity.

---

# 17. Error Handling

The retrieval API may return errors including:

### HTTP 400

Examples:

```text
Text content is empty.
Unsupported text file type.
Unsupported image file type.
Query cannot be empty.
No text chunks were generated.
```

### HTTP 404

Example:

```text
File not found.
```

Java-side failures are reported through stderr and a non-zero process exit code.

---

# 18. Persistence Behaviour

ChromaDB uses persistent local storage.

The database remains available between service restarts.

The local database directory is excluded from source control because indexed vectors are runtime data rather than source code.

Downloaded local model directories are also excluded from Git source control.

---

# 19. Current Retrieval Configuration

Important current parameters include:

```text
CHUNK_SIZE = 400
CHUNK_OVERLAP = 50
CHUNK_SEARCH_MULTIPLIER = 5
IMAGE_SCORE_CALIBRATION = 1.25
```

Changing these values can affect:

- Number of stored vectors
- Indexing time
- Search recall
- Chunk boundary behaviour
- Multimodal ranking

Changes should therefore be validated before release.

---

# 20. Performance Validation

The retrieval system was tested with 1,000 generated TXT files.

Observed results included:

```text
Text records before test: 12
Text records after test: 1012
Stress-test files listed: 1000
```

Measured indexing times included:

| Batch | Time |
|---:|---:|
| 200 files | 14.81 s |
| 300 files | 25.94 s |
| 450 files | 41.72 s |

The initial 50-file batch was used as a functional validation batch and was not timed.

An end-to-end semantic search over more than 1,000 text records completed in approximately:

```text
807 ms
```

for the query:

```text
software engineering
```

This timing includes Java CLI execution, HTTP communication, query embedding, vector search, multimodal processing, ranking, and output handling.

---

# 21. Deprecated Prototype Components

Some Java classes from earlier development stages remain useful for unit testing, demonstration, or modular experimentation.

Examples include:

```text
VectorStore
Retriever
SimilarityCalculator
TextEmbeddingEngine
RetrievalPipeline
```

Earlier versions used:

- Deterministic token hashing
- In-memory vector storage
- Java-only similarity search

These are no longer the primary production retrieval path.

The current main retrieval path is:

```text
Flutter
  ↓
Java BackendCli
  ↓
Local FastAPI Service
  ↓
BERT / MobileCLIP
  ↓
ChromaDB
```

Documentation and final deployment should therefore describe the Python/ChromaDB retrieval service as the main implementation.

---

# 22. Summary

The current system exposes a complete local multimodal retrieval API.

The main functional flow includes:

- Java-based local file parsing
- FastAPI-based retrieval service
- BERT text embeddings
- MobileCLIP image retrieval
- Long-document chunking
- ChromaDB persistent storage
- File-level result aggregation
- Calibrated multimodal ranking
- Local indexing, listing, searching, and deletion

The API remains fully local during normal operation and is designed to support the final desktop application workflow.