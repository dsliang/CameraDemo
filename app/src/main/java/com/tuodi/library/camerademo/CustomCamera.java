package com.tuodi.library.camerademo;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dsliang on 2016/5/27.
 */
public class CustomCamera extends Fragment {

    private static final String TAG = CustomCamera.class.getSimpleName();

    @Bind(R.id.surfaceCameraPreviewView)
    SurfaceView surfaceCameraPreviewView;
    SurfaceHolder holder;
    @Bind(R.id.btnShutter)
    ImageButton btnShutter;
    Camera camera;
    @Bind(R.id.spinnerPictureSize)
    Spinner spinnerPictureSize;
    boolean mFlashStatus;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.btnShutter, R.id.btnFlashLight})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnFlashLight:
                if (null != camera) {
                    Camera.Parameters parameters;
                    List flashModeslist;

                    parameters = camera.getParameters();
                    Log.d(TAG, "afterParameters: " + parameters.flatten());

                    mFlashStatus = Camera.Parameters.FLASH_MODE_TORCH.equals(parameters.getFlashMode()) || Camera.Parameters.FLASH_MODE_ON.equals(parameters.getFlashMode()) ? true : false;
                    mFlashStatus = !mFlashStatus;
                    CameraConfigurationHelper.setTorch(parameters, mFlashStatus);

                    camera.setParameters(parameters);
                    Camera.Parameters afterParameters;

                    afterParameters = camera.getParameters();
                    Log.d(TAG, "afterParameters: " + afterParameters.flatten());
                }
                break;
            case R.id.btnShutter:
                if (null != camera) {
                    //通过回调函数保存拍到的照片
                    camera.takePicture(null, null, new CapturePictureCallback(null, null));
                }
                break;

        }
    }

    class PreviewViewHolderCallback implements SurfaceHolder.Callback {


        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            //不指定那个摄像头,默认是打开后置摄像头
            camera = Camera.open();
            if (null == camera)
                return;

            try {
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            camera.startPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            if (null != camera) {
                camera.release();
                camera = null;
            }
        }
    }
}
