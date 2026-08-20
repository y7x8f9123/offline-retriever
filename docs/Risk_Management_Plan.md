# Project Risk Management Plan

| Item | Description |
|---|---|
| Project | Offline Accessible Multimodal Local Content Retrieval System |
| Version | Final Project Version |
| Platform | Windows Desktop |
| Date | 2026-08 |

---

## 1. Purpose

This document records the major technical, schedule, quality, accessibility, and release risks considered during the development of the Offline Accessible Multimodal Local Content Retrieval System.

The purpose of risk management was to:

- Identify issues that could affect project delivery.
- Evaluate the likelihood and impact of each risk.
- Apply mitigation strategies during development.
- Record the final status of major risks at project completion.

---

## 2. Risk Assessment

| ID | Risk | Probability | Impact | Mitigation Strategy | Final Status |
|---|---|---|---|---|---|
| R1 | Development schedule delays | Medium | High | Divide development into weekly milestones and prioritize core functionality. | Managed |
| R2 | Dependency compatibility issues | Medium | High | Use stable dependencies, document the environment, and test after dependency changes. | Managed |
| R3 | Machine-learning model startup or inference is slower than expected | Medium | Medium | Use local models, test cold startup, document startup delay, and avoid treating slow loading as immediate failure. | Managed with known limitation |
| R4 | Large test datasets consume excessive storage or repository space | High | Medium | Use generated or curated validation data and exclude large temporary test artifacts from Git. | Managed |
| R5 | PDF or DOCX parsing failures | Medium | Medium | Use Apache Tika-based parsing, validate supported formats, and handle extraction errors. | Managed |
| R6 | ChromaDB indexing or persistence problems | Low | High | Add integration testing for indexing, search, listing, and deletion. Keep runtime database files outside the public repository. | Managed |
| R7 | Accessibility objectives are not fully satisfied | Medium | High | Implement keyboard navigation, High Contrast Mode, Dynamic Font Scaling, and semantic labels. Treat WCAG 2.1 AA as a design objective rather than claiming formal certification. | Partially mitigated |
| R8 | Data loss caused by accidental source-code changes | Low | High | Use Git and GitHub version control and commit project changes regularly. | Managed |
| R9 | Java and Python service integration failure | Medium | High | Use a defined local API, health checking, integration tests, and documented startup procedures. | Managed |
| R10 | Multimodal ranking is unbalanced because BERT and MobileCLIP scores differ | Medium | Medium | Apply image-score calibration and validate with different query types. | Managed |
| R11 | Long documents produce too many duplicate search results | Medium | Medium | Use chunk-level indexing with file-level aggregation. | Managed |
| R12 | Scanned or image-only PDFs contain no extractable text | Medium | Medium | Document OCR as outside the final project scope. | Accepted limitation |
| R13 | Original source files are moved or deleted after indexing | Medium | Low | Store local file paths, check file existence, and document that re-indexing may be required. | Accepted limitation |
| R14 | Cross-platform validation cannot be completed | High | Medium | Refine final release scope to Windows Desktop. | Resolved through scope adjustment |

---

## 3. Risk Priority

Risks were evaluated according to their potential effect on the final project.

| Priority | Description |
|---|---|
| High | May significantly affect project completion, core functionality, data integrity, or final delivery. |
| Medium | May reduce usability, performance, or development efficiency but has practical mitigation. |
| Low | Limited effect on the final project and relatively easy to manage. |

Priority was considered together with probability and impact rather than as a separate numerical score.

---

## 4. Risk Monitoring During Development

Risk monitoring was performed throughout the eight-week development cycle.

The main areas monitored included:

- Project schedule
- Dependency installation and compatibility
- File parsing
- Machine-learning model loading
- ChromaDB persistence
- Java-to-Python communication
- Retrieval correctness
- Multimodal ranking
- Long-document behaviour
- Accessibility features
- Code coverage
- Performance and stress testing
- Repository cleanliness
- Release scope

New risks identified during development were handled through implementation changes, documentation updates, testing, or scope adjustment.

---

## 5. Major Risk Outcomes

### 5.1 Schedule Risk

The project used weekly development milestones.

When scope increased through multimodal retrieval, persistent storage, accessibility, integration testing, and final documentation, priority was given to the main working retrieval flow before optional improvements.

The project reached a final working state within the planned eight-week period.

---

### 5.2 Dependency and Environment Risk

The project combines:

```text
Flutter
Java
Maven
Python
FastAPI
ChromaDB
BERT
MobileCLIP
```

This introduced dependency-management risk.

The mitigation approach included:

- Maintaining separate Java, Flutter, and Python dependency environments.
- Testing after dependency changes.
- Recording setup instructions.
- Documenting the final environment.
- Avoiding unnecessary dependency changes near final release.

---

### 5.3 Machine-Learning Startup Risk

BERT and MobileCLIP model loading introduced a noticeable cold-start delay.

During integration testing, the Java-side service readiness timeout could expire before the Python service completed initialization.

This issue was investigated and documented.

Final treatment:

- The retrieval service is considered functional once `/health` confirms readiness.
- Slow model initialization is documented as a known startup limitation.
- Future improvement could include longer startup timeouts or progress reporting.

---

### 5.4 Document Parsing Risk

Supported document types include:

```text
TXT
PDF
DOCX
```

Text-based PDFs and DOCX files can be processed.

Scanned or image-only PDF files may not contain extractable text.

Final treatment:

```text
OCR is outside the final project scope.
```

This limitation is documented rather than hidden.

---

### 5.5 Vector Storage Risk

ChromaDB is used for persistent local vector storage.

Potential risks included:

- Incorrect indexing
- Failed deletion
- Persistence problems
- Duplicate records
- Incompatible vectors after model changes

Mitigation included integration tests for:

