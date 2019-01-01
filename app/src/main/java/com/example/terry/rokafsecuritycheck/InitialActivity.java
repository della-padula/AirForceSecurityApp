package com.example.terry.rokafsecuritycheck;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.*;

public class InitialActivity extends AppCompatActivity {

    PackageManager pm;
    private final String TAG = "MASTER_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(InitialActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                Map<String, File> externalLocations = ExternalStorage.getAllStorageLocations();
                File sdCard = externalLocations.get(ExternalStorage.SD_CARD);
                File externalSdCard = externalLocations.get(ExternalStorage.EXTERNAL_SD_CARD);



                Log.d("MASTER_LOG", "sdCard : " + sdCard);
                Log.d("MASTER_LOG", "externalSdCard : " + externalSdCard);
                Log.d(TAG, "allStorageLocations Size : " + getAllStorageLocations().size());
                SdCardUtil.getSDCardPathEx();

                // 허락을 한 경우 실행할 부분
                pm = getPackageManager();

                if(externalMemoryAvailable(InitialActivity.this))
                    Toast.makeText(InitialActivity.this, "external : 사용 가능(장착됨)", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(InitialActivity.this, "external : 사용 불가능(마운트되지 않음)", Toast.LENGTH_SHORT).show();

                checkCamera();
                checkMicAvailability();
                Log.d("MASTER_LOG", "onPermissionGranted: " + StorageUtils.getStorageList());
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(InitialActivity.this, "권한이 없습니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                // 거절을 한 경우 실행할 부분
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO)
                .check();
    }

    protected boolean externalMemoryAvailable(Activity context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        if (storages.length > 1 && storages[0] != null && storages[1] != null)
            return true;
        else
            return false;

    }

    protected boolean checkCamera() {
        // Check whether Camera is in the device
        boolean resultCamera;
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

        }
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d("MASTER_LOG : ", "Camera TRUE");
            resultCamera = true;
        } else {
            Log.d("MASTER_LOG : ", "Camera FALSE");
            resultCamera = false;
        }

        // Temporary Code
        int numCameras = Camera.getNumberOfCameras();
        if(numCameras > 0) {
            resultCamera = true;
        }
        Log.d("MASTER_LOG : ", "Number of Cameras : " + numCameras);

        return resultCamera;
    }

    protected boolean checkMicAvailability() {
        boolean isMicAvailable = getMicrophoneAvailable(this);

        if(isMicAvailable) {
            Log.d("MASTER_LOG : ", "Mic TRUE");
        } else {
            Log.d("MASTER_LOG : ", "Mic FALSE");
        }
        return isMicAvailable;
    }

    protected boolean getMicrophoneAvailable(Context context) {
        MediaRecorder recorder = new MediaRecorder();
        boolean available = true;

        // Temporary Source Code
        String[] externalArray;
        String secondaryStorage = System.getenv("SECONDARY_STORAGE");
        String externalStorage = System.getenv("EXTERNAL_STORAGE");

        if (secondaryStorage != null) {
            externalArray = secondaryStorage.split(":");
        } else {
            externalArray = new String[0];
        }

        Log.d(TAG, "externalStorage : " + externalStorage);
        Log.d(TAG, "secondaryStorage : " + secondaryStorage);
        Log.d(TAG, "externalArray : " + externalArray.toString());

        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setOutputFile(new File(context.getCacheDir(), "MediaUtil#micAvailTestFile").getAbsolutePath());

            recorder.prepare();
            recorder.start();

        } catch (Exception exception) {
            available = false;
        }

        recorder.release();
        return available;
    }

    public static final String SD_CARD = "sdCard";
    public static final String EXTERNAL_SD_CARD = "externalSdCard";
    private static final String ENV_SECONDARY_STORAGE = "SECONDARY_STORAGE";

    public static Map<String, File> getAllStorageLocations() {
        Map<String, File> storageLocations = new HashMap<>(10);
        File sdCard = Environment.getExternalStorageDirectory();
        storageLocations.put(SD_CARD, sdCard);
        final String rawSecondaryStorage = System.getenv(ENV_SECONDARY_STORAGE);
        if (!TextUtils.isEmpty(rawSecondaryStorage)) {
            String[] externalCards = rawSecondaryStorage.split(":");
            for (int i = 0; i < externalCards.length; i++) {
                String path = externalCards[i];
                storageLocations.put(EXTERNAL_SD_CARD + String.format(i == 0 ? "" : "_%d", i), new File(path));
            }
        }
        return storageLocations;
    }
}
