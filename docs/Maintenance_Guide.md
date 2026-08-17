# Maintenance Guide

## 1. Overview

This document provides maintenance guidance for the Offline Accessible Multimodal Local Content Retrieval System.

It is intended for developers who need to build, test, troubleshoot, maintain, or extend the project.

The current system consists of:

- Flutter desktop frontend
- Java backend and CLI bridge
- Local Python FastAPI retrieval service
- BERT text embedding model
- MobileCLIP image/text embedding model
- ChromaDB persistent vector storage

The current retrieval workflow supports:

- TXT
- PDF
- DOCX
- JPG
- JPEG
- PNG

The application follows an offline-first architecture. Once the required dependencies and machine-learning models are installed locally, normal indexing and retrieval can operate without external network access.

---

## 2. Project Structure

The main project directories are:

```text
shixi/
├── assets/
├── backend/
├── chroma_db/
├── dataset/
├── docs/
├── frontend/
├── models/
├── scripts/
├── tests/
├── .gitignore
└── README.md
```

### Main Components

- `backend/` – Java application logic, parsing, CLI integration, and tests.
- `frontend/` – Flutter user interface and frontend tests.
- `scripts/` – Python embedding, storage, and retrieval-service components.
- `chroma_db/` – local persistent vector database generated at runtime.
- `docs/` – project documentation.
- `models/` – local model resources where applicable.
- `dataset/` – development and validation datasets.
- `tests/` – additional testing resources.
- `assets/` – project assets.

Runtime data such as ChromaDB files and downloaded model files should not normally be committed to Git.

---

## 3. Current System Architecture

The main application flow is:

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
       |                   |
       +---------+---------+
                 |
                 v
              ChromaDB
