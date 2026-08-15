# End-User Manual

## Offline Accessible Multimodal Local Content Retrieval System

**Platform:** Flutter Windows Desktop  
**Version:** Week 7 Prototype  
**Date:** 2026-08

---

## 1. Introduction

The Offline Accessible Multimodal Local Content Retrieval System is designed to help users search and retrieve information from local files while keeping file processing on the local device.

The application combines a Flutter-based desktop user interface with a Java-based local retrieval backend.

The current prototype supports:

- Importing local TXT, PDF, and DOCX files
- Searching imported documents using text queries
- English and Chinese text retrieval
- Similarity-based result ranking
- Opening retrieved files with the operating system's default application
- Keyboard-only navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic labels for accessibility support

The application is designed around three major principles:

- Offline-first local processing
- Simple local content retrieval
- Accessible user interaction

This manual explains how to prepare, launch, and use the current Week 7 prototype.

---

## 2. System Requirements

The current development prototype is designed for Windows desktop.

For development or source-based execution, the following software is required:

- Windows operating system
- Flutter SDK
- Java Development Kit (JDK)
- Maven
- Git

The project should be stored locally before running the application.

The user should also have appropriate desktop applications installed for opening imported documents, such as a PDF viewer or a DOCX-compatible application.

---

## 3. Project Structure

The main project contains separate backend, frontend, model, documentation, and supporting directories.

```text
shixi/
├── assets/
├── backend/
├── dataset/
├── docs/
├── frontend/
├── models/
├── scripts/
├── tests/
├── .gitignore
└── README.md
```

The two main application components are:

- `backend/` – Java-based local parsing, embedding, vector storage, and retrieval logic.
- `frontend/` – Flutter Windows desktop user interface.

The Flutter frontend communicates with the Java backend locally through a packaged executable JAR.

---

## 4. Installation and Setup

### 4.1 Obtain the Project

Clone or download the project repository to the local computer.

If Git is being used, clone the repository and open the project directory in a development environment such as Visual Studio Code.

---

### 4.2 Verify Flutter

Check the Flutter development environment:

```bash
flutter doctor
```

Resolve any required Flutter or Windows desktop development issues reported by the command before continuing.

---

### 4.3 Install Flutter Dependencies

Navigate to the frontend directory:

```bash
cd frontend
```

Install the required Flutter dependencies:

```bash
flutter pub get
```

The current prototype uses Flutter packages including `file_picker` for selecting local files and `url_launcher` for opening retrieved files.

---

### 4.4 Prepare the Backend

Navigate to the backend directory:

```bash
cd backend
```

Run the backend test suite:

```bash
mvn test
```

A successful test run should finish with:

```text
BUILD SUCCESS
```

Package the backend as an executable JAR:

```bash
mvn clean package -DskipTests
```

The packaged backend should be created under:

```text
backend/target/backend-1.0-SNAPSHOT.jar
```

The Flutter frontend expects this JAR to exist when a search is performed.

---

## 5. Running the Application

### 5.1 Run the Flutter Interface

Navigate to the frontend directory:

```bash
cd frontend
```

Run the Windows desktop application:

```bash
flutter run -d windows
```

The application window should open after the Flutter build completes.

---

### 5.2 Backend Operation

The Java backend does not need to be started manually as a separate server.

When the user performs a search, the Flutter application starts the packaged backend JAR as a local process.

The frontend provides:

- The search query
- The maximum number of results
- The paths of imported local files

The backend indexes the supplied files, performs similarity retrieval, and returns ranked results to Flutter as JSON.

All of these operations occur locally.

---

### 5.3 Backend Verification

The backend can be tested independently from the backend directory using:

```bash
mvn test
```

The project also contains backend entry points used for development and retrieval verification.

---

## 6. Main Application Interface

The current Flutter prototype provides several main application sections.

The normal user workflow is:

1. Open the application.
2. Open the File Library.
3. Import one or more supported local documents.
4. Open the Search page.
5. Enter a search query.
6. Start the search.
7. Review the ranked results.
8. Open a matching document if required.
9. Adjust accessibility settings when required.

The interface is designed to keep the workflow simple and reduce unnecessary interaction steps.

---

## 7. Importing Local Files

### 7.1 Open the File Library

Navigate to the **File Library** page.

The page displays the local files currently imported into the application.

---

### 7.2 Import Files

Select:

**Import Files**

The Windows file-selection interface will open.

The current Flutter prototype supports:

- TXT
- PDF
- DOCX

Multiple files can be selected during one import operation.

After importing files, they appear in the local file library.

---

### 7.3 File Information

