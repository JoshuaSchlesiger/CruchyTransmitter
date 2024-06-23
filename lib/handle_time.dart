import 'package:shared_preferences/shared_preferences.dart';

Future<DateTime?> loadSavedTime(String storageKeyDateTimeUpdate, SharedPreferences prefs) async {
  int? storedTimeMillis = prefs.getInt(storageKeyDateTimeUpdate);
  if (storedTimeMillis != null) {
    DateTime storedTime = DateTime.fromMillisecondsSinceEpoch(storedTimeMillis);
    return storedTime;
  }
  return null;
}