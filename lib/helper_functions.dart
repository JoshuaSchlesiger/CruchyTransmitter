import 'package:shared_preferences/shared_preferences.dart';

/// Auxiliary function for loading the check time from the storage. The time is required to perform a daily "get" on the server
Future<DateTime?> loadSavedTime(String storageKeyDateTimeUpdate, SharedPreferences prefs) async {
  int? storedTimeMillis = prefs.getInt(storageKeyDateTimeUpdate);
  if (storedTimeMillis != null) {
    DateTime storedTime = DateTime.fromMillisecondsSinceEpoch(storedTimeMillis);
    return storedTime;
  }
  return null;
}