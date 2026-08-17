# Open-Source Compliance Report

## 1. Overview

This report documents the open-source dependency and licensing review for the Offline Accessible Multimodal Local Content Retrieval System.

The current Week 7 implementation contains three main technology layers:

- Java backend
- Flutter frontend
- Python machine-learning and retrieval service

The objectives of this review are to:

- Identify major direct open-source dependencies.
- Record their primary licensing categories.
- Distinguish software-library licensing from machine-learning model licensing.
- Identify important transitive dependency considerations.
- Confirm that the project contains its selected Apache License 2.0 project license.
- Record attribution and NOTICE obligations that should be reviewed before public distribution.
- Establish a procedure for reviewing future dependencies.

This report is a project compliance record and does not replace formal legal advice for commercial or large-scale redistribution.

---

## 2. Project License

The Offline Accessible Multimodal Local Content Retrieval System is distributed under:

```text
Apache License 2.0
```

A copy of the license is included at:

```text
LICENSE
```

in the project root.

The Apache License 2.0 permits use, modification, and redistribution subject to its terms, including applicable copyright, license, attribution, and NOTICE requirements.

---

## 3. Current Technology Stack

The current implementation uses:

```text
Flutter
Dart
Java
Maven
Apache Tika
Python
FastAPI
Uvicorn
PyTorch
Transformers / BERT
MobileCLIP
ChromaDB
```

The project also uses development and testing tools including:

```text
JUnit
JaCoCo
flutter_test
flutter_lints
```

Each third-party component remains subject to its own license.

---

# Java Backend

## 4. Java Dependency Review

The Java backend uses Maven for dependency management.

Dependency information can be inspected using:

```bash
mvn dependency:tree
```

Major backend dependencies include:

| Dependency | Purpose | Primary License Category |
|---|---|---|
| Apache Tika Core | File type detection and document processing | Apache License 2.0 |
| Apache Tika Parser Modules | PDF/DOCX and document parsing | Apache License 2.0 |
| Gson | JSON serialization and deserialization | Apache License 2.0 |
| JUnit | Backend testing | Eclipse Public License / related JUnit licensing |
| JaCoCo | Test coverage | Eclipse Public License |
| Maven Shade Plugin | Executable JAR packaging | Apache License 2.0 |

Exact dependency versions should be taken from:

```text
backend/pom.xml
```

at release time.

---

## 5. Apache Tika

Apache Tika is used for local document parsing and text extraction.

The current application uses it for formats including:

```text
PDF
DOCX
```

Apache Tika is distributed under the Apache License 2.0.

Because Tika parser packages pull in many transitive libraries, a packaged backend may also include libraries associated with:

- Apache PDFBox
- Apache POI
- Jackson
- Apache Commons
- Bouncy Castle
- XML libraries
- Compression libraries
- Metadata libraries
- Image-processing components

These transitive dependencies may have separate license and NOTICE requirements.

Before public binary distribution, the Maven dependency tree and redistributed JAR contents should be reviewed.

---

## 6. Java Packaging

The Java backend is packaged into an executable JAR using Maven.

The output is typically:

```text
backend/target/backend-1.0-SNAPSHOT.jar
```

Because the shaded JAR may contain code from third-party dependencies, redistribution should preserve applicable:

- LICENSE files
- NOTICE files
- Copyright statements
- Attribution requirements

A project's own Apache License 2.0 file does not replace third-party license obligations.

---

# Flutter Frontend

## 7. Flutter Dependency Review

The Flutter frontend uses Dart and Flutter dependency management.

Dependencies can be inspected using:

```bash
flutter pub deps --style=compact
```

and:

```text
frontend/pubspec.yaml
frontend/pubspec.lock
```

Major frontend dependencies include:

| Dependency | Purpose | License Category |
|---|---|---|
| Flutter SDK | Desktop user-interface framework | BSD-style |
| Dart SDK | Application language/runtime | BSD-style |
| file_picker | Native local file selection | MIT |
| url_launcher | Opening local files | BSD 3-Clause |
| cupertino_icons | Interface icons | BSD-style |
| flutter_test | Widget and UI testing | Flutter SDK licensing |
| flutter_lints | Linting rules | BSD-style |

