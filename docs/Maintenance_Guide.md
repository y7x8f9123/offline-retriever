# Maintenance Guide

## 1. Overview

This document provides maintenance guidance for the Offline Accessible Multimodal Local Content Retrieval System.

It is intended for developers who need to build, test, troubleshoot, maintain, or extend the project.

The final system consists of:

- Flutter Windows desktop frontend
- Java backend and CLI bridge
- Local Python FastAPI retrieval service
- BERT text embedding model
- MobileCLIP image and text embedding model
- ChromaDB persistent vector storage

The supported file formats are:

```text
TXT
PDF
DOCX
JPG
JPEG
PNG
```

The application follows an offline-first architecture.

Once the required dependencies and machine-learning models are installed locally, normal indexing and retrieval can operate without a remote search or inference API.

---

## 2. Final Project Structure

The final public repository is organized around the following main directories:

```text
offline-retriever/
├── assets/
├── backend/
├── dataset/
├── docs/
├── frontend/
├── scripts/
├── .gitignore
├── LICENSE
└── README.md
```

### Main Components

- `backend/` – Java application logic, file parsing, metadata processing, CLI integration, and backend tests.
- `frontend/` – Flutter Windows desktop user interface and frontend tests.
- `scripts/` – Python retrieval service, machine-learning integration, and ChromaDB operations.
- `docs/` – project documentation.
- `dataset/` – dataset documentation and validation resources.
- `assets/` – project assets.

Runtime-generated data such as ChromaDB storage and temporary test files should not normally be committed to Git.

Downloaded model resources should also be reviewed before inclusion because they may be large and may have separate redistribution requirements.

---

## 3. System Architecture

The final application flow is:

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

### Flutter Responsibilities

The Flutter frontend handles:

- File selection
- Search input
- Indexed-file display
- Search-result presentation
- File opening
- User interaction
- Accessibility-related interface behaviour

### Java Responsibilities

The Java backend handles:

- File scanning
- TXT, PDF, and DOCX parsing
- Metadata processing
- CLI operations
- Communication with the local retrieval service

### Python Responsibilities

The Python service handles:

- BERT inference
- MobileCLIP inference
- Long-document chunking
- ChromaDB operations
- Semantic retrieval
- File-level result aggregation
- Multimodal ranking

---

## 4. Platform Scope

The final release targets:

```text
Windows Desktop
```

The project was developed, tested, and functionally validated on Windows.

Flutter may contain generated files for other desktop platforms, but Linux and macOS runtime validation are outside the final project release scope.

---

## 5. Development Environment

Before maintaining the project, verify that the required development tools are available.

Typical requirements include:

- Windows
- Java Development Kit
- Maven
- Python
- pip
- Flutter SDK
- Windows desktop development tools

Check Java:

```powershell
java -version
```

Check Maven:

```powershell
mvn -version
```

Check Python:

```powershell
python --version
```

Check Flutter:

```powershell
flutter doctor
```

Any missing dependencies reported by these commands should be resolved before troubleshooting application code.

---

## 6. Python Environment

The local retrieval service depends on components including:

- FastAPI
- Uvicorn
- ChromaDB
- PyTorch
- Transformers
- BERT model resources
- MobileCLIP model resources
- NumPy
- Pillow

Before changing Python dependencies:

1. Confirm the current environment works.
2. Record the existing dependency versions.
3. Make dependency changes separately from unrelated code changes.
4. Restart the retrieval service.
5. Verify BERT loading.
6. Verify MobileCLIP loading.
7. Test text indexing.
8. Test image indexing.
9. Test semantic retrieval.

Machine-learning dependencies can be large and may have platform-specific requirements.

---

## 7. Starting the Local Retrieval Service

The retrieval service is implemented using FastAPI and Uvicorn.

It listens on:

```text
127.0.0.1:8765
```

The service should remain bound to the loopback interface for normal local operation.

From the project root, start it using:

```powershell
python scripts\service\retrieval_server.py
```

During startup, the service initializes:

1. ChromaDB
2. BERT
3. MobileCLIP

Typical successful startup output includes messages similar to:

