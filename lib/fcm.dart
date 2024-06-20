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

  static Future<void> firebaseMessagingBackgroundHandler(
      RemoteMessage message) async {
    Map<Weekday, List<Anime>> animeData = await fetchAndGroupAnimeByWeekday();

    Map<String, dynamic> messageJson = message.data;

    if (message.messageType == "release") {
      bool foundAnime = false;

      animeData.forEach((weekday, animeList) {
        if (foundAnime) {
          return;
        }

        animeList.forEach((anime) {
          if (foundAnime) {
            return;
          }

          if (anime.animeId == messageJson["animeId"]) {
            if (anime.notification) {
              foundAnime = true;
            }
          }
        });
      });
      if (!foundAnime) {return;}
      await Firebase.initializeApp();
      await _showNotification(message, messageJson);
    }

    // print('Nachricht im Hintergrund: ${message.messageId}');
  }

  static Future<void> _showNotification(
      RemoteMessage message, Map<String, dynamic> messageJson) async {
    const AndroidNotificationDetails androidNotificationDetails =
        AndroidNotificationDetails(
      'your channel id',
      'your channel name',
      channelDescription: 'your channel description',
      importance: Importance.max,
      priority: Priority.high,
      ticker: 'ticker',
    );
    const NotificationDetails notificationDetails =
        NotificationDetails(android: androidNotificationDetails);

    await FlutterLocalNotificationsPlugin().show(
      0,
      message.notification!.title!,
      message.notification!.body!,
      notificationDetails,
      payload: json.encode(message.data),
    );
  }
}
