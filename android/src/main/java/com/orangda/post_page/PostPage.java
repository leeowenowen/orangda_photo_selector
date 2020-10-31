package com.orangda.post_page;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.orangda.camera.Camera2Loader;
import com.orangda.camera.CameraLoader;
import com.orangda.utils.FileUtils;
import com.orangda.utils.GPUImageFilterTools;

import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.platform.PlatformView;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter;
import jp.co.cyberagent.android.gpuimage.util.Rotation;

public class PostPage implements PlatformView {
    private static final String TAG = "PostPage";
    private FlutterPlugin.FlutterPluginBinding mFlutterPluginBinding;
    private ActivityPluginBinding mActivityPluginBindingp;
    private GPUImageView mGPUImageView;

    private GPUImageFilter mNoImageFilter = new GPUImageFilter();
    private GPUImageFilter mCurrentImageFilter = mNoImageFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;

    private CameraLoader mCameraLoader;


    PostPage(Context context, FlutterPlugin.FlutterPluginBinding flutterPluginBinding, ActivityPluginBinding activityPluginBindingp, int id, Map<String, Object> params) {
        this.mFlutterPluginBinding = flutterPluginBinding;
        this.mActivityPluginBindingp = activityPluginBindingp;
        mActivityPluginBindingp.addRequestPermissionsResultListener(mRequestPermissionsResultListener);

        initCamera(context);
    }
    private void initCamera(Context context) {
        mCameraLoader = new Camera2Loader(mActivityPluginBindingp.getActivity());
        mCameraLoader.setOnPreviewFrameListener(new CameraLoader.OnPreviewFrameListener() {
            @Override
            public void onPreviewFrame(byte[] data, int width, int height) {
                mGPUImageView.updatePreviewFrame(data, width, height);
            }
        });

        mGPUImageView = new GPUImageView(context);
        mGPUImageView.setRatio(0.75f); // 固定使用 4:3 的尺寸
        updateGPUImageRotate();
        mGPUImageView.setRenderMode(GPUImageView.RENDERMODE_CONTINUOUSLY);
//        mGPUImageView.setFilter(new GPUImageWhiteBalanceFilter(
//                5000.0f,
//                0.0f
//        ));
        mGPUImageView.setFilter(new GPUImageGlassSphereFilter());
    }

    private void updateGPUImageRotate() {
        Rotation rotation = getRotation(mCameraLoader.getCameraOrientation());
        boolean flipHorizontal = false;
        boolean flipVertical = false;
        if (mCameraLoader.isFrontCamera()) { // 前置摄像头需要镜像
            if (rotation == Rotation.NORMAL || rotation == Rotation.ROTATION_180) {
                flipHorizontal = true;
            } else {
                flipVertical = true;
            }
        }
        mGPUImageView.getGPUImage().setRotation(rotation, flipHorizontal, flipVertical);
    }

    private Rotation getRotation(int orientation) {
        switch (orientation) {
            case 90:
                return Rotation.ROTATION_90;
            case 180:
                return Rotation.ROTATION_180;
            case 270:
                return Rotation.ROTATION_270;
            default:
                return Rotation.NORMAL;
        }
    }

    @Override
    public View getView() {
        return mGPUImageView;
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PictureSelector.create(mActivityPluginBindingp.getActivity())
                    .openGallery(PictureMimeType.ofAll())
                    .imageEngine(GlideEngine.createGlideEngine())
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

    @Override
    public void onFlutterViewAttached(@NonNull View flutterView) {
        Log.d(TAG, "onFlutterViewAttached:gpuimage width:" + mGPUImageView.getWidth() + " height:" + mGPUImageView.getHeight());
        Log.d(TAG, "onFlutterViewAttached:flutterView width:" + flutterView.getWidth() + " height:" + flutterView.getHeight());
        String permission = "android.permission.CAMERA";
        int requestCode = 8888;
        String[] permissions = {permission};

        if (ContextCompat.checkSelfPermission(
                mActivityPluginBindingp.getActivity(),
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            mActivityPluginBindingp.getActivity(), permissions,
                            requestCode);
        } else {
            onResume();
        }
    }

    void onResume() {
        if (ViewCompat.isLaidOut(mGPUImageView) && !mGPUImageView.isLayoutRequested()) {
            mCameraLoader.onResume(mGPUImageView.getWidth(), mGPUImageView.getHeight());
        } else {
            mGPUImageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                           int oldRight, int oldBottom) {
                    mGPUImageView.removeOnLayoutChangeListener(this);
                    mCameraLoader.onResume(mGPUImageView.getWidth(), mGPUImageView.getHeight());
                }
            });
        }
    }


    private PluginRegistry.RequestPermissionsResultListener mRequestPermissionsResultListener = new PluginRegistry.RequestPermissionsResultListener() {
        @Override
        public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            switch (requestCode) {
                case 200:
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        onResume();
                    }
                    break;
            }
            return false;
        }
    };

    @Override
    public void onFlutterViewDetached() {
        Log.d(TAG, "onFlutterViewDetached");
        mCameraLoader.onPause();
    }

    void switchCamera() {
        mGPUImageView.getGPUImage().deleteImage();
        mCameraLoader.switchCamera();
        updateGPUImageRotate();
    }

    private void saveSnapshot() {
        String fileName = System.currentTimeMillis() + ".jpg";
        mGPUImageView.saveToPictures("GPUImage", fileName, mOnPictureSavedListener);
    }

    private GPUImageView.OnPictureSavedListener mOnPictureSavedListener = new GPUImageView.OnPictureSavedListener() {
        @Override
        public void onPictureSaved(Uri uri) {
            String filePath = FileUtils.getRealFilePath(mActivityPluginBindingp.getActivity(), uri);
            Log.d(TAG, "save to " + filePath);
            Toast.makeText(mActivityPluginBindingp.getActivity(), "Saved: " + filePath, Toast.LENGTH_SHORT).show();
        }
    };

    private void switchFilterTo(GPUImageFilter filter) {
        if (mCurrentImageFilter == null
                || (filter != null && !mCurrentImageFilter.getClass().equals(filter.getClass()))) {
            mCurrentImageFilter = filter;
            mGPUImageView.setFilter(mCurrentImageFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mCurrentImageFilter);
//            mSeekBar.setVisibility(mFilterAdjuster.canAdjust() ? View.VISIBLE : View.GONE);
        } else {
//            mSeekBar.setVisibility(View.GONE);
        }
    }

}