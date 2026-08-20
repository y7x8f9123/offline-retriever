# End-User Manual

## Offline Accessible Multimodal Local Content Retrieval System

**Version:** Final Project Version  
**Platform:** Windows Desktop  
**Date:** 2026-08

---

## 1. Introduction

The Offline Accessible Multimodal Local Content Retrieval System is a Windows desktop application designed to help users search local documents and images using semantic search.

The system follows an offline-first design. Once the required dependencies and machine-learning models are available locally, normal indexing and retrieval operations are performed on the user's computer without requiring a remote search or inference API.

The application combines:

- Flutter desktop user interface
- Java backend
- Local FastAPI retrieval service
- BERT text embeddings
- MobileCLIP image and text embeddings
- ChromaDB persistent vector storage

The final implementation supports:

- TXT document indexing
- PDF document indexing
- DOCX document indexing
- JPG image indexing
- JPEG image indexing
- PNG image indexing
- Semantic text search
- Text-to-image semantic search
- Long-document chunking and retrieval
- Persistent local indexing
- Ranked multimodal search results
- Indexed-file management
- Opening original local files
- Keyboard navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic accessibility labels

---

## 2. Supported File Types

The application supports the following local file types:

| Content Type | Supported Formats |
|---|---|
| Text documents | TXT, PDF, DOCX |
| Images | JPG, JPEG, PNG |

Text-based PDF and DOCX files are processed by extracting their textual content.

Scanned or image-only PDF files may not be searchable if they do not contain extractable text because OCR is not implemented in the current version.

---

## 3. System Requirements

The final application targets Windows Desktop.

For source-based execution, the following software and resources are required:

- Windows operating system
- Flutter SDK
- Java Development Kit
- Apache Maven
- Python
- Required Python packages
- BERT model resources
- MobileCLIP model resources

Git is also required when obtaining the project by cloning the GitHub repository.

Users should have appropriate desktop applications installed if they want to open original PDF, DOCX, TXT, JPG, JPEG, or PNG files from search results.

---

## 4. Project Components

The repository is organized around the following major components:

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

### `frontend/`

Contains the Flutter Windows desktop user interface.

### `backend/`

Contains the Java backend, document parsing, metadata processing, retrieval integration, CLI functionality, and backend tests.

### `scripts/`

Contains the local Python retrieval service, machine-learning integration, and ChromaDB support.

### `docs/`

Contains project documentation including architecture, API, testing, maintenance, accessibility, user, and open-source compliance documentation.

### `dataset/`

Contains dataset-related documentation and project validation resources.

Runtime-generated data such as the ChromaDB database is not intended to be part of the clean public source-code structure.

---

## 5. Preparing the Application

### 5.1 Obtain the Project

Clone or download the project repository to the Windows computer.

After obtaining the source code, open the project directory in a development environment such as Visual Studio Code.

---

### 5.2 Verify Flutter

Check the Flutter environment using:

```powershell
flutter doctor
```

Resolve any Windows desktop-development issues reported by Flutter before running the frontend.

---

### 5.3 Install Flutter Dependencies

Navigate to the frontend directory:

```powershell
cd frontend
```

Install the required Flutter packages:

```powershell
flutter pub get
```

---

### 5.4 Build the Java Backend

Navigate to the backend directory:

```powershell
cd backend
```

Build the backend:

```powershell
mvn clean package -DskipTests
```

The packaged JAR is generated at:

```text
backend/target/backend-1.0-SNAPSHOT.jar
```

To run the backend tests instead:

```powershell
mvn test
```

---

## 6. Starting the Local Retrieval Service

The semantic retrieval functionality depends on the local Python retrieval service.

From the project root, run:

```powershell
python scripts\service\retrieval_server.py
```

The service operates locally at:

```text
http://127.0.0.1:8765
```

During startup, the service initializes:

- ChromaDB
- BERT
- MobileCLIP
- Text retrieval components
- Image retrieval components

A successful startup should eventually display:

```text
Offline Retriever backend ready.
Uvicorn running on http://127.0.0.1:8765
```

---

## 7. Checking Service Health

The local service can be checked from PowerShell using:

```powershell
Invoke-RestMethod http://127.0.0.1:8765/health
```

A successful health check confirms that the retrieval service is available.

The service health information can also be used to confirm that the required text and image retrieval components have been initialized.

---

## 8. Startup Time

The first startup may take additional time because BERT and MobileCLIP must be loaded into memory.

The exact startup time depends on the computer hardware and whether model resources have already been loaded or cached.

Users should allow the retrieval service to finish initialization before attempting indexing or semantic search.

If the application reports that the retrieval service is unavailable immediately after startup, wait for model loading to complete and check the `/health` endpoint again.

---

## 9. Running the Windows Application

Navigate to the Flutter frontend:

```powershell
cd frontend
```

Start the Windows desktop application:

