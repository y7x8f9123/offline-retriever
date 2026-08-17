# Project Demo Script

## Offline Accessible Multimodal Local Content Retrieval System

**Target Duration:** Approximately 5–7 minutes  
**Primary Demo Platform:** Windows Desktop  
**Version:** Week 7 Prototype  
**Date:** 2026-08

---

## 1. Introduction

Hello, this is a demonstration of the Offline Accessible Multimodal Local Content Retrieval System.

The purpose of this project is to provide an offline-first desktop application that allows users to semantically search local documents and images without relying on a cloud retrieval service during normal operation.

The project focuses on three main goals:

- Offline-first local processing
- Multimodal semantic retrieval
- Accessible user interaction

The current system combines:

- Flutter desktop frontend
- Java backend
- Local Python FastAPI service
- BERT text embeddings
- MobileCLIP image embeddings
- ChromaDB persistent vector storage

---

## 2. Application Overview

First, I will show the main application interface.

The Flutter application provides the user-facing workflow for:

- Importing local files
- Viewing indexed files
- Entering semantic queries
- Viewing ranked results
- Opening retrieved files
- Changing accessibility settings

The current system supports:

```text
TXT
PDF
DOCX
JPG
JPEG
PNG
```

Unlike a basic keyword-search application, this project converts document and image content into semantic vector representations.

This allows retrieval based on meaning rather than only exact keyword matching.

---

## 3. Local Retrieval Service

Before using semantic retrieval, the local retrieval service is started.

The Python service runs locally at:

```text
127.0.0.1:8765
```

During startup, it initializes:

1. ChromaDB
2. BERT
3. MobileCLIP

The health endpoint can be used to verify the system.

For example:

```powershell
Invoke-RestMethod http://127.0.0.1:8765/health
```

A healthy system reports:

```text
status = ok
bert_loaded = True
mobileclip_loaded = True
```

The service remains local to the user's computer.

---

## 4. File Import Demonstration

Next, I will open the File Library.

The user can select supported local files from the computer.

The current application supports both documents and images.

Document formats include:

```text
TXT
PDF
DOCX
```

Image formats include:

```text
JPG
JPEG
PNG
```

The original files remain in their existing local locations.

The retrieval system stores metadata and semantic vector representations rather than replacing the original files.

---

## 5. Text Document Processing

When a text document is imported, Java first extracts its content.

TXT files are processed directly.

PDF and DOCX files are parsed using the document-processing layer.

The extracted text is then sent to the local FastAPI service.

The text retrieval workflow is:

```text
Document
   ↓
Java Parsing
   ↓
Text Extraction
   ↓
Chunking
   ↓
BERT
   ↓
ChromaDB
```

This provides semantic document retrieval using a pretrained language model rather than the earlier deterministic vector prototype.

---

## 6. Long-Document Chunking

A major improvement in the current version is long-document chunking.

BERT cannot efficiently represent an arbitrarily long document as one input.

Therefore, long documents are automatically divided into overlapping chunks.

The current configuration is:

```text
Chunk size: 400 words
Chunk overlap: 50 words
```

Each chunk receives its own BERT embedding.

This means that information near the end of a long PDF, DOCX, or TXT document can still participate in retrieval.

Without chunking, later document content could be lost because of model input limits.

---

## 7. File-Level Aggregation

Although a long document may contain several internal vector records, the user should not see the same file repeatedly.

The retrieval service therefore groups matching chunks by their source file.

The best matching chunk is used to represent the document.

The final search results contain one result per source file.

This keeps the interface understandable while still allowing detailed long-document retrieval.

---

## 8. Image Processing

Images use a separate semantic pipeline.

When a JPG, JPEG, or PNG file is indexed, MobileCLIP creates an image embedding.

The workflow is:

```text
Local Image
    ↓
MobileCLIP
    ↓
Image Embedding
    ↓
ChromaDB
```

The image does not need to contain readable text.

MobileCLIP represents visual semantic information directly.

