import 'dart:io';

import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

import '../services/retrieval_service.dart';

class ResultsPage extends StatelessWidget {
  const ResultsPage({
    super.key,
    required this.query,
    required this.results,
  });

  final String query;
  final List<RetrievalResult> results;

  String _formatScore(double score) {
    return score.toStringAsFixed(4);
  }

  String _extensionOf(String fileName) {
    final index = fileName.lastIndexOf('.');

    if (index == -1 || index == fileName.length - 1) {
      return '';
    }

    return fileName.substring(index + 1).toLowerCase();
  }

  String _fileTypeLabel(String fileName) {
    switch (_extensionOf(fileName)) {
      case 'pdf':
        return 'PDF document';

      case 'docx':
        return 'Word document';

      case 'txt':
        return 'Text document';

      default:
        return 'Document';
    }
  }

  IconData _fileIcon(String fileName) {
    switch (_extensionOf(fileName)) {
      case 'pdf':
        return Icons.picture_as_pdf;

      case 'docx':
        return Icons.description;

      case 'txt':
        return Icons.text_snippet;

      default:
        return Icons.insert_drive_file;
    }
  }

  Future<void> _openFile(
    BuildContext context,
    RetrievalResult result,
  ) async {
    final file = File(result.filePath);

    if (!await file.exists()) {
      if (!context.mounted) {
        return;
      }

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            'File not found: ${result.filePath}',
          ),
        ),
      );
      return;
    }

    try {
      final uri = Uri.file(result.filePath);

      final opened = await launchUrl(
        uri,
        mode: LaunchMode.externalApplication,
      );

      if (!opened && context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              'Could not open ${result.fileName}.',
            ),
          ),
        );
      }
    } catch (e) {
      if (!context.mounted) {
        return;
      }

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            'Failed to open ${result.fileName}: $e',
          ),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Search Results'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Query: "$query"',
              style: Theme.of(context).textTheme.titleLarge,
            ),

            const SizedBox(height: 8),

            Text(
              '${results.length} matching file(s) found',
              style: Theme.of(context).textTheme.bodyMedium,
            ),

            const SizedBox(height: 24),

            Expanded(
              child: results.isEmpty
                  ? const Center(
                      child: Text(
                        'No matching files were found.',
                        textAlign: TextAlign.center,
                      ),
                    )
                  : ListView.builder(
                      itemCount: results.length,
                      itemBuilder: (context, index) {
                        final result = results[index];
                        final fileType =
                            _fileTypeLabel(result.fileName);

                        return Semantics(
                          label:
                              '${result.fileName}, $fileType, similarity ${_formatScore(result.score)}',
                          child: Card(
                            margin: const EdgeInsets.only(bottom: 14),
                            child: ListTile(
                              leading: Icon(
                                _fileIcon(result.fileName),
                                size: 36,
                              ),

                              title: Text(
                                result.fileName,
                              ),

                              subtitle: Text(
                                '$fileType    '
                                'Similarity: ${_formatScore(result.score)}',
                              ),

                              trailing: Row(
                                mainAxisSize: MainAxisSize.min,
                                children: [
                                  Text(
                                    '#${index + 1}',
                                    style: Theme.of(context)
                                        .textTheme
                                        .titleMedium
                                        ?.copyWith(
                                          fontWeight: FontWeight.bold,
                                        ),
                                  ),

                                  const SizedBox(width: 12),

                                  Semantics(
                                    button: true,
                                    label: 'Open ${result.fileName}',
                                    child: ElevatedButton.icon(
                                      onPressed: () {
                                        _openFile(
                                          context,
                                          result,
                                        );
                                      },
                                      icon: const Icon(
                                        Icons.open_in_new,
                                      ),
                                      label: const Text('Open'),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        );
                      },
                    ),
            ),
          ],
        ),
      ),
    );
  }
}