# Testing Report

## 1. Introduction

This document describes the final testing activities performed for the
Offline Accessible Multimodal Local Content Retrieval System.

The purpose of testing was to verify the correctness, reliability,
performance, and integration of the major system components, including:

-   File parsing

-   Metadata processing

-   Embedding generation

-   Vector retrieval

-   Persistent storage

-   Multimodal indexing

-   Java-to-Python communication

-   Flutter frontend behaviour

-   End-to-end application operation

Testing was performed throughout the eight-week development cycle rather
than only at the end of the project.

The final testing strategy included:

-   Unit testing

-   Integration testing

-   Code coverage analysis

-   Performance and stress testing

-   Flutter frontend testing

-   Manual Windows end-to-end testing

------------------------------------------------------------------------

## 2. Testing Scope

Testing covered the following major components:

-   TXT, PDF, and DOCX document parsing

-   Image file handling

-   File metadata extraction

-   Parser selection and factory logic

-   Text embedding functionality

-   Vector storage and retrieval

-   Cosine similarity calculation

-   Retrieval pipeline integration

-   ChromaDB persistent storage

-   Text indexing

-   Image indexing

-   Multimodal semantic search

-   Long-document chunking

-   File-level result aggregation

-   Indexed-file listing

-   Indexed-file deletion

-   Java-to-Python retrieval service communication

-   Backend performance and stress behaviour

-   Flutter frontend behaviour

The testing strategy focused primarily on functional and reusable system
components.

Application entry points, command-line orchestration, service startup
management, and some exceptional error-recovery paths were not primary
targets of unit testing.

------------------------------------------------------------------------

## 3. Testing Environment

The final testing environment included:

-   Windows desktop environment

-   Java backend

-   Apache Maven

-   JUnit

-   JaCoCo 0.8.12

-   Python local retrieval service

-   FastAPI

-   ChromaDB persistent vector database

-   BERT text embedding model

-   MobileCLIP image and text embedding model

-   Flutter Windows desktop frontend

The local retrieval service operated on:

``` text

http://127.0.0.1:8765
```

Before integration testing, the service was verified through the
`/health` endpoint.

A successful health check confirmed that:

-   The local service was running.

-   The text collection was accessible.

-   The image collection was accessible.

-   The BERT model was loaded.

-   The MobileCLIP model was loaded.

------------------------------------------------------------------------

## 4. Unit Testing

JUnit was used for Java backend testing.

Unit tests were created for the main reusable components of the backend,
including:

-   Embedding

-   Parsers

-   Metadata

-   Model classes

-   File I/O

-   Vector retrieval

-   Parser factory logic

-   Retrieval pipeline

### 4.1 Vector Retrieval Testing

The vector retrieval package was tested extensively.

Tests covered:

-   Vector record creation

-   Vector storage

-   Cosine similarity calculation

-   Identical vectors

-   Orthogonal vectors

-   Invalid vector dimensions

-   Ranked retrieval

-   Search-result representation

The vector package achieved high instruction coverage during final
testing.

------------------------------------------------------------------------

### 4.2 Parser Testing

Tests were used to verify supported document parsing behaviour.

The parser layer supports:

``` text

TXT

PDF

DOCX
```

and recognizes supported image formats for routing into the image
indexing pipeline.

Parser-related code achieved full instruction coverage in the final
JaCoCo analysis.

------------------------------------------------------------------------

### 4.3 Embedding Testing

The embedding module was tested for normal input and invalid input
behaviour.

Tests verified that:

-   Valid textual input can be processed.

-   Invalid or empty input is handled correctly.

-   The embedding interface behaves consistently.

The embedding package achieved full instruction coverage in the final
test run.

------------------------------------------------------------------------

### 4.4 Metadata and Model Testing

Metadata and model tests verified information including:

-   File names

-   File paths

-   File types

-   File sizes

-   Modification information

-   Vector-record data

-   Search-result data

These modules achieved high or complete instruction coverage.

------------------------------------------------------------------------

## 5. ChromaDB and Retrieval Service Integration Testing

Additional integration tests were added during final project testing for
`ChromaBridgeClient`.

