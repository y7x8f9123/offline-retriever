# API Reference

## 1. Overview

This document describes the main application interfaces and retrieval APIs of the Offline Accessible Multimodal Local Content Retrieval System.

The final implementation consists of three main layers:

- Flutter Windows desktop frontend
- Java backend and command-line bridge
- Local Python FastAPI retrieval service

The retrieval service integrates:

- BERT text embeddings
- MobileCLIP image and text embeddings
- ChromaDB persistent vector storage
- Long-document chunking
- File-level aggregation
- Multimodal score calibration

The local retrieval service listens on:

```text
http://127.0.0.1:8765
```

The system is designed for local execution.

Once the required dependencies and machine-learning model resources are available locally, normal indexing and retrieval do not require a remote semantic-search or inference server.

---

## 2. Architecture and API Flow

The main communication flow is:

```text
Flutter Frontend
       |
       v
Java Backend / BackendCli
       |
       v
Local FastAPI Service
127.0.0.1:8765
       |
       +-------------------+
       |                   |
       v                   v
     BERT              MobileCLIP
Text Embedding       Image / Text
       |                   |
       +---------+---------+
                 |
                 v
              ChromaDB
```

The Java backend acts as the bridge between the desktop application and the local retrieval service.

The Python service performs machine-learning inference, semantic retrieval, vector storage, result aggregation, and multimodal ranking.

---

# 3. Java Backend Entry Point

## 3.1 BackendCli

**Package**

```java
com.offlineretriever
```

`BackendCli` provides the primary command-line interface for indexing, searching, listing, and deleting indexed content.

The executable backend supports four main commands:

```text
index
search
list
delete
```

---

## 3.2 Index Command

Syntax:

```text
java -jar backend-1.0-SNAPSHOT.jar index <file1> [file2] ...
```

Indexes one or more supported local files.

Supported text document formats:

```text
TXT
PDF
DOCX
```

Supported image formats:

```text
JPG
JPEG
PNG
```

Text documents are parsed by the Java backend and their extracted content is sent to the local Python text-indexing API.

Images are routed to the image-indexing API.

Example:

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar index report.pdf notes.txt image.png
```

Example response structure:

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

Files that do not exist, use unsupported formats, or cannot be processed may appear in the `skipped` collection.

---

## 3.3 Search Command

Syntax:

```text
java -jar backend-1.0-SNAPSHOT.jar search <query> <topK>
```

Example:

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar search "software engineering" 5
```

The search operation performs semantic retrieval across both indexed text documents and images.

Example result:

```json
[
  {
    "id": "record-id",
    "fileName": "example.txt",
    "filePath": "C:\\documents\\example.txt",
    "fileType": "txt",
    "contentType": "text",
    "score": 0.42
  }
]
```

The score represents:

- BERT cosine similarity for text results
- Calibrated MobileCLIP cosine similarity for image results

---

## 3.4 List Command

Syntax:

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar list
```

Returns files currently indexed in the local retrieval database.

Long text documents may contain multiple stored chunks, but the listing is aggregated to file level so that one source document appears once.

---

## 3.5 Delete Command

Syntax:

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar delete <file-id>
```

Deletes the indexed representation of the selected file from ChromaDB.

For chunked text documents, all records associated with the original file identifier are removed.

Deleting an index entry does not delete the original source file from the Windows file system.

---

# 4. Local Retrieval Service

The local retrieval service is implemented using FastAPI.

Base address:

```text
http://127.0.0.1:8765
```