---

## 9. Multimodal Search Demonstration

Now I will demonstrate search.

The user enters one text query.

That query is processed through two retrieval paths.

```text
             Query
            /     \
           /       \
        BERT     MobileCLIP
         ↓           ↓
    Text Search   Image Search
           \       /
            \     /
          Combined Ranking
```

BERT searches the indexed document content.

MobileCLIP converts the same text query into its multimodal embedding space and searches the indexed images.

The application then combines both result types into one ranked list.

---

## 10. Text Search Example

For example, I can enter:

```text
database
```

The system generates a BERT embedding for the query.

ChromaDB performs cosine-similarity retrieval against the indexed text chunks.

Relevant documents are aggregated to file level and ranked.

This demonstrates semantic text retrieval rather than simple filename matching.

---

## 11. Image Search Example

For an image-oriented query, I can enter:

```text
red
```

The query is processed by MobileCLIP.

A visually relevant red image can then appear in the same search result list as text documents.

This demonstrates text-to-image retrieval.

The user does not need to switch to a separate image-search page.

---

## 12. Multimodal Score Calibration

BERT and MobileCLIP are independent machine-learning models.

Their raw cosine similarity values do not necessarily have identical score distributions.

The system therefore applies a small image-score calibration before combining the two result sets.

Current configuration:

```text
IMAGE_SCORE_CALIBRATION = 1.25
```

This calibration was tested using text-oriented and image-oriented queries.

The purpose is not to force images above documents.

The purpose is to make the two retrieval spaces more comparable when creating the unified ranking.

---

## 13. Search Results

The final Search Results page can contain both:

```text
text
image
```

results.

Each result includes information such as:

- File name
- File type
- Content type
- Similarity score
- Ranking position
- Open control

The score represents semantic similarity.

It should be interpreted as a ranking signal rather than as a probability or percentage.

---

## 14. Opening Retrieved Files

Each result provides an Open function.

Before opening a result, the application verifies that the original local file still exists.

If it exists, the operating system opens the file using the associated application.

For example:

- TXT files can open in a text editor.
- PDF files can open in a PDF viewer.
- DOCX files can open in a Word-compatible application.
- JPG and PNG files can open in an image viewer.

This allows the user to move directly from semantic retrieval to the original source file.

---

## 15. Persistent Vector Storage

The current version uses ChromaDB for persistent vector storage.

Two main collections are used:

```text
offline_retriever_text
offline_retriever_images
```

The text collection stores BERT embeddings.

The image collection stores MobileCLIP embeddings.

Because storage is persistent, indexed vectors can remain available across retrieval-service restarts.

This replaces the earlier in-memory-only vector-store prototype.

---

## 16. Accessibility Demonstration

Accessibility is another major project requirement.

The Flutter interface includes several accessibility-focused features.

### Keyboard Navigation

Important controls can be accessed using the keyboard.

For example:

```text
Tab
Shift + Tab
Enter
Space
```

This supports users who do not rely on mouse interaction.

### High Contrast Mode

The Settings page provides High Contrast Mode.

When enabled, the interface changes immediately to provide stronger visual separation.

### Dynamic Font Scaling

Users can select different text sizes.

The current interface provides:

- Small
- Medium
- Large
- Extra Large

The interface updates when the setting changes.

### Semantic Labels

Important interface controls contain semantic accessibility information to improve compatibility with assistive technologies.

These features support the project's WCAG 2.1 AA design objectives.

---

## 17. Offline-First Demonstration

The retrieval architecture is designed to keep normal processing local.

The system performs locally:

- File parsing
- Text extraction
- BERT inference
- MobileCLIP inference
- Vector storage
- Semantic search
- Result ranking

The FastAPI service runs only on:

```text
127.0.0.1
```

The application does not require a cloud semantic-search API during normal retrieval.

The machine-learning models must first be available locally.

After setup, the retrieval workflow can operate without continuous Internet access.

---

## 18. Performance and Scalability Test

