# Project Demo Script

## Offline Accessible Multimodal Local Content Retrieval System

**Target Duration:** Approximately 5 minutes  
**Primary Demo Platform:** Windows Desktop  
**Version:** Final Project Version  
**Date:** 2026-08

---

## 1. Introduction

Hello, this is a demonstration of the Offline Accessible Multimodal Local Content Retrieval System.

The purpose of this project is to provide an offline-first Windows desktop application that allows users to semantically search local documents and images without relying on a cloud retrieval service during normal operation.

The project focuses on three main goals:

- Offline-first local processing
- Multimodal semantic retrieval
- Accessible user interaction

The final system combines:

- Flutter desktop frontend
- Java backend
- Local Python FastAPI service
- BERT text embeddings
- MobileCLIP image and text embeddings
- ChromaDB persistent vector storage

---

## 2. Application Overview

First, I will show the main application interface.

The Flutter application provides the main workflow for:

- Importing local files
- Viewing indexed files
- Entering semantic queries
- Viewing ranked search results
- Opening retrieved files
- Changing accessibility settings

The supported file formats are:

```text
TXT
PDF
DOCX
JPG
JPEG
PNG
```

Unlike basic keyword search, this system converts text and images into semantic vector representations.

This allows retrieval based on meaning rather than only exact filename or keyword matching.

---

## 3. Local Retrieval Service

Before performing semantic retrieval, the local Python service is started.

It runs at:

```text
127.0.0.1:8765
```

During startup, it initializes:

1. ChromaDB
2. BERT
3. MobileCLIP

The service health can be checked using:

```powershell
Invoke-RestMethod http://127.0.0.1:8765/health
```

A healthy system confirms that:

```text
status = ok
bert_loaded = True
mobileclip_loaded = True
```

The retrieval service remains local to the user's computer.

---

## 4. File Import and Indexing

Next, I will import local files.

The application supports text documents:

```text
TXT
PDF
DOCX
```

and images:

```text
JPG
JPEG
PNG
```

For text documents, Java extracts the textual content and sends it to the local retrieval service.

The text indexing workflow is:

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

For images, MobileCLIP generates image embeddings:

```text
Image
  ↓
MobileCLIP
  ↓
Image Embedding
  ↓
ChromaDB
```

The original local files remain in their existing locations.

---

## 5. Long-Document Retrieval

Long documents are divided into overlapping chunks before embedding.

The current configuration is:

```text
Chunk size: 400 words
Chunk overlap: 50 words
```

Each chunk receives its own BERT embedding.

During search, chunk-level matches are aggregated back to file level.

This means a long document can be searched using information from different sections while still appearing only once in the final result list.

---

## 6. Multimodal Search

Now I will demonstrate semantic search.

A single natural-language query is processed through two retrieval paths:

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

BERT searches indexed document content.

MobileCLIP converts the same query into its multimodal embedding space and retrieves relevant images.

The system then combines text and image results into one ranked list.

---

## 7. Search Example

For example, I can search for:

```text
software engineering
```

The system generates a BERT embedding for the text query and searches the document vectors stored in ChromaDB.

For an image-oriented query, I can use a descriptive term such as:

```text
red
```

MobileCLIP can retrieve visually related indexed images.

The user does not need to switch between separate document and image search pages.

---

## 8. Multimodal Ranking

BERT and MobileCLIP use different embedding models and their raw cosine similarity values have different score distributions.

The application therefore applies image-score calibration before combining the results.

Current configuration:

```text
IMAGE_SCORE_CALIBRATION = 1.25
```

The purpose is to make text and image ranking more comparable rather than to force one content type above another.

---

## 9. Search Results and File Opening

The Search Results page can contain both text and image files.

Results include information such as:

- File name
- File type
- Content type
- Similarity score
- Ranking position

The score is used as a ranking signal rather than a probability.

Users can also open the original local file.

The file is opened using the associated Windows application.

Deleting an indexed record removes it from the local retrieval database but does not delete the original source file.

---

## 10. Accessibility Features

Accessibility is an important part of the project.

The final Windows interface includes:

- Keyboard navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic accessibility labels

Keyboard interaction includes:

```text
Tab
Shift + Tab
Enter
Space
```

The Settings page allows users to enable High Contrast Mode and adjust font size.

Available font-size levels include:

```text
Small
Medium
Large
Extra Large
```

The project uses WCAG 2.1 AA as an accessibility design objective.

---

## 11. Offline-First Design

Normal retrieval operations are performed locally.

These include:

- File parsing
- Text extraction
- BERT inference
- MobileCLIP inference
- ChromaDB storage
- Semantic search
- Result ranking

The FastAPI service is bound to:

```text
127.0.0.1
```

The application does not require a cloud semantic-search API during normal retrieval.

Initial dependency installation and model acquisition may require Internet access.

---

## 12. Scalability Validation

The project was tested with 1,000 generated TXT files.

Before the stress test:

```text
12 text records
```

After indexing:

```text
1012 text records
```

The indexed-file listing confirmed:

```text
1000 stress-test files
```

Measured indexing batches included:

```text
200 files → 14.81 seconds
300 files → 25.94 seconds
450 files → 41.72 seconds
```

With more than 1,000 text records stored, an end-to-end semantic search completed in approximately:

```text
807 milliseconds
```

for the query:

```text
software engineering
```

This demonstrates that the system remains operational at the planned validation scale.

---

## 13. Testing

The Java backend uses:

```text
JUnit
JaCoCo
```

Final measured coverage includes:

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

Core reusable modules achieved high coverage.

The complete backend test suite completed successfully with:

```text
BUILD SUCCESS
```

Flutter tests and manual Windows end-to-end testing were also used.

---

## 14. Open-Source and Documentation

The project is released under:

```text
Apache License 2.0
```

The repository includes technical and user documentation covering:

- Project requirements
- System architecture
- API reference
- Testing
- Maintenance
- End-user operation
- Accessibility
- Open-source compliance

This documentation reflects the final Windows implementation.

---

## 15. Final Scope and Limitations

The final validated release target is:

```text
Windows Desktop
```

The main known limitations are:

- OCR is not implemented for scanned or image-only PDFs.
- Cold model startup may take additional time.
- Performance depends on local hardware.
- Formal WCAG certification was not performed.

These limitations do not prevent the main multimodal retrieval workflow from operating.

---

## 16. Conclusion

This project demonstrates a functional offline-first multimodal local retrieval system.

The final implementation combines:

```text
Flutter
Java
FastAPI
BERT
MobileCLIP
ChromaDB
```

The system provides:

- Semantic retrieval for TXT, PDF, and DOCX files
- Semantic image retrieval for JPG, JPEG, and PNG files
- Long-document chunking
- File-level aggregation
- Persistent vector storage
- Multimodal ranking
- Local file opening
- Accessibility-focused interaction
- Offline-first processing
- Automated testing
- 1,000-file scalability validation

The final project therefore demonstrates an integrated Windows desktop workflow for local multimodal semantic retrieval.

Thank you for watching this demonstration.