Main endpoints:

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/health` | Check service, database, and model status |
| GET | `/files` | List indexed files |
| POST | `/index-text` | Index extracted document text |
| POST | `/index-image` | Index a local image |
| POST | `/search` | Perform multimodal semantic search |
| POST | `/delete` | Remove indexed content |

The service is bound to the local loopback interface and is not intended to operate as a public network API.

---

# 5. GET /health

Checks whether the local retrieval service is running and whether the main machine-learning components have loaded.

## Request

```http
GET /health
```

## Example Response

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

### `status`

Indicates service availability.

Typical value:

```text
ok
```

### `text_records`

Number of vector records currently stored in the text ChromaDB collection.

Because long documents are chunked, this value may exceed the number of original source files.

### `image_records`

Number of indexed image records.

### `bert_loaded`

Indicates whether the BERT text embedding model has loaded successfully.

### `mobileclip_loaded`

Indicates whether the MobileCLIP model has loaded successfully.

---

# 6. GET /files

Returns files indexed in the local vector database.

## Request

```http
GET /files
```

## Example Response

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

For text documents divided into multiple chunks, `/files` returns one logical file-level result.

The `exists` field indicates whether the original local source file is currently available at the stored path.

---

# 7. POST /index-text

Indexes textual content extracted from a TXT, PDF, or DOCX file.

## Request

```http
POST /index-text
Content-Type: application/json
```

Example body:

```json
{
  "file_path": "C:\\documents\\report.pdf",
  "content": "Extracted document text..."
}
```

## Validation

The endpoint validates:

- File existence
- Non-empty extracted content
- Supported file extension

Supported extensions:

```text
txt
pdf
docx
```

Unsupported files may return HTTP 400.

Missing source files may return HTTP 404.

---

## 7.1 Long-Document Chunking

Long textual content is divided using:

```text
CHUNK_SIZE = 400 words
CHUNK_OVERLAP = 50 words
```

Each chunk is embedded independently using BERT.

Chunk identifiers follow the pattern:

```text
<file_id>_chunk_<index>
```

Chunk metadata includes information such as:

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

---

## 7.2 Example Response

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

# 8. POST /index-image

Indexes a local image using MobileCLIP.

## Request

```http
POST /index-image
Content-Type: application/json
```

Example body:

```json
{
  "file_path": "C:\\pictures\\example.png"
}
```

Supported formats:

```text
jpg
jpeg
png
```

## Processing Flow

The endpoint:

1. Verifies that the file exists.
2. Validates the image extension.
3. Loads and processes the image.
4. Generates a MobileCLIP image embedding.
5. Builds file metadata.
6. Stores the embedding in the image ChromaDB collection.

## Example Response

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

# 9. POST /search

Performs semantic retrieval across indexed text documents and images.

## Request

```http
POST /search
Content-Type: application/json
```

Example body:

```json
{
  "query": "red image",
  "top_k": 5
}
```

## Validation

An empty query is invalid.

`top_k` is constrained to a minimum useful value.

---

## 9.1 Text Search Flow

Text search performs:

1. BERT query embedding
2. ChromaDB cosine-similarity search
3. Retrieval of chunk-level candidates
4. File-level aggregation
5. Selection of the highest-scoring chunk for each source file

The candidate count is derived from:

```text
chunk_search_k = top_k × CHUNK_SEARCH_MULTIPLIER
```

Current configuration:

```text
CHUNK_SEARCH_MULTIPLIER = 5
```

---

## 9.2 Image Search Flow

If indexed image records are available:

1. MobileCLIP converts the natural-language query into a text embedding.
2. The image ChromaDB collection is searched.
3. Cosine-similarity scores are obtained.
4. Image scores are calibrated before multimodal fusion.

Current calibration:

```text
IMAGE_SCORE_CALIBRATION = 1.25
```

Conceptually:

```text
calibrated_score =
raw_mobileclip_similarity × 1.25
```

---

## 9.3 Multimodal Fusion

Before final ranking:

- Text chunks are aggregated to file level.
- Image similarity scores are calibrated.
- Text and image results are combined.
- Results are sorted by descending score.
- The requested top-K results are returned.

---

## 9.4 Example Response

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

The Java bridge may expose only the fields required by the desktop application.

---

# 10. POST /delete

Deletes an indexed file.

## Request

```http
POST /delete
Content-Type: application/json
```

Example body:

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
the image record matching the requested id
```