The project requirement includes indexing at least 1,000 local files.

To validate this requirement, I generated 1,000 local TXT files and indexed them using the actual retrieval pipeline.

Before the stress test, the system contained:

```text
12 text records
```

After indexing:

```text
1012 text records
```

The indexed-file listing independently confirmed all:

```text
1000 stress-test files
```

The measured indexing batches included:

```text
200 files → 14.81 seconds
300 files → 25.94 seconds
450 files → 41.72 seconds
```

The initial 50-file batch was used for functional validation and was not timed.

This demonstrates that the system can index at least 1,000 local files successfully.

---

## 19. Search Performance

After the database contained more than 1,000 text records, an end-to-end semantic search was performed using:

```text
software engineering
```

The measured search time was approximately:

```text
807 milliseconds
```

This measurement includes the Java CLI request and local retrieval-service response.

The test confirms that semantic retrieval remains functional after the 1,000-file indexing test.

---

## 20. Automated Testing

The project contains automated testing for both backend and frontend components.

The Java backend uses:

```text
JUnit
```

JaCoCo is used for backend coverage measurement.

Current backend coverage results are approximately:

```text
Overall instruction coverage: 84%
Core functional modules: 93–100%
Vector retrieval package: 98%
```

The Flutter frontend also includes widget tests for major interface behavior.

---

## 21. Open-Source Compliance

The project is released under:

```text
Apache License 2.0
```

A LICENSE file is included in the repository.

Major open-source components include:

- Flutter
- Apache Tika
- FastAPI
- ChromaDB
- PyTorch
- Transformers
- BERT-related model resources
- MobileCLIP-related model resources
- file_picker
- url_launcher
- JUnit
- JaCoCo

The Open-Source Compliance Report documents dependency and model-license considerations.

---

## 22. Documentation

The repository includes technical and user documentation.

Major documents include:

- Product Requirements Document
- System Architecture Design
- API Reference
- Maintenance Guide
- End-User Manual
- Accessibility User Guide
- Open-Source Compliance Report
- Demo Script

These documents describe the current implementation, maintenance procedures, APIs, user workflow, accessibility operation, and licensing considerations.

---

## 23. Cross-Platform Status

Flutter includes desktop support for:

```text
Windows
macOS
Linux
```

The current project has been developed and functionally validated primarily on Windows.

The Flutter project contains the platform targets for macOS and Linux.

However, complete runtime testing of the full:

```text
Flutter
Java
Python
BERT
MobileCLIP
ChromaDB
```

pipeline on macOS and Linux requires access to those environments.

Therefore, Windows is the currently validated demo platform.

---

## 24. Current Limitations

The current prototype has several remaining limitations.

### OCR

OCR is not currently implemented.

Image-only scanned PDFs may therefore contain no extractable text for BERT indexing.

### In-Application Preview

Retrieved files are currently opened using the operating system's associated application rather than a complete built-in preview interface.

### Additional Office Formats

The current implemented document formats are:

```text
TXT
PDF
DOCX
```

Other Microsoft Office formats are not part of the current complete retrieval workflow.

### Cross-Platform Runtime Validation

Windows has been functionally tested.

Full macOS and Linux runtime validation remains future work when suitable environments are available.

---

## 25. Conclusion

This project demonstrates a functional offline-first multimodal local retrieval system.

The current implementation combines:

```text
Flutter
Java
FastAPI
BERT
MobileCLIP
ChromaDB
```

The system currently provides:

- TXT, PDF, and DOCX semantic retrieval
- JPG, JPEG, and PNG semantic image retrieval
- Long-document chunking
- File-level aggregation
- Persistent vector storage
- Multimodal ranking
- Local file opening
- Accessibility-focused interaction
- Offline-first processing
- Automated testing
- 1,000-file scalability validation
- Technical and user documentation
- Open-source compliance documentation

The project has therefore progressed from an early local vector-search prototype into an integrated multimodal semantic retrieval application.

Thank you for watching this demonstration.