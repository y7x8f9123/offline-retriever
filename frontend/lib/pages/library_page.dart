import 'dart:io';

import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

import '../services/retrieval_service.dart';

class LibraryPage extends StatefulWidget {
  const LibraryPage({
    super.key,
    required this.initialFiles,
  });

  final List<IndexedFile> initialFiles;

  @override
  State<LibraryPage> createState() =>
      _LibraryPageState();
}

class _LibraryPageState
    extends State<LibraryPage> {
  late List<IndexedFile> _files;

  bool _busy = false;

  @override
  void initState() {
    super.initState();

    _files = List<IndexedFile>.from(
      widget.initialFiles,
    );
  }

  Future<void> _reloadFiles() async {
    final files =
        await RetrievalService
            .loadIndexedFiles();

    if (!mounted) {
      return;
    }

    setState(() {
      _files = files;
    });
  }

  Future<void> _importFiles() async {
    final selectedFiles =
        await FilePicker.pickFiles(
      allowMultiple: true,
      type: FileType.custom,
      allowedExtensions: [
        'txt',
        'pdf',
        'docx',
        'jpg',
        'jpeg',
        'png',
      ],
    );

    if (selectedFiles.isEmpty) {
      return;
    }

    setState(() {
      _busy = true;
    });

    try {
      await RetrievalService.indexFiles(
        selectedFiles,
      );

      await _reloadFiles();

      if (!mounted) {
        return;
      }

      ScaffoldMessenger.of(context)
          .showSnackBar(
        SnackBar(
          content: Text(
            '${selectedFiles.length} '
            'file(s) indexed successfully.',
          ),
        ),
      );
    } catch (e) {
      if (!mounted) {
        return;
      }

      ScaffoldMessenger.of(context)
          .showSnackBar(
        SnackBar(
          content: Text(
            'Import failed: $e',
          ),
        ),
      );
    } finally {
      if (mounted) {
        setState(() {
          _busy = false;
        });
      }
    }
  }

  Future<void> _openFile(
    IndexedFile file,
  ) async {
    final localFile =
        File(file.filePath);

    if (!await localFile.exists()) {
      if (!mounted) {
        return;
      }

      ScaffoldMessenger.of(context)
          .showSnackBar(
        SnackBar(
          content: Text(
            'File not found: '
            '${file.filePath}',
          ),
        ),
      );

      return;
    }

    try {
      final opened =
          await launchUrl(
        Uri.file(
          file.filePath,
        ),
        mode:
            LaunchMode.externalApplication,
      );

      if (
          !opened &&
          mounted
      ) {
        ScaffoldMessenger.of(context)
            .showSnackBar(
          SnackBar(
            content: Text(
              'Could not open '
              '${file.fileName}.',
            ),
          ),
        );
      }
    } catch (e) {
      if (!mounted) {
        return;
      }

      ScaffoldMessenger.of(context)
          .showSnackBar(
        SnackBar(
          content: Text(
            'Failed to open '
            '${file.fileName}: $e',
          ),
        ),
      );
    }
  }

  Future<void> _removeFile(
    IndexedFile file,
  ) async {
    setState(() {
      _busy = true;
    });

    try {
      await RetrievalService
          .deleteIndexedFile(
        file.id,
      );

      await _reloadFiles();

      if (!mounted) {
        return;
      }

      ScaffoldMessenger.of(context)
          .showSnackBar(
        SnackBar(
          content: Text(
            '${file.fileName} '
            'removed from the index.',
          ),
        ),
      );
    } catch (e) {
      if (!mounted) {
        return;
      }

      ScaffoldMessenger.of(context)
          .showSnackBar(
        SnackBar(
          content: Text(
            'Delete failed: $e',
          ),
        ),
      );
    } finally {
      if (mounted) {
        setState(() {
          _busy = false;
        });
      }
    }
  }

  String _formatFileSize(
    int bytes,
  ) {
    if (bytes < 1024) {
      return '$bytes B';
    }

    if (bytes < 1024 * 1024) {
      return '${(bytes / 1024).toStringAsFixed(1)} KB';
    }

    return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
  }

  String _fileTypeLabel(
    String type,
  ) {
    switch (type.toLowerCase()) {
      case 'pdf':
        return 'PDF document';

      case 'docx':
        return 'Word document';

      case 'txt':
        return 'Text document';

      case 'jpg':
      case 'jpeg':
        return 'JPEG image';

      case 'png':
        return 'PNG image';

      default:
        return 'File';
    }
  }

  IconData _fileIcon(
    String type,
  ) {
    switch (type.toLowerCase()) {
      case 'pdf':
        return Icons.picture_as_pdf;

      case 'docx':
        return Icons.description;

      case 'txt':
        return Icons.text_snippet;

      case 'jpg':
      case 'jpeg':
      case 'png':
        return Icons.image;

      default:
        return Icons.insert_drive_file;
    }
  }

  @override
  Widget build(
    BuildContext context,
  ) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'File Library',
        ),
      ),
      body: Padding(
        padding:
            const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment:
              CrossAxisAlignment.stretch,
          children: [
            Semantics(
              button: true,
              label:
                  'Import supported local files',
              hint:
                  'Supported formats are TXT, PDF, DOCX, JPG, JPEG, and PNG',
              child:
                  ElevatedButton.icon(
                onPressed:
                    _busy
                        ? null
                        : _importFiles,
                icon: _busy
                    ? const SizedBox(
                        width: 18,
                        height: 18,
                        child:
                            CircularProgressIndicator(
                          strokeWidth: 2,
                        ),
                      )
                    : const Icon(
                        Icons.upload_file,
                      ),
                label: Text(
                  _busy
                      ? 'Processing...'
                      : 'Import Files',
                ),
              ),
            ),

            const SizedBox(
              height: 12,
            ),

            const Text(
              'Supported formats: '
              'TXT, PDF, DOCX, JPG, JPEG, PNG',
              textAlign:
                  TextAlign.center,
            ),

            const SizedBox(
              height: 24,
            ),

            Text(
              'Indexed Files '
              '(${_files.length})',
              style: Theme.of(context)
                  .textTheme
                  .titleLarge,
            ),

            const SizedBox(
              height: 12,
            ),

            Expanded(
              child: _files.isEmpty
                  ? const Center(
                      child: Text(
                        'No indexed files yet.\n'
                        'Select Import Files '
                        'to add local documents '
                        'or images.',
                        textAlign:
                            TextAlign.center,
                      ),
                    )
                  : ListView.separated(
                      itemCount:
                          _files.length,
                      separatorBuilder:
                          (
                            context,
                            index,
                          ) {
                        return const SizedBox(
                          height: 10,
                        );
                      },
                      itemBuilder:
                          (
                            context,
                            index,
                          ) {
                        final file =
                            _files[index];

                        final label =
                            _fileTypeLabel(
                          file.fileType,
                        );

                        final status =
                            file.exists
                                ? ''
                                : ' • Missing';

                        return Semantics(
                          label:
                              '${file.fileName}, '
                              '$label, '
                              '${_formatFileSize(file.fileSize)}'
                              '${file.exists ? '' : ', original file missing'}',
                          child: Card(
                            child:
                                ListTile(
                              leading:
                                  Icon(
                                _fileIcon(
                                  file.fileType,
                                ),
                                size: 36,
                              ),
                              title: Text(
                                file.fileName,
                              ),
                              subtitle: Text(
                                '$label • '
                                '${_formatFileSize(file.fileSize)}'
                                '$status',
                              ),
                              trailing: Row(
                                mainAxisSize:
                                    MainAxisSize.min,
                                children: [
                                  IconButton(
                                    tooltip:
                                        'Open ${file.fileName}',
                                    onPressed:
                                        file.exists &&
                                                !_busy
                                            ? () {
                                                _openFile(
                                                  file,
                                                );
                                              }
                                            : null,
                                    icon:
                                        const Icon(
                                      Icons
                                          .open_in_new,
                                    ),
                                  ),
                                  IconButton(
                                    tooltip:
                                        'Remove ${file.fileName} from index',
                                    onPressed:
                                        _busy
                                            ? null
                                            : () {
                                                _removeFile(
                                                  file,
                                                );
                                              },
                                    icon:
                                        const Icon(
                                      Icons
                                          .delete_outline,
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