```

The responsibilities are separated as follows:

### Flutter

Handles:

- File selection
- Search input
- Indexed-file display
- Search-result display
- File opening
- User interaction
- Accessibility-related UI behavior

### Java

Handles:

- File scanning
- TXT/PDF/DOCX parsing
- Metadata processing
- CLI commands
- Communication with the local retrieval service

### Python

Handles:

- BERT inference
- MobileCLIP inference
- Long-document chunking
- ChromaDB operations
- Semantic retrieval
- File-level result aggregation
- Multimodal ranking

---

## 4. Development Environment

Before working on the project, verify that the required development tools are available.

Typical requirements include:

- Java
- Maven
- Python
- pip
- Flutter
- Windows desktop development tools when building the Windows version

Check Java:

```bash
java -version
```

Check Maven:

```bash
mvn -version
```

Check Python:

```bash
python --version
```

Check Flutter:

```bash
flutter doctor
```

Any missing dependencies reported by these commands should be resolved before troubleshooting application code.

---

## 5. Python Environment

The Python retrieval service requires the dependencies used by:

- FastAPI
- Uvicorn
- ChromaDB
- BERT
- MobileCLIP
- PyTorch and related model libraries

Developers should use the project's configured Python environment when running the retrieval service.

Before changing Python dependencies:

1. Confirm the current environment works.
2. Record the existing dependency versions.
3. Make dependency changes separately from unrelated code changes.
4. Restart the retrieval service.
5. Test both BERT and MobileCLIP loading.
6. Test text and image retrieval.

Machine-learning dependencies can be large and may have platform-specific requirements.

---

## 6. Starting the Local Retrieval Service

The retrieval service is implemented with FastAPI and Uvicorn.

The service listens on:

```text
127.0.0.1:8765
```

It should remain bound to the loopback interface for normal local operation.

Start the service using the project's Python environment and retrieval server script.

For example, from the project root:

```powershell
python scripts\service\retrieval_server.py
```

During startup, the service initializes:

1. ChromaDB
2. BERT
3. MobileCLIP

Typical startup output includes messages similar to:

```text
Starting Offline Retriever backend...
Opening ChromaDB...
Loading BERT...
Loading MobileCLIP...
Offline Retriever backend ready.
```

The first startup may take longer because machine-learning models must be loaded into memory.

Do not assume the service has failed simply because model initialization takes several seconds.

---

## 7. Checking Retrieval Service Health

After starting the service, verify it before testing Java or Flutter integration.

On Windows PowerShell:

```powershell
Invoke-RestMethod http://127.0.0.1:8765/health
```

A healthy service returns information similar to:

```text
status            : ok
text_records      : 12
image_records     : 2
bert_loaded       : True
mobileclip_loaded : True
```

Verify:

```text
status = ok
bert_loaded = True
mobileclip_loaded = True
```

`text_records` represents vector records rather than strictly source-file count because long documents may contain multiple chunks.

---

## 8. Python Retrieval Service Endpoints

The main local endpoints are:

```text
GET  /health
GET  /files
POST /index-text
POST /index-image
POST /search
POST /delete
```

When modifying the service:

- Keep existing request and response structures compatible with Java where possible.
- Update the Java client when endpoint structures change.
- Update `API_Reference.md`.
- Re-run integration tests.
- Restart the service before testing new Python code.

Because the Python process remains running, editing a source file does not necessarily update the already-running service unless reload functionality is explicitly enabled.

For reliable testing, restart the service after important changes.

---

## 9. ChromaDB Maintenance

Persistent vector storage is implemented using ChromaDB.

The database is stored under:

```text
chroma_db/
```

Two collections are currently used:

```text
offline_retriever_text
offline_retriever_images
```

The text collection stores BERT embeddings.

The image collection stores MobileCLIP embeddings.

Both collections use cosine similarity.

### Important Rule

Do not manually edit ChromaDB internal database files.

Use application APIs or ChromaDB operations to modify indexed records.

### Database Compatibility

Existing vectors may become invalid if developers change:

- BERT model
- MobileCLIP model
- Embedding dimension
- Embedding preprocessing
- Distance metric

After such changes, existing data may need to be cleared and re-indexed.

---

## 10. Long-Document Chunking

Long text documents are divided into overlapping chunks before embedding.

Current configuration:

```text
CHUNK_SIZE = 400
CHUNK_OVERLAP = 50
```

These values are defined in the retrieval-service implementation.

The overlap helps preserve context around chunk boundaries.

Each chunk contains metadata including:

```text
fileId
chunkIndex
chunkCount
```

### Maintenance Considerations

Increasing chunk size may:

- Reduce the number of vectors
- Reduce indexing overhead
- Increase the amount of text processed per embedding
- Potentially lose fine-grained retrieval accuracy

Decreasing chunk size may:

- Increase the number of vectors
- Increase storage requirements
- Increase indexing time
- Improve retrieval granularity

Do not change chunking parameters without re-running retrieval and performance tests.

---

## 11. File-Level Aggregation

A long document may create multiple ChromaDB records.

Search results should nevertheless return the source file rather than multiple copies of the same document.

Text search therefore performs file-level aggregation.

Records are grouped using:

```text
fileId
```

The highest-scoring matching chunk is used to represent the source file.

When modifying this logic, verify that:

- One document does not appear repeatedly in search results.
- The most relevant chunk determines file ranking.
- Deletion removes all chunks belonging to the file.
- `/files` returns one entry per source file.

---

## 12. Multimodal Ranking

Text and image retrieval use different models:

```text
Text  → BERT
Image → MobileCLIP
```

Their raw similarity distributions are different.

The current implementation therefore applies image-score calibration.

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

### Maintenance Rule

Do not increase the calibration factor simply to force images above text results.

Changes should be based on retrieval tests using several query types, including:

- Image-oriented queries
- Text-oriented queries
- General semantic queries
- Irrelevant queries

After changing calibration, record the new value and update:

- API Reference
- Architecture documentation
- Maintenance Guide
- Relevant test records

---

## 13. ChromaDB Storage API

The main Python storage component is:

```text
scripts/storage/chroma_store.py
```

Its responsibilities include:

- Adding text records
- Adding image records
- Searching text embeddings
- Searching image embeddings
- Deleting files
- Listing indexed files
- Counting records

Important operations include:

```text
add_text_file()
add_image_file()
search_text()
search_images()
delete_file()
get_all_files()
text_count()
image_count()
```

Changes to this file can affect persistent indexed data and should be tested carefully.

---

## 14. BERT Maintenance

BERT provides semantic embeddings for:

- Text document chunks
- User text queries

The same embedding implementation must be used for both indexed documents and queries.

If the BERT model is changed:

1. Verify model loading.
2. Verify embedding dimensions.
3. Clear incompatible old vectors if necessary.
4. Re-index test documents.
5. Run semantic retrieval tests.
6. Run long-document tests.
7. Run performance tests.
8. Test offline operation.

Do not compare embeddings generated by incompatible models.

---

## 15. MobileCLIP Maintenance

MobileCLIP provides embeddings for:

- Local images
- Text queries used for image retrieval

This allows text-to-image semantic retrieval.

If the MobileCLIP implementation changes:

1. Verify image preprocessing.
2. Verify image embedding generation.
3. Verify text embedding generation.
4. Confirm both embeddings use the same MobileCLIP space.
5. Re-index images if required.
6. Re-run multimodal ranking tests.
7. Review the image calibration factor.

A successful model load does not by itself prove correct retrieval. Semantic queries should also be tested.

---

## 16. Java Backend Maintenance

The Java backend is located under:

```text
backend/src/main/java/com/offlineretriever/
```

Important responsibilities include:

- File scanning
- File parsing
- Metadata handling
- Local service communication
- CLI commands

Earlier Java-only vector retrieval components may remain in the repository for testing, demonstration, or historical implementation purposes.

However, the main application retrieval path now uses:

```text
Java
  ↓
