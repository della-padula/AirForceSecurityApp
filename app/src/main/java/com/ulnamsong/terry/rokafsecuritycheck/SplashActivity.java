package com.ulnamsong.terry.rokafsecuritycheck;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        Handler hand = new Handler();
        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        // 거절을 한 경우 실행할 부분
                        Toast.makeText(SplashActivity.this, "[설정]-[권한]에서 권한을 활성화하세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                };

                TedPermission.with(SplashActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setRationaleMessage("본 앱을 사용하려면 권한이 필요합니다.")
                        .setDeniedMessage("거부하셔서 사용불가입니다.\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA)
                        .check();
            }
        }, 2000);
    }
}
