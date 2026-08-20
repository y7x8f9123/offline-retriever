# Offline Multimodal Local Content Retrieval System

## Overview

This project is an offline-first multimodal retrieval application for semantically searching local documents and images.

The system supports local indexing and retrieval for:

- TXT
- PDF
- DOCX
- JPG
- JPEG
- PNG

The application combines a Flutter frontend, Java backend, local FastAPI retrieval service, BERT text embeddings, MobileCLIP image embeddings, and ChromaDB persistent vector storage.

Once the required dependencies and machine-learning models are available locally, normal indexing and semantic retrieval can operate without external network access.

---

## Key Features

- Offline-first local retrieval
- Semantic text search using BERT
- Text-to-image retrieval using MobileCLIP
- Persistent vector storage using ChromaDB
- TXT, PDF, and DOCX document indexing
- JPG, JPEG, and PNG image indexing
- Long-document chunking
- File-level result aggregation
- Multimodal score calibration
- Local file library
- Accessibility-focused Flutter interface
- WCAG 2.1 AA design objectives
- Java CLI and local FastAPI integration
- Windows desktop release target

---

## Architecture

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

### Text Retrieval

```text
TXT / PDF / DOCX
       ↓
Java Parsing
       ↓
Long-Document Chunking
       ↓
BERT Embeddings
       ↓
ChromaDB
       ↓
Semantic Search
       ↓
File-Level Aggregation
```

### Image Retrieval

```text
JPG / JPEG / PNG
       ↓
MobileCLIP Image Embedding
       ↓
ChromaDB
       ↓
MobileCLIP Text Query
       ↓
Semantic Image Retrieval
```

---

## Technology Stack

### Frontend

- Flutter
- Dart

### Java Backend

- Java
- Maven
- Apache Tika
- Gson
- JUnit
- JaCoCo

### Local Retrieval Service

- Python
- FastAPI
- Uvicorn

### Machine Learning

- BERT for text embeddings
- MobileCLIP for image and text-image embeddings
- PyTorch-based local inference

### Vector Storage

- ChromaDB
- Cosine similarity
- Persistent local storage

---

## Supported File Types

| Type | Formats |
|---|---|
| Text documents | TXT, PDF, DOCX |
| Images | JPG, JPEG, PNG |

---

## Long-Document Retrieval

Long documents are divided into overlapping chunks before embedding.

Current configuration:

```text
Chunk size: 400 words
Chunk overlap: 50 words
```

Each chunk is indexed independently in ChromaDB.

Search results are then aggregated back to file level so that a long document appears only once in the final result list.

---

## Multimodal Ranking

Text and image retrieval use different embedding models.

Because BERT and MobileCLIP cosine similarity scores have different distributions, image scores are calibrated before text and image results are combined.

Current configuration:

```text
IMAGE_SCORE_CALIBRATION = 1.25
```

The final unified result list contains both text documents and images.

---

## Local Retrieval Service

The local retrieval service runs at:

```text
http://127.0.0.1:8765
```

Main endpoints:

```text
GET  /health
GET  /files
POST /index-text
POST /index-image
POST /search
POST /delete
```

The service is bound to the local loopback interface and is not intended to operate as a public network service.

---

## Running the Retrieval Service

From the project root:

```powershell
python scripts\service\retrieval_server.py
```

A successful startup should eventually display:

```text
Offline Retriever backend ready.
Uvicorn running on http://127.0.0.1:8765
```

Check service health with:

```powershell
Invoke-RestMethod http://127.0.0.1:8765/health
```

---

## Building the Java Backend

From the backend directory:

```powershell
cd backend
mvn clean package -DskipTests
```

The packaged JAR is generated at:

```text
backend/target/backend-1.0-SNAPSHOT.jar
```

---

## Java CLI

### Index Files

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar index example.txt
```

Multiple files can be indexed:

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar index file1.txt file2.pdf image.png
```

### Search

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar search "software engineering" 5
```

### List Indexed Files

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar list
```

### Delete Indexed File

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar delete <file-id>
```

---

## Flutter Application

The Flutter application is located in:

```text
frontend/
```

Install dependencies:

```powershell
cd frontend
flutter pub get
```

Run on Windows:

```powershell
flutter run -d windows
```

Run tests:

```powershell
flutter test
```

The current release targets Windows and has been functionally validated on Windows.

---

## Testing

### Java Tests

```powershell
cd backend
mvn test
```

### Java Coverage

```powershell
mvn clean test jacoco:report
```

Final JaCoCo results:

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

The overall backend coverage includes application entry points, integration-oriented components, and service-related code that are less suitable for direct unit testing.

Core functional modules maintain substantially higher coverage, with the major reusable packages achieving between 63% and 100% instruction coverage.

Several core packages, including the embedding, parser, model, and I/O modules, achieved full instruction coverage, while the vector package reached 95% and the storage package reached 63%.

### Flutter Tests

```powershell
cd frontend
flutter test
```

---

## Scalability Validation

The retrieval pipeline was tested with 1,000 generated TXT files.

Observed results:

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

The initial 50-file batch was used for functional validation and was not timed.

With more than 1,000 text records stored, an end-to-end semantic search completed in approximately:

```text
807 ms
```

for the query:

```text
software engineering
```

---

## Offline-First Design

Normal retrieval processing is local.

The system performs locally:

- File parsing
- Metadata extraction
- BERT inference
- MobileCLIP inference
- Vector storage
- Semantic retrieval
- Result ranking

No remote retrieval API is required during normal operation.

Initial dependency installation and model download may require Internet access.

---

## Documentation

Detailed project documentation is available under:

```text
docs/
```

Important documents include:

- `System_Architecture_Design.md`
- `API_Reference.md`
- `Maintenance_Guide.md`
- `End_User_Manual.md`
- `Accessibility_User_Guide.md`
- `Testing_Report.md`
- `Open_Source_Compliance_Report.md`
- `Environment_Setup_Report.md`
- `Risk_Management_Plan.md`
- `PRD.md`
- `Demo_Script.md`

---

## Project Scope

The project was developed as an eight-week software engineering project.

The final application release targets Windows. The project focuses on demonstrating a complete local retrieval workflow including file ingestion, parsing, embedding generation, persistent vector storage, semantic retrieval, multimodal ranking, and an accessible desktop user interface.

---

## License

This project is released under the Apache License 2.0.

See the `LICENSE` file for the full license text.