FastAPI
  ↓
BERT / MobileCLIP
  ↓
ChromaDB
```

Developers should not accidentally reconnect the frontend to the old in-memory prototype retrieval path.

---

## 17. Building the Java Backend

The backend uses Maven.

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

After modifying Java code used by Flutter or the CLI, rebuild the JAR before integration testing.

---

## 18. Backend CLI

The current CLI supports:

```text
index
search
list
delete
```

From the project root:

### Index

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar index example.txt
```

Multiple files may be supplied:

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

The FastAPI service must be running for retrieval-service operations.

---

## 19. CLI Output

Successful CLI operations return machine-readable output.

Search output is JSON.

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

Avoid adding arbitrary debugging output to standard output if Flutter expects to decode the response as JSON.

Diagnostic messages should be separated from structured application output where possible.

---

## 20. File Parsing Maintenance

Text extraction is performed before BERT indexing.

Supported text formats currently include:

```text
TXT
PDF
DOCX
```

Supported image formats include:

```text
JPG
JPEG
PNG
```

When adding a new text document format:

1. Add or update parser support.
2. Update `ParserFactory`.
3. Test extraction.
4. Update frontend file selection.
5. Test indexing.
6. Test retrieval.
7. Update documentation.

When adding a new image format:

1. Verify MobileCLIP can load it correctly.
2. Update file-extension validation.
3. Update Java routing.
4. Update frontend file selection.
5. Test end-to-end indexing and retrieval.

Supporting a format in only one layer does not constitute complete application support.

---

## 21. Adding New File Types

Before describing a file type as supported, verify the complete flow:

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

A format should only be documented as supported after this complete workflow succeeds.

---

## 22. Backend Tests

Java backend tests are located under:

```text
backend/src/test/java/com/offlineretriever/
```

Run:

```powershell
cd backend
mvn test
```

After modifying core Java functionality, run the complete test suite rather than only the directly affected test.

A successful run should end with:

```text
BUILD SUCCESS
```

---

## 23. JaCoCo Coverage

JaCoCo is used for backend coverage measurement.

Generate the report using:

```powershell
mvn clean test jacoco:report
```

The report is generated under:

```text
backend/target/site/jacoco/
```

Current project testing has achieved approximately:

```text
Overall backend instruction coverage: 84%
Core functional modules: approximately 93–100%
Vector retrieval package: approximately 98%
```

Entry-point and demonstration classes are not the primary unit-testing targets and account for part of the uncovered code.

