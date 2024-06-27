import 'package:crunchy_transmitter/anime/anime.dart';
import 'package:crunchy_transmitter/config/config.dart';
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
  final response = await http.get(Uri.parse("${Config.serverUrl}anime"));

  if (response.statusCode == 200) {
    List<dynamic> data = jsonDecode(response.body);
    return data.map((json) => Anime.fromJson(json)).toList();
  } else {
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

Map<Weekday, List<Anime>> sortAnimeByCurrentWeekday(
    Map<Weekday, List<Anime>> animeData) {
  final sortedWeekdays = WeekdayExtension.sortedByCurrentFirst();
  final sortedAnimeData = <Weekday, List<Anime>>{};
  for (var weekday in sortedWeekdays) {
    if (animeData.containsKey(weekday)) {
      sortedAnimeData[weekday] = animeData[weekday]!;
    }
  }

  return sortedAnimeData;
}

Future<void> saveAnimeDataToSharedPreferences(
    Map<Weekday, List<Anime>> animeData,
    SharedPreferences prefs,
    String storageKeyAnimeData) async {
  final String jsonString = jsonEncode(animeData.map((key, value) {
    return MapEntry(
        key.toString(), value.map((anime) => anime.toJson()).toList());
  }));
  prefs.setString(storageKeyAnimeData, jsonString);
}

Future<void> updateSingleAnimeInSharedPreferences(
    Anime anime, SharedPreferences prefs) async {
  final String? jsonString = prefs.getString('animeData');

  final Map<String, dynamic> decodedData = jsonDecode(jsonString!);
  final Map<Weekday, List<Anime>> animeData = Map.fromEntries(
    decodedData.entries.map((entry) {
      return MapEntry(
        WeekdayExtension.fromString(entry.key),
        (entry.value as List<dynamic>)
            .map<Anime>((json) => Anime.fromJsonInStorage(json))
            .toList(),
      );
    }),
  );

  final weekday = WeekdayExtension.getWeekdayName(anime.episode.releaseTime);
  final index = animeData[weekday]
      ?.indexWhere((element) => element.animeId == anime.animeId);
  if (index != null && index != -1) {
    animeData[weekday]?[index] = anime;
  }

  final updatedJsonString = jsonEncode(animeData.map((key, value) {
    return MapEntry(
        key.toString(), value.map((anime) => anime.toJson()).toList());
  }));

  await prefs.setString('animeData', updatedJsonString);
}

Future<Map<Weekday, List<Anime>>?> handleAnimeStorageAvailability(
    String? animeDataString, String storageKeyAnimeData, prefs) async {
  Map<Weekday, List<Anime>>? animeData;

  if (animeDataString != null) {
    final Map<String, dynamic> jsonMap = jsonDecode(animeDataString);
    animeData = Map<Weekday, List<Anime>>.from(jsonMap.map(
      (key, value) => MapEntry(WeekdayExtension.fromString(key),
          (value as List).map((e) => Anime.fromJsonInStorage(e)).toList()),
    ));
    animeData = sortAnimeByCurrentWeekday(animeData);
    await saveAnimeDataToSharedPreferences(
        animeData, prefs, storageKeyAnimeData);
  } else {
    animeData = await fetchAndGroupAnimeByWeekday();
    animeData = sortAnimeByCurrentWeekday(animeData);
    await saveAnimeDataToSharedPreferences(
        animeData, prefs, storageKeyAnimeData);
  }

  return animeData;
}
