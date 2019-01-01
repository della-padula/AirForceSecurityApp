package com.example.terry.rokafsecuritycheck;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.telephony.TelephonyManager;

public class SecurityCheck {

    // 카메라 물리적 제거 여부 확인
    public static int checkCamera() {
        // Check how many Cameras is in the device
        int numCameras = Camera.getNumberOfCameras();
        //Log.d("MASTER_LOG : ", "Number of Cameras : " + numCameras);
        return numCameras;
    }

    public static boolean checkGPSAvailability(PackageManager pm) {
        boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        return hasGps;
    }

    // 셀룰러 가용성 확인
    public static Boolean isMobileAvailable(Context appcontext) {
        TelephonyManager tel = (TelephonyManager) appcontext.getSystemService(Context.TELEPHONY_SERVICE);
        return ((tel.getNetworkOperator() != null && tel.getNetworkOperator().equals("")) ? false : true);
    }
}
