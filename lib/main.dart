import 'dart:convert';

import 'package:crunchy_transmitter/anime/anime.dart';
import 'package:crunchy_transmitter/anime/anime_handler.dart';
import 'package:crunchy_transmitter/settings_page.dart';
import 'package:crunchy_transmitter/weekday.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

Future<void> main() async {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
          scaffoldBackgroundColor: const Color.fromARGB(255, 20, 20, 20)),
      home: const MyHomePage(title: 'Crunchy Transmitter'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int selectedIndex = -1;
  final String _storageKeyAnimeData = 'animeData';
  Map<Weekday, List<Anime>>? _animeData;
  final Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();

    _prefs.then((prefs) async {
      final String? animeDataString = prefs.getString(_storageKeyAnimeData);

      if (animeDataString != null) {
        final Map<String, dynamic> jsonMap = jsonDecode(animeDataString);
        _animeData = Map<Weekday, List<Anime>>.from(jsonMap.map(
          (key, value) => MapEntry(WeekdayExtension.fromString(key),
              (value as List).map((e) => Anime.fromJsonInStorage(e)).toList()),
        ));
      } else {
        _animeData = await fetchAndGroupAnimeByWeekday();
        await saveAnimeDataToSharedPreferences(_animeData!, prefs);
      }

      setState(() {
        _isLoading = false;
      });
    });
  }

  Future<void> _updateAnime(Anime anime) async {
    final SharedPreferences prefs = await _prefs;
    print(anime);
    await updateSingleAnimeInSharedPreferences(anime, prefs);

    final String? animeDataString = prefs.getString(_storageKeyAnimeData);
    print(animeDataString);
    if (animeDataString != null) {
      final Map<String, dynamic> jsonMap = jsonDecode(animeDataString);
      _animeData = Map<Weekday, List<Anime>>.from(jsonMap.map(
        (key, value) => MapEntry(WeekdayExtension.fromString(key),
            (value as List).map((e) => Anime.fromJsonInStorage(e)).toList()),
      ));
    }

    setState(() {
      _animeData = _animeData;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color.fromARGB(255, 33, 33, 33),
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              widget.title,
              style: const TextStyle(
                color: Color.fromARGB(255, 244, 117, 33),
                fontWeight: FontWeight.bold,
              ),
            ),
            Padding(
              padding:
                  const EdgeInsets.only(left: 16.0), // Versetzt nach rechts
              child: Text(
                "%Frühlingsseason ${DateTime.now().year}",
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 12.0,
                ),
              ),
            ),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(
              Icons.settings,
              color: Color.fromARGB(155, 255, 255, 255),
            ),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const SettingsPage()),
              );
            },
          ),
        ],
      ),
      body: _isLoading
          ? const Center(
              child: CircularProgressIndicator(),
            )
          : _animeData != null
              ? ListView(
                  children: [
                    Padding(
                      padding: const EdgeInsets.only(left: 20, top: 10),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          SizedBox(
                            width: 110,
                            height: 30,
                            child: OutlinedButton(
                              onPressed: () {
                                setState(() {
                                  selectedIndex = selectedIndex == 0 ? -1 : 0;
                                });
                              },
                              style: OutlinedButton.styleFrom(
                                side: BorderSide(
                                  color: selectedIndex == 0
                                      ? Colors.green
                                      : const Color.fromRGBO(97, 97, 97, 1),
                                ),
                              ),
                              child: Text(
                                'Deaktivierte',
                                style: TextStyle(
                                  color: selectedIndex == 0
                                      ? Colors.white
                                      : const Color.fromARGB(
                                          255, 168, 168, 168),
                                  fontSize: 10,
                                ),
                              ),
                            ),
                          ),
                          const SizedBox(width: 16),
                          SizedBox(
                            width: 110,
                            height: 30,
                            child: OutlinedButton(
                              onPressed: () {
                                setState(() {
                                  selectedIndex = selectedIndex == 1 ? -1 : 1;
                                });
                              },
                              style: OutlinedButton.styleFrom(
                                side: BorderSide(
                                  color: selectedIndex == 1
                                      ? Colors.green
                                      : const Color.fromRGBO(97, 97, 97, 1),
                                ),
                              ),
                              child: Text(
                                'Aktivierte',
                                style: TextStyle(
                                  color: selectedIndex == 1
                                      ? Colors.white
                                      : const Color.fromARGB(
                                          255, 168, 168, 168),
                                  fontSize: 10,
                                ),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                    ..._animeData!.entries.map((entry) {
                      return Column(
                        children: [
                          buildSection(entry.key),
                          buildGrid(entry.value),
                        ],
                      );
                    }),
                  ],
                )
              : const Center(
                  child: Text(
                    'No data found',
                    style: TextStyle(color: Color.fromARGB(255, 255, 255, 255)),
                  ),
                ),
    );
  }

  Widget buildSection(Weekday title) {
    return Padding(
      padding: const EdgeInsets.only(top: 15, bottom: 15),
      child: Align(
        alignment: Alignment.center,
        child: Text(
          title.toGerman(),
          style: const TextStyle(
            fontSize: 26,
            fontWeight: FontWeight.bold,
            color: Color.fromARGB(255, 255, 255, 255),
          ),
        ),
      ),
    );
  }

  Widget buildGrid(List<Anime> animeList) {
    return GridView.count(
      shrinkWrap: true,
      childAspectRatio: 0.6,
      primary: false,
      padding: const EdgeInsets.only(left: 0, right: 0),
      crossAxisSpacing: 0,
      mainAxisSpacing: 0,
      crossAxisCount: 2,
      children: List.generate(
          animeList.length, (index) => buildGridItem(animeList[index])),
    );
  }

  Widget buildGridItem(Anime anime) {
    final int releaseHour = anime.episode.releaseTime.hour;
    final String releaseMinute =
        anime.episode.releaseTime.minute.toString().padLeft(2, '0');

    final int? correctionDate = anime.episode.correctionDate?.day;

    return GestureDetector(
        onTap: () {
          anime.notification = !anime.notification;
          _updateAnime(anime);
        },
        child: Center(
          child: SizedBox(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                ColorFiltered(
                  colorFilter: anime.notification
                      ? const ColorFilter.mode(
                          Colors.transparent,
                          BlendMode.saturation,
                        )
                      : const ColorFilter.mode(
                          Colors.grey,
                          BlendMode.saturation,
                        ),
                  child: Image.network(
                    anime.imageUrl,
                    width: 120,
                    height: 180,
                  ),
                ),
                Container(
                  constraints: const BoxConstraints(
                    maxWidth: 120,
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Text(
                        anime.title,
                        style: const TextStyle(
                          color: Color.fromARGB(255, 244, 117, 33),
                        ),
                        maxLines: 4,
                        overflow: TextOverflow.ellipsis,
                      ),
                      if (anime.episode.correctionDate == null)
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              '$releaseHour:$releaseMinute Uhr',
                              style: const TextStyle(
                                color: Colors.white,
                              ),
                            ),
                            Text(
                              anime.episode.episode,
                              style: const TextStyle(
                                color: Colors.white,
                              ),
                            ),
                          ],
                        )
                      else
                        Text(
                          '$correctionDate',
                          style: const TextStyle(
                            color: Colors.white,
                          ),
                        ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ));
  }
}
