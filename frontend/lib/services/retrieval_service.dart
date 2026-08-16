import 'dart:convert';
import 'dart:io';

import 'package:file_picker/file_picker.dart';

class RetrievalResult {
  final String fileName;
  final String filePath;
  final String fileType;
  final double score;

  const RetrievalResult({
    required this.fileName,
    required this.filePath,
    required this.fileType,
    required this.score,
  });

  factory RetrievalResult.fromJson(
    Map<String, dynamic> json,
  ) {
    return RetrievalResult(
      fileName: json['fileName'] as String? ?? '',
      filePath: json['filePath'] as String? ?? '',
      fileType: json['fileType'] as String? ?? '',
      score: (json['score'] as num?)?.toDouble() ?? 0.0,
    );
  }
}

class IndexedFile {
  final String id;
  final String fileName;
  final String filePath;
  final String fileType;
  final int fileSize;
  final double lastModified;
  final bool exists;

  const IndexedFile({
    required this.id,
    required this.fileName,
    required this.filePath,
    required this.fileType,
    required this.fileSize,
    required this.lastModified,
    required this.exists,
  });

  factory IndexedFile.fromJson(
    Map<String, dynamic> json,
  ) {
    return IndexedFile(
      id: json['id'] as String? ?? '',
      fileName: json['fileName'] as String? ?? '',
      filePath: json['filePath'] as String? ?? '',
      fileType: json['fileType'] as String? ?? '',
      fileSize: (json['fileSize'] as num?)?.toInt() ?? 0,
      lastModified:
          (json['lastModified'] as num?)?.toDouble() ?? 0.0,
      exists: json['exists'] as bool? ?? false,
    );
  }
}

class RetrievalService {
  static const String _jarPath =
      '../backend/target/backend-1.0-SNAPSHOT.jar';

  static Future<void> indexFiles(
    List<PlatformFile> files,
  ) async {
    final paths = files
        .where((file) => file.path != null)
        .map((file) => file.path!)
        .toList();

    if (paths.isEmpty) {
      throw StateError(
        'No valid local files were selected.',
      );
    }

    await _runBackend(
      [
        'index',
        ...paths,
      ],
    );
  }

  static Future<List<RetrievalResult>> search({
    required String query,
    int topK = 5,
  }) async {
    if (query.trim().isEmpty) {
      throw ArgumentError(
        'Search query cannot be empty.',
      );
    }

    final output = await _runBackend(
      [
        'search',
        query.trim(),
        topK.toString(),
      ],
    );

    final decoded = jsonDecode(output);

    if (decoded is! List) {
      throw const FormatException(
        'Backend returned an invalid search response.',
      );
    }

    return decoded
        .map(
          (item) => RetrievalResult.fromJson(
            Map<String, dynamic>.from(item as Map),
          ),
        )
        .toList();
  }

  static Future<List<IndexedFile>>
      loadIndexedFiles() async {
    final output = await _runBackend(
      ['list'],
    );

    final decoded = jsonDecode(output);

    if (decoded is! List) {
      throw const FormatException(
        'Backend returned an invalid file list.',
      );
    }

    return decoded
        .map(
          (item) => IndexedFile.fromJson(
            Map<String, dynamic>.from(item as Map),
          ),
        )
        .toList();
  }

  static Future<void> deleteIndexedFile(
    String id,
  ) async {
    if (id.trim().isEmpty) {
      throw ArgumentError(
        'Indexed file ID cannot be empty.',
      );
    }

    await _runBackend(
      [
        'delete',
        id,
      ],
    );
  }

  static Future<String> _runBackend(
    List<String> backendArguments,
  ) async {
    final jar = File(_jarPath);

    if (!await jar.exists()) {
      throw StateError(
        'Backend JAR not found at $_jarPath. '
        'Run "mvn clean package" in the backend directory first.',
      );
    }

    final result = await Process.run(
      'java',
      [
        '-jar',
        _jarPath,
        ...backendArguments,
      ],
      runInShell: true,
    );

    if (result.exitCode != 0) {
      final error =
          result.stderr.toString().trim();

      throw StateError(
        error.isEmpty
            ? 'Backend failed with exit code '
                '${result.exitCode}.'
            : error,
      );
    }

    final output =
        result.stdout.toString().trim();

    if (output.isEmpty) {
      throw const FormatException(
        'Backend returned no output.',
      );
    }

    return output;
  }
}