import 'package:crunchy_transmitter/anime/anime.dart';
import 'package:crunchy_transmitter/config/config.dart';
import 'package:crunchy_transmitter/weekday.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart';

Future<Map<Weekday, List<Anime>>?> fetchAndGroupAnimeByWeekday() async {
  List<Anime>? animeList = await fetchData();
  if (animeList != null) {
    Map<Weekday, List<Anime>> animeMap = groupAnimeByWeekday(animeList);
    return animeMap;
  }
  return null;
}

Future<List<Anime>?> fetchData() async {
  final response = await http.get(Uri.parse("${Config.serverUrl}anime"));

  if (response.statusCode == 200) {
    List<dynamic> data = jsonDecode(response.body);
    return data.map((json) => Anime.fromJson(json)).toList();
  } else {
    return null;
  }
}

Map<Weekday, List<Anime>> groupAnimeByWeekday(List<Anime> animeList) {
  Map<Weekday, List<Anime>> animeMap = {};
  for (final anime in animeList) {

    final weekday = WeekdayExtension.getWeekdayName(anime.episode.dateOfWeekday);
    if (!animeMap.containsKey(weekday)) {
      animeMap[weekday] = [];
    }
    animeMap[weekday]!.add(anime);
  }
  return animeMap;
}

Map<Weekday, List<Anime>> sortAnimeByWeekdayAndTime(
    Map<Weekday, List<Anime>> animeData) {
  final currentWeekday = DateTime.now().weekday;

  const allWeekdays = Weekday.values;

  final rotatedWeekdays = allWeekdays
      .skip(currentWeekday - 1)
      .followedBy(allWeekdays.take(currentWeekday - 1))
      .toList();

  final sortedAnimeData = <Weekday, List<Anime>>{};

  for (final weekday in rotatedWeekdays) {
    final animeList = animeData[weekday] ?? [];
    animeList.sort((a, b) {
      if (a.episode.releaseTime == null) return 1;
      if (b.episode.releaseTime == null) return -1;
      return a.episode.releaseTime!.compareTo(b.episode.releaseTime!);
    });
    sortedAnimeData[weekday] = animeList;
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

Future<bool> updateSingleAnimeInSharedPreferences(
    Anime anime, SharedPreferences prefs) async {
  bool returnValue = false;

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

  final weekday = WeekdayExtension.getWeekdayName(anime.episode.dateOfWeekday);
  final index = animeData[weekday]
      ?.indexWhere((element) => element.animeId == anime.animeId);
  if (index != null && index != -1) {
    animeData[weekday]?[index] = anime;
    returnValue = anime.notification;
  }

  final updatedJsonString = jsonEncode(animeData.map((key, value) {
    return MapEntry(
        key.toString(), value.map((anime) => anime.toJson()).toList());
  }));

  await prefs.setString('animeData', updatedJsonString);
  return returnValue;
}
