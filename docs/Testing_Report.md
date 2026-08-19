# Testing Report

## 1. Introduction

This document describes the testing activities performed for the Offline Accessible Multimodal Local Content Retrieval System.

The purpose of testing was to verify the correctness, reliability, and integration of the major system components, including file parsing, metadata processing, embedding generation, vector retrieval, persistent storage, multimodal indexing, and communication between the Java backend and the local Python retrieval service.

Testing was performed throughout the eight-week development cycle rather than only at the end of the project. Unit testing, integration testing, performance testing, code coverage analysis, frontend testing, and manual end-to-end testing were used to validate the implementation.

---

## 2. Testing Scope

Testing covered the following major components:

- TXT, PDF, and DOCX document parsing
- Image file handling
- File metadata extraction
- Parser selection and factory logic
- Text embedding functionality
- Vector storage and retrieval
- Cosine similarity calculation
- Retrieval pipeline integration
- ChromaDB persistent storage
- Text indexing
- Image indexing
- Multimodal semantic search
- Indexed-file listing
- Indexed-file deletion
- Java-to-Python retrieval service communication
- Backend performance and stress behaviour
- Flutter frontend behaviour

The testing strategy focused primarily on functional and reusable system components. Application entry points, command-line demonstration code, and some service startup and error-recovery paths were not primary targets of unit testing.

---

## 3. Testing Environment

The final testing environment included:

- Windows desktop environment
- Java backend
- Apache Maven
- JUnit test framework
- JaCoCo 0.8.12
- Python local retrieval service
- FastAPI
- ChromaDB persistent vector database
- BERT text embedding model
- MobileCLIP image and text embedding model
- Flutter desktop frontend

The local retrieval service operated on:

```text
http://127.0.0.1:8765
```

Before integration testing, the service was verified through the `/health` endpoint.

A successful health check confirmed that:

- the local service was running;
- the text collection was accessible;
- the image collection was accessible;
- the BERT model was loaded;
- the MobileCLIP model was loaded.

---

## 4. Unit Testing

JUnit was used for Java backend testing.

Unit tests were created for the main reusable components of the backend, including:

- embedding;
- parsers;
- metadata;
- model classes;
- file I/O;
- vector retrieval;
- parser factory logic;
- retrieval pipeline.

### 4.1 Vector Retrieval Testing

The vector retrieval package was tested extensively.

Tests covered:

- vector record creation;
- vector storage;
- cosine similarity calculation;
- identical vectors;
- orthogonal vectors;
- invalid vector dimensions;
- ranked retrieval;
- search-result representation.

The vector package achieved high instruction and branch coverage during final testing.

### 4.2 Parser Testing

Tests were used to verify supported document parsing behaviour.

The parser layer supports:

- TXT;
- PDF;
- DOCX;
- image-file recognition.

Parser-related code achieved full instruction coverage in the final JaCoCo analysis.

### 4.3 Embedding Testing

The embedding module was tested for normal input and invalid input behaviour.

Tests verified that:

- valid textual input can be processed;
- invalid or empty input is handled correctly;
- the embedding interface behaves consistently.

The embedding package achieved full instruction coverage in the final test run.

### 4.4 Metadata and Model Testing

Metadata and model tests verified information such as:

- file names;
- file paths;
- file types;
- file sizes;
- modification information;
- vector-record data;
- search-result data.

These modules achieved high or complete instruction coverage.

---

## 5. ChromaDB and Retrieval Service Integration Testing

Additional integration tests were added during final project testing for `ChromaBridgeClient`.

Unlike isolated unit tests, these tests communicate with the actual local Python retrieval service and persistent ChromaDB storage.

The integration tests covered four major operations: text indexing, semantic search, file deletion, and image indexing.

### 5.1 Text Indexing

A temporary text file was created and indexed through the Java bridge.

The test verified that:

1. the file could be submitted to the retrieval service;
2. the content could be processed and embedded;
3. the resulting data was stored;
4. the indexed file appeared in the indexed-file listing.

### 5.2 Semantic Search

A semantic query was submitted through the Java bridge.

The test verified that:

- the search request completed successfully;
- a result list was returned;
- returned result metadata could be accessed correctly;
- result scores were available for ranking.

### 5.3 File Deletion

A temporary text file was indexed and subsequently deleted.

The test verified that:

1. the file was successfully indexed;
2. its identifier could be retrieved;
3. the delete operation completed successfully;
4. the deleted record no longer appeared in the indexed-file list.

### 5.4 Image Indexing

A valid temporary PNG image was generated during the integration test.

The image was indexed through the same local retrieval architecture used by the application.

The test verified:

- image-file acceptance;
- communication with the image indexing endpoint;
- MobileCLIP-based image processing;
- ChromaDB persistence;
- image metadata;
- correct image content type.

This provided integration-level verification of the multimodal image indexing path.

---

## 6. Code Coverage Analysis

JaCoCo 0.8.12 was used to measure Java backend code coverage.

During final testing, additional integration tests were introduced for the storage layer. Before these tests were added, the `com.offlineretriever.storage` package had no automated test coverage.

After the new integration tests were introduced, storage instruction coverage increased to **63%**.

The final measured coverage of the storage components was:

| Component | Instruction Coverage |
| --- | ---: |
| `BridgeIndexedFile` | 100% |
| `BridgeSearchResult` | 100% |
| TypeToken helper classes | 100% |
| `ChromaBridgeClient` | 58% |
| Storage package overall | 63% |

