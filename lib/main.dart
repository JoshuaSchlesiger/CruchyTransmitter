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
      body: Padding(
        padding: const EdgeInsets.only(
            left: 20, top: 10), // Hier kannst du das Padding einstellen
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.start,
              children: [
                SizedBox(
                  width: 110,
                  height: 30,
                  child: OutlinedButton(
                    onPressed: () {
                      setState(() {
                        if (selectedIndex == 0) {
                          selectedIndex = -1;
                        } else {
                          selectedIndex = 0;
                        }
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
                        if (selectedIndex == 1) {
                          selectedIndex = -1;
                        } else {
                          selectedIndex = 1;
                        }
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
            const SizedBox(
                height: 20), // Abstand zwischen den Buttons und den Bildern
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: <Widget>[
                GestureDetector(
                  onTap: () {
                    setState(() {});
                  },
                  child: Column(
                    children: <Widget>[
                      Image.network(
                        'https://via.placeholder.com/240x360',
                        width: 160,
                        height: 175,
                      ),
                      Text('Text 1'),
                      Text('Text 2'),
                      Text('Text 3'),
                    ],
                  ),
                ),
                GestureDetector(
                  onTap: () {
                    setState(() {});
                  },
                  child: Column(
                    children: <Widget>[
                      Image.network(
                        'https://via.placeholder.com/240x360',
                        width: 160,
                        height: 175,
                      ),
                      Text('Text 1'),
                      Text('Text 2'),
                      Text('Text 3'),
                    ],
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
