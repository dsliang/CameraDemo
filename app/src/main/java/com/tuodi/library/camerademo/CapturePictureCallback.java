package com.tuodi.library.camerademo;

import android.hardware.Camera;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dsliang on 2016/5/29.
 */
public class CapturePictureCallback implements Camera.PictureCallback {

    private static final String TAG = CapturePictureCallback.class.getSimpleName();

    String filePath;
    String fileName;

    public CapturePictureCallback(String picPath, String picName) {

        if (TextUtils.isEmpty(picPath)) {
            filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "CapturePicture";
        } else {
            filePath = picPath;
        }

        Log.d(TAG, "path: " + filePath);

        if (TextUtils.isEmpty(picName)) {
            fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
        } else {
            fileName = picName;
        }

        Log.d(TAG, "name: " + fileName);

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        File path;
        File file;
        FileOutputStream outputStream = null;

        path = new File(filePath);
        file = new File(filePath, fileName);

        if (!path.exists()) {
            path.mkdir();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "success save jpg file");

        //onPictureTaken调用以后会自动停止预览,需要手动启动一次
        camera.startPreview();
    }
}
