import 'package:intl/intl.dart';

class Episode {
  final int episodeID;
  final String episode;
  final DateTime? releaseTime;
  final DateTime dateOfWeekday;
  final DateTime? dateOfCorretionDate;

  Episode({
    required this.episodeID,
    required this.episode,
    required this.releaseTime,
    required this.dateOfWeekday,
    this.dateOfCorretionDate,
  });

  factory Episode.fromJson(Map<String, dynamic> json) {
    return Episode(
      episodeID: json['episodeID'],
      episode: json['episode'],
      releaseTime: json.containsKey('releaseTime') && json['releaseTime'] != null
          ? DateFormat('MMM d, yyyy, h:mm:ss a').parse(json['releaseTime'])
          : null,
      dateOfWeekday: DateFormat('MMM d, yyyy').parse(json['dateOfWeekday']),
      dateOfCorretionDate: json.containsKey('dateOfCorretionDate')
          ? DateFormat('MMM d, yyyy').parse(json['dateOfCorretionDate'])
          : null,
    );
  }

  factory Episode.fromJsonInStorage(Map<String, dynamic> json) {
    return Episode(
      episodeID: json['episodeID'],
      episode: json['episode'],
      releaseTime: json.containsKey('releaseTime') && json['releaseTime'] != null
          ? DateTime.parse(json['releaseTime'])
          : null,
      dateOfWeekday: DateTime.parse(json['dateOfWeekday']),
      dateOfCorretionDate: json.containsKey('dateOfCorretionDate')
          ? DateTime.parse(json['dateOfCorretionDate'])
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'episodeID': episodeID,
      'episode': episode,
      'releaseTime': releaseTime != null
          ? DateFormat('yyyy-MM-ddTHH:mm:ss').format(releaseTime!)
          : null,
      'dateOfWeekday': DateFormat('yyyy-MM-dd').format(dateOfWeekday),
      if (dateOfCorretionDate != null)
        'dateOfCorretionDate':
            DateFormat('yyyy-MM-ddTHH:mm:ss').format(dateOfCorretionDate!),
    };
  }
}
