import 'package:crunchy_transmitter/anime/episode.dart';

class Anime {
  final int animeId;
  final String title;
  final Episode episode;
  final String imageUrl;
  final String crunchyrollUrl;
  bool notification;

  Anime({
    required this.animeId,
    required this.title,
    required this.episode,
    required this.imageUrl,
    required this.notification,
    required this.crunchyrollUrl,
  });

  factory Anime.fromJson(Map<String, dynamic> json) {
    return Anime(
        animeId: json['animeId'],
        title: json['title'],
        episode: Episode.fromJson(json['episode']),
        imageUrl: json['imageUrl'],
        notification: false,
        crunchyrollUrl: json['crunchyrollUrl']);
  }
  factory Anime.fromJsonInStorage(Map<String, dynamic> json) {
    return Anime(
        animeId: json['animeId'],
        title: json['title'],
        episode: Episode.fromJsonInStorage(json['episode']),
        imageUrl: json['imageUrl'],
        notification: json['notification'],
        crunchyrollUrl: json['crunchyrollUrl']);
  }

  Map<String, dynamic> toJson() {
    return {
      'animeId': animeId,
      'title': title,
      'episode': episode.toJson(),
      'imageUrl': imageUrl,
      'notification': notification,
      'crunchyrollUrl': crunchyrollUrl,
    };
  }
}