Coverage percentage should not be increased by removing meaningful code or writing tests with no useful behavioral validation.

---

## 24. Flutter Maintenance

The Flutter project is located under:

```text
frontend/
```

Install dependencies:

```powershell
cd frontend
flutter pub get
```

Check the development environment:

```powershell
flutter doctor
```

Run the Windows desktop application:

```powershell
flutter run -d windows
```

Run tests:

```powershell
flutter test
```

Generate frontend coverage:

```powershell
flutter test --coverage
```

---

## 25. Flutter File Selection

The frontend should remain synchronized with backend-supported formats.

Current supported formats are:

```text
txt
pdf
docx
jpg
jpeg
png
```

When changing supported formats, verify both:

- Flutter file-picker configuration
- Backend indexing support

The original local file path must be preserved because it is used for:

- Indexing
- Metadata
- File existence checks
- Opening results

---

## 26. Search Result Maintenance

Search results may contain both:

```text
text
image
```

content types.

The frontend should not assume that every result is a text document.

Important result fields include:

```text
id
fileName
filePath
fileType
contentType
score
```

Additional fields may be returned by the retrieval service, such as:

```text
recordId
chunkIndex
rawScore
```

The frontend should only depend on fields required for its UI behavior.

---

## 27. File Opening

Retrieved files are opened using the operating system's associated application.

Before opening a file, the application should verify that the original path still exists.

The indexed vector database does not contain a complete copy of the source file.

Therefore:

```text
Deleting or moving the original file
```

may cause file-opening operations to fail even though an old vector record remains indexed.

The `/files` endpoint provides an `exists` field to help detect this condition.

---

## 28. Frontend Testing

Important frontend workflows to test include:

- File import
- TXT selection
- PDF selection
- DOCX selection
- Image selection
- Search input
- Empty-query validation
- Search result display
- Text result display
- Image result display
- File opening
- Missing-file behavior
- Keyboard navigation
- Accessibility labels

When data models change, update widget-test mock objects accordingly.

---

## 29. Accessibility Maintenance

Accessibility is a core project requirement.

UI changes should preserve:

- Keyboard-accessible interaction
- Semantic labels
- Screen-reader compatibility
- Readable text
- Appropriate text scaling
- Sufficient visual contrast
- Clear status messages
- Understandable navigation
- Clearly identified controls

Accessibility testing should be repeated after significant UI changes.

The project targets WCAG 2.1 AA principles where applicable.

---

## 30. Offline-First Requirements

Normal retrieval operations should remain local.

The following data should not be sent to external services during normal application use:

- User files
- Extracted document content
- Search queries
- Embeddings
- Indexed metadata

The FastAPI service is intentionally bound to:

```text
127.0.0.1
```

rather than an externally accessible host.

Model download or dependency installation may require Internet access during initial setup.

After required models are locally available, normal retrieval should work without network access.

---

## 31. Offline Validation

After dependency and model setup, offline behavior should be tested periodically.

A recommended test is:

1. Start the system while required models are available locally.
2. Disable Internet access.
3. Start or restart the retrieval service.
4. Verify `/health`.
5. Index a local text file.
6. Perform semantic search.
7. Index an image.
8. Perform image retrieval.

The application should not attempt to contact cloud retrieval APIs during this workflow.

---

## 32. Model Portability

A development computer may already contain cached BERT or MobileCLIP model files.

This can hide setup requirements when the project is moved to another machine.

Before release, document:

- Required Python packages
- Required model names
- Model download procedure
- Local model/cache location where applicable
- Offline startup requirements

Do not claim that a fresh Git clone can immediately operate offline unless all required model resources are included or installed by the setup process.

---

## 33. Performance and Stress Testing

The project requirement includes indexing at least 1,000 local files.

A stress test has been completed using 1,000 generated TXT files.

Observed text-record count:

```text
Before: 12
After:  1012
```

The indexed-file listing independently confirmed:

```text
1000 stress-test files
```

Measured indexing batches included:

```text
200 files → 14.81 seconds
300 files → 25.94 seconds
450 files → 41.72 seconds
```

