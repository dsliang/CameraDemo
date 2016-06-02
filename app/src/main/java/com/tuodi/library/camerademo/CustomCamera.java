package com.tuodi.library.camerademo;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dsliang on 2016/5/27.
 */
public class CustomCamera extends Fragment implements SelectDialogOnClink {

    private static final String TAG = CustomCamera.class.getSimpleName();

    //闪光灯
    private static final int FLASH_LIGHT = 0x1;
    //对焦模式
    private static final int FOCUS_MODE = 0x2;
    //预览分辨率
    private static final int PREVIEW_SIZE = 0x3;
    //图片辨率
    private static final int PICTURE_SIZE = 0x4;

    @Bind(R.id.surfaceCameraPreviewView)
    SurfaceView surfaceCameraPreviewView;
    SurfaceHolder holder;
    @Bind(R.id.btnShutter)
    ImageButton btnShutter;
    Camera mCamera;
    boolean mFlashStatus;
    Map<Integer, List<String>> mFunctionSelectionMap = new HashMap<>();
    Map<Integer, String> mCurrentMode = new HashMap<>();

    Map<Integer, List<Camera.Size>> mSize = new HashMap<>();
    Map<Integer, Camera.Size> mCurrentSize = new HashMap<>();
    @Bind(R.id.switchAutoFoucs)
    Switch switchAutoFoucs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coustom_camera, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    Handler mHandler = new Handler();

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        holder = surfaceCameraPreviewView.getHolder();
        //在api 11(包含api11,无需再调用setType函数)
        //注意:setTpye函数在surfaceCreated函数里面调用无效.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new PreviewViewHolderCallback());

