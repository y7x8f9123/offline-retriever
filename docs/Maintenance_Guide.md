# Maintenance Guide

## 1. Overview

This document provides maintenance guidance for the Offline Accessible Multimodal Local Content Retrieval System. It is intended for developers who need to maintain, test, troubleshoot, or extend the project after initial development.

The project uses a modular architecture consisting of a Java backend and Flutter Windows desktop frontend.

The backend handles:

- File parsing
- Text representation generation
- Vector storage
- Similarity calculation
- Local retrieval
- Command-line integration with the Flutter frontend

The Flutter application provides:

- Local file import
- Search interaction
- Retrieval result display
- Opening retrieved local files
- Accessibility features

The current end-to-end prototype supports TXT, PDF, and DOCX documents and English and Chinese text retrieval.

---

## 2. Project Structure

The main project directories are:

```text
shixi/
├── assets/
├── backend/
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

- `backend/` – Java backend implementation and backend tests.
- `frontend/` – Flutter user interface, frontend-backend integration, and Flutter tests.
- `docs/` – technical documentation, project reports, and user guides.
- `models/` – local model resources.
- `dataset/` – datasets and validation data used during development.
- `assets/` – project assets.
- `scripts/` – supporting development scripts.
- `tests/` – additional project-level testing resources.

---

## 3. Backend Maintenance

The backend source code is located under:

```text
backend/src/main/java/com/offlineretriever/
```

The major backend modules include:

- `embedding` – text representation generation.
- `factory` – parser selection.
- `io` – local file scanning.
- `metadata` – file metadata extraction.
- `model` – shared data models.
- `parser` – file parsing implementations.
- `vector` – vector storage, similarity calculation, and retrieval.
- `RetrievalPipeline` – integration of the core retrieval workflow.
- `BackendCli` – command-line bridge between Flutter and the Java backend.

Developers should preserve the existing modular package structure when adding new functionality.

Changes to one module should avoid introducing unnecessary dependencies on unrelated modules.

---

## 4. Building and Packaging the Backend

The backend uses Maven for dependency management, compilation, testing, coverage reporting, and packaging.

From the `backend` directory, compile the project using:

```bash
mvn clean compile
```

A successful build should finish with:

```text
BUILD SUCCESS
```

When dependencies have changed or Maven needs to refresh dependency information, use:

```bash
mvn clean compile -U
```

### Packaging the Executable Backend

The Flutter frontend requires the packaged executable backend JAR.

Create it using:

```bash
mvn clean package -DskipTests
```

The expected output is:

```text
backend/target/backend-1.0-SNAPSHOT.jar
```

The Maven Shade Plugin is used to package the backend and its required dependencies into an executable JAR.

The JAR manifest identifies:

```text
com.offlineretriever.BackendCli
```

as the main application class.

After changing backend code used by the Flutter application, rebuild the JAR before performing end-to-end testing.

---

## 5. Backend CLI Integration

`BackendCli` provides the local interface between Flutter and the Java retrieval pipeline.

The command format is:

```text
java -jar backend-1.0-SNAPSHOT.jar <query> <topK> <file1> [file2] ...
```

For example:

```text
java -jar backend-1.0-SNAPSHOT.jar "software engineering" 5 "sample.txt"
```

The CLI:

1. Reads the search query.
2. Reads the requested `topK` value.
3. Receives one or more local file paths.
4. Indexes the supplied documents.
5. Performs local retrieval.
6. Produces ranked results.
7. Writes the results to standard output as JSON.

The JSON result data includes information required by Flutter, including:

- File name
- Original local file path
- Similarity score

### Maintenance Requirement

Standard output from `BackendCli` should remain valid JSON during normal successful retrieval.

Debugging or diagnostic messages should not be mixed into the JSON response because this can cause Flutter JSON decoding to fail.

---

## 6. Running Backend Tests

The backend test suite is located under:

```text
backend/src/test/java/com/offlineretriever/
```

Run the complete test suite using:

```bash
mvn test
```

Tests cover major backend components including:

- File scanning
- Metadata extraction
- File parsing
- Text representation generation
- Vector storage
- Similarity calculation
- Retrieval
- End-to-end retrieval pipeline
- Performance and stress scenarios
- Invalid input handling
- Chinese text representation

After modifying a core backend component, the relevant unit tests and complete Maven test suite should be executed before committing the change.

A successful final test run should report:

```text
BUILD SUCCESS
```

---

## 7. Test Coverage

JaCoCo is used to evaluate backend test coverage.

Generate the test and coverage report using:

```bash
mvn clean test jacoco:report
```

The generated HTML report can be reviewed under:

```text
backend/target/site/jacoco/
```

Core functional modules should maintain high test coverage.

New functionality should include appropriate unit or integration tests whenever practical.

Application entry points and demonstration classes may have lower coverage because they are not the primary targets of unit testing.

When implementation changes invalidate an existing test expectation, the test should be reviewed and updated to match the intended new behavior rather than being removed without justification.

---

## 8. Text Embedding Maintenance

Text representation functionality is defined through the `EmbeddingEngine` abstraction.

The current `TextEmbeddingEngine` produces a deterministic 256-dimensional local vector representation.

The current implementation supports:

- English alphanumeric tokens
- Chinese characters
- Chinese character combinations
- Local deterministic processing

The current implementation is a lightweight prototype representation and does not use a pretrained BERT model.

### Important Maintenance Rule

If the embedding dimension is changed, developers must review:

- `TextEmbeddingEngine`
- Embedding tests
- Vector retrieval tests
- Similarity calculations
- Performance benchmarks
- Documentation

All vectors compared by cosine similarity must use compatible dimensions.

### Extending the Embedding Layer

When introducing a new embedding implementation:

1. Implement the appropriate embedding engine.
2. Keep model loading and preprocessing logic inside the embedding module.
3. Avoid network dependencies that violate the offline-first design.
4. Add tests for normal and invalid input.
5. Validate multilingual behavior where applicable.
6. Validate compatibility with the vector retrieval layer.
7. Document any local model requirements.
8. Re-run performance tests.

A future BERT or similar local embedding implementation should continue to use the existing abstraction where practical.

---

## 9. File Parsing Maintenance

The parsing layer uses the shared `Parser` abstraction.

The current parser structure includes:

```text
Parser
├── TextParser
├── DocumentParser
└── ImageParser
```

Current parser mappings include:

```text
.txt            → TextParser
.pdf            → DocumentParser
.docx           → DocumentParser
.jpg/.jpeg/.png → ImageParser
```

The current end-to-end Flutter workflow exposes TXT, PDF, and DOCX documents.

### TextParser

`TextParser` handles plain-text documents.

Changes should be tested with:

- Normal text
- Empty or minimal text
- English content
- Chinese content where applicable

### DocumentParser

`DocumentParser` uses Apache Tika for document text extraction.

The current desktop workflow uses it for:

- PDF
- DOCX

When updating Apache Tika or document parsing logic, manually verify both PDF and DOCX retrieval.

Text-based Chinese PDF content should also be included in regression testing because multilingual document retrieval is supported by the current prototype.

### ImageParser

`ImageParser` exists at the backend abstraction level.

Image retrieval is not currently integrated into the end-to-end Flutter workflow.

Developers should not describe image retrieval as fully implemented until parsing, embedding, frontend import, retrieval, and result display have all been integrated and tested.

---

## 10. Adding Support for New File Types

When adding support for a new file type:

1. Create or update the appropriate parser implementation.
2. Implement the existing parser interface.
3. Add file-type selection logic to `ParserFactory`.
4. Add the extension to the Flutter `FilePicker` configuration.
5. Add appropriate file-type labeling and icons to the frontend.
6. Add unit tests for parsing behavior.
7. Verify the file through `RetrievalPipeline`.
8. Verify the complete Flutter-to-Java workflow.
9. Verify that the retrieved file can be opened if required.
10. Update API, user, and maintenance documentation.

Supporting a format in `ParserFactory` alone does not mean that it is supported end-to-end.

---

## 11. Maintaining Vector Retrieval

The vector retrieval module contains:

- `VectorRecord`
- `VectorStore`
- `Retriever`
- `SearchResult`
- `SimilarityCalculator`

`VectorRecord` currently preserves the original local file path in addition to identifying information and the embedding vector.

The path is required by the Flutter results interface to open the original local document.

Changes to embedding dimensions or vector representation must be tested carefully because they may affect similarity calculations and retrieval results.

When modifying retrieval logic:

1. Run `SimilarityCalculatorTest`.
2. Run `RetrieverTest`.
3. Run `VectorStoreTest`.
4. Run retrieval benchmark tests.
5. Run `RetrievalPipelineTest`.
6. Execute the complete Maven test suite.
7. Perform an end-to-end Flutter search.

Performance should also be checked when changes affect large numbers of vector records.

---

## 12. Frontend Maintenance

The Flutter application is located in:

```text
frontend/
```

Before running or testing the frontend, ensure Flutter is installed correctly.

Check the environment using:

```bash
flutter doctor
```

Install or refresh dependencies using:

```bash
flutter pub get
```

Run the Windows application using:

```bash
flutter run -d windows
```

Developers should verify that UI changes do not break:

- File importing
- File removal
- Search input
- Backend invocation
- Result display
- File-type display
- File opening
- Accessibility controls

---

## 13. Flutter File Import

The File Library uses the Flutter `file_picker` package.

The current import workflow accepts:

```text
txt
pdf
docx
```

When changing supported file types, update both the frontend file picker and backend parser configuration.

The frontend maintains the original local path of imported files because this path is required by the backend and later used for opening search results.

File removal from the application library should not delete the original local file.

---

## 14. Flutter-to-Java Retrieval Integration

Frontend-backend integration is implemented through:

```text
frontend/lib/services/retrieval_service.dart
```

`RetrievalService`:

1. Validates the query.
2. Collects imported local file paths.
3. Checks for the backend JAR.
4. Starts Java using `Process.run`.
5. Passes the query, `topK`, and file paths.
6. Reads standard output.
7. Decodes backend JSON.
8. Creates `RetrievalResult` objects.
9. Returns the results to the user interface.

### Backend JAR Location

The current development configuration expects:

```text
../backend/target/backend-1.0-SNAPSHOT.jar
```

when Flutter is launched from the `frontend` directory.

If the project directory structure changes, this path must be updated.

### Common Integration Failure

If Flutter reports that the backend JAR does not exist, rebuild it:

```bash
cd backend
mvn clean package -DskipTests
```

---

## 15. Search Results and File Opening

The Flutter results interface displays:

- File name
- File type
- Similarity score
- Ranking
- Open control

The interface distinguishes:

```text
.txt  → Text document
.pdf  → PDF document
.docx → Word document
```

The `url_launcher` package is used to open a returned local file with the operating system's associated application.

Before attempting to open a file, the frontend checks that the original path still exists.

When modifying result behavior, test:

- TXT opening
- PDF opening
- DOCX opening
- Missing file behavior
- File-type labeling
- Ranking display
- Similarity display

---

## 16. Running Frontend Tests

From the `frontend` directory, run:

```bash
flutter test
```

To generate Flutter test coverage:

```bash
flutter test --coverage
```

Changes to constructor parameters in pages or service models may require corresponding updates to widget tests.

For example, if a new required field is added to `RetrievalResult`, mock `RetrievalResult` objects in `widget_test.dart` must also provide the field.

Changes to user-facing components should be tested for both normal interaction and accessibility-related behavior.

---

## 17. Accessibility Maintenance

Accessibility is a core project requirement.

When modifying the Flutter interface, developers should preserve:

- Clear text labels
- Keyboard-accessible interaction
- Screen-reader compatibility
- Semantic labels
- Readable text scaling
- Sufficient visual contrast
- Understandable navigation and feedback

Accessibility-related changes should be checked against the project's accessibility documentation and WCAG 2.1 AA objectives.

Important interactive controls such as file import, search, and file opening should continue to provide appropriate semantic information.

---

## 18. Offline-First and Security Requirements

The application is designed to process local content without requiring cloud retrieval services.

Future maintenance should preserve the following principles:

- User files remain local.
- File content should not be transmitted to external retrieval services.
- Search queries should remain local during normal retrieval.
- Embedding and retrieval processing should remain local.
- Secrets, passwords, API keys, or authorization tokens must not be committed to the repository.
- New dependencies should be reviewed before integration.
- Network functionality should not be introduced unless explicitly required and reviewed.

The Flutter frontend currently invokes a Java process on the same local machine.

This local process architecture should not be described as a remote server or cloud service.

A security review should be repeated when major dependencies or data-processing components are changed.

---

## 19. Dependency Maintenance

Dependencies should be updated carefully rather than automatically upgrading all packages at once.

### Backend Dependencies

For backend dependencies:

1. Review changes in `pom.xml`.
2. Check compatibility with the current Java environment.
3. Rebuild the backend.
4. Run all tests.
5. Review test coverage.
6. Repackage the executable JAR.
7. Perform an end-to-end Flutter search.

Important current backend dependencies include Apache Tika for document parsing and Maven plugins used for testing, coverage, and executable JAR packaging.

### Flutter Dependencies

For Flutter dependencies:

1. Review changes in `pubspec.yaml`.
2. Run:

```bash
flutter pub get
```

3. Run:

```bash
flutter test
```

4. Launch the Windows application.
5. Verify the main workflows.

Important current Flutter dependencies include:

- `file_picker`
- `url_launcher`

Major dependency upgrades should be performed separately so that compatibility problems can be identified easily.

Generated plugin registration files may change automatically after adding or updating Flutter plugins. These generated changes should be reviewed together with the associated `pubspec.yaml` and `pubspec.lock` changes.

---

## 20. Performance Maintenance

The backend contains performance and benchmark tests for vector retrieval.

Performance should be re-evaluated when changing:

- Embedding dimensions
- Similarity calculation
- Vector storage
- Retrieval ranking
- Large-library behavior

Benchmark results should record:

- Vector library size
- Vector dimension
- `topK`
- Average search latency
- Minimum search latency
- Maximum search latency

Performance results should not be assumed to remain valid after major retrieval implementation changes.

---

## 21. Documentation Maintenance

Documentation is stored under:

```text
docs/
```

When implementation changes, update the relevant documentation.

Important documents include:

- Project Requirements Document
- System Architecture Design
- API Reference
- Maintenance Guide
- Accessibility User Guide
- End-User Manual
- Open-Source Compliance Report
- Demo Script

API documentation should be updated whenever public classes or methods are added, removed, or significantly changed.

The End-User Manual should be updated whenever user-facing workflows change.

The Open-Source Compliance Report should be reviewed whenever a dependency is added, removed, or upgraded.

The Demo Script should reflect functionality that has actually been implemented and tested.

---

## 22. Troubleshooting

### Backend Does Not Compile

Run:

```bash
mvn clean compile
```

Review the first compilation error rather than later errors because subsequent errors may be caused by the initial failure.

---

### Backend Tests Fail

Run:

```bash
mvn test
```

Identify the failing test and check whether the corresponding implementation has recently changed.

If an implementation was intentionally changed, check whether the test still contains an obsolete expected value.

---

### Executable JAR Cannot Be Started

Repackage the backend:

```bash
mvn clean package -DskipTests
```

Confirm that:

```text
target/backend-1.0-SNAPSHOT.jar
```

exists.

The following command should display the CLI usage information when no search arguments are supplied:

```bash
java -jar target/backend-1.0-SNAPSHOT.jar
```

---

### Flutter Dependencies Are Missing

Run:

```bash
flutter pub get
```

Then verify the environment:

```bash
flutter doctor
```

---

### Windows Flutter Plugin Build Fails

Flutter desktop plugins require the Windows development environment to support symbolic links.

If Flutter reports that plugin builds require symlink support, enable Windows Developer Mode and rebuild the application.

---

### Search Returns Unexpected Results

Check:

1. Whether the file was imported successfully.
2. Whether the backend received the correct file path.
3. Whether the correct parser was selected.
4. Whether the parser extracted usable text.
5. Whether an embedding was generated.
6. Whether the vector record exists in `VectorStore`.
7. Whether the query representation has the expected dimension.
8. Whether cosine similarity is being calculated correctly.
9. Whether the backend returned valid JSON.
10. Whether Flutter decoded the returned result correctly.

Because the current embedding mechanism is lightweight and deterministic, similarity quality should not be interpreted as equivalent to a pretrained semantic language model.

---

### Chinese Search Returns Unexpected Results

Check that:

1. Chinese content was extracted successfully from the source document.
2. The current 256-dimensional embedding implementation is being used.
3. The Chinese text is not contained only inside an image or scanned PDF page.
4. The backend JAR has been rebuilt after embedding changes.

---

### PDF or DOCX Cannot Be Retrieved

Check:

1. The extension is supported by `ParserFactory`.
2. Apache Tika successfully extracts text from the document.
3. The document contains machine-readable text.
4. The file path is accessible.

Scanned PDFs may require OCR, which is not currently implemented.

---

### Retrieved File Cannot Be Opened

Check:

1. `filePath` was returned by the backend.
2. The original file still exists.
3. The file has not been moved or renamed.
4. Windows has an associated application for the file type.
5. `url_launcher` is correctly installed.

---

## 23. Recommended End-to-End Regression Test

After major changes, perform the following manual regression test:

```text
Build Backend JAR
      ↓
