package com.example.terry.rokafsecuritycheck;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import java.io.File;

public class SecurityCheck {

    // 카메라 물리적 제거 여부 확인
    public static int checkCamera() {
        int numCameras = Camera.getNumberOfCameras();
        return numCameras;
    }

    public static boolean checkGPSAvailability(PackageManager pm) {
        boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        return hasGps;
    }

    // 셀룰러 가용성 확인
    public static boolean isMobileAvailable(Context appcontext) {
        TelephonyManager tel = (TelephonyManager) appcontext.getSystemService(Context.TELEPHONY_SERVICE);
        return ((tel.getNetworkOperator() != null && tel.getNetworkOperator().equals("")) ? false : true);
    }

    // SD카드가 인식되는지 안되는지 확인
    public static boolean externalMemoryAvailable(Activity context) {
        File[] storage = ContextCompat.getExternalFilesDirs(context, null);
        if (storage.length > 1 && storage[0] != null && storage[1] != null)
            return true;
        else
            return false;
    }
}
