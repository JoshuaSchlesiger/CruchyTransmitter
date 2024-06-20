import 'package:crunchy_transmitter/config.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class FCM {
  static void instanceProcess() {
    final FirebaseMessaging firebaseMessaging = FirebaseMessaging.instance;

    firebaseMessaging.requestPermission();

    firebaseMessaging.getToken().then((token) {
      // print("FCM Token: $token");

      String body = jsonEncode({'token': token, 'password': Config.password});
      http
          .post(
        Uri.parse(Config.serverUrl),
        headers: {
          'Content-Type': 'application/json',
        },
        body: body,
      )
          .then((response) {
        // if (response.statusCode == 200) {
        //   print('Token erfolgreich an Server gesendet');
        // } else {
        //   print(
        //       'Fehler beim Senden des Tokens an den Server: ${response.statusCode}');
        // }
      }).catchError((error) {
        // print('Fehler beim Senden des Tokens an den Server: $error');
      });
    });
  }
}
