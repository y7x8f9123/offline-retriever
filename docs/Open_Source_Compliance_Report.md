# Open-Source Compliance Report

## 1. Overview

This report documents the open-source licensing and dependency compliance review for the Offline Accessible Multimodal Local Content Retrieval System.

The review covers the main Java backend and Flutter frontend dependencies used by the current Week 7 prototype.

The objectives of this review are to:

- Identify major direct open-source dependencies.
- Review their applicable licenses.
- Distinguish direct dependencies from transitive dependencies.
- Confirm that the project can be distributed under its selected open-source license.
- Identify license and notice information that should be retained during future distribution.
- Document newly introduced dependencies used for local file import and opening retrieved files.

---

## 2. Project License

The Offline Accessible Multimodal Local Content Retrieval System is licensed under:

**Apache License 2.0**

A complete copy of the Apache License 2.0 is included in the project root:

`LICENSE`

The Apache License 2.0 permits use, modification, and distribution subject to its license conditions.

---

## 3. Backend Dependency Review

The Java backend uses Maven for dependency management.

Dependency information was reviewed using:

```bash
mvn dependency:tree
```

The main direct backend dependencies identified in the current project are:

| Dependency | Version | Purpose | License |
|---|---:|---|---|
| Apache Tika Core | 3.2.3 | File type detection and content extraction | Apache License 2.0 |
| Apache Tika Standard Parsers | 3.2.3 | PDF, DOCX, and other document parsing | Apache License 2.0 |
| JUnit | 4.13.2 | Backend unit and integration testing | Eclipse Public License 1.0 |

Apache Tika is used by the current document parsing workflow to extract text from PDF and DOCX documents.

JUnit is used as a development and testing dependency rather than part of the application's normal retrieval runtime logic.

---

## 4. Apache Tika Transitive Dependencies

Apache Tika introduces a significant number of transitive dependencies required to support different document and file formats.

Examples observed in the Maven dependency tree include libraries associated with:

- Apache PDFBox
- Apache POI
- Jackson
- Bouncy Castle
- Apache Commons libraries
- Image and document parsing
- Metadata processing
- Compression and archive handling
- XML processing

These libraries are not declared individually as primary application components but are introduced through Apache Tika's parser modules.

Apache Tika and its transitive dependency set may include components with separate copyright notices and license terms.

Therefore, future binary or source distributions should preserve applicable third-party:

- LICENSE files
- NOTICE files
- Copyright statements
- Attribution requirements

Where the packaged executable backend redistributes third-party libraries, the applicable third-party licensing information should remain available to users.

---

## 5. Maven Build and Packaging Plugins

The backend also uses Maven plugins during development and packaging.

Important plugins include:

- JaCoCo Maven Plugin
- Maven Shade Plugin

JaCoCo is used for backend coverage analysis.

The Maven Shade Plugin is used to create the executable backend JAR used by the Flutter frontend.

Build plugins form part of the development and release toolchain and should also be reviewed when versions are changed.

---

## 6. Frontend Dependency Review

The Flutter frontend uses Dart and Flutter package management.

Dependencies were reviewed using:

```bash
flutter pub deps --style=compact
```

and:

```text
frontend/pubspec.yaml
```

The current main frontend dependencies include:

| Dependency | Purpose | License |
|---|---|---|
| Flutter SDK | Desktop user interface framework | BSD-style / BSD 3-Clause |
| cupertino_icons | Cupertino-style interface icons | BSD-style |
| file_picker | Local file-selection interface | MIT |
| url_launcher | Opening retrieved files with external applications | BSD 3-Clause |
| flutter_test | Flutter widget testing | Flutter SDK licensing |
| flutter_lints | Development linting rules | BSD 3-Clause |

The `file_picker` project is distributed under the MIT License. :contentReference[oaicite:1]{index=1}

The Flutter-maintained `url_launcher` package is distributed under the BSD 3-Clause License. :contentReference[oaicite:2]{index=2}

---

## 7. file_picker

The current prototype uses `file_picker` to provide native local file selection.

The package is used to allow users to import supported local documents into the Flutter File Library.

The current application exposes the following formats through the file picker:

- TXT
- PDF
- DOCX

`file_picker` is licensed under the MIT License. :contentReference[oaicite:3]{index=3}

When distributing the application, the copyright and license terms associated with the package should be retained as required by the MIT License.

---

## 8. url_launcher

The current prototype uses `url_launcher` to open retrieved local files with the operating system's associated external application.

The package is used after a search result has been returned and the original local file path has been validated.

For example:

- TXT documents may open in the configured text editor.
- PDF documents may open in the configured PDF viewer.
- DOCX documents may open in the configured document application.

`url_launcher` is maintained in the Flutter packages repository and uses the BSD 3-Clause License. :contentReference[oaicite:4]{index=4}

Applicable copyright and license statements should be preserved during redistribution.

---

## 9. Flutter Transitive Dependencies

Flutter and the installed plugins introduce additional Dart and platform-specific packages.

Examples include packages associated with:

- Collection handling
- Asynchronous utilities
- File-system access
- Windows platform integration
- Linux platform integration
- macOS platform integration
- Plugin platform interfaces
- Web integration
- Testing infrastructure

These packages are managed automatically through Flutter and Dart dependency resolution.

They should not be treated as original project source code.

When preparing a distributable build, applicable licensing information supplied with Flutter, Dart, and plugin dependencies should be preserved.

---

## 10. Generated Flutter Plugin Files

Adding Flutter plugins such as `file_picker` and `url_launcher` generates or modifies platform registration files.