Each imported file is displayed with information including:

- File name
- File type
- File size
- File-type icon

The interface distinguishes between:

- Text documents
- PDF documents
- Word documents

---

### 7.4 Removing Files

An imported file can be removed from the application library using the remove control displayed beside the file.

Removing a file from the application library does not delete the original document from the user's computer.

---

## 8. Searching for Local Content

### 8.1 Open the Search Page

Navigate to the **Search** page after importing one or more files.

---

### 8.2 Enter a Query

Select the search input field and enter text describing the content you want to find.

Queries can contain English or Chinese text.

Examples include:

```text
offline retrieval
```

or:

```text
甲方
```

---

### 8.3 Start the Search

Select the **Search** button or submit the query using the keyboard.

The Flutter application passes the query and imported file paths to the local Java backend.

The backend then:

1. Selects the appropriate parser for each file.
2. Extracts textual content.
3. Generates local vector representations.
4. Compares the query against the imported documents.
5. Calculates similarity scores.
6. Ranks the matching documents.
7. Returns the results to the Flutter interface.

---

### 8.4 Supported Document Parsing

TXT files are processed using the text parser.

PDF and DOCX documents are processed using the document parser, which uses Apache Tika for text extraction.

Text-based English and Chinese documents can participate in the current retrieval workflow.

Scanned or image-only PDF documents may not contain directly extractable text and are not currently supported through OCR.

---

### 8.5 Empty Queries

A search should contain meaningful text.

If the Search control is activated without a valid query, the interface displays:

```text
Please enter a search query.
```

The empty search is not sent to the backend.

---

### 8.6 No Imported Files

At least one supported local file must be imported before a useful retrieval operation can be performed.

If no local files are available, the search service cannot perform document retrieval.

---

## 9. Search Results

Search results are displayed on the **Search Results** page.

The page displays:

- The submitted query
- Number of matching files
- Ranked search results
- File name
- File type
- Similarity score
- Ranking position
- Open button

For example:

```text
document.pdf
PDF document    Similarity: 0.4210
#1    Open
```

Higher similarity generally indicates greater overlap between the current query representation and the indexed document representation.

The current similarity score should be interpreted as a retrieval ranking signal rather than a probability or percentage.

---

## 10. Opening Search Results

Each search result provides an **Open** button.

When the button is activated, the application checks whether the original local file still exists.

If the file exists, Windows opens it using the system's associated application.

For example:

- TXT files may open in the configured text editor.
- PDF files may open in the configured PDF viewer.
- DOCX files may open in the configured document application.

The application uses the original local file path returned by the retrieval backend.

If the file has been moved, renamed, or deleted after being imported, the application may display a file-not-found message.

---

# 11. Accessibility Guide

Accessibility is a core design requirement of the application.

The current prototype provides:

- Keyboard-only navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic labels for screen readers

---

## 12. Keyboard Navigation

The application supports keyboard-only operation.

Available keyboard controls include:

- **Tab** – Move to the next control.
- **Shift + Tab** – Move to the previous control.
- **Enter** – Activate the selected control.
- **Space** – Activate buttons and switches.

This allows users to navigate important application controls without relying entirely on a mouse or touch input.

---

## 13. High Contrast Mode

High Contrast Mode increases the visual contrast between foreground and background elements to improve readability.

To enable High Contrast Mode:

1. Open the **Settings** page.
2. Turn on the **High Contrast Mode** switch.
3. The application theme updates immediately.

To return to the normal appearance, disable the same switch.

---

## 14. Font Size Adjustment

The application provides four predefined font size levels:

- Small
- Medium
- Large
- Extra Large

To adjust the interface font size:

1. Open the **Settings** page.
2. Locate the **Font Size** control.
3. Move the font-size slider.
4. Select the preferred size.

The interface updates immediately without requiring an application restart.

---

## 15. Screen Reader Support

Important user-interface elements include semantic labels designed to improve compatibility with assistive technologies.

Semantic information is provided for important elements such as:

- Navigation buttons
- Search controls
- File-import controls
- Search-result controls
- Accessibility settings
- Main application sections

Semantic labeling provides additional context about interface elements to users who rely on assistive technologies.

---

## 16. Accessibility Usage Recommendations

Users who prefer keyboard navigation can use `Tab` and `Shift + Tab` to move through interactive controls and use `Enter` or `Space` to activate them.

Users who require stronger visual separation between interface elements can enable High Contrast Mode.

Users who require larger interface text can select Large or Extra Large through the font-size setting.

These features can be used independently or together depending on individual accessibility requirements.

---