---

## 8. file_picker

The project uses:

```text
file_picker
```

to allow users to choose supported local files.

Current supported file categories include:

```text
TXT
PDF
DOCX
JPG
JPEG
PNG
```

The package uses a permissive open-source license.

Applicable copyright and license text should be retained when required by its license.

---

## 9. url_launcher

The project uses:

```text
url_launcher
```

to open local files with the operating system's associated application.

This may include:

- Text editors
- PDF viewers
- Word-compatible applications
- Image viewers

The package is part of the Flutter package ecosystem and uses BSD-style licensing.

Applicable copyright and license notices should remain available during redistribution.

---

## 10. Flutter Platform Components

Flutter generates platform-specific integration files for:

```text
Windows
Linux
macOS
```

Examples include:

```text
frontend/windows/flutter/generated_plugin_registrant.cc
frontend/windows/flutter/generated_plugins.cmake

frontend/linux/flutter/generated_plugin_registrant.cc
frontend/linux/flutter/generated_plugins.cmake

frontend/macos/Flutter/GeneratedPluginRegistrant.swift
```

These files are generated by Flutter tooling and should remain synchronized with:

```text
pubspec.yaml
pubspec.lock
```

They should not be treated as independently authored project modules.

---

# Python Retrieval Service

## 11. Python Dependency Review

The current main semantic retrieval path uses Python.

Major components include:

| Dependency | Purpose | Primary License Category |
|---|---|---|
| FastAPI | Local REST API | MIT |
| Uvicorn | Local ASGI server | BSD-style |
| Pydantic | Request/response data validation | MIT |
| ChromaDB | Persistent vector database | Open-source; verify packaged version at release |
| PyTorch | Machine-learning inference | BSD 3-Clause main project plus bundled third-party licenses |
| Transformers | BERT model loading and inference | Apache License 2.0 |
| NumPy | Numerical processing | BSD-style |
| Pillow | Image handling | HPND-style permissive license |

The exact installed environment should be recorded before release.

A useful command is:

```powershell
pip freeze
```

A more targeted dependency list can also be maintained in a project requirements file.

---

## 12. FastAPI

FastAPI provides the local retrieval API.

The service exposes endpoints such as:

```text
GET  /health
GET  /files
POST /index-text
POST /index-image
POST /search
POST /delete
```

FastAPI is distributed under the MIT License.

Its own dependencies, such as Starlette and Pydantic, should also be included in release-time dependency review.

---

## 13. Uvicorn

Uvicorn runs the local FastAPI application.

The application binds to:

```text
127.0.0.1:8765
```

during normal operation.

Uvicorn is part of the Python runtime dependency chain and should be included in the final Python dependency inventory.

---

## 14. ChromaDB

ChromaDB is used as the persistent local vector database.

The application stores:

```text
offline_retriever_text
offline_retriever_images
```

collections.

ChromaDB also introduces transitive dependencies.

Before release, developers should verify the exact ChromaDB version and its dependency metadata from the installed environment rather than assuming that all ChromaDB transitive packages use the same license.

The runtime database directory:

```text
chroma_db/
```

contains user-generated application data and should not normally be treated as distributable source code.

---

## 15. PyTorch

PyTorch is used for local machine-learning inference.

The main PyTorch project uses BSD 3-Clause licensing.

However, PyTorch distributions contain or depend on multiple third-party components with additional licenses.

Therefore, redistribution of packaged PyTorch binaries should preserve the license and attribution information supplied with the PyTorch distribution.

Developers should not describe PyTorch as having only one license without acknowledging bundled third-party components.

---

## 16. Transformers and BERT

The project uses Transformer-based text embedding functionality for BERT inference.

The Transformers library is distributed under the Apache License 2.0.

The library license applies to the software library.

The license of a specific pretrained BERT model must be reviewed separately.

