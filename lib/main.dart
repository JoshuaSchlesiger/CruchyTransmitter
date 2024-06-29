import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'my_app/my_app.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  //Google FCM init
  await Firebase.initializeApp();

  SharedPreferences prefs = await SharedPreferences.getInstance();
  bool seenWelcomeScreen = prefs.getBool('seenWelcomeScreen') ?? false;

  runApp(MyApp(seenWelcomeScreen : seenWelcomeScreen));
}
