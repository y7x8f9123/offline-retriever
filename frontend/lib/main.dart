import 'package:flutter/material.dart';
import 'pages/home_page.dart';

void main() {
  runApp(const OfflineRetrieverApp());
}

class OfflineRetrieverApp extends StatefulWidget {
  const OfflineRetrieverApp({super.key});

  @override
  State<OfflineRetrieverApp> createState() => _OfflineRetrieverAppState();
}

class _OfflineRetrieverAppState extends State<OfflineRetrieverApp> {
  bool _highContrast = false;
  double _fontScale = 1.0;

  void _setHighContrast(bool value) {
    setState(() {
      _highContrast = value;
    });
  }

  void _setFontScale(double value) {
    setState(() {
      _fontScale = value;
    });
  }

  @override
  Widget build(BuildContext context) {
    final normalTheme = ThemeData(
      colorScheme: ColorScheme.fromSeed(
        seedColor: Colors.blue,
        brightness: Brightness.light,
      ),
      useMaterial3: true,
    );

    final highContrastTheme = ThemeData(
      brightness: Brightness.dark,
      colorScheme: const ColorScheme.dark(
        primary: Colors.yellow,
        onPrimary: Colors.black,
        surface: Colors.black,
        onSurface: Colors.white,
      ),
      scaffoldBackgroundColor: Colors.black,
      useMaterial3: true,
    );

    return MaterialApp(
      title: 'Offline Local Retrieval System',
      debugShowCheckedModeBanner: false,
      theme: _highContrast ? highContrastTheme : normalTheme,
      builder: (context, child) {
        final mediaQuery = MediaQuery.of(context);

        return MediaQuery(
          data: mediaQuery.copyWith(
            textScaler: TextScaler.linear(_fontScale),
          ),
          child: child!,
        );
      },
      home: HomePage(
        highContrast: _highContrast,
        fontScale: _fontScale,
        onHighContrastChanged: _setHighContrast,
        onFontScaleChanged: _setFontScale,
      ),
    );
  }
}