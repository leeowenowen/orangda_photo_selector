
import 'dart:async';

import 'package:flutter/services.dart';

class OrangdaPhotoSelector {
  static const MethodChannel _channel =
      const MethodChannel('orangda_photo_selector');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