```powershell
flutter run -d windows
```

The application window should open after Flutter finishes building the Windows application.

---

## 10. Main Application Workflow

The normal application workflow is:

```text
Select Local Files
        ↓
Index Files
        ↓
Extract Content / Process Images
        ↓
Generate Embeddings
        ↓
Store Embeddings in ChromaDB
        ↓
Enter Natural-Language Query
        ↓
Semantic Retrieval
        ↓
Rank Results
        ↓
Display Documents and Images
```

All retrieval processing is performed through the local application components.

---

## 11. Importing Local Files

The application allows supported local files to be selected from the computer.

Supported document formats are:

```text
.txt
.pdf
.docx
```

Supported image formats are:

```text
.jpg
.jpeg
.png
```

To import files:

1. Open the application.
2. Use the file import or file selection function.
3. Select one or more supported files.
4. Confirm the selection.
5. Allow the application to process and index the files.

Files with unsupported formats are not part of the current retrieval workflow.

---

## 12. Document Indexing

When a supported text document is indexed, the application performs the following operations:

1. Reads the selected file.
2. Extracts textual content.
3. Collects file metadata.
4. Splits long content into chunks when required.
5. Generates BERT embeddings.
6. Stores embeddings and metadata in ChromaDB.

The indexed information remains available for later semantic searches unless the corresponding indexed file is deleted from the local index.

---

## 13. Long-Document Processing

Long documents are divided into overlapping chunks before embedding.

The current configuration uses:

```text
Chunk size: 400 words
Chunk overlap: 50 words
```

Each chunk can be indexed independently.

During retrieval, chunk-level matches are aggregated back to file level so that the same long document does not unnecessarily appear many times in the final result list.

This allows semantic retrieval to identify relevant sections within larger documents.

---

## 14. Image Indexing

Supported image files are processed using MobileCLIP.

The image indexing workflow is:

```text
JPG / JPEG / PNG
        ↓
Image Processing
        ↓
MobileCLIP Image Embedding
        ↓
ChromaDB
```

The resulting embeddings allow images to be retrieved using natural-language text queries.

For example, a query describing the content of an image may retrieve semantically related indexed images even when the image itself contains no searchable filename text.

---

## 15. Performing a Search

After files have been indexed:

1. Navigate to the search interface.
2. Enter a natural-language query.
3. Submit the query.
4. Wait for local semantic retrieval to complete.
5. Review the ranked results.

Example queries may include:

```text
software engineering
```

or descriptive concepts related to the indexed documents and images.

The system searches semantic meaning rather than relying entirely on exact keyword matching.

---

## 16. Text Semantic Search

Text queries are embedded using the BERT-based text retrieval pipeline.

The query embedding is compared with stored document embeddings in ChromaDB.

Relevant text documents are ranked according to semantic similarity.

This allows the application to retrieve related documents even when the query does not exactly match the wording used in the original document.

---

## 17. Image Semantic Search

The image retrieval pipeline uses MobileCLIP.

A natural-language query is converted into a MobileCLIP text embedding and compared with stored image embeddings.

This allows users to retrieve indexed images based on their semantic content.

The image search does not require users to know the exact image filename.

---

## 18. Multimodal Results

Text documents and images use different embedding models.

Because BERT and MobileCLIP similarity scores have different score distributions, image scores are calibrated before the results are combined.

The current configuration uses:

```text
IMAGE_SCORE_CALIBRATION = 1.25
```

The final result list can therefore contain both documents and images in a unified ranked view.

---

## 19. Opening Search Results

Where supported, users can open the original local file from the search results.

The file is opened using the application associated with that file type in Windows.

For example:

- PDF files may open in the configured PDF viewer.
- DOCX files may open in a compatible word-processing application.
- TXT files may open in the configured text editor.
- Images may open in the configured image viewer.

The original file must still exist at its recorded local path.

If the file has been moved, renamed, or deleted outside the application, the stored path may no longer be valid.

---

## 20. Indexed File Management

The system maintains information about indexed local files.

Users can view files currently stored in the local retrieval index.

Indexed-file management supports:

- Listing indexed files
- Identifying stored files
- Deleting indexed files

Deleting an indexed entry removes its retrieval records from the local vector database.

Deleting an indexed entry does not delete the user's original file from the Windows file system.

---

## 21. Java CLI

The Java backend also provides command-line functionality.

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

### Delete an Indexed File

```powershell
java -jar backend\target\backend-1.0-SNAPSHOT.jar delete <file-id>
```

---

## 22. Accessibility Features

Accessibility is included as part of the Windows desktop interface.

The implemented accessibility features include:

- Keyboard navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic labels

The project uses WCAG 2.1 AA as an accessibility design objective.

Detailed accessibility information is available in:

```text
docs/Accessibility_User_Guide.md
```

---

## 23. Keyboard Navigation

