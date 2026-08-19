# Dataset Repository

| Item | Description |
|------|-------------|
| Project | Offline Accessible Multimodal Local Content Retrieval System |
| Version | 1.0 |
| Author | Xuefei Yao |
| Finalisation | 2026-08 |

---

## 1. Purpose

This directory documents the evaluation data used during development of the Offline Accessible Multimodal Local Content Retrieval System.

The datasets and local test files were used to validate document parsing, semantic retrieval, image-text retrieval, multimodal indexing, and scalability. The datasets are evaluation resources only and are not required for normal application operation.

---

## 2. Original Dataset Plan

Four public datasets were selected during the initial project planning stage:

- **Natural Questions (NQ)** — text embedding and semantic retrieval evaluation.
- **COCO** — image embedding and image-text retrieval evaluation.
- **RVL-CDIP** — scanned document and document-image evaluation.
- **Wikipedia Corpus** — long-document and large-scale retrieval evaluation.

During development, some of the originally referenced download sources became unavailable or were no longer accessible. As a result, the final evaluation used small locally available subsets where practical together with representative local test files and generated scalability-test data.

---

## 3. Final Dataset Usage

### 3.1 Natural Questions

A small text subset was retained and used as representative input for text parsing and semantic retrieval experiments. The complete Natural Questions dataset is not distributed with this repository.

### 3.2 COCO

A small image subset was retained for image embedding and text-to-image retrieval experiments. The complete COCO dataset is not distributed with this repository.

### 3.3 RVL-CDIP

RVL-CDIP was included in the original evaluation plan for scanned-document testing. The originally referenced source was not available during final development, so the full dataset was not included in the final repository.

### 3.4 Wikipedia Corpus

A Wikipedia corpus was originally planned for long-document and batch-processing evaluation. The originally referenced source was not available during final development, so the full corpus was not included in the final repository. Long-document and scalability behaviour was instead validated using representative local and generated test files.

---

## 4. Repository Policy

- Large public datasets are not bundled with the repository.
- Only small evaluation subsets may be retained when useful for reproducible testing.
- Temporary development files, generated stress-test files, and duplicate sample files should not be committed.
- Original dataset files are not modified.
- Dataset licenses and redistribution requirements must be respected.
- Evaluation data is not required for normal offline retrieval operation.

---

## 5. Final Project Status

Dataset planning was used to guide evaluation throughout development, but the final application is designed to operate on user-provided local files rather than on a fixed bundled dataset.

Final validation therefore combines available dataset subsets with representative local files and generated test data appropriate to each test scenario.