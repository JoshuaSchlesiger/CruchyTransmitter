import 'package:crunchy_transmitter/anime/anime.dart';
import 'package:crunchy_transmitter/config.dart';
import 'package:crunchy_transmitter/weekday.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';

class FCM {
  static void instanceProcess() {
    final FirebaseMessaging firebaseMessaging = FirebaseMessaging.instance;

    firebaseMessaging.requestPermission();

    firebaseMessaging.getToken().then((token) {
      saveTokenToSharedPreferences(token!);
    });
  }

  static void saveTokenToSharedPreferences(String token) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();

    String? savedToken = prefs.getString('token');

    if (savedToken == null) {
      await prefs.setString('token', token);
      sendTokenToServer(token);
      return;
    }

    if (token != savedToken) {
      await prefs.setString('token', token);
      sendTokenToServer(token);

      String? animeDataString = prefs.getString('animeData');
      if (animeDataString == null) {
        return;
      }

      final Map<String, dynamic> jsonMap = jsonDecode(animeDataString);
      Map<Weekday, List<Anime>>? animeData =
          Map<Weekday, List<Anime>>.from(jsonMap.map(
        (key, value) => MapEntry(WeekdayExtension.fromString(key),
            (value as List).map((e) => Anime.fromJsonInStorage(e)).toList()),
      ));

      animeData.forEach((weekday, animeList) async {
        for (var anime in animeList) {
          if (anime.notification) {
            await changeSubscriptionAnime(anime.animeId);
          }
        }
      });
    }
  }

  static void sendTokenToServer(String token) async {
    String body = jsonEncode({'token': token, 'password': Config.password});
    http
        .post(
      Uri.parse("${Config.serverUrl}registerToken"),
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
  }

  static Future<int> changeSubscriptionAnime(int animeId) async {
    final FirebaseMessaging firebaseMessaging = FirebaseMessaging.instance;

    try {
      String? token = await firebaseMessaging.getToken();

      String body = jsonEncode(
          {'token': token, 'password': Config.password, 'animeID': animeId});
      var response = await http.post(
        Uri.parse("${Config.serverUrl}updateAnimeSub"),
        headers: {'Content-Type': 'application/json'},
        body: body,
      );

      if (response.statusCode == 200) {
        if (response.body == 'added') {
          return 1;
        } else if (response.body == 'deleted') {
          return 0;
        } else {
          return -1;
        }
      } else {
        return -1;
      }
    } catch (error) {
      return -1;
    }
  }
}
