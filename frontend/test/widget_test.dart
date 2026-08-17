import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:frontend/pages/results_page.dart';
import 'package:frontend/pages/search_page.dart';
import 'package:frontend/services/retrieval_service.dart';

void main() {
  group('Search Page Tests', () {
    testWidgets(
      'Search page displays main search interface',
      (WidgetTester tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: SearchPage(),
          ),
        );

        expect(
          find.text('Search Local Files'),
          findsOneWidget,
        );

        expect(
          find.text('Search query'),
          findsOneWidget,
        );

        expect(
          find.text('Search'),
          findsWidgets,
        );

        expect(
          find.byType(TextField),
          findsOneWidget,
        );

        expect(
          find.text(
            'Search is performed locally using '
            'the persistent semantic index. '
            'Your files and queries are not uploaded '
            'to the internet.',
          ),
          findsOneWidget,
        );
      },
    );

    testWidgets(
      'Empty search query displays validation message',
      (WidgetTester tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: SearchPage(),
          ),
        );

        final searchButton =
            find.widgetWithText(
          ElevatedButton,
          'Search',
        );

        await tester.tap(
          searchButton,
        );

        await tester.pump();

        expect(
          find.text(
            'Please enter a search query.',
          ),
          findsOneWidget,
        );
      },
    );
  });

  group('Results Page Tests', () {
    testWidgets(
      'Results page displays retrieval results',
      (WidgetTester tester) async {
        const results =
            <RetrievalResult>[
          RetrievalResult(
            fileName: 'test_a.txt',
            filePath:
                r'C:\test\test_a.txt',
            fileType: 'txt',
            score: 0.5669,
          ),
          RetrievalResult(
            fileName: 'test_b.txt',
            filePath:
                r'C:\test\test_b.txt',
            fileType: 'txt',
            score: 0.0,
          ),
        ];

        await tester.pumpWidget(
          const MaterialApp(
            home: ResultsPage(
              query:
                  'software engineering',
              results: results,
            ),
          ),
        );

        expect(
          find.text(
            'Query: "software engineering"',
          ),
          findsOneWidget,
        );

        expect(
          find.text(
            '2 matching file(s) found',
          ),
          findsOneWidget,
        );

        expect(
          find.text('test_a.txt'),
          findsOneWidget,
        );

        expect(
          find.text('test_b.txt'),
          findsOneWidget,
        );

        expect(
          find.textContaining(
            'Similarity: 0.5669',
          ),
          findsOneWidget,
        );

        expect(
          find.textContaining(
            'Similarity: 0.0000',
          ),
          findsOneWidget,
        );

        expect(
          find.text('#1'),
          findsOneWidget,
        );

        expect(
          find.text('#2'),
          findsOneWidget,
        );
      },
    );

    testWidgets(
      'Results page displays empty state when no results are returned',
      (WidgetTester tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: ResultsPage(
              query: 'unknown query',
              results:
                  <RetrievalResult>[],
            ),
          ),
        );

        expect(
          find.text(
            'Query: "unknown query"',
          ),
          findsOneWidget,
        );

        expect(
          find.text(
            '0 matching file(s) found',
          ),
          findsOneWidget,
        );

        expect(
          find.text(
            'No matching files were found.',
          ),
          findsOneWidget,
        );
      },
    );
  });
}