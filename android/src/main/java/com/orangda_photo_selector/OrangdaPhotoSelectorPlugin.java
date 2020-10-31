package com.orangda_photo_selector;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.orangda.constants.Constants;
import com.orangda.post_page.PostPageFactory;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** OrangdaPhotoSelectorPlugin */
public class OrangdaPhotoSelectorPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel mChannel;
  private PostPageFactory mPostPageFactory = new PostPageFactory();

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    mChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "orangda_photo_selector");
    mChannel.setMethodCallHandler(this);

    mPostPageFactory.setFlutterPluginBinding(flutterPluginBinding);
    flutterPluginBinding.getPlatformViewRegistry().registerViewFactory(Constants.PLUGIN_KEY_VIEW_POST_PAGE, mPostPageFactory);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding mFlutterPluginBinding) {
    mChannel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
    mPostPageFactory.setActivityPluginBinding(activityPluginBinding);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding mFlutterPluginBinding) {

  }

  @Override
  public void onDetachedFromActivity() {
    mPostPageFactory.setActivityPluginBinding(null);
  }
}