Run Maven Tests
      ↓
Run Flutter Tests
      ↓
Launch Flutter Windows Application
      ↓
Import TXT File
      ↓
Import PDF File
      ↓
Import DOCX File
      ↓
Perform English Search
      ↓
Perform Chinese Search
      ↓
Verify Ranked Results
      ↓
Verify File-Type Labels
      ↓
Open Retrieved Files
      ↓
Verify Accessibility Controls
```

This workflow verifies that individual modules continue to work together as a complete desktop application.

---

## 24. Recommended Change Workflow

For future maintenance, use the following workflow:

```text
Identify Change
      ↓
Modify Module
      ↓
Add or Update Tests
      ↓
Run Module Tests
      ↓
Run Full Test Suite
      ↓
Review Coverage
      ↓
Rebuild Backend JAR
      ↓
Perform End-to-End Test
      ↓
Update Documentation
      ↓
Commit Changes
```

Small, isolated changes are preferred because they are easier to test, review, and maintain.

---

## 25. Maintenance Principles

Future development should continue to follow these principles:

- Preserve modular architecture.
- Keep offline processing as the default.
- Maintain accessibility requirements.
- Add tests when introducing new functionality.
- Avoid unnecessary dependencies.
- Keep technical documentation synchronized with implementation.
- Review security implications when changing file processing or external dependencies.
- Rebuild the backend JAR after backend changes.
- Test frontend-backend integration after interface changes.
- Keep commits focused and understandable.

Following these practices will help keep the project maintainable, testable, and suitable for continued development.