```text
Starting Offline Retriever backend...
Opening ChromaDB...
Loading BERT...
Loading MobileCLIP...
Offline Retriever backend ready.
```

The first startup may take additional time because machine-learning models must be loaded into memory.

A slow cold start should not immediately be treated as service failure.

---

## 8. Retrieval Service Health Check

After starting the service, verify it before testing Java or Flutter integration.

On Windows PowerShell:

```powershell
Invoke-RestMethod http://127.0.0.1:8765/health
```

A healthy service should confirm that the service is available and that the required retrieval components have loaded.

Important conditions include:

```text
status = ok
bert_loaded = True
mobileclip_loaded = True
```

Record counts represent stored vector records and may not equal the number of original files because long documents can generate multiple chunks.

---

## 9. Retrieval Service Endpoints

The main local endpoints are:

```text
GET  /health
GET  /files
POST /index-text
POST /index-image
POST /search
POST /delete
```

When changing an endpoint:

- Keep request and response formats compatible with Java where possible.
- Update the Java bridge if the API changes.
- Update `API_Reference.md`.
- Restart the Python service.
- Rebuild the Java backend if required.
- Run integration tests.
- Run a manual end-to-end test.

Because the Python process remains running, source-code changes may not affect an already-running service unless reload functionality is enabled.

Restart the service after important changes.

---

## 10. ChromaDB Maintenance

ChromaDB is used for persistent local vector storage.

The system uses two main collections:

```text
offline_retriever_text
offline_retriever_images
```

The text collection stores BERT-based text embeddings.

The image collection stores MobileCLIP image embeddings.

Both are used for cosine-similarity retrieval.

### Important Rule

Do not manually edit ChromaDB internal database files.

Use application APIs or ChromaDB operations to modify indexed records.

### Compatibility

Existing vectors may become incompatible if developers change:

- BERT model
- MobileCLIP model
- Embedding dimensions
- Embedding preprocessing
- Similarity or distance configuration

After such changes, old vector data may need to be cleared and indexed again.

---

## 11. Runtime Database Data

Runtime ChromaDB data is generated during application use.

It may contain:

- Vector embeddings
- File metadata
- Local file paths
- Information derived from indexed files

Development ChromaDB databases should not normally be committed to a public repository.

When preparing a clean release, verify that user-generated or development database files are excluded.

---

## 12. Long-Document Chunking

Long text documents are divided into overlapping chunks before embedding.

Current configuration:

```text
CHUNK_SIZE = 400
CHUNK_OVERLAP = 50
```

The overlap helps retain context around chunk boundaries.

Chunk metadata includes information such as:

```text
fileId
chunkIndex
chunkCount
```

### Maintenance Considerations

Increasing chunk size may:

- Reduce the number of vectors
- Reduce storage overhead
- Reduce indexing overhead
- Reduce retrieval granularity

Decreasing chunk size may:

- Increase the number of vectors
- Increase storage requirements
- Increase indexing time
- Improve fine-grained retrieval

Do not change chunking parameters without re-running retrieval and performance tests.

---

## 13. File-Level Aggregation

A long document may create multiple vector records.

Search results should still return the source file as a single result rather than displaying repeated chunks.

Text search therefore performs file-level aggregation.

Records are grouped using:

```text
fileId
```

The highest-scoring relevant chunk is used to represent the source file.

After modifying aggregation logic, verify that:

- A document does not appear repeatedly.
- The most relevant chunk determines ranking.
- Deletion removes all chunks associated with the file.
- `/files` returns one logical entry per source file.

---

## 14. Multimodal Ranking

Text and image retrieval use different embedding models:

```text
Text  → BERT
Image → MobileCLIP
```

Their raw cosine similarity score distributions are different.

The current implementation applies image-score calibration.

Current value:

```text
IMAGE_SCORE_CALIBRATION = 1.25
```

Conceptually:

```text
text final score =
BERT cosine similarity

image final score =
MobileCLIP cosine similarity × 1.25
```

Do not change the calibration factor only to force image results above document results.

Any change should be evaluated using:

- Text-oriented queries
- Image-oriented queries
- General semantic queries
- Irrelevant queries

