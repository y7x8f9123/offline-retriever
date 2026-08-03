import 'package:flutter/material.dart';

class LibraryPage extends StatelessWidget {
  const LibraryPage({super.key});

  @override
  Widget build(BuildContext context) {
    final files = [
      {
        'name': 'project_report.pdf',
        'type': 'PDF document',
        'size': '2.4 MB',
        'icon': Icons.picture_as_pdf,
      },
      {
        'name': 'meeting_notes.docx',
        'type': 'Word document',
        'size': '850 KB',
        'icon': Icons.description,
      },
      {
        'name': 'sample_image.png',
        'type': 'Image',
        'size': '1.2 MB',
        'icon': Icons.image,
      },
      {
        'name': 'research_data.txt',
        'type': 'Text document',
        'size': '120 KB',
        'icon': Icons.text_snippet,
      },
    ];

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
              label: 'Import files into the local library',
              child: ElevatedButton.icon(
                onPressed: () {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('File import will be connected later.'),
                    ),
                  );
                },
                icon: const Icon(Icons.upload_file),
                label: const Text('Import Files'),
              ),
            ),
            const SizedBox(height: 12),
            OutlinedButton.icon(
              onPressed: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text('File library refreshed.'),
                  ),
                );
              },
              icon: const Icon(Icons.refresh),
              label: const Text('Refresh Library'),
            ),
            const SizedBox(height: 24),
            Text(
              'Local Files (${files.length})',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 12),
            Expanded(
              child: ListView.separated(
                itemCount: files.length,
                separatorBuilder: (context, index) =>
                    const SizedBox(height: 10),
                itemBuilder: (context, index) {
                  final file = files[index];

                  return Semantics(
                    label:
                        '${file['name']}, ${file['type']}, ${file['size']}',
                    button: true,
                    child: Card(
                      child: ListTile(
                        leading: Icon(
                          file['icon'] as IconData,
                          size: 36,
                        ),
                        title: Text(file['name'] as String),
                        subtitle: Text(
                          '${file['type']} • ${file['size']}',
                        ),
                        trailing: IconButton(
                          tooltip: 'Remove ${file['name']}',
                          onPressed: () {
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(
                                content: Text(
                                  '${file['name']} removal is not connected yet.',
                                ),
                              ),
                            );
                          },
                          icon: const Icon(Icons.delete_outline),
                        ),
                        onTap: () {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(
                              content: Text('Selected ${file['name']}'),
                            ),
                          );
                        },
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