package com.tuodi.library.camerademo;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dsliang on 2016/5/27.
 */
public class CustomCamera extends Fragment implements SelectDialog.DialogOnClink {

    private static final String TAG = CustomCamera.class.getSimpleName();

    //闪光灯
    private static final int FLASH_LIGHT = 0x1;

    @Bind(R.id.surfaceCameraPreviewView)
    SurfaceView surfaceCameraPreviewView;
    SurfaceHolder holder;
    @Bind(R.id.btnShutter)
    ImageButton btnShutter;
    Camera mCamera;
    @Bind(R.id.spinnerPictureSize)
    Spinner spinnerPictureSize;
    boolean mFlashStatus;
    Map<Integer, List<String>> mFunctionSelectionMap = new HashMap<>();
    Map<Integer, String> mCurrentMode = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coustom_camera, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        holder = surfaceCameraPreviewView.getHolder();
        //在api 11(包含api11,无需再调用setType函数)
        //注意:setTpye函数在surfaceCreated函数里面调用无效.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new PreviewViewHolderCallback());

//        spinnerPictureSize.setAdapter(new SimpleAdapter(getActivity(),null,android.R.layout.simple_list_item_1));

    }

    private void getCameraInitData() {
        initDefaultSetting();
    }

    private void initDefaultSetting() {
        Camera.Parameters parameters;
        String flashMode;
        List<String> list;

        if (null != mCamera) {
            parameters = mCamera.getParameters();

            flashMode = parameters.getFlashMode();
            if (!TextUtils.isEmpty(flashMode)) {
                mCurrentMode.put(FLASH_LIGHT, flashMode);
            }

            list = parameters.getSupportedFlashModes();
            if (null != list) {
                mFunctionSelectionMap.put(FLASH_LIGHT, list);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.btnShutter, R.id.btnFlashLight})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnFlashLight:
                if (null != mCamera) {
                    Camera.Parameters parameters;
                    SelectDialog dialog;

                    parameters = mCamera.getParameters();
                    dialog = SelectDialog.newInstance(FLASH_LIGHT, mFunctionSelectionMap.get(FLASH_LIGHT), mCurrentMode.get(FLASH_LIGHT));
                }
//                if (null != mCamera) {
//                    Camera.Parameters parameters;
//                    List flashModeslist;
//
//                    parameters = mCamera.getParameters();
//                    Log.d(TAG, "afterParameters: " + parameters.flatten());
//
//                    mFlashStatus = Camera.Parameters.FLASH_MODE_TORCH.equals(parameters.getFlashMode()) || Camera.Parameters.FLASH_MODE_ON.equals(parameters.getFlashMode()) ? true : false;
//                    mFlashStatus = !mFlashStatus;
//                    CameraConfigurationHelper.setTorch(parameters, mFlashStatus);
//
//                    mCamera.setParameters(parameters);
//                    Camera.Parameters afterParameters;
//
//                    afterParameters = mCamera.getParameters();
//                    Log.d(TAG, "afterParameters: " + afterParameters.flatten());
//                }
                break;
            case R.id.btnShutter:
                if (null != mCamera) {
                    //通过回调函数保存拍到的照片
                    mCamera.takePicture(null, null, new CapturePictureCallback(null, null));
                }
                break;

        }
    }

    @Override
    public void onClink(int function, int postion) {
        List<String> list;
        String expectedMode;
        String currentMode;

        list = mFunctionSelectionMap.get(function);
        expectedMode = list.get(postion);
        currentMode = mCurrentMode.get(function);

        switch (postion) {
            case FLASH_LIGHT:
                CameraConfigurationHelper.setTorch();
                break;
        }
    }

    class PreviewViewHolderCallback implements SurfaceHolder.Callback {


        @Override
        public void surfaceCreated(SurfaceHolder holder) {

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
