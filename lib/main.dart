import 'package:crunchy_transmitter/settings_page.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
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
  int _counter = 0;
  String _storageKeyCounter = '';
  final Future<SharedPreferences> _prefs = SharedPreferences.getInstance();

  Future<void> _incrementCounter() async {
    setState(() {
      _counter++;
    });

    (await _prefs).setInt(_storageKeyCounter, _counter);
  }

  @override
  void initState() {
    _storageKeyCounter = "counter";

    super.initState();
    _prefs.then((prefs) {
      setState(() {
        _counter = prefs.getInt(_storageKeyCounter) ?? 0;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
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
      body: ListView(
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
                            : const Color.fromARGB(255, 168, 168, 168),
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
                            : const Color.fromARGB(255, 168, 168, 168),
                        fontSize: 10,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
          buildSection('Montag'),
          buildGrid(),
          buildSection('Dienstag'),
          buildGrid(),
        ],
      ),
    );
  }

  Widget buildSection(String title) {
    return Padding(
      padding: const EdgeInsets.only(top: 15, bottom: 15),
      child: Align(
        alignment: Alignment.center,
        child: Text(
          title,
          style: const TextStyle(
            fontSize: 26,
            fontWeight: FontWeight.bold,
            color: Color.fromARGB(255, 255, 255, 255),
          ),
        ),
      ),
    );
  }

  Widget buildGrid() {
    return GridView.count(
      shrinkWrap: true,
      childAspectRatio: 0.46,
      primary: false,
      padding: const EdgeInsets.only(left: 20, right: 20),
      crossAxisSpacing: 10,
      mainAxisSpacing: 10,
      crossAxisCount: 2,
      children: List.generate(3, (index) => buildGridItem()),
    );
  }

  Widget buildGridItem() {
    return GestureDetector(
      onTap: () {
        // Hier passiert etwas, wenn der Container angeklickt wird
      },
      child: Center(
        child: Container(
          padding: const EdgeInsets.all(8),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              Image.network(
                'https://via.placeholder.com/240x360',
                width: 120,
                height: 180,
              ),
              Container(
                constraints: const BoxConstraints(
                  maxWidth: 120,
                ),
                child: const Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    Text(
                      "I'll Use My Appraisal Skill to Rise in the World I'll Use My Appraisal Skill to Rise in the World ",
                      style: TextStyle(
                        color: Color.fromARGB(255, 244, 117, 33),
                      ),
                      maxLines: 4,
                      overflow: TextOverflow.ellipsis,
                    ),
                    Text(
                      'Folge 1109 ',
                      style: TextStyle(
                        color: Colors.white,
                      ),
                    ),
                    Text(
                      '16:00 Uhr',
                      style: TextStyle(
                        color: Colors.white,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}