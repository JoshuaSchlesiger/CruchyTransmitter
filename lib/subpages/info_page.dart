import 'package:crunchy_transmitter/config/config.dart';
import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

class InfoPage extends StatelessWidget {
  final String title;
  const InfoPage({required this.title, super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color.fromARGB(255, 33, 33, 33),
        leading: Container(
          padding: const EdgeInsets.only(left: 8, top: 8, bottom: 8),
          child: Image.asset('assets/ic_launcher.png'),
        ),
        automaticallyImplyLeading: false,
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: const TextStyle(
                color: Color.fromARGB(255, 244, 117, 33),
                fontWeight: FontWeight.bold,
              ),
            ),
            const Padding(
              padding: EdgeInsets.only(left: 12.0),
              child: Text(
                'Informationen',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 12,
                ),
              ),
            ),
          ],
        ),
        actions: <Widget>[
          IconButton(
            icon: const Icon(Icons.close, color: Colors.white),
            onPressed: () {
              Navigator.pop(context);
            },
          ),
        ],
      ),
      body: Stack(
        children: <Widget>[
          const Positioned(
            top: 10,
            right: 10,
            child: Text(
              "Version: ${Config.version}",
              style: TextStyle(
                color: Colors.white,
                fontSize: 14,
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 30.0),
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  const Text(
                    'Ich bin total begeistert, dass du den CrunchyTransmitter installiert hast! Es ist großartig, dass du genauso für Anime brennst wie ich! 🌟\n\n'
                    'Das Projekt ist Open Source auf GitHub. Schau es dir an – ich bin gespannt auf deine Meinung! 🚀 \n\n'
                    'Unterstütze mich doch gerne, damit dieses Projekt so lange am leben bleibt wie möglich.',
                    style: TextStyle(
                      fontSize: 20,
                      color: Colors.white,
                    ),
                  ),
                  const SizedBox(height: 20),
                  GestureDetector(
                    onTap: _launchURL,
                    child: Image.asset(
                      'assets/logo-PayPal.png',
                      // width: 100,
                      // height: 100,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _launchURL() async {
    await launchUrl(Uri.parse(Config.paypal));
  }
}