- Text indexing
- Image indexing
- Semantic search
- Indexed-file listing
- Indexed-file deletion

The runtime database is treated as local application data and excluded from the clean public repository.

---

### 5.6 Long-Document Risk

Long documents can produce many vector records.

Without additional handling, this could cause:

- Duplicate search results
- Larger storage requirements
- Slower indexing

The final implementation uses:

```text
Chunk size: 400 words
Chunk overlap: 50 words
```

and file-level aggregation.

This allows chunk-level semantic retrieval while returning one logical source document in the final result list.

---

### 5.7 Multimodal Ranking Risk

Text and image results use different models:

```text
Text  → BERT
Image → MobileCLIP
```

Their raw cosine similarity score distributions are not directly comparable.

The final implementation mitigates this through:

```text
IMAGE_SCORE_CALIBRATION = 1.25
```

The calibrated image score is combined with text results before final ranking.

This approach reduces major ranking imbalance while remaining simple and transparent.

---

### 5.8 Accessibility Risk

The project uses WCAG 2.1 AA as an accessibility design objective.

Implemented accessibility features include:

- Keyboard navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic accessibility labels

Formal third-party WCAG certification was not performed.

For this reason, the final project does not claim formal WCAG 2.1 AA certification.

The risk is therefore considered partially mitigated rather than fully eliminated.

---

### 5.9 Platform Risk

The original project concept considered desktop support across:

```text
Windows
macOS
Linux
```

However, the final validated release scope was adjusted to:

```text
Windows Desktop
```

This scope adjustment removed the risk of claiming unverified macOS or Linux runtime support.

Generated Flutter platform files for other systems do not represent validated final release targets.

---

## 6. Data and Privacy Risks

The application indexes local files and stores metadata and vector embeddings.

Potential local data may include:

- File paths
- File names
- Extracted text
- Embeddings
- ChromaDB records

Mitigation includes:

- Keeping normal retrieval processing local.
- Binding the retrieval API to `127.0.0.1`.
- Excluding development databases from the public repository.
- Avoiding inclusion of private user documents.
- Avoiding API keys, passwords, and credentials in source control.

The final architecture does not require a cloud semantic-search service during normal use.

---

## 7. Repository and Release Risks

Final repository preparation introduced several release-related risks:

- Temporary test files being committed
- Development ChromaDB data being included
- Old documentation contradicting the final implementation
- Platform scope being described incorrectly
- Outdated coverage results appearing in documentation

Mitigation included:

- Removing unnecessary generated files.
- Updating `.gitignore`.
- Synchronizing README and technical documentation.
- Updating final test coverage figures.
- Refining the final platform scope to Windows.

---

## 8. Testing as Risk Mitigation

Testing was used as a major risk-control mechanism.

The final project includes:

- Java unit testing
- Java integration testing
- ChromaDB integration testing
- Flutter testing
- Manual Windows testing
- End-to-end retrieval testing
- Performance and stress testing
- JaCoCo coverage analysis

Final backend test execution completed with:

```text
BUILD SUCCESS
```

Core functional modules achieved high coverage, while overall backend coverage was lower because application entry points and integration-oriented code were also included in the total.

---

## 9. Performance Risk

The retrieval pipeline was validated with 1,000 generated TXT files.

Observed data included:

```text
Text records before test: 12
Text records after test: 1012
Stress-test files confirmed: 1000
```

Measured indexing batches included:

| Batch | Time |
|---:|---:|
| 200 files | 14.81 s |
| 300 files | 25.94 s |
| 450 files | 41.72 s |

With more than 1,000 text records stored, one semantic search completed in approximately:

```text
807 ms
```

These results reduce the immediate risk of the application failing at the planned validation scale.

They should not be interpreted as hardware-independent production benchmarks.

---

## 10. Contingency Approach

If major issues were encountered during development, the project used the following general contingency strategy:

1. Protect the core local retrieval workflow.
2. Prioritize file indexing, semantic search, and persistent storage.
3. Delay optional enhancements if they threatened final delivery.
4. Reduce test-data size when needed during debugging.
5. Replace unstable implementation choices where necessary.
6. Document accepted limitations instead of hiding incomplete areas.
7. Adjust project scope when a requirement could not be responsibly validated.

This approach was used most clearly for the final Windows-only release scope.

---

## 11. Accepted Final Limitations

The following limitations remain accepted at project completion:

- OCR is not implemented.
- Cold model startup may take additional time.
- Performance varies with local hardware.
- BERT and MobileCLIP require calibrated score fusion.
- Source files moved outside the application may need re-indexing.
- Formal WCAG certification was not performed.
- Linux and macOS runtime validation are outside the final release scope.
- Production-scale benchmarking is outside the project scope.

These limitations are documented in the relevant technical and user documentation.

---

## 12. Final Risk Status

At project completion:

- Core indexing functionality is operational.
- Text semantic retrieval is operational.
- Image semantic retrieval is operational.
- ChromaDB persistence is operational.
- Java-to-Python integration is operational.
- File listing and deletion are operational.
- Long-document aggregation is operational.
- Windows frontend operation is validated.
- Major temporary test artifacts are excluded from the clean public repository.
- Known limitations are documented.

No unresolved risk identified in this plan prevents the main final Windows retrieval workflow from operating.

---

## 13. Conclusion

Risk management was used throughout the project to control technical complexity, integration issues, performance concerns, accessibility expectations, repository quality, and release scope.

The most important mitigation strategies were:

- Modular architecture
- Incremental development
- Git and GitHub version control
- Automated testing
- Integration testing
- Manual end-to-end validation
- Documentation
- Scope refinement where required

The final risk profile is considered manageable for the completed eight-week project.

Remaining limitations are documented clearly and can be addressed through future development if the system is extended beyond the final Windows release.