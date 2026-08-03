import 'package:flutter/material.dart';

class ResultsPage extends StatelessWidget {
  const ResultsPage({
    super.key,
    required this.query,
  });

  final String query;

  @override
  Widget build(BuildContext context) {
    final results = [
      {
        'name': 'project_report.pdf',
        'type': 'PDF',
        'score': '0.92',
        'icon': Icons.picture_as_pdf,
      },
      {
        'name': 'meeting_notes.docx',
        'type': 'Word',
        'score': '0.88',
        'icon': Icons.description,
      },
      {
        'name': 'sample_image.png',
        'type': 'Image',
        'score': '0.84',
        'icon': Icons.image,
      },
    ];

    return Scaffold(
      appBar: AppBar(
        title: const Text("Search Results"),
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
              '${results.length} matching files found',
              style: const TextStyle(color: Colors.grey),
            ),

            const SizedBox(height: 24),

            Expanded(
              child: ListView.builder(
                itemCount: results.length,
                itemBuilder: (context, index) {

                  final result = results[index];

                  return Card(
                    margin: const EdgeInsets.only(bottom: 14),
                    child: ListTile(

                      leading: Icon(
                        result['icon'] as IconData,
                        size: 36,
                      ),

                      title: Text(result['name'] as String),

                      subtitle: Text(
                        'Type: ${result['type']}    Similarity: ${result['score']}',
                      ),

                      trailing: ElevatedButton(
                        onPressed: () {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(
                              content: Text(
                                'Opening ${result['name']}...',
                              ),
                            ),
                          );
                        },
                        child: const Text("Open"),
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