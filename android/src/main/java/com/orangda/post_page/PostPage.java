package com.orangda.post_page;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.platform.PlatformView;

public class PostPage implements PlatformView {
    private LinearLayout mLinearLayout;
    private FlutterPlugin.FlutterPluginBinding binding;

    PostPage(Context context, FlutterPlugin.FlutterPluginBinding binding, int id, Map<String, Object> params) {
        this.binding = binding;
        LinearLayout mLinearLayout = new LinearLayout(context);
        mLinearLayout.setBackgroundColor(Color.rgb(100, 200, 100));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(900, 900);
        mLinearLayout.setLayoutParams(lp);
        Button btn = new Button(context);
        btn.setText("select photo");
        mLinearLayout.addView(btn);
        btn.setOnClickListener(onClickListener);
        this.mLinearLayout = mLinearLayout;
    }

    @Override
    public View getView() {
        return mLinearLayout;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofAll())
                    .loadImageEngine(GlideEngine.createGlideEngine())
                    .forResult(new OnResultCallbackListener<LocalMedia>() {
                        @Override
                        public void onResult(List<LocalMedia> result) {
                            // onResult Callback
                        }

                        @Override
                        public void onCancel() {
                            // onCancel Callback
                        }
                    });
        }
    };

    @Override
    public void dispose() {
    }
}