## 17. Offline and Privacy Design

The application follows an offline-first design.

The current retrieval workflow runs locally:

```text
Local Files
    ↓
Flutter Desktop Application
    ↓
Local Java Backend
    ↓
Local Retrieval
    ↓
Flutter Search Results
```

Local file processing and retrieval do not require user document content or search queries to be uploaded to an external retrieval service.

This design provides several benefits:

- Local content remains on the user's device.
- Retrieval can operate without continuous internet connectivity.
- Local processing reduces dependence on external services.
- Sensitive local documents are not intentionally transmitted to remote retrieval services.

The current frontend-backend connection uses a local Java process rather than a remote server.

Users should still follow normal device-security practices when storing sensitive files locally.

---

## 18. Troubleshooting

### Flutter Application Does Not Start

Run:

```bash
flutter doctor
```

Check for missing Flutter or Windows desktop development components.

Then run:

```bash
flutter pub get
```

before trying to launch the application again.

---

### Backend JAR Is Missing

If searching reports that the backend JAR cannot be found, navigate to the backend directory and run:

```bash
mvn clean package -DskipTests
```

Confirm that the following file exists:

```text
backend/target/backend-1.0-SNAPSHOT.jar
```

Then restart or retry the Flutter application.

---

### Backend Tests Fail

From the backend directory, run:

```bash
mvn test
```

Review the first reported test or compilation failure.

---

### Flutter Tests Fail

From the frontend directory, run:

```bash
flutter test
```

Review the first reported widget or compilation failure.

---

### Files Cannot Be Imported

Confirm that the selected document uses one of the currently supported extensions:

```text
.txt
.pdf
.docx
```

---

### PDF Content Cannot Be Found

The current document parser extracts text from text-based PDF files.

If the PDF contains only scanned page images, text extraction may not succeed because OCR is not currently implemented.

---

### Search Cannot Be Started

Check that:

- At least one supported file has been imported.
- The search field contains a valid text query.
- The packaged backend JAR exists.

Empty queries are rejected by the interface.

---

### Search Result Cannot Be Opened

Confirm that:

- The original file still exists.
- The file has not been moved or renamed.
- Windows has an application associated with the file type.

---

### Interface Text Is Difficult to Read

Open **Settings** and:

- Increase the font size.
- Enable **High Contrast Mode**.

---

### Keyboard Navigation

Use `Tab` and `Shift + Tab` to move between controls.

Use `Enter` or `Space` to activate the currently selected control.

---

## 19. Current Prototype Scope

The current Week 7 prototype provides an end-to-end local document retrieval workflow.

Implemented user-facing functionality includes:

- Local TXT import
- Local PDF import
- Local DOCX import
- English text retrieval
- Chinese text retrieval
- Similarity-based ranking
- Local Flutter-to-Java backend integration
- Opening retrieved local files
- Keyboard navigation
- High Contrast Mode
- Dynamic Font Scaling
- Semantic accessibility labels

The current prototype uses a lightweight 256-dimensional deterministic text representation rather than a pretrained BERT model.

The project continues to follow a modular design so that additional functionality can be incorporated in future versions, including:

- Improved language-model-based embeddings
- Image retrieval
- OCR for scanned documents
- Persistent local vector storage
- Additional supported file formats
- Further accessibility improvements
- Additional user-interface refinement

---

## 20. Additional Documentation

Additional project information is available under the `docs` directory:

- `PRD.md` – project requirements.
- `System_Architecture_Design.md` – system architecture.
- `API_Reference.md` – backend and integration API documentation.
- `Maintenance_Guide.md` – developer maintenance instructions.
- `Open_Source_Compliance_Report.md` – open-source dependency and licensing information.
- `Demo_Script.md` – demonstration procedure.
- `Week5_Accessibility_User_Guide.md` – original accessibility prototype guide.
- `Week5_Usability_Test.md` – usability testing documentation.

---

## 21. Conclusion

The Offline Accessible Multimodal Local Content Retrieval System provides an offline-first prototype for retrieving information from local documents through an accessible Windows desktop interface.

The current Week 7 prototype integrates the Flutter frontend with the Java retrieval backend and supports an end-to-end workflow covering local file import, document parsing, English and Chinese text representation, similarity retrieval, ranked result display, and opening original local documents.

TXT, PDF, and DOCX documents are currently supported through the desktop workflow.

The application also provides keyboard navigation, high contrast display options, dynamic font scaling, and semantic interface labels to improve accessibility.

This manual provides the information required to prepare the development environment, build the backend, launch the application, import documents, perform local searches, review and open results, and use the implemented accessibility features.