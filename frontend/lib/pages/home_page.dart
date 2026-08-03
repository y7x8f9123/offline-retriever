import 'package:flutter/material.dart';

import 'library_page.dart';
import 'search_page.dart';
import 'settings_page.dart';

class HomePage extends StatelessWidget {
  const HomePage({
    super.key,
    required this.highContrast,
    required this.fontScale,
    required this.onHighContrastChanged,
    required this.onFontScaleChanged,
  });

  final bool highContrast;
  final double fontScale;
  final ValueChanged<bool> onHighContrastChanged;
  final ValueChanged<double> onFontScaleChanged;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Offline Local Retrieval System'),
        centerTitle: true,
      ),
      body: FocusTraversalGroup(
        policy: OrderedTraversalPolicy(),
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 600),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Semantics(
                    label: 'Local file retrieval application',
                    image: true,
                    child: const Icon(
                      Icons.folder_open,
                      size: 80,
                    ),
                  ),
                  const SizedBox(height: 20),
                  Text(
                    'Local Semantic Search',
                    textAlign: TextAlign.center,
                    style:
                        Theme.of(context).textTheme.headlineMedium?.copyWith(
                              fontWeight: FontWeight.bold,
                            ),
                  ),
                  const SizedBox(height: 10),
                  const Text(
                    'Search your local documents completely offline.',
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 40),

                  // First keyboard focus target.
                  FocusTraversalOrder(
                    order: const NumericFocusOrder(1),
                    child: Semantics(
                      button: true,
                      label: 'Open file library',
                      hint: 'View and manage local files',
                      child: SizedBox(
                        width: 240,
                        child: ElevatedButton.icon(
                          autofocus: true,
                          onPressed: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => const LibraryPage(),
                              ),
                            );
                          },
                          icon: const Icon(Icons.folder),
                          label: const Padding(
                            padding: EdgeInsets.symmetric(vertical: 12),
                            child: Text('File Library'),
                          ),
                        ),
                      ),
                    ),
                  ),

                  const SizedBox(height: 15),

                  // Second keyboard focus target.
                  FocusTraversalOrder(
                    order: const NumericFocusOrder(2),
                    child: Semantics(
                      button: true,
                      label: 'Open search interface',
                      hint: 'Search the local file library',
                      child: SizedBox(
                        width: 240,
                        child: ElevatedButton.icon(
                          onPressed: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => const SearchPage(),
                              ),
                            );
                          },
                          icon: const Icon(Icons.search),
                          label: const Padding(
                            padding: EdgeInsets.symmetric(vertical: 12),
                            child: Text('Search'),
                          ),
                        ),
                      ),
                    ),
                  ),

                  const SizedBox(height: 15),

                  // Third keyboard focus target.
                  FocusTraversalOrder(
                    order: const NumericFocusOrder(3),
                    child: Semantics(
                      button: true,
                      label: 'Open accessibility settings',
                      hint:
                          'Change high contrast mode and application font size',
                      child: SizedBox(
                        width: 240,
                        child: ElevatedButton.icon(
                          onPressed: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => SettingsPage(
                                  highContrast: highContrast,
                                  fontScale: fontScale,
                                  onHighContrastChanged:
                                      onHighContrastChanged,
                                  onFontScaleChanged: onFontScaleChanged,
                                ),
                              ),
                            );
                          },
                          icon: const Icon(Icons.settings),
                          label: const Padding(
                            padding: EdgeInsets.symmetric(vertical: 12),
                            child: Text('Settings'),
                          ),
                        ),
                      ),
                    ),
                  ),

                  const SizedBox(height: 28),

                  Semantics(
                    label:
                        'Keyboard instructions. Press Tab or Shift plus Tab to move between controls. Press Enter or Space to activate a selected control.',
                    child: const Text(
                      'Keyboard: Tab / Shift+Tab to navigate, Enter or Space to select.',
                      textAlign: TextAlign.center,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}