After changing the calibration value, update relevant documentation and testing records.

---

## 15. ChromaDB Storage Component

The main Python storage component is located under:

```text
scripts/storage/
```

Its responsibilities include:

- Adding text records
- Adding image records
- Searching text embeddings
- Searching image embeddings
- Deleting indexed files
- Listing indexed files
- Counting records

Changes to storage code can affect persistent data and should be tested carefully.

---

## 16. BERT Maintenance

BERT provides semantic embeddings for:

- Text document chunks
- User text queries

Indexed text and search queries must use compatible embedding logic.

If the BERT model or preprocessing changes:

1. Verify model loading.
2. Verify embedding dimensions.
3. Verify query embedding generation.
4. Clear incompatible vectors if required.
5. Re-index test documents.
6. Run semantic retrieval tests.
7. Run long-document tests.
8. Run performance tests.
9. Verify offline operation.

Embeddings from incompatible models should not be mixed in the same collection.

---

## 17. MobileCLIP Maintenance

MobileCLIP provides embeddings for:

- Local images
- Text queries used for image retrieval

This enables text-to-image semantic search.

If MobileCLIP changes:

1. Verify image preprocessing.
2. Verify image embedding generation.
3. Verify text embedding generation.
4. Confirm image and text embeddings remain in the same compatible embedding space.
5. Re-index images if required.
6. Run multimodal retrieval tests.
7. Review the image-score calibration factor.

Successful model loading alone does not confirm retrieval correctness.

Semantic queries should also be tested.

---

## 18. Java Backend Maintenance

The Java backend is located under:

```text
backend/src/main/java/com/offlineretriever/
```

Its main responsibilities include:

- File scanning
- File parsing
- Metadata handling
- Retrieval-service communication
- CLI commands
- Application orchestration

The final application retrieval flow is:

```text
Java Backend
     ↓
FastAPI Service
     ↓
BERT / MobileCLIP
     ↓
ChromaDB
```

Changes to Java request or response handling should always be tested against the running Python retrieval service.

---

## 19. Building the Java Backend

From the project root:

```powershell
cd backend
```

Compile:

```powershell
mvn clean compile
```

Run tests:

```powershell
mvn test
```

Package the executable JAR:

```powershell
mvn clean package -DskipTests
```

The expected JAR is:

```text
backend/target/backend-1.0-SNAPSHOT.jar
```

After modifying Java code used by the CLI or frontend integration, rebuild the JAR before end-to-end testing.

---

## 20. Java CLI

The CLI supports:

```text
index
search
list
delete
```

### Index

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar index example.txt
```

Multiple files:

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar index file1.txt file2.pdf image.png
```

### Search

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar search "software engineering" 5
```

### List

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar list
```

### Delete

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar delete <file-id>
```

The local FastAPI retrieval service must be available for retrieval operations.

---

## 21. CLI Output

CLI operations may return machine-readable JSON.

For example:

```json
[
  {
    "id": "example-id",
    "fileName": "example.txt",
    "filePath": "C:\\documents\\example.txt",
    "fileType": "txt",
    "contentType": "text",
    "score": 0.42
  }
]
```

Avoid adding arbitrary debugging text to standard output where another component expects JSON.

Diagnostic messages should be separated from structured output where possible.

---

## 22. File Parsing Maintenance

Supported text formats are:

```text
TXT
PDF
DOCX
```

Supported image formats are:

```text
JPG
JPEG
PNG
```

When adding a new text format:

1. Add parser support.
2. Update parser selection logic.
3. Test extraction.
4. Update frontend file selection.
5. Test indexing.
6. Test search.
7. Test result opening.
8. Update documentation.

When adding a new image format:

1. Verify that MobileCLIP can process it.
2. Update extension validation.
3. Update Java routing if required.
4. Update frontend file selection.
5. Test indexing.
6. Test semantic retrieval.
7. Update documentation.

A file type should not be described as supported until the complete workflow has been validated.

---

## 23. Adding New File Types

Before documenting a new file type as supported, verify:

```text
Frontend selection
       ↓
Java recognition
       ↓