Unlike isolated unit tests, these tests communicate with the actual
local Python retrieval service and persistent ChromaDB storage.

The integration tests covered four major operations:

-   Text indexing

-   Semantic search

-   File deletion

-   Image indexing

------------------------------------------------------------------------

### 5.1 Text Indexing

A temporary text file was created and indexed through the Java bridge.

The test verified that:

1\. The file could be submitted to the retrieval service.

2\. The content could be processed and embedded.

3\. The resulting data was stored.

4\. The indexed file appeared in the indexed-file listing.

This validated the path:

``` text

Java

  ↓

FastAPI

  ↓

BERT

  ↓

ChromaDB
```

------------------------------------------------------------------------

### 5.2 Semantic Search

A semantic query was submitted through the Java bridge.

The test verified that:

-   The search request completed successfully.

-   A result list was returned.

-   Returned result metadata could be accessed correctly.

-   Result scores were available for ranking.

This provided integration-level verification of semantic retrieval
rather than only isolated vector calculations.

------------------------------------------------------------------------

### 5.3 File Deletion

A temporary text file was indexed and subsequently deleted.

The test verified that:

1\. The file was successfully indexed.

2\. Its identifier could be retrieved.

3\. The delete operation completed successfully.

4\. The deleted record no longer appeared in the indexed-file list.

Deletion affects the indexed representation rather than deleting the
original source file.

------------------------------------------------------------------------

### 5.4 Image Indexing

A valid temporary PNG image was generated during the integration test.

The image was indexed through the same local retrieval architecture used
by the application.

The test verified:

-   Image-file acceptance

-   Communication with the image indexing endpoint

-   MobileCLIP-based image processing

-   ChromaDB persistence

-   Image metadata

-   Correct image content type

This provided integration-level verification of the multimodal image
indexing path.

------------------------------------------------------------------------

## 6. Code Coverage Analysis

JaCoCo 0.8.12 was used to measure Java backend code coverage.

The final overall backend coverage was:

``` text

Overall instruction coverage: 61%

Overall branch coverage: 44%
```

These overall figures include:

-   Application entry points

-   Command-line orchestration

-   Service-management logic

-   Integration-oriented code

-   Core reusable functional modules

For this reason, the overall percentage is lower than the coverage
achieved by the core functional packages.

During final testing, additional integration tests were introduced for
the storage layer.

Before these tests were added, the `com.offlineretriever.storage`
package had no automated test coverage.

After the new integration tests were introduced, storage instruction
coverage increased to:

``` text

63%
```

### 6.1 Storage Coverage

The final measured coverage of the storage components was:

\| Component \| Instruction Coverage \|

\| --- \| ---: \|

\| `BridgeIndexedFile` \| 100% \|

\| `BridgeSearchResult` \| 100% \|

\| TypeToken helper classes \| 100% \|

\| `ChromaBridgeClient` \| 58% \|

\| Storage package overall \| 63% \|

### 6.2 Core Package Coverage

Other major packages achieved the following instruction coverage during
final testing:

\| Package \| Instruction Coverage \|

\| --- \| ---: \|

\| `com.offlineretriever.embedding` \| 100% \|

\| `com.offlineretriever.parser` \| 100% \|

\| `com.offlineretriever.model` \| 100% \|

\| `com.offlineretriever.io` \| 100% \|

\| `com.offlineretriever.metadata` \| 97% \|

\| `com.offlineretriever.vector` \| 95% \|

\| `com.offlineretriever.factory` \| 93% \|

\| `com.offlineretriever.storage` \| 63% \|

The root `com.offlineretriever` package had lower coverage because it
contains application entry points and command-line or demonstration
classes such as:

-   `BackendCli`

-   `App`

-   `PipelineDemo`

These classes primarily contain:

-   Application startup

-   Orchestration

-   Command-line handling

-   Demonstration logic

and were not the primary targets of unit testing.

`RetrievalPipeline`, which contains reusable retrieval functionality
within the same package, achieved substantially higher coverage than the
package-level figure.

For this reason, project-level coverage should be interpreted together
with the coverage of the core functional modules rather than as a single
isolated percentage.

------------------------------------------------------------------------

