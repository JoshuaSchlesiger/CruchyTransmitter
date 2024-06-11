import 'package:crunchy_transmitter/anime/episode.dart';

class Anime {
  final int animeId;
  final String title;
  final Episode episode;
  final String imageUrl;

  Anime({
    required this.animeId,
    required this.title,
    required this.episode,
    required this.imageUrl,
  });

  factory Anime.fromJson(Map<String, dynamic> json) {
    return Anime(
      animeId: json['animeId'],
      title: json['title'],
      episode: Episode.fromJson(json['episode']),
      imageUrl: json['imageUrl'],
    );
  }
  factory Anime.fromJsonInStorage(Map<String, dynamic> json) {
    return Anime(
      animeId: json['animeId'],
      title: json['title'],
      episode: Episode.fromJsonInStorage(json['episode']),
      imageUrl: json['imageUrl'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'animeId': animeId,
      'title': title,
      'episode': episode.toJson(),
      'imageUrl': imageUrl,
    };
  }
}