The initial 50-file batch was used for functional validation and was not timed.

End-to-end semantic search with more than 1,000 text records completed in approximately:

```text
807 ms
```

for:

```text
software engineering
```

Performance tests should be repeated after major changes to:

- BERT model
- Chunking
- ChromaDB configuration
- Retrieval logic
- Multimodal ranking

---

## 34. Stress-Test Data

Generated stress-test files are development artifacts and should not normally be committed to the repository.

For example:

```text
stress_test_files/
```

should remain local unless a specific small test fixture is intentionally required by automated testing.

Large generated test datasets can unnecessarily increase repository size.

The same rule applies to temporary manual test files such as experimental TXT or image files.

---

## 35. Common Problem: Retrieval Service Appears Frozen

During startup, the service may appear inactive while loading BERT or MobileCLIP.

Check the console output.

If startup has reached:

```text
Uvicorn running on http://127.0.0.1:8765
```

verify health using:

```powershell
Invoke-RestMethod http://127.0.0.1:8765/health
```

If both models are reported as loaded, the backend is ready.

---

## 36. Common Problem: Connection Refused

If Java reports that it cannot connect to:

```text
127.0.0.1:8765
```

verify that the Python retrieval service is running.

Check:

```powershell
Invoke-RestMethod http://127.0.0.1:8765/health
```

If this fails, troubleshoot the Python service before troubleshooting Java or Flutter.

---

## 37. Common Problem: Old Python Code Still Running

If Python source code has been changed but behavior has not changed:

1. Stop the retrieval service.
2. Confirm the old Python process has exited.
3. Restart the service.
4. Re-run `/health`.
5. Repeat the test.

A running Python process may still contain the previous implementation in memory.

---

## 38. Common Problem: Java Changes Do Not Appear

If Java source code has changed but CLI or Flutter behavior remains unchanged, rebuild the JAR:

```powershell
cd backend
mvn clean package -DskipTests
```

Then return to the project root and re-run the CLI.

Flutter may still be invoking an older packaged JAR if the backend was not rebuilt.

---

## 39. Common Problem: Search Quality Changes

If semantic ranking becomes unexpectedly poor, check:

- Correct BERT model loaded
- Correct MobileCLIP model loaded
- Existing vectors generated using compatible models
- Chunking configuration
- Image calibration factor
- ChromaDB cosine configuration
- Query preprocessing
- Whether old test records remain in the database

Use several controlled test files rather than evaluating search quality from only one query.

---

## 40. Common Problem: Duplicate Text Results

If the same source document appears multiple times:

- Verify every chunk contains the correct `fileId`.
- Verify file-level aggregation is enabled.
- Verify aggregation occurs before final top-K selection.

Chunk IDs should not be treated as separate source files in the final UI.

---

## 41. Common Problem: File Exists Is False

An indexed file may return:

```text
exists = false
```

when:

- The source file was deleted
- The source file was moved
- The indexed path is no longer valid
- A temporary test file was removed after indexing

The vector record may still remain in ChromaDB.

Delete stale records or re-index the file from its new path.

---

## 42. Common Problem: Corrupted Chinese Path Display

Some command-line environments may display Chinese file names incorrectly because of terminal encoding.

This does not necessarily mean that the original file or metadata is corrupted.

Check:

- The actual file path
- Python Unicode handling
- Java UTF-8 handling
- PowerShell/terminal encoding

Avoid modifying stored metadata solely because one terminal renders Unicode incorrectly.

---

## 43. Dependency Maintenance

Dependencies should be upgraded carefully.

Avoid upgrading all Java, Python, and Flutter dependencies simultaneously.

For Java dependency changes:

1. Modify `pom.xml`.
2. Compile.
3. Run tests.
4. Generate coverage.
5. Package the JAR.
6. Run integration tests.

For Python dependency changes:

1. Update the environment.
2. Start the service.
3. Verify BERT.
4. Verify MobileCLIP.
5. Verify ChromaDB.
6. Test indexing and retrieval.

For Flutter dependency changes:

1. Update `pubspec.yaml`.
2. Run `flutter pub get`.
3. Run `flutter test`.
4. Launch the desktop application.
5. Test the complete workflow.

---

## 44. Security Maintenance

Do not commit:

- Passwords
- API keys
- Access tokens
- Authorization headers
- Private credentials

Review new dependencies and network functionality before integration.

The application should not expose the FastAPI service externally unless explicitly required.

The current local binding:

```text
127.0.0.1
```

should be preserved for the normal desktop architecture.

---

## 45. Git Maintenance

Before committing:

```powershell
git status
```

Review every staged file.

Temporary test data should not be included accidentally.

A typical workflow is:

```powershell
git add <required-files>
git status
git commit -m "Describe the change"
git push origin main
```

Do not use:

```powershell
git add .
```

without first checking for generated model files, ChromaDB data, stress-test datasets, or temporary test files.

---

## 46. Documentation Maintenance

Documentation is stored under:

```text
docs/
```

Important documents include:

- Project Requirements Document
- System Architecture Design
- API Reference
- Maintenance Guide
- End User Manual
- Accessibility User Guide
- Demo Script
- Testing and performance documentation

When implementation changes, documentation should be updated in the same development period where practical.

Important values that should remain synchronized across documents include:

```text
CHUNK_SIZE
CHUNK_OVERLAP
CHUNK_SEARCH_MULTIPLIER
IMAGE_SCORE_CALIBRATION
supported file formats
API endpoints
CLI commands
```

---

## 47. Cross-Platform Maintenance

Flutter supports desktop targets including:

- Windows
- macOS
- Linux

The current project has been developed and functionally validated on Windows.

When macOS or Linux hardware or environments become available, verify:

- Flutter desktop build
- Java availability
- Python startup
- File path handling
- Local process invocation
- FastAPI startup
- ChromaDB persistence
- BERT loading
- MobileCLIP loading
- File selection
- File opening

Do not report a platform as fully tested until the complete runtime workflow has been validated on that platform.

---

## 48. Pre-Release Maintenance Checklist

Before a final release, verify:

```text
[ ] Java backend builds successfully
[ ] Maven tests pass
[ ] JaCoCo report generated
[ ] Python retrieval service starts
[ ] /health returns ok
[ ] BERT loads successfully
[ ] MobileCLIP loads successfully
[ ] TXT indexing works
[ ] PDF indexing works
[ ] DOCX indexing works
[ ] JPG/JPEG indexing works
[ ] PNG indexing works
[ ] Long-document retrieval works
[ ] Text semantic search works
[ ] Image semantic search works
[ ] Multimodal ranking works
[ ] File-level aggregation works
[ ] ChromaDB persistence works
[ ] Delete operation works
[ ] Flutter tests pass
[ ] Flutter application launches
[ ] Search results display correctly
[ ] Local files can be opened
[ ] Offline retrieval works
[ ] Accessibility checks completed
[ ] Documentation matches implementation
[ ] Temporary test data is not staged
```

---

## 49. Key Configuration Values

The current retrieval configuration includes:

```text
CHUNK_SIZE = 400
CHUNK_OVERLAP = 50
CHUNK_SEARCH_MULTIPLIER = 5
IMAGE_SCORE_CALIBRATION = 1.25
```

These values affect system behavior and should be treated as part of the retrieval configuration.

Any changes should be tested and documented.

---

## 50. Conclusion

The current project has evolved from an early Java-only retrieval prototype into a local multimodal retrieval system using:

```text
Flutter
Java
FastAPI
BERT
MobileCLIP
ChromaDB
```

Future maintenance should preserve the modular and offline-first design.

The most important maintenance principles are:

1. Keep frontend, Java, Python, and storage interfaces synchronized.
2. Re-index vectors when embedding compatibility changes.
3. Validate retrieval quality after ranking or chunking changes.
4. Preserve local-only processing during normal operation.
5. Keep generated data and model files out of Git where appropriate.
6. Run automated and integration tests after major changes.
7. Keep documentation synchronized with the actual implementation.

Following these practices will help maintain a stable, testable, and extensible offline multimodal retrieval system.