is removed.

## Example Response

```json
{
  "status": "ok",
  "id": "file-id"
}
```

The operation affects the local index only.

The original user file is not deleted from the Windows file system.

---

# 11. ChromaStore API

The Python storage layer uses:

```python
ChromaStore
```

The runtime database is stored under a local ChromaDB directory generated by the application.

Two persistent collections are used:

```text
offline_retriever_text
offline_retriever_images
```

Both collections use cosine similarity.

---

## 11.1 `add_text_file`

```python
add_text_file(
    record_id: str,
    embedding: list[float],
    metadata: dict[str, Any],
    document: str = "",
) -> None
```

Stores or updates one text vector record.

Chunked documents use a different `record_id` for each chunk.

---

## 11.2 `add_image_file`

```python
add_image_file(
    file_id: str,
    embedding: list[float],
    metadata: dict[str, Any],
) -> None
```

Stores or updates one image vector record.

---

## 11.3 `search_text`

```python
search_text(
    query_embedding: list[float],
    top_k: int = 5,
) -> dict
```

Queries the text ChromaDB collection.

Returned ChromaDB data includes structures such as:

```text
ids
metadatas
distances
```

---

## 11.4 `search_images`

```python
search_images(
    query_embedding: list[float],
    top_k: int = 5,
) -> dict
```

Queries the image ChromaDB collection using a MobileCLIP-compatible query embedding.

---

## 11.5 `delete_file`

```python
delete_file(
    file_id: str,
) -> None
```

Deletes text chunks associated with the supplied file identifier and removes a matching image record where applicable.

---

## 11.6 `get_all_files`

```python
get_all_files() -> list[dict]
```

Returns indexed files.

Text chunks are deduplicated using the original file identifier so that one source document appears once.

---

## 11.7 `text_count`

```python
text_count() -> int
```

Returns the number of text vector records stored in ChromaDB.

The count may exceed the number of source files because long documents may contain multiple chunks.

---

## 11.8 `image_count`

```python
image_count() -> int
```

Returns the number of indexed image records.

---

# 12. BERT Text Embedding API

The text retrieval pipeline uses a local BERT-based embedding engine.

Typical operation:

```python
embedding = text_engine.embed(text)
```

The output is a numerical embedding used for cosine-similarity retrieval.

Compatible BERT embedding logic must be used for both:

- Document chunks
- Search queries

This ensures indexed text and queries exist in the same semantic vector space.

---

# 13. MobileCLIP API

MobileCLIP provides image and text embeddings for image retrieval.

## 13.1 Image Embedding

Typical operation:

```python
image_engine.embed_image(file_path)
```

Generates a numerical embedding for a local image.

---

## 13.2 Text Embedding

Typical operation:

```python
image_engine.embed_text(query)
```

Generates a text embedding in the MobileCLIP multimodal embedding space.

This allows natural-language queries to retrieve semantically related images.

---

# 14. Java Parsing APIs

The Java parsing layer handles local document text extraction before BERT indexing.

## 14.1 Parser Interface

Conceptual interface:

```java
String parse(String filePath);
```

The parser abstraction provides a common entry point for supported text formats.

---

## 14.2 TextParser

Handles:

```text
.txt
```

and returns textual content from plain-text files.

---

## 14.3 DocumentParser

Handles supported document formats including:

```text
.pdf
.docx
```

and extracts textual content using the configured document-processing libraries.

---

## 14.4 ParserFactory

Typical routing:

| Extension | Processing Route |
|---|---|
| `.txt` | TextParser |
| `.pdf` | DocumentParser |
| `.docx` | DocumentParser |
| `.jpg` | Image indexing |
| `.jpeg` | Image indexing |
| `.png` | Image indexing |

Images are routed to MobileCLIP-based indexing rather than BERT text parsing.

