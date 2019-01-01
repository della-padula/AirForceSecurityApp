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
import android.util.Log;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class InitialActivity extends AppCompatActivity {

    PackageManager pm;

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

                // 허락을 한 경우 실행할 부분
                pm = getPackageManager();

                Toast.makeText(InitialActivity.this, "extenal : " + externalMemoryAvailable(InitialActivity.this), Toast.LENGTH_SHORT).show();
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

    protected boolean checkExternalCard() {
        // Check whether ExternalCard is in the device
        String status = Environment.getExternalStorageState();
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;

        Log.d("MASTER_LOG > ", Environment.getExternalStorageState());

        if (status.equals(Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(
                    InitialActivity.this,
                    "SD카드가 장착되어 있습니다.",
                    Toast.LENGTH_LONG
            ).show();
        }
        else if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY))
        {
            Toast.makeText(
                    InitialActivity.this,
                    "SD카드가 장착되어 있지만 읽기 전용이므로 쓰기가 불가능 합니다.",
                    Toast.LENGTH_LONG
            ).show();
        }
        else if (status.equals(Environment.MEDIA_REMOVED))
        {
            Toast.makeText(
                    InitialActivity.this,
                    "SD카드가 장착되어 있지 않습니다.",
                    Toast.LENGTH_LONG
            ).show();
        }
        else if (status.equals(Environment.MEDIA_SHARED))
        {
            Toast.makeText(
                    InitialActivity.this,
                    "SD카드가 장착되어 있지만, USB 저장 장치로 PC에서 사용중입니다.",
                    Toast.LENGTH_LONG
            ).show();
        }
        else if (status.equals(Environment.MEDIA_BAD_REMOVAL))
        {
            Toast.makeText(
                    InitialActivity.this,
                    "SD카드의 마운트를 해제하기 전에 제거 했습니다.",
                    Toast.LENGTH_LONG
            ).show();
        }
        else if (status.equals(Environment.MEDIA_CHECKING))
        {
            Toast.makeText(
                    InitialActivity.this,
                    "SD카드를 확인중 입니다.",
                    Toast.LENGTH_LONG
            ).show();
        }
        else if (status.equalsIgnoreCase(Environment.MEDIA_NOFS))
        {
            Toast.makeText(
                    InitialActivity.this,
                    "SD카드는 장착되어 있지만, 공백이거나 지원되지 않는 "+
                            "파일 시스템을 이용하고 있습니다.",
                    Toast.LENGTH_LONG
            ).show();
        }
        else if (status.equalsIgnoreCase(Environment.MEDIA_UNMOUNTABLE))
        {
            Toast.makeText(
                    InitialActivity.this,
                    "SD카드는 장착되어 있지만, 마운트 할 수 없습니다.",
                    Toast.LENGTH_LONG
            ).show();
        }
        else if (status.equalsIgnoreCase(Environment.MEDIA_UNMOUNTED))
        {
            Toast.makeText(
                    InitialActivity.this,
                    "SD카드는 존재하지만, 마운트 되어있지 않습니다.",
                    Toast.LENGTH_LONG
            ).show();
        }
        else
        {
            Toast.makeText(
                    InitialActivity.this,
                    "기타요인으로 이용 불가능한 상태 입니다.",
                    Toast.LENGTH_LONG
            ).show();
        }

        return (mExternalStorageAvailable && mExternalStorageWriteable);
    }

    public boolean externalMemoryAvailable() {
        if (Environment.isExternalStorageRemovable()) {
            //device support sd card. We need to check sd card availability.
            String state = Environment.getExternalStorageState();
            return state.equals(Environment.MEDIA_MOUNTED) || state.equals(
                    Environment.MEDIA_MOUNTED_READ_ONLY);
        } else {
            //device not support sd card.
            return false;
        }
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
}
