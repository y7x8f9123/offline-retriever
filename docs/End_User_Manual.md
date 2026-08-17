# End-User Manual

## Offline Accessible Multimodal Local Content Retrieval System

**Version:** Week 7 Prototype  
**Primary Tested Platform:** Windows Desktop  
**Date:** 2026-08

---

## 1. Introduction

The Offline Accessible Multimodal Local Content Retrieval System is designed to help users search local documents and images using semantic search while keeping normal retrieval processing on the local computer.

The current application combines:

- Flutter desktop user interface
- Java local backend
- Local FastAPI retrieval service
- BERT text embeddings
- MobileCLIP image embeddings
- ChromaDB persistent vector storage

The current prototype supports:

- TXT document indexing
- PDF document indexing
- DOCX document indexing
- JPG image indexing
- JPEG image indexing
- PNG image indexing
- Semantic text search
- Text-to-image semantic search
- Long-document retrieval
- Persistent local indexing
- Ranked multimodal search results
- Opening original local files
- Keyboard navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic accessibility labels

The application follows three main principles:

- Offline-first local processing
- Multimodal semantic retrieval
- Accessible user interaction

---

## 2. Supported File Types

The current retrieval workflow supports:

| Content Type | Formats |
|---|---|
| Text documents | TXT, PDF, DOCX |
| Images | JPG, JPEG, PNG |

Text-based PDF and DOCX files are processed by extracting their textual content.

Scanned or image-only PDFs may not be searchable if they contain no extractable text because OCR is not currently implemented.

---

## 3. System Requirements

The current prototype has been developed and functionally validated primarily on Windows.

For source-based execution, the following software is required:

- Windows operating system
- Flutter SDK
- Java Development Kit
- Maven
- Python
- Required Python machine-learning packages
- Required BERT and MobileCLIP model resources
- Git, if cloning the repository

Users should also have appropriate desktop applications installed for opening local files such as PDF, DOCX, TXT, JPG, and PNG files.

---

## 4. Project Structure

The main project structure includes:

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

The main runtime components are:

```text
frontend/
```

Flutter user interface.

```text
backend/
```

Java file-processing and application backend.

```text
scripts/
```

Python retrieval service, embedding models, and ChromaDB integration.

```text
chroma_db/
```

Persistent local vector database generated during runtime.

---

## 5. Preparing the Application

### 5.1 Obtain the Project

Clone or download the repository to the local computer.

Open the project directory in a development environment such as Visual Studio Code.

---

## 5.2 Check Flutter

Run:

```powershell
flutter doctor
```

Resolve any required Flutter desktop-development issues before continuing.

---

## 5.3 Install Flutter Dependencies

Navigate to:

```powershell
cd frontend
```

Run:

```powershell
flutter pub get
```

---

## 5.4 Build the Java Backend

From the project root:

```powershell
cd backend
```

Run tests:

```powershell
mvn test
```

A successful test run should finish with:

```text
BUILD SUCCESS
```

Build the executable JAR:

```powershell
mvn clean package -DskipTests
```

The expected output is:

```text
backend/target/backend-1.0-SNAPSHOT.jar
```

Return to the project root when finished.

---

## 6. Starting the Local Retrieval Service

The semantic retrieval service must be running before normal indexing and searching.

From the project root, run:

```powershell
python scripts\service\retrieval_server.py
```

During startup, the system initializes:

1. ChromaDB
2. BERT
3. MobileCLIP

Typical startup output includes:

```text
Starting Offline Retriever backend...
Opening ChromaDB...
Loading BERT...
Loading MobileCLIP...
Offline Retriever backend ready.
```

The model-loading stage may take several seconds.

When ready, the console should show:

```text
Uvicorn running on http://127.0.0.1:8765
```

Keep this terminal running while using the retrieval system.

---

## 7. Verifying the Retrieval Service

Open another PowerShell terminal.

Run:

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

Before continuing, verify:

```text
status = ok
bert_loaded = True
mobileclip_loaded = True
```

---

## 8. Running the Flutter Application

Navigate to the frontend directory:

```powershell
cd frontend
```

Run:

```powershell
flutter run -d windows
```

After Flutter finishes building, the desktop application window should open.

---

## 9. Main User Workflow

The normal workflow is:

1. Start the local retrieval service.
2. Launch the Flutter application.
3. Import or select supported local files.
4. Index the files.
5. Enter a semantic search query.
6. Start the search.
7. Review ranked text and image results.
8. Open a matching local file when required.
9. Use accessibility settings when needed.

---

## 10. Importing Local Files

Open the File Library or file-import interface.

Select:

```text
Import Files
```

Choose one or more supported files.

Supported formats include:

```text
.txt
.pdf
.docx
.jpg
.jpeg
.png
```

Multiple files may be selected during one import operation where supported by the interface.

The original local files are not copied into the application database.

The application stores metadata and semantic vector representations while preserving the original file path.

---

## 11. Text Document Indexing

TXT, PDF, and DOCX files are processed as text documents.

The indexing workflow is:

```text
Local Document
      ↓
Java Parser
      ↓
Extracted Text
      ↓
Long-Document Chunking
      ↓
BERT Embeddings
      ↓
ChromaDB
```

TXT files are read directly as text.

PDF and DOCX documents are parsed before semantic indexing.

---

## 12. Long-Document Processing

Long documents are automatically divided into smaller overlapping sections before indexing.

Current configuration:

```text
Chunk size: 400 words
Chunk overlap: 50 words
```

This allows information later in a long PDF, DOCX, or TXT document to participate in semantic retrieval.

Users do not need to manually divide long documents.

Although one document may create multiple internal vector records, it is displayed as one source file in normal search results.

---

## 13. Image Indexing

JPG, JPEG, and PNG files use the MobileCLIP image pipeline.

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

The image does not need to contain text.

MobileCLIP creates a semantic representation of the visual content.

This allows users to search for images using text queries such as:

```text
red
```

or:

```text
dog
```

depending on the indexed image content.

---

## 14. Performing a Search

Open the Search page.

Enter a text query describing the information or visual content you want to retrieve.

Examples:

```text
software engineering
```

```text
database
```

```text
red
```

```text
dog
```

The query is processed by both retrieval paths where applicable:

```text
Query
 ├── BERT → Text Search
 └── MobileCLIP → Image Search
```

The results are then combined and ranked.

---

## 15. Empty Queries

The search query must contain meaningful text.

If the user attempts to search without entering a query, the application prevents the search and displays an appropriate message such as:

```text
Please enter a search query.
```

---

## 16. Semantic Search

The system performs semantic retrieval rather than only exact keyword matching.

For text documents:

```text
Query
  ↓
BERT
  ↓
Semantic Vector
  ↓
ChromaDB Search
```

For images:

```text
Text Query
   ↓
MobileCLIP
   ↓
Multimodal Vector
   ↓
Image Search
```

This allows related content to be retrieved even when the exact search phrase does not appear literally in the source content.

---

## 17. Multimodal Ranking

Text and image results are produced by different machine-learning models.

Because the similarity scores generated by BERT and MobileCLIP are not naturally identical in scale, image scores are calibrated before the final ranking.

The system then returns one combined result list containing both:

```text
text
image
```

content types.

Users do not need to manually select separate text-search and image-search modes.

---

## 18. Search Results

Search results may display information such as:

- File name
- File type
- Content type
- Similarity score
- Ranking position
- Open control

A result may represent:

```text
TXT document
PDF document
DOCX document
JPG image
JPEG image
PNG image
```

Higher similarity normally indicates that the content is more semantically related to the query.

The similarity score is a ranking signal and should not be interpreted as a probability or confidence percentage.

---

## 19. Opening a Search Result

Select:

```text
Open
```

for the desired result.

The application uses the stored original local file path.

If the file still exists, it is opened using the operating system's associated application.

Examples:

- TXT → configured text editor
- PDF → PDF viewer
- DOCX → Word-compatible application
- JPG/PNG → image viewer

---

## 20. Missing Local Files

The vector database does not contain a full copy of the source file.

If the original file has been:

- Deleted
- Moved
- Renamed

the indexed record may still exist, but opening the original file may fail.

The system checks whether indexed file paths still exist locally.

---

## 21. Persistent Local Index

Indexed semantic vectors are stored in ChromaDB.

This means the database can remain available across retrieval-service restarts.

The user does not necessarily need to recreate the entire vector database every time the service starts.

The database is stored locally under:

```text
chroma_db/
```

---

## 22. Removing Indexed Files

Indexed files can be removed from the retrieval database.

For long text documents, all internal chunks associated with the selected source file are deleted together.

Deleting an indexed record does not delete the original local document or image from the computer.

---

# Accessibility Guide

## 23. Accessibility Overview

Accessibility is a core project requirement.

The current interface includes support for:

- Keyboard-only navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic labels
- Accessible feedback and navigation

The project is designed around WCAG 2.1 AA principles where applicable.

---

## 24. Keyboard Navigation

Users can operate important interface controls using the keyboard.

Common controls include:

```text
Tab
```

Move to the next interactive control.

```text
Shift + Tab
```

Move to the previous control.

```text
Enter
```

Activate the selected control.

```text
Space
```

Activate compatible buttons or switches.

This allows important application functions to be used without relying entirely on a mouse.

---

## 25. High Contrast Mode

High Contrast Mode improves the visual separation between foreground and background elements.

To enable it:

1. Open Settings.
2. Locate High Contrast Mode.
3. Enable the switch.

The interface updates immediately.

Disable the same option to return to the normal appearance.

---

## 26. Font Size Adjustment

The application supports multiple text-size levels.

Available sizes include:

- Small
- Medium
- Large
- Extra Large

To change the font size:

1. Open Settings.
2. Locate the Font Size control.
3. Select the preferred size.

The interface updates without requiring an application restart.

---

## 27. Semantic Labels

Important interface elements contain semantic information to improve compatibility with assistive technologies.

Semantic labels are used for important controls including:

- Navigation
- File import
- Search
- Search results
- File opening
- Accessibility settings

These labels provide additional context for screen-reader and accessibility-tool users.

---

## 28. Accessibility Recommendations

Users who prefer keyboard interaction can navigate using:

```text
Tab
Shift + Tab
Enter
Space
```

Users who require stronger visual contrast can enable:

```text
High Contrast Mode
```

Users who require larger text can select:

```text
Large
Extra Large
```

These settings may be used individually or together.

---

# Offline and Privacy

## 29. Offline-First Operation

Normal retrieval processing takes place locally.

The system performs locally:

- File parsing
- Text extraction
- BERT inference
- MobileCLIP inference
- Vector storage
- Semantic search
- Result ranking

The retrieval service uses:

```text
127.0.0.1:8765
```

which is the local loopback interface.

---

## 30. Internet Requirements

Initial setup may require Internet access for:

- Installing dependencies
- Downloading BERT model resources
- Downloading MobileCLIP model resources

Once the required dependencies and model resources are installed locally, normal retrieval is designed to operate without Internet access.

---

## 31. Privacy

During normal local retrieval:

- User files remain on the local machine.
- Extracted document text is processed locally.
- Search queries are processed locally.
- Vector embeddings are stored locally.
- No cloud semantic-search API is required.

Users should still follow normal computer-security practices when storing sensitive files.

---

# Troubleshooting

## 32. Retrieval Service Does Not Start

Run:

```powershell
python scripts\service\retrieval_server.py
```

Check the first reported error.

Common causes include:

- Missing Python package
- Missing model resource
- Python environment problem
- ChromaDB dependency problem

---

## 33. Retrieval Service Appears Frozen

The service may appear inactive while loading BERT or MobileCLIP.

Wait for:

```text
Offline Retriever backend ready.
```

and:

```text
Uvicorn running on http://127.0.0.1:8765
```

Then check:

```powershell
Invoke-RestMethod http://127.0.0.1:8765/health
```

---

## 34. Cannot Connect to Backend

If the application cannot connect to:

```text
127.0.0.1:8765
```

verify that the Python retrieval service is running.