Software-library licensing and model-weight licensing should not be treated as identical.

---

# Machine-Learning Model Licensing

## 17. Model License Review

The application uses machine-learning model resources for:

```text
BERT
MobileCLIP
```

Model weights are separate artifacts from the source libraries used to execute them.

Before redistribution of model files, the following should be checked:

1. Exact model name.
2. Exact model version or revision.
3. Original model repository.
4. Model license.
5. Attribution requirements.
6. Redistribution permissions.
7. Commercial-use limitations, if any.
8. Whether model files are actually bundled with the application.

A model being downloadable through an open model repository does not automatically mean that it can be redistributed under the project's Apache License 2.0.

---

## 18. BERT Model Resources

The current text pipeline uses a BERT-based embedding model.

Before bundling BERT model files in a public release, record:

```text
Model name
Model source
Model revision
Model license
Model copyright
```

If the model is downloaded during installation rather than redistributed with the repository, the installation documentation should identify the source and license.

---

## 19. MobileCLIP

MobileCLIP is used for:

- Image embeddings
- Text embeddings for image retrieval

The source-code license and the license of the specific pretrained MobileCLIP weights must both be reviewed.

If pretrained weights are distributed with a release, their applicable license terms must be preserved.

If weights are downloaded separately during setup, the setup instructions should identify their source.

---

## 20. Models and Git

Large model files should not be committed to the repository unless:

- Redistribution is permitted.
- The repository size is acceptable.
- The applicable model license is documented.
- Required attribution is included.

Where models are downloaded separately, the project should document:

```text
model source
download procedure
local storage location
license
```

---

# Transitive Dependencies

## 21. Transitive Dependency Review

All three major ecosystems introduce transitive dependencies:

```text
Maven
Flutter / pub
pip
```

Direct dependency review alone is not sufficient for a final distributable package.

Before release, developers should generate current dependency inventories.

### Java

```bash
mvn dependency:tree
```

### Flutter

```bash
flutter pub deps --style=compact
```

### Python

```bash
pip freeze
```

If possible, Python license metadata should also be exported using an appropriate dependency-audit tool.

---

## 22. Why Transitive Dependencies Matter

A direct dependency may depend on libraries using different licenses.

Examples include:

- Cryptography libraries
- Compression libraries
- HTTP libraries
- Numerical libraries
- Image-processing libraries
- Model-runtime components
- Database libraries

Redistributing these components may require preserving additional:

- License text
- NOTICE files
- Copyright notices
- Attribution statements

---

# Project Data and Generated Files

## 23. ChromaDB Data

The directory:

```text
chroma_db/
```

contains runtime-generated vector database data.

This directory should not normally be distributed as project source code.

It may contain embeddings and metadata derived from local user files.

For privacy reasons, development databases should be reviewed or deleted before public release.

---

## 24. Test Files

Temporary test files such as:

```text
stress_test_files/
animal_test.txt
database_test.txt
chunk_test.txt
```

are generated development artifacts.

They should not normally be included in the public repository unless deliberately retained as small reusable test fixtures.

Large generated stress-test datasets should be excluded from Git.

---

# Offline-First and Privacy Considerations

## 25. Offline-First Compliance

The project is designed so that normal retrieval can run locally after setup.

Open-source components perform:

- File parsing
- Text embedding
- Image embedding
- Vector storage
- Semantic retrieval
- User-interface functions

The core retrieval pipeline does not require a cloud search API.

---

## 26. Network Behaviour Review

Future dependencies should be checked for unexpected:

- Telemetry
- Automatic uploads
- Remote inference
- Cloud API calls
- Analytics
- Background downloads

Dependencies that conflict with the project's offline-first objective should be reviewed before adoption.

---

# Security and Repository Compliance

## 27. Secrets Review

The repository should not contain:

- Passwords
- API keys
- Access tokens
- Private credentials
- Authentication cookies
- Private certificates

A release review should search for accidental secrets before publication.

---

## 28. Third-Party Assets

Third-party:

