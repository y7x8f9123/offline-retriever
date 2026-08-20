import 'package:flutter/material.dart';

class SettingsPage extends StatefulWidget {
  const SettingsPage({
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
  State<SettingsPage> createState() => _SettingsPageState();
}

class _SettingsPageState extends State<SettingsPage> {
  late bool _highContrast;
  late double _fontScale;

  @override
  void initState() {
    super.initState();

    // Read the current application settings when the page opens.
    _highContrast = widget.highContrast;
    _fontScale = widget.fontScale;
  }

  String _fontScaleLabel(double value) {
    if (value <= 0.9) {
      return 'Small';
    } else if (value <= 1.1) {
      return 'Medium';
    } else if (value <= 1.3) {
      return 'Large';
    } else {
      return 'Extra Large';
    }
  }

  void _changeHighContrast(bool value) {
    setState(() {
      _highContrast = value;
    });

    widget.onHighContrastChanged(value);
  }

  void _changeFontScale(double value) {
    setState(() {
      _fontScale = value;
    });

    widget.onFontScaleChanged(value);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Settings'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Center(
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 800),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  'Accessibility',
                  style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                ),
                const SizedBox(height: 16),

                Card(
                  child: Semantics(
                    label: 'High contrast mode setting',
                    toggled: _highContrast,
                    child: SwitchListTile(
                      title: const Text('High Contrast Mode'),
                      subtitle: const Text(
                        'Use stronger foreground and background contrast.',
                      ),
                      secondary: const Icon(Icons.contrast),
                      value: _highContrast,
                      onChanged: _changeHighContrast,
                    ),
                  ),
                ),

                const SizedBox(height: 16),

                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(20),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        Row(
                          children: [
                            const Icon(Icons.text_fields),
                            const SizedBox(width: 12),
                            Expanded(
                              child: Text(
                                'Font Size',
                                style: Theme.of(context).textTheme.titleMedium,
                              ),
                            ),
                            Text(
                              _fontScaleLabel(_fontScale),
                              style: const TextStyle(
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 12),
                        Semantics(
                          label:
                              'Font size slider, currently ${_fontScaleLabel(_fontScale)}',
                          slider: true,
                          child: Slider(
                            value: _fontScale,
                            min: 0.8,
                            max: 1.4,
                            divisions: 3,
                            label: _fontScaleLabel(_fontScale),
                            onChanged: _changeFontScale,
                          ),
                        ),
                        const SizedBox(height: 8),
                        const Text(
                          'Preview text: Search your local documents completely offline.',
                        ),
                      ],
                    ),
                  ),
                ),

                const SizedBox(height: 16),

                const Card(
                  child: ListTile(
                    leading: Icon(Icons.record_voice_over),
                    title: Text('Screen Reader Support'),
                    subtitle: Text(
                      'Semantic labels are provided for navigation controls, form fields, buttons, and file results.',
                    ),
                  ),
                ),

                const SizedBox(height: 16),

                const Card(
                  child: ListTile(
                    leading: Icon(Icons.keyboard),
                    title: Text('Keyboard Navigation'),
                    subtitle: Text(
                      'Use Tab and Shift+Tab to move between controls, and Enter or Space to activate them.',
                    ),
                  ),
                ),

                const SizedBox(height: 24),

                Text(
                  'About',
                  style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                ),

                const SizedBox(height: 12),

                const Card(
                  child: ListTile(
                    leading: Icon(Icons.info_outline),
                    title: Text('Offline Local Retrieval System'),
                    subtitle: Text(
                      'Accessible Windows desktop interface developed with Flutter.',
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}