Run:

```powershell
Invoke-RestMethod http://127.0.0.1:8765/health
```

If this fails, restart the retrieval service.

---

## 35. Java Backend JAR Is Missing

Build it using:

```powershell
cd backend
mvn clean package -DskipTests
```

Confirm that this file exists:

```text
backend/target/backend-1.0-SNAPSHOT.jar
```

---

## 36. Java Changes Are Not Taking Effect

After changing Java source code, rebuild the JAR.

Run:

```powershell
cd backend
mvn clean package -DskipTests
```

Then retry the operation.

---

## 37. Python Changes Are Not Taking Effect

Stop the running Python retrieval service.

Restart it using:

```powershell
python scripts\service\retrieval_server.py
```

The running Python process may still contain the previous code until restarted.

---

## 38. A File Cannot Be Indexed

Verify that its extension is supported.

Supported text formats:

```text
.txt
.pdf
.docx
```

Supported image formats:

```text
.jpg
.jpeg
.png
```

Also verify that the file still exists at the selected location.

---

## 39. PDF Text Cannot Be Retrieved

The current parser works best with text-based PDFs.

If a PDF consists only of scanned images, text may not be available for BERT indexing.

OCR is not currently included in the main retrieval workflow.

---

## 40. Search Result Cannot Be Opened

Check whether the original file:

- Still exists
- Has been moved
- Has been renamed
- Has an associated application installed

The semantic index does not contain a replacement copy of the original file.

---

## 41. Search Results Look Unexpected

Semantic search is approximate.

Possible causes include:

- Very short or ambiguous queries
- Semantically broad test files
- Low semantic similarity
- Model limitations
- Old indexed records remaining in ChromaDB

Try a more descriptive query before assuming the retrieval service has failed.

---

## 42. Negative Similarity Scores

A result may occasionally have a negative similarity score.

This is valid for cosine similarity and indicates very low semantic similarity.

It does not indicate a software error.

---

## 43. Indexed Files No Longer Exist

The `/files` operation may show:

```text
exists = false
```

for a file that has been moved or deleted after indexing.

Remove the stale index entry or re-index the file from its new location.

---

# Current Scope

## 44. Current Implemented Features

The current Week 7 prototype includes:

- TXT indexing
- PDF indexing
- DOCX indexing
- JPG indexing
- JPEG indexing
- PNG indexing
- BERT semantic text search
- MobileCLIP semantic image search
- Long-document chunking
- File-level aggregation
- Persistent ChromaDB storage
- Multimodal score calibration
- Local retrieval service
- Flutter desktop interface
- File opening
- Accessibility features
- Offline-first processing

---

## 45. Current Limitations

The current prototype does not yet provide:

- OCR for scanned PDFs
- Full in-application document preview
- All Microsoft Office file formats
- Complete runtime validation on all desktop operating systems

The project has been primarily tested on Windows.

Flutter includes macOS and Linux desktop targets, but complete runtime testing of the full Java/Python/ML pipeline still requires access to the relevant environments.

---

## 46. Additional Documentation

Additional documentation is available under:

```text
docs/
```

Important files include:

- `PRD.md`
- `System_Architecture_Design.md`
- `API_Reference.md`
- `Maintenance_Guide.md`
- `Open_Source_Compliance_Report.md`
- `Demo_Script.md`
- `Week5_Accessibility_User_Guide.md`
- `Week5_Usability_Test.md`

---

## 47. Conclusion

The Offline Accessible Multimodal Local Content Retrieval System provides a local semantic retrieval workflow for documents and images.

The current implementation combines:

```text
Flutter
Java
FastAPI
BERT
MobileCLIP
ChromaDB
```

Users can index supported local files, perform semantic searches, review ranked multimodal results, and open original local files while keeping normal retrieval processing on the local machine.

The interface also provides keyboard navigation, high contrast options, dynamic font scaling, and semantic accessibility information.

This manual provides the main instructions required to prepare the development environment, start the retrieval service, build the Java backend, launch the Flutter interface, index local files, perform searches, use accessibility options, and troubleshoot common problems.