Examples include:

```text
frontend/windows/flutter/generated_plugin_registrant.cc
frontend/windows/flutter/generated_plugins.cmake
frontend/linux/flutter/generated_plugin_registrant.cc
frontend/linux/flutter/generated_plugins.cmake
frontend/macos/Flutter/GeneratedPluginRegistrant.swift
```

These generated files are part of the Flutter plugin integration process.

They should remain synchronized with:

```text
pubspec.yaml
pubspec.lock
```

Developers should avoid manually modifying generated plugin registration files unless required by Flutter tooling or platform-specific maintenance.

---

## 11. Development Dependencies

Some dependencies are used only during development or testing.

Examples include:

- JUnit
- flutter_test
- flutter_lints
- JaCoCo
- Testing support packages

Development dependencies are conceptually separated from production application functionality.

Their licensing information is still relevant because they form part of the project's development and build environment.

---

## 12. Source Code Attribution

Third-party libraries remain the property of their respective copyright holders.

The project does not claim ownership of third-party source code, libraries, Flutter plugins, or bundled parser components.

Where third-party components are redistributed, applicable:

- Copyright notices
- License text
- Attribution
- NOTICE information

should be retained according to the relevant license terms.

---

## 13. Apache License and NOTICE Requirements

The project itself includes the Apache License 2.0 in the root `LICENSE` file.

For dependencies distributed under the Apache License 2.0, required copyright, attribution, license, and NOTICE information should be preserved where applicable.

Apache Tika introduces many parser-related subcomponents and transitive dependencies.

Therefore, the license and notice information associated with the packaged backend should be reviewed before public binary distribution.

---

## 14. Offline-First Compliance Considerations

The project is designed to operate locally and does not require a cloud-based retrieval service for its core functionality.

Open-source dependencies are used locally for functions including:

- File selection
- File parsing
- Text extraction
- Text representation generation
- Vector retrieval
- User-interface functionality
- Opening local files
- Testing

The current Flutter-to-Java connection starts a local Java process rather than connecting to an external retrieval server.

Future dependencies should be reviewed before integration to ensure that they do not introduce unexpected:

- Network requirements
- Telemetry
- External data transmission
- Licensing restrictions

that conflict with the project's offline-first design.

---

## 15. Adding New Dependencies

Before adding a new dependency, developers should:

1. Identify the dependency and required version.
2. Review its official license.
3. Check whether the license is compatible with the intended project distribution.
4. Identify required attribution or NOTICE files.
5. Review important transitive dependencies.
6. Confirm that the dependency does not introduce unnecessary network functionality.
7. Update this compliance report when necessary.
8. Run the project's automated tests after integration.
9. Perform an end-to-end application test where appropriate.

Dependencies with unclear, proprietary, or incompatible licensing should not be added without further review.

---

## 16. Dependency Upgrade Procedure

When upgrading backend or frontend dependencies:

1. Review the dependency changelog.
2. Confirm the new version's license.
3. Review newly introduced transitive dependencies.
4. Update the dependency configuration file.
5. Rebuild the relevant application component.
6. Run automated tests.
7. Perform an end-to-end retrieval test.
8. Update documentation if behavior or licensing changes.

Backend dependency changes should be reflected in:

```text
backend/pom.xml
```

Frontend dependency changes should be reflected in:

```text
frontend/pubspec.yaml
frontend/pubspec.lock
```

---

## 17. Release Checklist

Before a public release, the following checks should be completed:

- Confirm that the root `LICENSE` file is present.
- Re-run the Maven dependency tree.
- Re-run the Flutter dependency listing.
- Review newly introduced dependencies.
- Review third-party license and NOTICE requirements.
- Preserve required copyright notices.
- Preserve applicable LICENSE and NOTICE files.
- Check that no proprietary code or assets have been unintentionally included.
- Check that no passwords, tokens, API keys, or other secrets are committed.
- Confirm that the executable backend does not accidentally omit required attribution.
- Confirm that Flutter plugin licensing has been documented.
- Confirm that the project documentation identifies major third-party components.
- Repeat the compliance review after major dependency upgrades.

---

## 18. Current Compliance Status

Based on the Week 7 dependency review, the project uses established open-source frameworks, libraries, and plugins for its current implementation.

The project-level Apache License 2.0 has been added to the repository.

The major direct dependencies have identifiable open-source licenses.

The current major license categories include:

- Apache License 2.0
- Eclipse Public License 1.0
- MIT License
- BSD 3-Clause or BSD-style licenses

No intentionally proprietary runtime dependency was identified during the current review.

Apache Tika introduces a large set of transitive components, so their accompanying license and notice information should continue to be preserved and reviewed when preparing distributable backend builds.

The newly introduced Flutter plugins `file_picker` and `url_launcher` use permissive MIT and BSD licensing respectively. :contentReference[oaicite:5]{index=5}

The current project is therefore suitable to proceed toward Week 8 release preparation, subject to continued preservation of applicable third-party license and attribution requirements.

---

## 19. Conclusion

The Week 7 open-source compliance review identified the major Java and Flutter dependencies used by the current prototype and documented their licensing requirements.

The project now includes:

- An Apache License 2.0 project license
- Apache Tika dependency documentation
- Flutter dependency documentation
- `file_picker` license documentation
- `url_launcher` license documentation
- A process for reviewing future dependency changes
- A release-oriented compliance checklist

Open-source compliance should remain part of the maintenance and release process, particularly when new parsers, models, Flutter plugins, libraries, or platform-specific components are introduced.