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
}
