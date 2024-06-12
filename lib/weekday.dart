enum Weekday {
  monday,
  tuesday,
  wednesday,
  thursday,
  friday,
  saturday,
  sunday,
}

extension WeekdayExtension on Weekday {
  static Weekday fromGerman(String german) {
    switch (german.toLowerCase()) {
      case 'Montag':
        return Weekday.monday;
      case 'Dienstag':
        return Weekday.tuesday;
      case 'Mittwoch':
        return Weekday.wednesday;
      case 'Donnerstag':
        return Weekday.thursday;
      case 'Freitag':
        return Weekday.friday;
      case 'Samstag':
        return Weekday.saturday;
      case 'Sonntag':
        return Weekday.sunday;
      default:
        throw ArgumentError('Invalid German weekday: $german');
    }
  }

  static Weekday fromString(String string) {
    switch (string.toLowerCase()) {
      case 'weekday.monday':
        return Weekday.monday;
      case 'weekday.tuesday':
        return Weekday.tuesday;
      case 'weekday.wednesday':
        return Weekday.wednesday;
      case 'weekday.thursday':
        return Weekday.thursday;
      case 'weekday.friday':
        return Weekday.friday;
      case 'weekday.saturday':
        return Weekday.saturday;
      case 'weekday.sunday':
        return Weekday.sunday;
      default:
        throw ArgumentError('Invalid German weekday: $string');
    }
  }

  static Weekday getWeekdayName(DateTime dateTime) {
    switch (dateTime.weekday) {
      case DateTime.monday:
        return Weekday.monday;
      case DateTime.tuesday:
        return Weekday.tuesday;
      case DateTime.wednesday:
        return Weekday.wednesday;
      case DateTime.thursday:
        return Weekday.thursday;
      case DateTime.friday:
        return Weekday.friday;
      case DateTime.saturday:
        return Weekday.saturday;
      case DateTime.sunday:
        return Weekday.sunday;
      default:
        throw Exception("Invalid weekday");
    }
  }

  String toGerman() {
    switch (this) {
      case Weekday.monday:
        return 'Montag';
      case Weekday.tuesday:
        return 'Dienstag';
      case Weekday.wednesday:
        return 'Mittwoch';
      case Weekday.thursday:
        return 'Donnerstag';
      case Weekday.friday:
        return 'Freitag';
      case Weekday.saturday:
        return 'Samstag';
      case Weekday.sunday:
        return 'Sonntag';
      default:
        throw ArgumentError('Invalid weekday: $this');
    }
  }

  static List<Weekday> sortedByCurrentFirst() {
    final currentWeekday = WeekdayExtension.getWeekdayName(DateTime.now());
    final weekdays = Weekday.values.toList();
    final result = <Weekday>[];

    for (var i = currentWeekday.index; i < weekdays.length; i++) {
      result.add(weekdays[i]);
    }

    for (var i = 0; i < currentWeekday.index; i++) {
      result.add(weekdays[i]);
    }

    return result;
  }
}
