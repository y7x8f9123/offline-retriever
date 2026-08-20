# Project Requirements Document (PRD)

## 1. Project Overview

### Project Name

Offline Accessible Multimodal Local Content Retrieval System

### Background

Many users struggle to efficiently search and retrieve information from local files such as PDFs, documents, images, and screenshots.

Existing solutions may depend on cloud services, require paid subscriptions, or provide limited accessibility support.

This project was designed to explore an offline-first, open-source local retrieval system that enables semantic search across multiple content types while protecting user privacy.

The original project concept considered cross-platform desktop support. During development, the final validated release scope was refined to Windows Desktop.

### Project Goal

Develop an offline-first desktop application that allows users to search local documents and images using semantic search technologies.

The system should:

- Support semantic retrieval across text documents and images.
- Perform normal retrieval processing locally.
- Protect user data from unnecessary cloud upload.
- Provide an accessibility-focused desktop interface.
- Maintain a modular and maintainable architecture.
- Provide a final validated Windows desktop implementation.

---

## 2. Target Users

The system is designed for users who need to efficiently search and retrieve information from local files while maintaining privacy.

### Primary Users

- General computer users who store large numbers of local documents and images.
- Students who need to locate study materials, reports, notes, and reference files.
- Office users who frequently search local reports and business documents.
- Users who benefit from keyboard navigation, high-contrast presentation, font scaling, and semantic accessibility support.

### User Needs

Users should be able to:

- Search local content using natural-language queries.
- Retrieve semantically related text documents.
- Retrieve semantically related images.
- Manage indexed local files.
- Keep normal retrieval processing on the local computer.
- Use accessibility-focused interface features.
- Open original local files from retrieval results where possible.

---

## 3. Functional Requirements

The final system shall provide the following core functionality.

### 3.1 File Management

The application shall support local indexing of:

```text
TXT
PDF
DOCX
JPG
JPEG
PNG
```

The system shall allow users to:

- Select supported local files.
- Index supported files.
- View indexed files.
- Delete indexed records.
- Open original local files where the source path remains valid.

Deleting an indexed record shall not delete the original user file.

---

### 3.2 Text Content Processing

For supported text documents, the system shall:

- Extract textual content.
- Support TXT parsing.
- Support text extraction from PDF files.
- Support text extraction from DOCX files.
- Divide long documents into overlapping chunks.
- Generate semantic text embeddings using a BERT-based model.
- Store text vectors and metadata locally in ChromaDB.

OCR for scanned or image-only PDFs is outside the final project scope.

---

### 3.3 Image Processing

For supported image files, the system shall:

- Accept JPG, JPEG, and PNG files.
- Generate image embeddings using MobileCLIP.
- Store image embeddings and metadata locally.
- Support text-to-image semantic retrieval.

---

### 3.4 Semantic Search

The system shall:

- Accept natural-language search queries.
- Generate BERT embeddings for text retrieval.
- Generate MobileCLIP text embeddings for image retrieval.
- Perform cosine-similarity vector search.
- Aggregate chunk-level text matches to file-level results.
- Combine text and image results into a unified ranking.
- Return the requested top-K results.

---

### 3.5 Persistent Vector Storage

The system shall use ChromaDB for local persistent vector storage.

Separate logical collections shall be used for:

```text
Text content
Image content
```

Indexed data should remain available between application sessions unless explicitly deleted.

---

### 3.6 User Interface

The final desktop interface shall provide:

- File selection
- Search input
- Search-result display
- Indexed-file display
- Settings
- File-opening operations where supported
- Error and status feedback

The final validated frontend target is Windows Desktop.

---

### 3.7 Accessibility

The application shall include accessibility-focused features including:

- Keyboard navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic accessibility labels

WCAG 2.1 AA is used as a design objective.

Formal third-party WCAG certification is outside the scope of the eight-week project.

---

## 4. Non-Functional Requirements

### 4.1 Performance

The application should:

- Return semantic search results within a practical response time on the development environment.
- Remain usable with larger local file collections.
- Support at least 1,000 indexed local text files during project validation.

Performance may depend on:

- CPU performance
- Available memory
- Storage performance
- Machine-learning model loading time
- Number and size of indexed files

---

### 4.2 Privacy

Normal retrieval operations shall be performed locally.

The system shall not require user files to be uploaded to a remote semantic-search service.

Local operations include:

- File parsing
- Metadata extraction
- Text embedding
- Image embedding
- Vector storage
- Semantic retrieval
- Result ranking

Initial dependency installation and machine-learning model acquisition may require Internet access.

---

### 4.3 Platform Compatibility

The final validated release scope is:

```text
Windows Desktop
```

Flutter may provide framework-level support for additional desktop platforms.

However, macOS and Linux runtime validation are outside the final project release scope.

---

### 4.4 Accessibility

The interface should follow WCAG 2.1 AA design principles where applicable.

Accessibility functionality should remain usable under:

- Keyboard-only interaction
- High Contrast Mode
- Increased font sizes

Formal accessibility certification is not a final project requirement.

---

### 4.5 Maintainability

The project shall:

- Use a modular architecture.
- Maintain separation between frontend, Java backend, Python retrieval service, machine-learning models, and vector storage.
- Provide clear technical documentation.
- Maintain readable source code.
- Provide API, testing, maintenance, user, accessibility, and compliance documentation.

---

### 4.6 Offline-First Operation

Once required dependencies and model resources are installed locally, normal semantic retrieval should not depend on a remote inference or search API.

