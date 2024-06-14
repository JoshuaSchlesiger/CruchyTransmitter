import 'dart:convert';

import 'package:crunchy_transmitter/anime/anime.dart';
import 'package:crunchy_transmitter/anime/anime_handler.dart';
import 'package:crunchy_transmitter/fcm.dart';
import 'package:crunchy_transmitter/settings_page.dart';
import 'package:crunchy_transmitter/weekday.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter/services.dart';
import 'package:url_launcher/url_launcher.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await Firebase.initializeApp();
  FirebaseMessaging.onBackgroundMessage(FCM.firebaseMessagingBackgroundHandler);

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);

    return MaterialApp(
      title: 'CrunchyTransmitter',
      theme: ThemeData(
          scaffoldBackgroundColor: const Color.fromARGB(255, 20, 20, 20)),
      home: const MyHomePage(title: 'CrunchyTransmitter'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int selectedIndex = 0;
  final String _storageKeyFilterIndex = 'filter';
  final String _storageKeyAnimeData = 'animeData';
  Map<Weekday, List<Anime>>? _animeData;

  final Future<SharedPreferences> _prefs = SharedPreferences.getInstance();
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    FCM.instanceProcess();

    _prefs.then((prefs) async {
      final int? filterIndex = prefs.getInt(_storageKeyFilterIndex);
      if (filterIndex != null) {
        selectedIndex = filterIndex;
      }

      final String? animeDataString = prefs.getString(_storageKeyAnimeData);

      if (animeDataString != null) {
        final Map<String, dynamic> jsonMap = jsonDecode(animeDataString);
        _animeData = Map<Weekday, List<Anime>>.from(jsonMap.map(
          (key, value) => MapEntry(WeekdayExtension.fromString(key),
              (value as List).map((e) => Anime.fromJsonInStorage(e)).toList()),
        ));
        _animeData = sortAnimeByCurrentWeekday(_animeData!);
      } else {
        _animeData = await fetchAndGroupAnimeByWeekday();
        _animeData = sortAnimeByCurrentWeekday(_animeData!);
        await saveAnimeDataToSharedPreferences(
            _animeData!, prefs, _storageKeyAnimeData);
      }

      setState(() {
        _isLoading = false;
      });
    });
  }

  Future<void> _updateFilterIndex(int index, String storageKeyAnimeData) async {
    final SharedPreferences prefs = await _prefs;
    prefs.setInt(storageKeyAnimeData, index);
  }

  Future<void> _updateAnime(Anime anime) async {
    final SharedPreferences prefs = await _prefs;
    await updateSingleAnimeInSharedPreferences(anime, prefs);

    final String? animeDataString = prefs.getString(_storageKeyAnimeData);
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
                MaterialPageRoute(
                    builder: (context) => SettingsPage(
                          title: widget.title,
                        )),
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
                                  selectedIndex = 0;
                                  _updateFilterIndex(0, _storageKeyFilterIndex);
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
                                'Alle',
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
                                  selectedIndex = 1;
                                  _updateFilterIndex(1, _storageKeyFilterIndex);
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
                                'Abonnierte',
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
                          buildSection(entry.key, entry.value),
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

  Widget buildSection(Weekday title, List<Anime> anime) {
    final filteredAnimeList =
        anime.where((anime) => !(selectedIndex == 1 && !anime.notification));

    if (filteredAnimeList.isEmpty) {
      return const SizedBox(height: 0);
    }

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
    final double screenWidth = MediaQuery.of(context).size.width;
    final double spacing = screenWidth * 0.04;

    return Column(
      children: List.generate((animeList.length / 2).ceil(), (index) {
        final startIndex = index * 2;
        final endIndex = startIndex + 2;
        final items = animeList.sublist(startIndex,
            endIndex < animeList.length ? endIndex : animeList.length);

        return Padding(
          padding: EdgeInsets.only(bottom: spacing),
          child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: items.map((anime) {
                return Expanded(
                  child: Padding(
                    padding: EdgeInsets.symmetric(horizontal: spacing / 2),
                    child: buildGridItem(anime),
                  ),
                );
              }).toList()),
        );
      }),
    );
  }

  Widget buildGridItem(Anime anime) {
    final int releaseHour = anime.episode.releaseTime.hour;
    final String releaseMinute =
        anime.episode.releaseTime.minute.toString().padLeft(2, '0');

    final int? correctionDate = anime.episode.correctionDate?.day;

    final double imageHeight = MediaQuery.of(context).size.height * 0.32;

    return GestureDetector(
        onTap: () {
          anime.notification = !anime.notification;
          _updateAnime(anime);
        },
        onLongPressStart: (LongPressStartDetails details) {
          _showCustomMenu(details.globalPosition, anime.crunchyrollUrl);
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
                    height: imageHeight,
                    fit: BoxFit.cover,
                  ),
                ),
                Column(
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
              ],
            ),
          ),
        ));
  }

  void _showCustomMenu(Offset position, String url) {
    showMenu(
      context: context,
      position: RelativeRect.fromLTRB(
        position.dx,
        position.dy,
        position.dx,
        position.dy,
      ),
      color: const Color.fromARGB(121, 0, 0, 0),
      items: <PopupMenuEntry>[
        PopupMenuItem(
          child: Center(
            child: Container(
              decoration: BoxDecoration(
                border: Border.all(
                  color: const Color.fromARGB(0, 33, 149, 243),
                  width: 2.0,
                ),
                borderRadius: BorderRadius.circular(10.0),
              ),
              child: ElevatedButton(
                onPressed: () async {
                  await launchUrl(Uri.parse(url));
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color.fromARGB(255, 255, 255, 255),
                  textStyle: const TextStyle(
                    fontSize: 16.0,
                    fontWeight: FontWeight.bold,
                  ),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10.0),
                  ),
                ),
                child: const Text(
                  'Anschauen',
                  style: TextStyle(
                    color: Colors.black,
                    fontSize: 16.0,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }
}
