

import 'package:crunchy_transmitter/welcome_page/welcome_screen.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class WelcomeScreenState extends State<WelcomeScreen> {
  late PageController _pageController;
  int _currentPage = 0;

  @override
  void initState() {
    super.initState();
    _pageController = PageController();
  }

  void _onPageChanged(int page) {
    setState(() {
      _currentPage = page;
    });
  }

  void _markWelcomeScreenSeen() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setBool('seenWelcomeScreen', true);
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Willkommensbildschirm'),
        automaticallyImplyLeading: false,
      ),
      body: PageView(
        controller: _pageController,
        onPageChanged: _onPageChanged,
        children: [
          _buildPage(1, 'Willkommen zur App!', 'Erste Seite Beschreibung'),
          _buildPage(2, 'Noch eine Seite', 'Zweite Seite Beschreibung'),
          _buildPage(3, 'Letzte Seite', 'Dritte Seite Beschreibung'),
        ],
      ),
      bottomNavigationBar: BottomAppBar(
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 16.0),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              TextButton(
                child: Text(
                  _currentPage < 2 ? 'Weiter' : 'Fertig',
                  style: const TextStyle(color: Colors.blue),
                ),
                onPressed: () {
                  if (_currentPage < 2) {
                    _pageController.nextPage(
                      duration: const Duration(milliseconds: 300),
                      curve: Curves.ease,
                    );
                  } else {
                    _markWelcomeScreenSeen();
                    Navigator.of(context).pushReplacementNamed('/home');
                  }
                },
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildPage(int pageNumber, String title, String description) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 32.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            title,
            style: const TextStyle(fontSize: 24.0, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 20.0),
          Text(
            description,
            textAlign: TextAlign.center,
            style: const TextStyle(fontSize: 18.0),
          ),
          const SizedBox(height: 40.0),
          Text(
            'Seite $pageNumber von 3',
            style: const TextStyle(fontSize: 16.0, color: Colors.grey),
          ),
        ],
      ),
    );
  }
}