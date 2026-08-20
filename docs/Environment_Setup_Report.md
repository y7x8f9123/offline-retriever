# Environment Setup Validation Report

| Item | Description |
|---|---|
| Project | Offline Accessible Multimodal Local Content Retrieval System |
| Version | Final Project Version |
| Author | Xuefei Yao |
| Date | 2026-08 |

---

## 1. Purpose

This document records the final development and execution environment used for the Offline Accessible Multimodal Local Content Retrieval System.

It verifies that the main development tools, frameworks, local retrieval components, machine-learning dependencies, and version-control environment required by the project were successfully configured during the eight-week development cycle.

---

## 2. Final Development Environment

The project was developed and functionally validated on Windows.

| Component | Status | Purpose |
|---|---|---|
| Windows 11 | Installed and validated | Primary development and release platform |
| Visual Studio Code | Installed and validated | Main development environment |
| Git | Installed and validated | Local version control |
| GitHub | Connected and validated | Remote source-code repository |
| Flutter SDK | Installed and validated | Windows desktop frontend |
| Dart SDK | Installed with Flutter | Frontend application development |
| Java Development Kit | Installed and validated | Java backend development |
| Apache Maven | Installed and validated | Backend build and dependency management |
| Apache Tika | Integrated and validated | PDF and DOCX parsing |
| Python | Installed and validated | Local retrieval and ML service |
| FastAPI | Installed and validated | Local retrieval API |
| Uvicorn | Installed and validated | Local FastAPI server |
| ChromaDB | Installed and validated | Persistent local vector storage |
| BERT model resources | Available and validated | Text embedding |
| MobileCLIP model resources | Available and validated | Image and text-image embedding |
| JUnit | Integrated | Java backend testing |
| JaCoCo | Integrated | Java code coverage analysis |
| Flutter Test | Integrated | Frontend testing |

---

## 3. Platform Scope

The final project release targets:

```text
Windows Desktop
```

The project was developed, tested, and functionally validated on Windows.

The final release scope was limited to Windows.

---

## 4. Version Control Validation

The project uses Git for version control and GitHub for remote repository hosting.

Typical validation commands include:

```bash
git --version
git status
git add .
git commit -m "update project"
git push
```

Validation confirmed that:

- Git was installed successfully.
- The local repository was initialized successfully.
- The remote GitHub repository was connected successfully.
- Project changes could be committed and pushed to the `main` branch.
- Source code and project documentation were maintained under version control.

---

## 5. Flutter Environment Validation

Flutter was installed and configured for Windows desktop development.

The environment can be verified using:

```powershell
flutter doctor
```

Project dependencies are installed using:

```powershell
cd frontend
flutter pub get
```

The Windows desktop application can be launched using:

```powershell
flutter run -d windows
```

Flutter testing can be executed using:

```powershell
flutter test
```

The Flutter environment was successfully used to develop and manually validate the final desktop user interface.

---

## 6. Java Backend Environment Validation

The backend uses Java and Maven.

The backend project is located in:

```text
backend/
```

Dependencies and compilation can be validated using:

```powershell
cd backend
mvn clean package -DskipTests
```

Backend tests can be executed using:

```powershell
mvn test
```

Code coverage can be generated using:

```powershell
mvn clean test jacoco:report
```

The Maven build and backend test suite completed successfully during final project testing.

---

## 7. Document Parsing Environment

The Java backend uses Apache Tika for document parsing and text extraction.

The implemented retrieval workflow supports:

```text
TXT
PDF
DOCX
```

Apache Tika and its associated parser modules were successfully integrated through Maven.

Text-based PDF and DOCX files were successfully processed during project testing.

Image-only or scanned PDFs may require OCR, which is outside the current implementation scope.

---

## 8. Python Retrieval Environment

The project uses a local Python retrieval service for machine-learning inference and persistent vector storage.

The service includes:

- FastAPI
- Uvicorn
- ChromaDB
- BERT text embedding
- MobileCLIP image embedding
- MobileCLIP text embedding for image retrieval

The local service is started from the project root using:

```powershell
python scripts\service\retrieval_server.py
```

The service operates on:

```text
http://127.0.0.1:8765
```

A successful startup confirms that the required local retrieval components have been initialized.

---

## 9. Local Service Validation

The retrieval service health can be checked using:

```powershell
Invoke-RestMethod http://127.0.0.1:8765/health
```

The final environment was validated to support:

- local FastAPI service startup;
- BERT model loading;
- MobileCLIP model loading;
- ChromaDB access;
- text indexing;
- image indexing;
- semantic retrieval;
- indexed-file listing;
- indexed-file deletion.

Cold startup may take additional time because the machine-learning models must be loaded into memory before the service becomes fully available.

---

## 10. ChromaDB Validation

ChromaDB is used for persistent local vector storage.

The retrieval system stores text and image embeddings locally and performs similarity-based retrieval without requiring a remote vector database.

The database is generated during normal application execution.

Runtime ChromaDB data should not normally be committed to the public source-code repository because it may contain embeddings and metadata derived from local files.

---

## 11. Machine-Learning Environment

The final retrieval implementation uses:

```text
BERT
MobileCLIP
```

BERT is used for document and query text embeddings.

MobileCLIP is used for:

- image embeddings;
- text embeddings used for image retrieval.

The required model resources were successfully loaded during final integration testing.

Once the dependencies and model resources are available locally, normal retrieval operations can run without a remote inference API.

---

## 12. Final Project Structure

The final repository is organized around the following major components:

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

The main functional areas are:

```text
backend/
```

Java parsing, metadata processing, retrieval integration, CLI functionality, and backend tests.

```text
frontend/
```

Flutter Windows desktop application.

```text
scripts/
```

Local Python retrieval service, machine-learning integration, and ChromaDB support.

```text
docs/
```

Project requirements, architecture, testing, API, maintenance, accessibility, user, and compliance documentation.

```text
dataset/
```

Dataset documentation and validation resources.

---

## 13. Testing Environment Validation

The final development environment supported both automated and manual testing.

Backend testing included:

- JUnit unit tests;
- integration testing;
- ChromaDB retrieval-service testing;
- JaCoCo code coverage analysis;
- performance and stress testing.

Frontend testing included:

- Flutter tests;
- manual Windows desktop testing.

The final Maven test execution completed successfully with:

```text
BUILD SUCCESS
```

---

## 14. Offline-First Environment Validation

The final environment was designed so that normal retrieval processing is performed locally.

Local operations include:

- file parsing;
- metadata extraction;
- text embedding;
- image embedding;
- vector storage;
- semantic search;
- result ranking;
- user-interface interaction.

Initial dependency installation and machine-learning model download may require Internet access.

After setup is complete, normal indexing and retrieval do not require a cloud search API.

---

## 15. Final Validation Summary

By the end of the project, the required Windows development environment had been successfully configured and used to implement the complete retrieval workflow.

The following major components were operational:

- Flutter Windows desktop frontend
- Java backend
- Maven build system
- Apache Tika document parsing
- Python local retrieval service
- FastAPI
- ChromaDB persistent vector storage
- BERT text embeddings
- MobileCLIP image retrieval
- JUnit backend tests
- JaCoCo code coverage
- Flutter frontend tests
- Git and GitHub version control

The final environment therefore supports continued development, testing, demonstration, and Windows execution of the Offline Accessible Multimodal Local Content Retrieval System.