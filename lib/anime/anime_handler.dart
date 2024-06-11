import 'package:crunchy_transmitter/anime/anime.dart';
import 'package:crunchy_transmitter/weekday.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart';

Future<Map<Weekday, List<Anime>>> fetchAndGroupAnimeByWeekday() async {
  List<Anime> animeList = await fetchData();
  Map<Weekday, List<Anime>> animeMap = groupAnimeByWeekday(animeList);
  return animeMap;
}

Future<List<Anime>> fetchData() async {
  print('Hello, world!');
  final response = await http.get(Uri.parse(
      'https://crunchytransmitter.ddns.net/CrunchyTransmitter-1.0-SNAPSHOT/anime'));

  if (response.statusCode == 200) {
    // Wenn die Anfrage erfolgreich war, konvertiere die empfangenen Daten in eine Liste von Anime-Objekten
    List<dynamic> data = jsonDecode(response.body);
    return data.map((json) => Anime.fromJson(json)).toList();
  } else {
    // Wenn die Anfrage fehlgeschlagen ist, wirf eine Ausnahme
    throw Exception('Failed to load data');
  }
}

Map<Weekday, List<Anime>> groupAnimeByWeekday(List<Anime> animeList) {
  Map<Weekday, List<Anime>> animeMap = {};
  for (final anime in animeList) {
    final weekday = WeekdayExtension.getWeekdayName(anime.episode.releaseTime);
    if (!animeMap.containsKey(weekday)) {
      animeMap[weekday] = [];
    }
    animeMap[weekday]!.add(anime);
  }
  return animeMap;
}

// Funktion zum Speichern der Daten in SharedPreferences
Future<void> saveAnimeDataToSharedPreferences(
    Map<Weekday, List<Anime>> animeData, SharedPreferences prefs) async {
  // Konvertiere die Map in ein JSON-String, da SharedPreferences nur primitive Datentypen speichern kann
  final String jsonString = jsonEncode(animeData.map((key, value) {
    return MapEntry(
        key.toString(), value.map((anime) => anime.toJson()).toList());
  }));
  prefs.setString('animeData', jsonString);
}

Future<Map<Weekday, List<Anime>>?> loadAnimeDataFromSharedPreferences() async {
  SharedPreferences prefs = await SharedPreferences.getInstance();
  String? jsonString = prefs.getString('animeData');
  if (jsonString != null) {
    // Wenn Daten vorhanden sind, konvertiere den JSON-String zurück in die Map
    Map<Weekday, dynamic> jsonMap = jsonDecode(jsonString);
    // Konvertiere die dynamische Map in die gewünschte Map<String, List<Anime>>
    Map<Weekday, List<Anime>> animeData =
        Map<Weekday, List<Anime>>.from(jsonMap.map(
      (key, value) =>
          MapEntry(key, (value as List).map((e) => Anime.fromJson(e)).toList()),
    ));
    return animeData;
  } else {
    // Wenn keine Daten vorhanden sind, gib null zurück oder einen leeren Map, je nach Bedarf
    return null;
  }
}
