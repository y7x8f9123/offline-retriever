import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:frontend/pages/search_page.dart';
import 'package:frontend/pages/results_page.dart';

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

        expect(find.text('Search Local Files'), findsOneWidget);
        expect(find.text('Search query'), findsOneWidget);
        expect(find.text('Search'), findsWidgets);
        expect(find.byType(TextField), findsOneWidget);

        expect(
          find.text(
            'Search is performed locally. Your files and queries are not uploaded to the internet.',
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

        final searchButton = find.widgetWithText(
          ElevatedButton,
          'Search',
        );

        await tester.tap(searchButton);
        await tester.pump();

        expect(
          find.text('Please enter a search query.'),
          findsOneWidget,
        );
      },
    );

    testWidgets(
      'Valid query navigates to results page',
      (WidgetTester tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: SearchPage(),
          ),
        );

        await tester.enterText(
          find.byType(TextField),
          'offline retrieval',
        );

        final searchButton = find.widgetWithText(
          ElevatedButton,
          'Search',
        );

        await tester.tap(searchButton);
        await tester.pumpAndSettle();

        expect(find.text('Search Results'), findsOneWidget);

        expect(
          find.text('Query: "offline retrieval"'),
          findsOneWidget,
        );

        expect(
          find.text('3 matching files found'),
          findsOneWidget,
        );
      },
    );
  });

  group('Results Page Tests', () {
    testWidgets(
      'Results page displays mock retrieval results',
      (WidgetTester tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: ResultsPage(
              query: 'offline retrieval',
            ),
          ),
        );

        expect(
          find.text('Query: "offline retrieval"'),
          findsOneWidget,
        );

        expect(find.text('project_report.pdf'), findsOneWidget);
        expect(find.text('meeting_notes.docx'), findsOneWidget);
        expect(find.text('sample_image.png'), findsOneWidget);

        expect(
          find.text('3 matching files found'),
          findsOneWidget,
        );
      },
    );

    testWidgets(
      'Open button displays feedback message',
      (WidgetTester tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: ResultsPage(
              query: 'offline retrieval',
            ),
          ),
        );

        final openButtons = find.widgetWithText(
          ElevatedButton,
          'Open',
        );

        expect(openButtons, findsNWidgets(3));

        await tester.tap(openButtons.first);
        await tester.pump();

        expect(
          find.text('Opening project_report.pdf...'),
          findsOneWidget,
        );
      },
    );
  });
}