Parsing / image routing
       ↓
Embedding
       ↓
ChromaDB storage
       ↓
Search
       ↓
Result display
       ↓
File opening
```

Support in only one layer is not complete application support.

---

## 24. Backend Testing

Java backend tests are located under:

```text
backend/src/test/java/com/offlineretriever/
```

Run:

```powershell
cd backend
mvn test
```

After modifying core functionality, run the complete backend test suite rather than only a single affected test.

A successful final run should complete with:

```text
BUILD SUCCESS
```

Integration tests involving the retrieval service require the local FastAPI service and required model resources to be available.

---

## 25. JaCoCo Coverage

JaCoCo is used for Java backend code-coverage measurement.

Generate the report using:

```powershell
mvn clean test jacoco:report
```

The report is generated under:

```text
backend/target/site/jacoco/
```

Final measured backend coverage includes:

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

The lower overall percentage is influenced by application entry points, CLI orchestration, integration-oriented code, and service-management paths.

Core reusable functional modules have substantially higher coverage.

Coverage should be interpreted together with functional, integration, and end-to-end testing rather than as a single isolated percentage.

Do not increase coverage artificially by removing meaningful code or creating tests that do not validate useful behaviour.

---

## 26. Flutter Maintenance

The Flutter project is located under:

```text
frontend/
```

Install dependencies:

```powershell
cd frontend
flutter pub get
```

Run the Windows application:

```powershell
flutter run -d windows
```

Run Flutter tests:

```powershell
flutter test
```

When modifying frontend code, verify:

- File selection
- Search input
- Empty-query handling
- Search-result display
- Indexed-file management
- Settings
- Keyboard navigation
- High Contrast Mode
- Font scaling

The final validated frontend target is Windows.

---

## 27. Accessibility Maintenance

The application includes accessibility-focused interface functionality.

Implemented features include:

- Keyboard navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic accessibility labels

When modifying Flutter UI components:

1. Confirm that keyboard navigation still works.
2. Check that interactive controls remain reachable.
3. Preserve semantic labels.
4. Test standard and High Contrast modes.
5. Test all supported font sizes.
6. Verify that enlarged text does not hide critical controls.

The project uses WCAG 2.1 AA as an accessibility design objective.

Do not claim formal accessibility certification without appropriate formal validation.

---

## 28. Opening Local Files

Search results may allow users to open the original indexed file.

The system relies on the stored local path.

If a file is:

- Moved
- Renamed
- Deleted

the stored path may become invalid.

If file-location handling is modified, test all supported formats and ensure that the application does not delete the user's original source file when removing an index record.

---

## 29. Indexed-File Deletion

Deleting a file from the application index should remove its associated ChromaDB records.

For long documents, all chunks belonging to the same logical file must be removed.

Deletion of an indexed record must not delete the original local source file.

After modifying deletion logic, test:

- Text-file deletion
- Long-document deletion
- Image deletion
- `/files` output after deletion
- Search results after deletion

---

## 30. Performance Testing

The retrieval pipeline was stress-tested using 1,000 generated TXT files.

Observed indexing data included:

```text
Text records before test: 12
Text records after test: 1012
Stress-test files confirmed: 1000
```

Measured batches included:

| Batch | Time |
|---:|---:|
| 200 files | 14.81 s |
| 300 files | 25.94 s |
| 450 files | 41.72 s |

The initial 50-file batch was used for functional validation and was not timed.

With more than 1,000 text records stored, one end-to-end semantic search completed in approximately:

```text
807 ms
```

for the query:

```text
software engineering
```

These results were measured in the local development environment and should not be treated as hardware-independent production benchmarks.

After major changes to chunking, embeddings, database storage, or ranking logic, re-run performance testing.

---

## 31. Cold Startup Behaviour

Cold startup may take more than 30 seconds because the retrieval service must initialize:

- ChromaDB
- BERT
- MobileCLIP

A Java-side startup timeout may expire before the Python service is fully ready.

If this occurs:

1. Check whether the Python service is still loading models.
2. Wait for initialization to complete.
3. Verify `/health`.
4. Retry the Java or Flutter operation.

Possible future improvements include:

- Longer startup timeout
- Model-loading progress display
- Better service lifecycle management
- Clearer distinction between slow startup and actual failure

---

## 32. Offline-First Behaviour

Normal retrieval operations are performed locally.

These include:

- File parsing
- Metadata extraction
- BERT inference
- MobileCLIP inference
- Vector storage
- Semantic retrieval
- Result ranking

Initial dependency installation or model download may require Internet access.

After setup, normal retrieval should not require a remote semantic-search or inference API.

When adding dependencies, review them for:

- Telemetry
- Remote inference
- Automatic uploads
- Cloud API calls
- Analytics
- Background network requests

---

## 33. Security and Privacy Maintenance

The repository and release package should not contain:

- API keys
- Passwords
- Access tokens
- Private credentials
- Authentication cookies
- Private certificates
- Private user documents
- User-generated ChromaDB databases

Before preparing a public release:

1. Review Git status.
2. Review generated files.
3. Review runtime database directories.
4. Search for secrets.
5. Confirm that development test data does not contain sensitive content.

---

## 34. Dependency Maintenance

The project uses dependencies from three major ecosystems:

```text
Maven
Flutter / pub
Python / pip
```

Useful commands include:

### Java

```powershell
mvn dependency:tree
```

### Flutter

```powershell
flutter pub deps --style=compact
```

### Python

```powershell
pip freeze
```

When updating dependencies:

1. Record existing versions.
2. Update one ecosystem at a time where possible.
3. Rebuild the project.
4. Run unit tests.
5. Run integration tests.
6. Run manual end-to-end testing.
7. Verify offline behaviour.
8. Review licensing implications.
9. Update documentation if behaviour changes.

---

## 35. Documentation Maintenance

Important project documentation includes:

```text
README.md
docs/System_Architecture_Design.md
docs/API_Reference.md
docs/Testing_Report.md
docs/Maintenance_Guide.md
docs/End_User_Manual.md
docs/Accessibility_User_Guide.md
docs/Open_Source_Compliance_Report.md
docs/Environment_Setup_Report.md
docs/Risk_Management_Plan.md
docs/PRD.md
docs/Demo_Script.md
```

Documentation should be updated when changes affect:

- Architecture
- Supported file types
- API endpoints
- Models
- Vector storage
- Chunking parameters
- Ranking parameters
- Setup instructions
- Testing results
- Accessibility behaviour
- Release scope

Documentation should describe the actual implementation rather than planned functionality.

---

## 36. Recommended Change Workflow

For significant maintenance changes:

```text
Create change
     ↓
