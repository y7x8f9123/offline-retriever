import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';

import 'library_page.dart';
import 'search_page.dart';
import 'settings_page.dart';

class HomePage extends StatefulWidget {
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
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final List<PlatformFile> _importedFiles = [];

  void _updateImportedFiles(List<PlatformFile> files) {
    setState(() {
      _importedFiles
        ..clear()
        ..addAll(files);
    });
  }

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
                                builder: (context) => LibraryPage(
                                  initialFiles: _importedFiles,
                                  onFilesChanged: _updateImportedFiles,
                                ),
                              ),
                            );
                          },
                          icon: const Icon(Icons.folder),
                          label: const Padding(
                            padding: EdgeInsets.symmetric(
                              vertical: 12,
                            ),
                            child: Text('File Library'),
                          ),
                        ),
                      ),
                    ),
                  ),

                  const SizedBox(height: 15),

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
                                builder: (context) => SearchPage(
                                  files: _importedFiles,
                                ),
                              ),
                            );
                          },
                          icon: const Icon(Icons.search),
                          label: const Padding(
                            padding: EdgeInsets.symmetric(
                              vertical: 12,
                            ),
                            child: Text('Search'),
                          ),
                        ),
                      ),
                    ),
                  ),

                  const SizedBox(height: 15),

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
                                  highContrast: widget.highContrast,
                                  fontScale: widget.fontScale,
                                  onHighContrastChanged:
                                      widget.onHighContrastChanged,
                                  onFontScaleChanged:
                                      widget.onFontScaleChanged,
                                ),
                              ),
                            );
                          },
                          icon: const Icon(Icons.settings),
                          label: const Padding(
                            padding: EdgeInsets.symmetric(
                              vertical: 12,
                            ),
                            child: Text('Settings'),
                          ),
                        ),
                      ),
                    ),
                  ),

                  const SizedBox(height: 28),

                  Text(
                    '${_importedFiles.length} local file(s) imported',
                    textAlign: TextAlign.center,
                  ),

                  const SizedBox(height: 12),

                  const Text(
                    'Keyboard: Tab / Shift+Tab to navigate, Enter or Space to select.',
                    textAlign: TextAlign.center,
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