import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../welcome_page/welcome_screen.dart';
import 'my_home_page.dart';

class MyApp extends StatelessWidget {
  final bool seenWelcomeScreen;
  const MyApp({super.key, required this.seenWelcomeScreen});

  @override
  Widget build(BuildContext context) {
    
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);

    return MaterialApp(
      title: 'CrunchyTransmitter',
      theme: ThemeData(
          scaffoldBackgroundColor: const Color.fromARGB(255, 20, 20, 20)),
      initialRoute: '/',
      routes: {
        '/': (context) => seenWelcomeScreen 
            ? const MyHomePage(title: 'CrunchyTransmitter') 
            : const WelcomeScreen(),
        '/home': (context) => const MyHomePage(title: 'CrunchyTransmitter'),
      },
    );
  }
}