The local retrieval service should operate on the loopback interface:

```text
127.0.0.1
```

---

## 5. System Scope

### 5.1 In Scope

The final project scope includes:

- Windows desktop application
- Offline-first local retrieval
- TXT indexing
- PDF text extraction and indexing
- DOCX text extraction and indexing
- JPG indexing
- JPEG indexing
- PNG indexing
- BERT-based semantic text retrieval
- MobileCLIP-based image retrieval
- Text-to-image semantic search
- Long-document chunking
- File-level result aggregation
- Multimodal result ranking
- ChromaDB persistent vector storage
- Indexed-file listing
- Indexed-file deletion
- Opening original files
- Accessibility-focused UI functionality
- Java CLI functionality
- Local FastAPI integration
- Automated and manual testing
- Technical and user documentation

---

### 5.2 Out of Scope

The following features are outside the final project scope:

- Cloud-based file storage
- Remote semantic-search APIs
- Real-time collaboration
- Online search-engine integration
- Mobile application release
- OCR for scanned documents
- Full conversational LLM assistant
- Cloud synchronization
- Formal WCAG certification
- macOS runtime validation
- Linux runtime validation
- Production-scale distributed deployment

---

## 6. Final Success Criteria

The project is considered successful if the final implementation demonstrates the following:

### File Processing

- TXT files can be indexed.
- PDF files with extractable text can be indexed.
- DOCX files can be indexed.
- JPG, JPEG, and PNG files can be indexed.

### Semantic Retrieval

- Natural-language text queries return semantically related text documents.
- Natural-language queries can retrieve semantically related images.
- Text and image results can be combined into one ranked result set.

### Persistent Storage

- ChromaDB stores indexed vectors locally.
- Indexed records remain available across normal application sessions.
- Indexed files can be listed and removed.

### Long-Document Retrieval

- Long documents can be divided into chunks.
- Chunk-level retrieval is aggregated back to file-level results.
- The same document does not unnecessarily appear multiple times.

### Scalability

The system shall successfully validate indexing of at least:

```text
1,000 local text files
```

without backend failure.

### Performance

Semantic search should complete within a practical local response time under the validated project workload.

### Offline-First Operation

Normal indexing and semantic retrieval shall operate locally after required dependencies and model resources are available.

### Accessibility

The final interface shall provide:

- Keyboard navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic labels

### Platform

The final application shall be functionally validated on:

```text
Windows Desktop
```

---

## 7. Assumptions and Constraints

### 7.1 Assumptions

The project assumes that:

- Users have sufficient local storage.
- Users operate on modern desktop hardware.
- Required BERT and MobileCLIP resources can be installed locally.
- Supported local files are accessible by the application.
- Text-based PDF and DOCX content can be extracted by the configured document-processing layer.

---

### 7.2 Constraints

The project is constrained by:

- Local CPU and memory performance.
- Machine-learning model startup time.
- Local storage capacity.
- Embedding-model accuracy.
- Differences between BERT and MobileCLIP score distributions.
- Lack of OCR for scanned documents.
- Eight-week project duration.
- Final Windows-only validation scope.

The initial installation process may require Internet access for dependencies and model resources.

---

## 8. Final Implementation Architecture

The final retrieval architecture is:

```text
Flutter Windows Frontend
          |
          v
      Java Backend
          |
          v
 Local FastAPI Service
   127.0.0.1:8765
          |
    +-----+------+
    |            |
    v            v
   BERT      MobileCLIP
    |            |
    +-----+------+
          |
          v
       ChromaDB
```

The Java layer handles file processing and application integration.

The Python service handles machine-learning inference, vector storage, semantic retrieval, aggregation, and multimodal ranking.

---

## 9. Requirement Adjustments During Development

Several project requirements were refined during implementation.

### Platform Scope

The initial project concept considered Windows, macOS, and Linux.

The final validated release scope was adjusted to Windows Desktop.

### Accessibility

WCAG 2.1 AA remained an accessibility design objective.

The final project implements accessibility-focused functionality but does not claim formal WCAG certification.

### Document Processing

Text-based PDF and DOCX extraction is supported.

OCR for image-only documents was not included in the final scope.

### Retrieval Architecture

The final implementation uses:

- Java for file-processing and application integration
- Python FastAPI for machine-learning retrieval services
- BERT for text embeddings
- MobileCLIP for image retrieval
- ChromaDB for persistent vector storage

These adjustments reflect the implemented and validated system at the end of the project.

---

## 10. Related Documentation

Detailed implementation and validation information is available in:

```text
docs/System_Architecture_Design.md
docs/API_Reference.md
docs/Testing_Report.md
docs/Maintenance_Guide.md
docs/End_User_Manual.md
docs/Accessibility_User_Guide.md
docs/Open_Source_Compliance_Report.md
docs/Environment_Setup_Report.md
docs/Risk_Management_Plan.md
```

The project root:

```text
README.md
```

provides the main overview and execution instructions.

---

## 11. Conclusion

The final project requirements define a Windows desktop, offline-first multimodal retrieval application capable of indexing and semantically searching supported local documents and images.

The implemented system combines:

- Flutter
- Java
- FastAPI
- BERT
- MobileCLIP
- ChromaDB

to provide local document parsing, embedding generation, persistent vector storage, semantic retrieval, multimodal ranking, and an accessibility-focused desktop interface.

The final scope reflects both the original project objectives and the implementation adjustments confirmed during the eight-week development process.