        switchAutoFoucs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {


                if (true == isChecked) {
                    Log.d(TAG, "call autoFocus function");
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            buttonView.setChecked(false);
                            //不断循环对焦
//                            mHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    buttonView.setChecked(true);
//                                }
//                            }, 1500);
                            if (true == success)
                                Log.d(TAG, "Autofocus success");
                            else
                                Log.d(TAG, "Autofocus fails");
                        }

                    });
                } else {
                    Log.d(TAG, "call cancelAutoFocus function");
                    mCamera.cancelAutoFocus();
                }
            }
        });
    }

    private void getCameraInitData() {
        initDefaultSetting();
    }

    private void initDefaultSetting() {
        Camera.Parameters parameters;
        String flashMode;
        String focusMode;
        Camera.Size previewSize;
        Camera.Size pictureSize;
        List<String> list;
        List<Camera.Size> sizeList;

        if (null != mCamera) {

            parameters = mCamera.getParameters();

            //闪光灯
            flashMode = parameters.getFlashMode();
            if (!TextUtils.isEmpty(flashMode)) {
                mCurrentMode.put(FLASH_LIGHT, flashMode);
            }
            list = parameters.getSupportedFlashModes();
            if (null != list) {
                mFunctionSelectionMap.put(FLASH_LIGHT, list);
            }

            //对焦模式
            focusMode = parameters.getFocusMode();
            if (!TextUtils.isEmpty(focusMode)) {
                mCurrentMode.put(FOCUS_MODE, focusMode);
            }
            list = parameters.getSupportedFocusModes();
            if (null != list) {
                mFunctionSelectionMap.put(FOCUS_MODE, list);
            }

            //预览分辨率
            previewSize = parameters.getPreviewSize();
            mCurrentSize.put(PREVIEW_SIZE, previewSize);
            sizeList = parameters.getSupportedPreviewSizes();
            mSize.put(PREVIEW_SIZE, sizeList);
            list = new ArrayList<String>();
            for (Camera.Size item : sizeList) {
                list.add(String.valueOf(item.hashCode()));
            }
            mFunctionSelectionMap.put(PREVIEW_SIZE, list);

            //预览分辨率
            pictureSize = parameters.getPictureSize();
            mCurrentSize.put(PICTURE_SIZE, previewSize);
            sizeList = parameters.getSupportedPictureSizes();
            mSize.put(PICTURE_SIZE, sizeList);
            list = new ArrayList<String>();
            for (Camera.Size item : sizeList) {
                list.add(String.valueOf(item.hashCode()));
            }
            mFunctionSelectionMap.put(PICTURE_SIZE, list);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.btnShutter, R.id.btnFlashLight, R.id.btnFocus, R.id.btnPreviewSize, R.id.btnPictureSize})
    public void onClick(View view) {
        Camera.Parameters parameters;

        switch (view.getId()) {
            case R.id.btnShutter:
                if (null != mCamera) {
                    //通过回调函数保存拍到的照片
                    mCamera.takePicture(null, null, new CapturePictureCallback(null, null));
                }
                break;
            case R.id.btnFlashLight:
                if (null != mCamera) {
                    parameters = mCamera.getParameters();

                    SelectDialog
                            .newInstance(FLASH_LIGHT, mFunctionSelectionMap.get(FLASH_LIGHT), mCurrentMode.get(FLASH_LIGHT))
                            .show(getFragmentManager(), "flash light");
                }
                break;

            case R.id.btnFocus:
                if (null != mCamera) {
                    parameters = mCamera.getParameters();

                    SelectDialog
                            .newInstance(FOCUS_MODE, mFunctionSelectionMap.get(FOCUS_MODE), mCurrentMode.get(FOCUS_MODE))
                            .show(getFragmentManager(), "focus mode");
                }
                break;
            case R.id.btnPreviewSize:
                if (null != mCamera) {
                    SpecialSelectDialog
                            .newInstance(PREVIEW_SIZE, mSize.get(PREVIEW_SIZE), mCurrentSize.get(PREVIEW_SIZE))
                            .show(getFragmentManager(), "preview size");
                }
                break;
            case R.id.btnPictureSize:
                if (null != mCamera) {
                    SpecialSelectDialog
                            .newInstance(PICTURE_SIZE, mSize.get(PICTURE_SIZE), mCurrentSize.get(PICTURE_SIZE))
                            .show(getFragmentManager(), "picture size");
                }
                break;

        }
    }

    @Override
    public void onClink(int function, int postion) {
        boolean setParameters;
        List<String> list;
        String expectedMode;
        Camera.Parameters parameters;
        Camera.Size expectedSize;

        list = mFunctionSelectionMap.get(function);
        expectedMode = list.get(postion);

        parameters = mCamera.getParameters();
        Log.d(TAG, "before parameters:" + parameters.flatten());

        setParameters = true;
        switch (function) {
            case FLASH_LIGHT:
                CameraConfigurationHelper.setFlashMode(parameters, expectedMode);
                mCurrentMode.put(FLASH_LIGHT, expectedMode);
                break;
            case FOCUS_MODE:
                CameraConfigurationHelper.setFocusMode(parameters, expectedMode);
                mCurrentMode.put(FOCUS_MODE, expectedMode);
                break;
            case PREVIEW_SIZE:
                expectedSize = mSize.get(PREVIEW_SIZE).get(postion);
                CameraConfigurationHelper.setPreviewSize(parameters, expectedSize);
                mCurrentSize.put(PREVIEW_SIZE, expectedSize);
                break;
            case PICTURE_SIZE:
                expectedSize = mSize.get(PICTURE_SIZE).get(postion);
                CameraConfigurationHelper.setPictureSize(parameters, expectedSize);
                mCurrentSize.put(PICTURE_SIZE, expectedSize);
                break;
            default:
                setParameters = false;
                Log.d(TAG, "after parameters:" + mCamera.getParameters().flatten());
        }

        if (true == setParameters) {
            //不一定全部参数都需要停止预览
            mCamera.stopPreview();
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }
    }

    class PreviewViewHolderCallback implements SurfaceHolder.Callback {


        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            Camera.Parameters parameters;

            //不指定那个摄像头,默认是打开后置摄像头
            mCamera = Camera.open();
            if (null == mCamera)
                return;

            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            getCameraInitData();
            //预览旋转90度
            mCamera.setDisplayOrientation(90);

            parameters = mCamera.getParameters();

            //图片旋转90度
            parameters.setRotation(90);
            mCamera.setParameters(parameters);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mCamera.enableShutterSound(true);
            }

            mCamera.startPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            if (null != mCamera) {
                mCamera.release();
                mCamera = null;
            }
        }
    }

}