## 7. Final Automated Test Result

The Maven test suite completed successfully after the local retrieval
service was available.

Final execution status:

``` text

BUILD SUCCESS
```

The test execution completed without failures or errors.

This confirmed that the backend unit tests and ChromaDB integration
tests could execute successfully together.

The successful test run validated the major backend components used by
the final application.

------------------------------------------------------------------------

## 8. Service Startup Observation

Integration testing identified an important startup behaviour.

During a cold start, the local retrieval service may require more than
30 seconds to become fully available because the service must
initialise:

-   ChromaDB

-   BERT text embedding model

-   MobileCLIP model

The Java bridge waits for the local service to become available before
performing retrieval operations.

During one cold-start integration-test execution, the following message
was reported:

``` text

Local retrieval service did not become ready.
```

Further investigation confirmed that the retrieval service itself was
functional.

The service completed model loading after the Java-side startup waiting
period had expired.

A subsequent health check returned a successful status and confirmed
that both BERT and MobileCLIP were loaded correctly.

Once the service was running, the complete Maven test suite passed
successfully.

This behaviour is therefore considered a startup-time limitation rather
than a failure of the retrieval or storage functionality.

Possible future improvements include:

-   Increasing the startup timeout

-   Displaying model-loading progress

-   Improving service lifecycle management

-   Distinguishing slow startup from service failure

------------------------------------------------------------------------

## 9. Performance and Stress Testing

Performance and stress testing were performed to verify that the
retrieval system remained operational with a larger local collection.

The final scalability test used:

``` text

1,000 generated TXT files
```

The test focused on:

-   Batch indexing

-   Persistent storage

-   Indexed-file counting

-   Retrieval stability

-   Search response time

------------------------------------------------------------------------

### 9.1 Initial Database State

Before the stress test, the text collection contained:

``` text

12 text records
```

After the 1,000 generated files were indexed, the collection contained:

``` text

1012 text records
```

The indexed-file listing confirmed:

``` text

1000 stress-test files
```

This demonstrated that all generated stress-test files were indexed
successfully.

------------------------------------------------------------------------

### 9.2 Batch Indexing Results

The files were processed in several batches.

Measured batches included:

\| Batch Size \| Indexing Time \|

\| ---: \| ---: \|

\| 200 files \| 14.81 s \|

\| 300 files \| 25.94 s \|

\| 450 files \| 41.72 s \|

The initial 50-file batch was used for functional validation and was not
timed.

Together, the batches represented:

``` text

50 + 200 + 300 + 450 = 1000 files
```

No backend failure occurred during the complete 1,000-file indexing
validation.

------------------------------------------------------------------------

### 9.3 Search Performance

After more than 1,000 text records were stored, an end-to-end semantic
search was executed using:

``` text

software engineering
```

The observed search time was approximately:

``` text

807 ms
```

The query successfully returned ranked semantic results from the
populated ChromaDB collection.

This demonstrated that the retrieval system remained responsive at the
planned project validation scale.

------------------------------------------------------------------------

### 9.4 Performance Interpretation

The stress-test results demonstrate that the implemented pipeline can
index and retrieve content from at least 1,000 local text files in the
tested environment.

However, these measurements should not be interpreted as
hardware-independent production benchmarks.

Performance may vary according to:

-   CPU

-   Available memory

-   Storage performance

-   File size

-   Document length

-   Number of generated chunks

-   Model-loading state

-   Database size

The objective of the test was to validate project-scale stability and
practical local performance rather than production-scale distributed
benchmarking.

------------------------------------------------------------------------

## 10. Long-Document Testing

The final retrieval pipeline supports long-document chunking.

Current configuration:

``` text

Chunk size: 400 words

Chunk overlap: 50 words
```

Testing verified that:

-   Long content can be divided into multiple chunks.

-   Each chunk can be embedded independently.

-   Chunk metadata retains the original file identifier.

-   Search can retrieve relevant chunks.

-   Chunk results can be aggregated back to the original file.

-   A long document does not need to appear repeatedly in the final
    result list.

This validates the file-level aggregation behaviour used by the final
semantic retrieval pipeline.

------------------------------------------------------------------------

