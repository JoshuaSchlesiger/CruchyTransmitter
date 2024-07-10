import 'package:crunchy_transmitter/fcm.dart';
import 'package:crunchy_transmitter/subpages/no_internet_page.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:connectivity/connectivity.dart';

import 'my_app/my_app.dart';

@pragma('vm:entry-point')
Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  await Firebase.initializeApp();

  if (message.notification != null) {
    String? url = message.data['url'];
    SharedPreferences prefs = await SharedPreferences.getInstance();
    if (url != null && url.isNotEmpty) {
      prefs.setBool('backgroundMessage', true);
      prefs.setString('backgroundMessageURL', url);
    } else {
      prefs.setBool('backgroundMessage', false);
    }
  }
}

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  var connectivityResult = await Connectivity().checkConnectivity();
  bool isConnected = (connectivityResult == ConnectivityResult.mobile ||
      connectivityResult == ConnectivityResult.wifi);

  if (!isConnected) {
    runApp(const NoInternetApp());
  } else {
    // Google FCM init
    await Firebase.initializeApp();
    FCM.instanceProcess();

    SharedPreferences prefs = await SharedPreferences.getInstance();
    bool seenWelcomeScreen = prefs.getBool('seenWelcomeScreen') ?? false;

    FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);
    runApp(MyApp(seenWelcomeScreen: seenWelcomeScreen));
  }
}
