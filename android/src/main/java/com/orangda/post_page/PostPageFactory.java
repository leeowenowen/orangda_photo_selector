package com.orangda.post_page;

import android.content.Context;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class PostPageFactory extends PlatformViewFactory {
    private  FlutterPlugin.FlutterPluginBinding mFlutterPluginBinding;
    private ActivityPluginBinding mActivityPluginBindingp;

    public PostPageFactory() {
        super(StandardMessageCodec.INSTANCE);
    }

    public void setFlutterPluginBinding(FlutterPlugin.FlutterPluginBinding flutterPluginBinding){
        mFlutterPluginBinding = flutterPluginBinding;
    }

    public void setActivityPluginBinding(ActivityPluginBinding activityPluginBinding){
        mActivityPluginBindingp = activityPluginBinding;
    }

    @Override
    public PlatformView create(Context context, int i, Object o) {
        Map<String, Object> params = null;
        if(o != null) {
         params =(Map<String, Object>) o;
        }
        return new PostPage(context, this.mFlutterPluginBinding, this.mActivityPluginBindingp, i, params);
    }
}