## 11. Multimodal Retrieval Testing

The final system combines text and image retrieval.

Text retrieval uses:

``` text

BERT
```

Image retrieval uses:

``` text

MobileCLIP
```

Testing verified that:

-   Text documents can be semantically retrieved.

-   Images can be indexed.

-   Natural-language queries can be used for image retrieval.

-   Text and image results can be returned through the same search
    workflow.

Because BERT and MobileCLIP have different raw similarity score
distributions, the final implementation applies image-score calibration.

Current configuration:

``` text

IMAGE_SCORE_CALIBRATION = 1.25
```

The purpose of the calibration is to improve comparability between text
and image ranking scores.

------------------------------------------------------------------------

## 12. Frontend Testing

Flutter tests were used during frontend development to verify important
user-interface behaviour.

Testing included areas such as:

-   Search input handling

-   Empty-query behaviour

-   Navigation

-   Result presentation

-   Basic user interaction

Frontend testing complemented the backend test suite by validating
application behaviour from the user-interface layer.

Manual testing was also performed using the Windows Flutter desktop
application.

The final validated frontend target is:

``` text

Windows Desktop
```

------------------------------------------------------------------------

## 13. Accessibility Testing

Accessibility-related functionality was also reviewed during frontend
testing.

The implemented interface includes:

-   Keyboard navigation

-   High Contrast Mode

-   Dynamic Font Scaling

-   Semantic accessibility labels

Manual checks were used to verify that:

-   Important controls can be reached using the keyboard.

-   High Contrast Mode changes the interface presentation.

-   Font scaling changes text size.

-   Enlarged text does not prevent basic application use.

-   Important interactive elements retain meaningful labels.

WCAG 2.1 AA is used as an accessibility design objective.

Formal third-party WCAG certification was outside the scope of the
project.

------------------------------------------------------------------------

## 14. Manual End-to-End Testing

In addition to automated tests, the complete application workflow was
tested manually.

The main workflow included:

1\. Starting the local retrieval environment.

2\. Verifying the FastAPI service.

3\. Opening the Flutter Windows desktop application.

4\. Selecting supported local files.

5\. Indexing text documents and images.

6\. Generating BERT and MobileCLIP embeddings.

7\. Storing generated embeddings in ChromaDB.

8\. Submitting natural-language search queries.

9\. Retrieving semantically related text and image results.

10\. Displaying ranked results in the frontend.

11\. Listing previously indexed files.

12\. Opening local source files.

13\. Deleting indexed content.

The tests confirmed that the main multimodal retrieval workflow operated
across:

``` text

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

Manual testing also identified stale index records whose original local
source files had already been removed. These records were shown as
missing because their stored source paths no longer existed.

The stale records were removed from the index. After cleanup, the File
Library displayed only the remaining indexed files with valid local
sources.

The Open function was manually verified for existing local text and
image files. Valid files were successfully opened using their associated
Windows applications.

The delete workflow was tested through the File Library. Removing an
indexed item removed its indexed representation while leaving the
original source file on disk.

The stress-test records were also removed from the persistent index
after scalability testing so that they would not interfere with normal
final retrieval validation.

High Contrast Mode and dynamic font scaling were manually verified
through the Settings interface.

This provided final end-to-end validation of the implemented
architecture.

------------------------------------------------------------------------

## 15. Offline-First Validation

The final architecture was reviewed to verify that normal retrieval
processing occurs locally.

The following operations are performed locally:

-   File parsing

-   Text extraction

-   Text chunking

-   BERT inference

-   MobileCLIP inference

-   ChromaDB storage

-   Semantic search

-   Result aggregation

-   Result ranking

The retrieval service operates on:

``` text