Build affected component
     ↓
Run unit tests
     ↓
Start local retrieval service
     ↓
Run integration tests
     ↓
Run manual end-to-end test
     ↓
Review performance if relevant
     ↓
Update documentation
     ↓
Commit changes
```

Avoid combining unrelated dependency changes, architecture changes, and feature changes in a single maintenance step where possible.

---

## 37. Final Maintenance Checklist

Before considering a major change complete, confirm:

- The project builds successfully.
- Backend tests pass.
- Required integration tests pass.
- Flutter tests pass where applicable.
- The FastAPI service starts successfully.
- BERT loads successfully.
- MobileCLIP loads successfully.
- ChromaDB remains accessible.
- Text indexing works.
- Image indexing works.
- Semantic search works.
- File listing works.
- File deletion works.
- Windows frontend behaviour remains functional.
- Accessibility-related UI behaviour remains functional.
- Documentation reflects the updated implementation.
- No temporary development data has been unintentionally committed.

---

## 38. Conclusion

The final project uses a modular local architecture consisting of a Flutter Windows frontend, Java backend, Python retrieval service, BERT, MobileCLIP, and ChromaDB.

The most important maintenance principle is to preserve compatibility across these layers.

Changes to parsing, embedding models, API structures, vector storage, chunking, or ranking can affect multiple components and should therefore be validated through both automated and end-to-end testing.

By maintaining synchronized code, tests, runtime configuration, and documentation, the system can continue to be extended while preserving its offline-first multimodal retrieval workflow.