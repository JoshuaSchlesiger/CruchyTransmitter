import 'package:crunchy_transmitter/main.dart';
import 'package:flutter/material.dart';

class NoInternetApp extends StatelessWidget {
  final String title;
  final String text;
  const NoInternetApp({super.key, required this.title, required this.text});

  Widget _buildReloadButton(BuildContext context) {
    return ElevatedButton(
      onPressed: () {
        main();
      },
      child: const Text('Neu laden'),
    );
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
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
            ],
          ),
        ),
        backgroundColor: const Color.fromARGB(255, 20, 20, 20),
        body: Center(
          child: Padding(
            padding: const EdgeInsets.all(20.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  text,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 20.0,
                  ),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 20),
                _buildReloadButton(context),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