Major interface controls support standard keyboard navigation.

Common controls include:

| Key | Function |
|---|---|
| Tab | Move to the next control |
| Shift + Tab | Move to the previous control |
| Enter | Activate the selected control |
| Space | Activate supported buttons and switches |

Keyboard behaviour may depend on the type of Flutter control currently selected.

---

## 24. High Contrast Mode

The application provides a High Contrast Mode to improve visual distinction between interface elements.

To enable it:

1. Open the Settings page.
2. Locate **High Contrast Mode**.
3. Enable the option.

The application updates the interface presentation immediately.

---

## 25. Font Size

The application provides multiple font size levels:

```text
Small
Medium
Large
Extra Large
```

Users can change the font size through the Settings page.

The interface updates without requiring the application to restart.

---

## 26. Offline-First Operation

Normal retrieval processing is designed to remain on the local computer.

Local operations include:

- File parsing
- Metadata extraction
- Text embedding
- Image embedding
- ChromaDB storage
- Semantic retrieval
- Result ranking

The application does not require a remote semantic-search or inference API during normal retrieval.

Internet access may still be required during initial setup to obtain software dependencies and machine-learning model resources.

---

## 27. Local Data and Privacy

Indexed information is stored locally using ChromaDB.

The local database may contain:

- Vector embeddings
- File metadata
- Local file paths
- Information derived from indexed content

Users should treat the local database as application data.

Development or user-generated ChromaDB databases should not normally be uploaded to public source-code repositories.

---

## 28. Troubleshooting

### Retrieval Service Does Not Start

Confirm that Python and the required dependencies are installed.

Run:

```powershell
python scripts\service\retrieval_server.py
```

Allow additional time for BERT and MobileCLIP to load.

Then check:

```powershell
Invoke-RestMethod http://127.0.0.1:8765/health
```

---

### Port 8765 Is Unavailable

The local retrieval service uses:

```text
127.0.0.1:8765
```

If another application is already using this port, the retrieval service may fail to start.

Close the conflicting process or change the configuration consistently across the components that communicate with the local service.

---

### Search Returns No Relevant Results

Check that:

- Files have been successfully indexed.
- The local retrieval service is running.
- The required models have loaded.
- The indexed documents contain extractable text.
- The query is related to the indexed content.

For image retrieval, confirm that supported image files were indexed successfully.

---

### PDF Produces No Useful Searchable Content

The current implementation extracts text from text-based PDF files.

A scanned PDF containing only images may not provide extractable text.

OCR is not implemented in the current version.

---

### Original File Cannot Be Opened

The application stores the local path of indexed files.

If a file is moved, renamed, or deleted after indexing, the stored path may become invalid.

Re-index the file from its new location if necessary.

---

### Application Cannot Reach the Retrieval Service

Confirm that the service is running at:

```text
http://127.0.0.1:8765
```

Check the health endpoint and wait for model initialization to complete.

---

## 29. Testing the Installation

Backend tests can be executed using:

```powershell
cd backend
mvn test
```

Java code coverage can be generated using:

```powershell
mvn clean test jacoco:report
```

Flutter tests can be executed using:

```powershell
cd frontend
flutter test
```

Successful execution of these commands helps verify that the local development environment is configured correctly.

---

## 30. Known Limitations

The final project has several known limitations.

- The final release targets Windows.
- OCR for scanned or image-only documents is not implemented.
- Initial machine-learning model loading can increase startup time.
- Model resources and dependencies must be available locally before fully offline operation.
- Original files moved or deleted outside the application may no longer open from stored search results.
- Performance depends on local hardware and the number and size of indexed files.
- Formal accessibility certification and extensive external assistive-technology testing were outside the scope of the project.

These limitations do not prevent the primary local multimodal retrieval workflow from operating.

---

## 31. Additional Documentation

Additional technical information is available in the `docs/` directory.

Important documents include:

- `System_Architecture_Design.md`
- `API_Reference.md`
- `Testing_Report.md`
- `Maintenance_Guide.md`
- `Accessibility_User_Guide.md`
- `Open_Source_Compliance_Report.md`
- `Environment_Setup_Report.md`
- `Risk_Management_Plan.md`
- `PRD.md`
- `Demo_Script.md`

The project root `README.md` provides the main project overview and quick-start information.

---

## 32. Conclusion

The Offline Accessible Multimodal Local Content Retrieval System provides a local Windows desktop workflow for indexing and semantically searching documents and images.

The final implementation integrates Flutter, Java, Python, BERT, MobileCLIP, and ChromaDB to support local multimodal retrieval while maintaining an offline-first architecture.

Users can index supported documents and images, perform natural-language semantic searches, manage indexed content, and use accessibility-focused interface features through the Windows desktop application.

For technical implementation, maintenance, testing, API, accessibility, and licensing details, refer to the corresponding documents in the `docs/` directory.