127.0.0.1:8765
```

No remote semantic-search API is required during normal retrieval after
the required dependencies and model resources are available locally.

Initial dependency installation and model acquisition may require
Internet access.

------------------------------------------------------------------------

## 16. Known Testing Limitations

The final test suite has several limitations.

### 16.1 Entry-Point Coverage

Application entry-point and CLI classes have relatively low automated
coverage because testing focused on reusable functional components.

### 16.2 Service Failure Paths

Some exceptional service-management paths are difficult to reproduce
reliably in automated tests, including:

-   Python process startup failure

-   Unexpected service termination

-   Port conflicts

-   Interrupted startup

-   Model-loading failure

-   Corrupted or unavailable local model files

### 16.3 Integration-Test Requirements

Integration tests depend on:

-   Local retrieval service availability

-   ChromaDB

-   BERT

-   MobileCLIP

Cold model loading can increase test execution time.

### 16.4 Performance Environment

Performance results were obtained in a local development environment.

They should not be interpreted as hardware-independent production
benchmarks.

### 16.5 OCR

OCR was not implemented.

Therefore, scanned or image-only PDF retrieval was outside the final
testing scope.

### 16.6 Platform Scope

The final project was validated on:

``` text

Windows Desktop
```

macOS and Linux runtime validation were outside the final project scope.

### 16.7 Missing Local Source Files

If an indexed source file is moved or deleted outside the application,
its stored index record may remain until it is removed from the index.
Manual testing demonstrated that such stale records can be identified
because the original source path no longer exists. Automatic
stale-record cleanup could be considered as a future improvement.

### 16.8 Cross-Modal Ranking

Text results are generated using BERT while image results are generated
using MobileCLIP. Because these models have different similarity-score
distributions, their raw scores are not inherently equivalent. A fixed
image-score calibration factor is currently used before the two result
sets are combined. More advanced score normalization or ranking fusion
could improve cross-modal ranking quality in future work.

These limitations do not prevent the main Windows multimodal retrieval
workflow from operating.

------------------------------------------------------------------------

## 17. Final Testing Status

At the end of the testing phase:

-   TXT parsing was operational.

-   PDF text extraction was operational.

-   DOCX text extraction was operational.

-   Text indexing was operational.

-   Image indexing was operational.

-   BERT-based text retrieval was operational.

-   MobileCLIP-based image retrieval was operational.

-   ChromaDB persistence was operational.

-   Multimodal semantic search was operational.

-   Long-document chunking was operational.

-   File-level aggregation was operational.

-   File listing was operational.

-   File importing was operational.

-   Existing indexed local text and image files could be opened
    successfully.

-   Deleting an index record did not delete the original source file.

-   Missing local source files could be identified.

-   Stale index records could be removed.

-   High Contrast Mode was manually verified.

-   Dynamic font scaling was manually verified.

-   Indexed-file deletion was operational.

-   Java-to-Python integration was operational.

-   Core backend packages achieved high code coverage.

-   Storage integration coverage was added.

-   Backend tests completed with `BUILD SUCCESS`.

-   The 1,000-file stress test completed without backend failure.

-   Semantic search remained operational with more than 1,000 text
    records.

-   The complete Windows application workflow was manually verified.

Final backend coverage was:

``` text

Instruction coverage: 61%

Branch coverage: 44%
```

Core functional package instruction coverage ranged from:

``` text

63% to 100%
```

with several packages achieving full instruction coverage.

The remaining uncovered code is concentrated primarily in:

-   Application entry points

-   Command-line orchestration

-   Service startup management

-   Integration-oriented control flow

-   Exceptional failure paths

rather than the main reusable retrieval algorithms.

------------------------------------------------------------------------

## 18. Conclusion

The final testing results demonstrate that the Offline Accessible
Multimodal Local Content Retrieval System successfully implements its
major functional objectives.

Testing covered:

-   Unit-level backend functionality

-   Java-to-Python integration

-   ChromaDB persistence

-   BERT text retrieval

-   MobileCLIP image retrieval

-   Long-document processing

-   Multimodal ranking

-   Flutter frontend behaviour

-   Accessibility-focused functionality

-   Offline-first operation

-   Performance and scalability

The Java backend achieved:

``` text

61% overall instruction coverage

44% overall branch coverage
```

while the core reusable packages achieved substantially higher
instruction coverage.

The system also successfully indexed 1,000 generated TXT files and
remained capable of semantic retrieval with more than 1,000 stored text
records.

Together with successful automated testing and manual Windows end-to-end
validation, these results provide evidence that the final implementation
is functional, integrated, and stable at the intended project scale.
