import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';

class LibraryPage extends StatefulWidget {
  const LibraryPage({
    super.key,
    required this.initialFiles,
    required this.onFilesChanged,
  });

  final List<PlatformFile> initialFiles;
  final ValueChanged<List<PlatformFile>> onFilesChanged;

  @override
  State<LibraryPage> createState() => _LibraryPageState();
}

class _LibraryPageState extends State<LibraryPage> {
  late List<PlatformFile> _files;
  final Map<String, int> _fileSizes = {};

  @override
  void initState() {
    super.initState();

    _files = List<PlatformFile>.from(widget.initialFiles);

    _loadExistingFileSizes();
  }

  Future<void> _loadExistingFileSizes() async {
    for (final file in _files) {
      if (file.path != null) {
        _fileSizes[file.path!] = await file.length();
      }
    }

    if (mounted) {
      setState(() {});
    }
  }

  Future<void> _importFiles() async {
    final files = await FilePicker.pickFiles(
      allowMultiple: true,
      type: FileType.custom,
      allowedExtensions: [
        'txt',
        'pdf',
        'docx',
      ],
    );

    if (files.isEmpty) {
      return;
    }

    int addedCount = 0;

    for (final file in files) {
      if (file.path == null) {
        continue;
      }

      final alreadyAdded = _files.any(
        (existing) => existing.path == file.path,
      );

      if (!alreadyAdded) {
        final size = await file.length();

        _files.add(file);
        _fileSizes[file.path!] = size;

        addedCount++;
      }
    }

    if (!mounted) {
      return;
    }

    setState(() {});

    widget.onFilesChanged(
      List<PlatformFile>.from(_files),
    );

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('$addedCount file(s) imported.'),
      ),
    );
  }

  void _removeFile(int index) {
    final file = _files[index];
    final fileName = file.name;

    setState(() {
      if (file.path != null) {
        _fileSizes.remove(file.path);
      }

      _files.removeAt(index);
    });

    widget.onFilesChanged(
      List<PlatformFile>.from(_files),
    );

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(
          '$fileName removed from the local library.',
        ),
      ),
    );
  }

  String _formatFileSize(int bytes) {
    if (bytes < 1024) {
      return '$bytes B';
    }

    if (bytes < 1024 * 1024) {
      return '${(bytes / 1024).toStringAsFixed(1)} KB';
    }

    return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('File Library'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Semantics(
              button: true,
              label: 'Import supported local documents',
              hint: 'Supported formats are TXT, PDF, and DOCX',
              child: ElevatedButton.icon(
                onPressed: _importFiles,
                icon: const Icon(Icons.upload_file),
                label: const Text('Import Files'),
              ),
            ),

            const SizedBox(height: 12),

            const Text(
              'Supported formats: TXT, PDF, DOCX',
              textAlign: TextAlign.center,
            ),

            const SizedBox(height: 24),

            Text(
              'Local Files (${_files.length})',
              style: Theme.of(context).textTheme.titleLarge,
            ),

            const SizedBox(height: 12),

            Expanded(
              child: _files.isEmpty
                  ? const Center(
                      child: Text(
                        'No files imported yet.\n'
                        'Select Import Files to add TXT, PDF, or DOCX documents.',
                        textAlign: TextAlign.center,
                      ),
                    )
                  : ListView.separated(
                      itemCount: _files.length,
                      separatorBuilder: (context, index) {
                        return const SizedBox(height: 10);
                      },
                      itemBuilder: (context, index) {
                        final file = _files[index];

                        final size = file.path == null
                            ? 0
                            : (_fileSizes[file.path!] ?? 0);

                        final typeLabel = _fileTypeLabel(
                          file.name,
                        );

                        return Semantics(
                          label:
                              '${file.name}, $typeLabel, ${_formatFileSize(size)}',
                          child: Card(
                            child: ListTile(
                              leading: Icon(
                                _fileIcon(file.name),
                                size: 36,
                              ),

                              title: Text(file.name),

                              subtitle: Text(
                                '$typeLabel • ${_formatFileSize(size)}',
                              ),

                              trailing: IconButton(
                                tooltip: 'Remove ${file.name}',
                                onPressed: () {
                                  _removeFile(index);
                                },
                                icon: const Icon(
                                  Icons.delete_outline,
                                ),
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