package com.tuodi.library.camerademo;

import android.hardware.Camera;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by dsliang on 2016/5/30.
 */
public class CameraConfigurationHelper {


    private static final String TAG = CameraConfigurationHelper.class.getSimpleName();

    private CameraConfigurationHelper() {
    }

    public static void setPictureSize(Camera.Parameters parameters, Camera.Size desiredSize) {
        List previewSizes;
        Camera.Size realSize;

        previewSizes = parameters.getSupportedPictureSizes();

        realSize = findSettableValue("set picture size", previewSizes, desiredSize);

        if (null != realSize) {
            parameters.setPictureSize(realSize.width, realSize.height);
        }
    }

    public static void setPreviewSize(Camera.Parameters parameters, Camera.Size desiredSize) {
        List previewSizes;
        Camera.Size realSize;

        previewSizes = parameters.getSupportedPreviewSizes();

        realSize = findSettableValue("set preview size", previewSizes, desiredSize);

        if (null != realSize) {
            parameters.setPreviewSize(realSize.width, realSize.height);
        }
    }

    public static void setFlashMode(Camera.Parameters parameters, String mode) {
        List<String> modes;
        String expectedMode;

        modes = parameters.getSupportedFlashModes();
        expectedMode = findSettableValue("flash mode", modes, mode);

        if (!TextUtils.isEmpty(expectedMode)) {
            parameters.setFlashMode(expectedMode);
        }

    }

    public static void setFocusMode(Camera.Parameters parameters, String mode) {
        List<String> modes;
        String expectedMode;

        modes = parameters.getSupportedFocusModes();
        expectedMode = findSettableValue("focus mode", modes, mode);

        if (!TextUtils.isEmpty(expectedMode)) {
            parameters.setFocusMode(expectedMode);
        }

    }


    public static void setTorch(Camera.Parameters parameters, boolean on) {
        List flashModes;
        String flashMode;

        flashModes = parameters.getSupportedFlashModes();

        if (on) {
            flashMode = findSettableValue("set torch", flashModes, Camera.Parameters.FLASH_MODE_TORCH, Camera.Parameters.FLASH_MODE_ON);
        } else {
            flashMode = findSettableValue("set torch", flashModes, Camera.Parameters.FLASH_MODE_OFF);
        }

        if (!TextUtils.isEmpty(flashMode)) {
            if (flashMode.equals(parameters.getFlashMode())) {
                Log.i(TAG, "Flash mode already set to " + flashMode);
            } else {
                Log.i(TAG, "Setting flash mode to " + flashMode);
                parameters.setFlashMode(flashMode);
            }
        }
    }

    public static String findSettableValue(String name,
                                           Collection<String> supportValues,
                                           String... desiredValues) {

        if (null == supportValues || null == desiredValues) {
            return null;
        }

        Log.i(TAG, "Requesting " + name + " value from among: " + Arrays.toString(desiredValues));
        Log.i(TAG, "Supported " + name + " values: " + supportValues);

        if (null != supportValues) {
            for (String desiredValue : desiredValues) {
                if (supportValues.contains(desiredValue)) {
                    Log.i(TAG, "Can set " + name + " to: " + desiredValue);
                    return desiredValue;
                }
            }
        }

        Log.i(TAG, "No supported values match");
        return null;
    }

    public static Camera.Size findSettableValue(String name,
                                                Collection<Camera.Size> supportValues,
                                                Camera.Size... desiredValues) {
        Log.i(TAG, "Requesting " + name + " value from among: " + Arrays.toString(desiredValues));
        Log.i(TAG, "Supported " + name + " values: " + supportValues);

        if (null == supportValues || null == desiredValues) {
            return null;
        }

        if (null != supportValues) {
            for (Camera.Size desiredValue : desiredValues) {
                for (Camera.Size supportValue : supportValues) {
                    if (supportValue.equals(desiredValue)) {
                        Log.i(TAG, "Can set " + name + " to: " + desiredValue);
                        return desiredValue;
                    }
                }

            }
        }

        Log.i(TAG, "No supported values match");
        return null;
    }

}
