import 'dart:convert';
import 'dart:io';

import 'package:file_picker/file_picker.dart';

class RetrievalResult {
  final String fileName;
  final String filePath;
  final double score;

  const RetrievalResult({
    required this.fileName,
    required this.filePath,
    required this.score,
  });

  factory RetrievalResult.fromJson(Map<String, dynamic> json) {
    return RetrievalResult(
      fileName: json['fileName'] as String,
      filePath: json['filePath'] as String,
      score: (json['score'] as num).toDouble(),
    );
  }
}

class RetrievalService {
  static Future<List<RetrievalResult>> search({
    required String query,
    required List<PlatformFile> files,
    int topK = 5,
  }) async {
    if (query.trim().isEmpty) {
      throw ArgumentError('Search query cannot be empty.');
    }

    final filePaths = files
        .where((file) => file.path != null)
        .map((file) => file.path!)
        .toList();

    if (filePaths.isEmpty) {
      throw StateError('No local files have been imported.');
    }

    final jarPath = '../backend/target/backend-1.0-SNAPSHOT.jar';

    final jarFile = File(jarPath);

    if (!await jarFile.exists()) {
      throw StateError(
        'Backend JAR not found at $jarPath. '
        'Run "mvn clean package -DskipTests" in the backend directory first.',
      );
    }

    final arguments = <String>[
      '-jar',
      jarPath,
      query.trim(),
      topK.toString(),
      ...filePaths,
    ];

    final processResult = await Process.run(
      'java',
      arguments,
      runInShell: true,
    );

    if (processResult.exitCode != 0) {
      final error = processResult.stderr.toString().trim();

      throw StateError(
        error.isEmpty
            ? 'Backend process failed with exit code ${processResult.exitCode}.'
            : 'Backend process failed: $error',
      );
    }

    final stdout = processResult.stdout.toString().trim();

    if (stdout.isEmpty) {
      return [];
    }

    final jsonText = _extractJsonArray(stdout);

    final decoded = jsonDecode(jsonText);

    if (decoded is! List) {
      throw const FormatException(
        'Backend returned an unexpected response.',
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

  static String _extractJsonArray(String output) {
    final start = output.lastIndexOf('[');
    final end = output.lastIndexOf(']');

    if (start == -1 || end == -1 || end < start) {
      throw FormatException(
        'No valid JSON result was found in backend output:\n$output',
      );
    }

    return output.substring(start, end + 1);
  }
}