Other major packages achieved the following instruction coverage during final testing:

| Package | Instruction Coverage |
| --- | ---: |
| `com.offlineretriever.embedding` | 100% |
| `com.offlineretriever.parser` | 100% |
| `com.offlineretriever.model` | 100% |
| `com.offlineretriever.io` | 100% |
| `com.offlineretriever.metadata` | 97% |
| `com.offlineretriever.vector` | 95% |
| `com.offlineretriever.factory` | 93% |

The root `com.offlineretriever` package had lower overall coverage because it contains application entry points and command-line or demonstration classes such as:

- `BackendCli`;
- `App`;
- `PipelineDemo`.

These classes primarily contain application startup, orchestration, command-line handling, and demonstration logic and were not the primary targets of unit testing.

`RetrievalPipeline`, which contains reusable retrieval functionality within the same package, achieved substantially higher coverage than the package-level figure.

For this reason, project-level coverage should be interpreted together with the coverage of the core functional modules rather than as a single isolated percentage.

---

## 7. Final Automated Test Result

The Maven test suite completed successfully after the local retrieval service was available.

Final execution status:

```text
BUILD SUCCESS
```

The test execution completed without failures or errors.

This confirmed that the existing backend unit tests and the newly added ChromaDB integration tests could execute successfully together.

---

## 8. Service Startup Observation

Integration testing identified an important startup behaviour.

During a cold start, the local retrieval service may require more than 30 seconds to become fully available because the service must initialise:

- ChromaDB;
- the BERT text embedding model;
- the MobileCLIP model.

The Java bridge waits for the local service to become available before performing retrieval operations.

During one cold-start integration-test execution, the following message was reported:

```text
Local retrieval service did not become ready.
```

Further investigation confirmed that the retrieval service itself was functional. The service completed model loading after the Java-side startup waiting period had expired.

A subsequent health check returned a successful status and confirmed that both BERT and MobileCLIP were loaded correctly. Once the service was running, the complete Maven test suite passed successfully.

This behaviour is therefore considered a startup-time limitation rather than a failure of the retrieval or storage functionality.

A future release could improve this behaviour by:

- increasing the startup timeout;
- displaying model-loading progress;
- improving service lifecycle management;
- distinguishing slow startup from service failure.

---

## 9. Performance Testing

Performance and stress testing were included in the backend test suite.

The purpose of these tests was to verify that the retrieval components remained operational when processing larger numbers of records and repeated retrieval operations.

Performance testing focused on:

- repeated vector insertion;
- retrieval across multiple records;
- ranking behaviour;
- execution stability;
- basic scalability of the local retrieval pipeline.

These tests were intended to identify obvious performance regressions rather than provide production-scale benchmarking.

Detailed production benchmarking across different hardware configurations remains an area for future work.

---

## 10. Frontend Testing

Flutter tests were used during frontend development to verify important user-interface behaviour.

Testing included areas such as:

- search input handling;
- empty-query behaviour;
- navigation;
- result presentation;
- basic user interaction.

Frontend testing complemented the backend test suite by validating application behaviour from the user-interface layer.

Manual testing was also performed throughout development using the Windows Flutter desktop application.

---

## 11. Manual End-to-End Testing

In addition to automated tests, the complete application workflow was tested manually.

The main workflow included:

1. starting the local retrieval environment;
2. opening the Flutter desktop application;
3. selecting supported local files;
4. indexing text documents and images;
5. storing generated embeddings in ChromaDB;
6. submitting natural-language search queries;
7. retrieving semantically related text and image results;
8. displaying ranked results in the frontend;
9. listing previously indexed files;
10. deleting indexed content.

The tests confirmed that the main multimodal retrieval workflow operated across the Flutter frontend, Java backend, Python retrieval service, machine-learning models, and persistent vector database.

---

## 12. Known Testing Limitations

The current test suite has several limitations.

First, application entry-point and CLI classes have relatively low automated coverage because testing focused on reusable functional components.

Second, some exceptional service-management paths are difficult to reproduce reliably in automated tests, including:

- Python process startup failure;
- unexpected service termination;
- port conflicts;
- interrupted startup;
- model-loading failure;
- corrupted or unavailable local model files.

Third, integration tests depend on the local retrieval service and machine-learning models being available. Cold model loading can increase test execution time.

Finally, performance results were obtained in a local development environment and should not be interpreted as hardware-independent production benchmarks.

These limitations do not prevent the main retrieval workflow from operating, but they identify areas that could be strengthened in future development.

---

## 13. Final Testing Status

At the end of the testing phase:

- core document parsing functionality was operational;
- text indexing was operational;
- image indexing was operational;
- BERT-based text retrieval was operational;
- MobileCLIP-based image retrieval was operational;
- ChromaDB persistence was operational;
- multimodal semantic search was operational;
- file listing and deletion were operational;
- core backend packages achieved high code coverage;
- storage integration coverage was added;
- backend tests completed with `BUILD SUCCESS`;
- the complete application workflow was manually verified.

The remaining uncovered code is concentrated primarily in application entry points, command-line orchestration, service startup management, and exceptional failure paths rather than the main retrieval algorithms.

Overall, the final testing results indicate that the implemented system satisfies the major functional objectives of the project and provides a stable basis for future development.