- Images
- Fonts
- Icons
- Documents
- Datasets
- Test resources

must also have compatible usage rights.

Open-source software licensing does not automatically cover unrelated media assets.

---

# Adding New Dependencies

## 29. Dependency Approval Procedure

Before adding a dependency:

1. Identify the exact package and version.
2. Review the official license.
3. Review the source repository.
4. Review major transitive dependencies.
5. Check redistribution obligations.
6. Check NOTICE requirements.
7. Check for network or telemetry behaviour.
8. Check compatibility with offline-first operation.
9. Add the dependency.
10. Run automated tests.
11. Perform integration testing.
12. Update this compliance report.

Dependencies with unclear or restrictive licenses should receive further review before inclusion.

---

## 30. Dependency Upgrade Procedure

When upgrading dependencies:

1. Record the old and new versions.
2. Review the new license metadata.
3. Review newly introduced dependencies.
4. Read the relevant release notes.
5. Rebuild the project.
6. Run automated tests.
7. Run end-to-end retrieval tests.
8. Verify offline behaviour.
9. Update documentation.

License assumptions should not automatically be carried forward from one major dependency version to another.

---

# Release Compliance

## 31. Release Checklist

Before a public source or binary release:

```text
[ ] Root LICENSE file is present
[ ] Project license is Apache License 2.0
[ ] Maven dependency tree reviewed
[ ] Flutter dependency list reviewed
[ ] Python dependency list reviewed
[ ] BERT model license reviewed
[ ] MobileCLIP model license reviewed
[ ] Third-party LICENSE files preserved where required
[ ] Third-party NOTICE files preserved where required
[ ] Copyright notices preserved
[ ] Model attribution requirements reviewed
[ ] Temporary test files removed
[ ] ChromaDB development database excluded
[ ] No private user documents included
[ ] No passwords or API keys included
[ ] No proprietary assets included accidentally
[ ] README matches actual implementation
[ ] Open-source compliance report updated
```

---

## 32. Current Compliance Summary

The current project uses primarily permissive open-source software ecosystems.

Major known license categories include:

```text
Apache License 2.0
MIT License
BSD-style licenses
Eclipse Public License
```

The project itself contains an Apache License 2.0 file.

The major source-code dependencies reviewed so far are compatible with continued development of the current open-source project.

However, final redistribution compliance also depends on:

- Exact dependency versions
- Transitive dependency licenses
- Bundled binary components
- Specific BERT model license
- Specific MobileCLIP model and weight license

These items should be verified again when preparing the final release package.

---

## 33. Compliance Risk Areas

The main areas requiring attention before final distribution are:

### Machine-Learning Models

Model weights may have licenses independent of the libraries that load them.

### PyTorch Distribution

PyTorch contains multiple third-party components and associated license files.

### Apache Tika

Tika introduces a large parser dependency tree.

### ChromaDB

The installed database package and transitive Python packages should be captured from the actual release environment.

### Flutter Plugins

Plugin and platform dependencies should remain synchronized with the final `pubspec.lock`.

---

## 34. Recommended Final Deliverables

For a polished public release, the repository should contain or generate:

```text
LICENSE
THIRD_PARTY_LICENSES.md
dependency inventory
model attribution information
Open_Source_Compliance_Report.md
```

A future `THIRD_PARTY_LICENSES.md` file can consolidate the final release dependency information into one user-facing notice.

---

## 35. Conclusion

The Week 7 compliance review has been updated to reflect the actual current architecture of the Offline Accessible Multimodal Local Content Retrieval System.

The review now covers:

- Java and Maven dependencies
- Flutter and Dart dependencies
- Python dependencies
- FastAPI
- ChromaDB
- PyTorch
- Transformers
- BERT model resources
- MobileCLIP model resources
- Transitive dependencies
- Model licensing
- Release attribution requirements
- Generated runtime data
- Offline-first considerations

The project can continue toward final release preparation under its Apache License 2.0 project license, provided that all applicable third-party software and model licensing requirements are preserved and verified before redistribution.