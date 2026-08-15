import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';

import '../services/retrieval_service.dart';
import 'results_page.dart';

class SearchPage extends StatefulWidget {
  const SearchPage({
    super.key,
    required this.files,
  });

  final List<PlatformFile> files;

  @override
  State<SearchPage> createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  final TextEditingController _searchController = TextEditingController();

  bool _isSearching = false;

  Future<void> _performSearch() async {
    final query = _searchController.text.trim();

    if (query.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Please enter a search query.'),
        ),
      );
      return;
    }

    if (widget.files.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text(
            'No local files have been imported. '
            'Please import TXT files first.',
          ),
        ),
      );
      return;
    }

    setState(() {
      _isSearching = true;
    });

    try {
      final results = await RetrievalService.search(
        query: query,
        files: widget.files,
        topK: 5,
      );

      if (!mounted) {
        return;
      }

      await Navigator.push(
        context,
        MaterialPageRoute(
          builder: (context) => ResultsPage(
            query: query,
            results: results,
          ),
        ),
      );
    } catch (e) {
      if (!mounted) {
        return;
      }

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            'Search failed: $e',
          ),
        ),
      );
    } finally {
      if (mounted) {
        setState(() {
          _isSearching = false;
        });
      }
    }
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Search'),
      ),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(32),
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 700),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const Icon(
                  Icons.search,
                  size: 72,
                ),

                const SizedBox(height: 20),

                Text(
                  'Search Local Files',
                  textAlign: TextAlign.center,
                  style:
                      Theme.of(context).textTheme.headlineMedium?.copyWith(
                            fontWeight: FontWeight.bold,
                          ),
                ),

                const SizedBox(height: 12),

                const Text(
                  'Enter a keyword or natural-language query to search your local file library.',
                  textAlign: TextAlign.center,
                ),

                const SizedBox(height: 12),

                Text(
                  '${widget.files.length} local file(s) available for search',
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.bodySmall,
                ),

                const SizedBox(height: 32),

                Semantics(
                  label: 'Search query input field',
                  textField: true,
                  child: TextField(
                    controller: _searchController,
                    autofocus: true,
                    enabled: !_isSearching,
                    textInputAction: TextInputAction.search,
                    onSubmitted: (_) {
                      if (!_isSearching) {
                        _performSearch();
                      }
                    },
                    decoration: const InputDecoration(
                      labelText: 'Search query',
                      hintText: 'Search local documents...',
                      prefixIcon: Icon(Icons.manage_search),
                      border: OutlineInputBorder(),
                    ),
                  ),
                ),

                const SizedBox(height: 16),

                Semantics(
                  button: true,
                  label: 'Search the local file library',
                  child: ElevatedButton.icon(
                    onPressed: _isSearching ? null : _performSearch,
                    icon: _isSearching
                        ? const SizedBox(
                            width: 18,
                            height: 18,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                            ),
                          )
                        : const Icon(Icons.search),
                    label: Padding(
                      padding: const EdgeInsets.symmetric(
                        vertical: 14,
                      ),
                      child: Text(
                        _isSearching ? 'Searching...' : 'Search',
                      ),
                    ),
                  ),
                ),

                const SizedBox(height: 24),

                const Card(
                  child: Padding(
                    padding: EdgeInsets.all(16),
                    child: Row(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Icon(Icons.info_outline),
                        SizedBox(width: 12),
                        Expanded(
                          child: Text(
                            'Search is performed locally. '
                            'Your files and queries are not uploaded '
                            'to the internet.',
                          ),
                        ),
                      ],
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