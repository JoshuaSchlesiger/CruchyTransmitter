import 'package:crunchy_transmitter/config.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:http/http.dart' as http;

class FCM {
  static void instanceProcess() {
    final FirebaseMessaging _firebaseMessaging = FirebaseMessaging.instance;

    _firebaseMessaging.requestPermission();

    _firebaseMessaging.getToken().then((token) {
      print("FCM Token: $token");

      http.post(Uri.parse(Config.serverUrl),
          headers: {'password': Config.password},
          body: {'token': token}).then((response) {
        if (response.statusCode == 200) {
          print('Token erfolgreich an Server gesendet');
        } else {
          print(
              'Fehler beim Senden des Tokens an den Server: ${response.statusCode}');
        }
      }).catchError((error) {
        print('Fehler beim Senden des Tokens an den Server: $error');
      });
    });
  }

  static Future<void> firebaseMessagingBackgroundHandler(
      RemoteMessage message) async {
    await Firebase.initializeApp();
    print('Nachricht im Hintergrund: ${message.messageId}');
  }
}