---

# 15. File Metadata

Indexed file metadata includes fields such as:

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

Metadata is used for:

- Search-result presentation
- Opening original local files
- File-level aggregation
- Indexed-file listing
- Deleting all chunks belonging to one document
- Checking whether a source file still exists

---

# 16. File Identifier Generation

The retrieval system uses deterministic file identifiers derived from the normalized local file path.

Conceptually:

```text
SHA-256(normalized absolute file path)
```

For example:

```python
normalized_path = resolved_path.lower()
file_id = sha256(normalized_path)
```

This provides a stable identifier for a file at a particular local path.

Chunk identifiers follow:

```text
<file_id>_chunk_<index>
```

This allows all chunk records belonging to the same logical document to be grouped and deleted together.

---

# 17. Search Result Fields

A search result may include:

| Field | Description |
|---|---|
| `id` | Logical file identifier |
| `recordId` | Individual vector-record identifier |
| `fileName` | Source filename |
| `filePath` | Original local path |
| `fileType` | File extension/type |
| `contentType` | `text` or `image` |
| `chunkIndex` | Matching text chunk index, or image sentinel value |
| `rawScore` | Similarity before calibration |
| `score` | Final ranking score |

Not every layer is required to expose every internal field.

The Java bridge and Flutter frontend may use a simplified result representation.

---

# 18. Error Handling

API clients should account for errors including:

- Missing local files
- Unsupported extensions
- Empty document content
- Empty search queries
- Local service unavailable
- Model initialization failure
- ChromaDB access failure
- Invalid request data

HTTP error responses from the Python service should be handled by the Java bridge and presented to the frontend in a user-understandable way where appropriate.

---

# 19. Service Startup Considerations

The FastAPI service may take additional time during cold startup because it initializes:

- ChromaDB
- BERT
- MobileCLIP

Clients should not assume immediate availability after process startup.

The `/health` endpoint should be used to verify readiness before performing indexing or search operations.

Cold-start behaviour is an important integration consideration because model loading may take longer than normal API request processing.

---

# 20. API Compatibility Rules

When changing the local API:

1. Update the Python request or response model.
2. Update the Java client.
3. Rebuild the Java backend.
4. Restart the Python retrieval service.
5. Run integration tests.
6. Run CLI tests.
7. Run the Windows Flutter workflow.
8. Update this API reference.

Changes to field names, types, endpoint paths, or response structures should not be made independently in only one layer.

---

# 21. Offline and Security Considerations

The API is intended for local application use.

The service binds to:

```text
127.0.0.1
```

rather than a public network interface.

Normal operations include:

- Local parsing
- Local embedding inference
- Local vector storage
- Local semantic search
- Local result ranking

The API should not introduce unnecessary remote network dependencies.

User files, paths, extracted content, embeddings, and database records should be treated as local application data.

---

# 22. Platform Scope

The final application release targets Windows Desktop.

The API architecture itself is based on local Java/Python communication, but the final project was functionally validated on Windows.

Linux and macOS runtime behaviour are outside the validated final release scope.

---

# 23. Related Documentation

Additional implementation details are available in:

```text
docs/System_Architecture_Design.md
docs/Testing_Report.md
docs/Maintenance_Guide.md
docs/End_User_Manual.md
docs/Accessibility_User_Guide.md
docs/Open_Source_Compliance_Report.md
```

The project root `README.md` contains the primary project overview and quick-start instructions.

---

# 24. Conclusion

The final API architecture connects the Flutter Windows desktop interface, Java backend, local FastAPI retrieval service, BERT, MobileCLIP, and ChromaDB.

The primary local API supports:

```text
health checking
file listing
text indexing
image indexing
multimodal search
indexed-file deletion
```

The Java CLI provides a second interface for:

```text
index
search
list
delete
```

Together, these interfaces provide the communication layer required for the final offline-first multimodal retrieval workflow.