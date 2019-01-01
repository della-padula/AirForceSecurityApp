package com.example.terry.rokafsecuritycheck;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import butterknife.ButterKnife;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.*;

import static com.example.terry.rokafsecuritycheck.SecurityCheck.checkCamera;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MASTER_LOG";
    private long         mLastLocationMillis;
    private int          millisCount = 0;

    Location             mLastLocation;
    PackageManager       pm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                checkSecurity();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 거절을 한 경우 실행할 부분
                Toast.makeText(MainActivity.this, "[설정]-[권한]에서 권한을 활성화하세요.", Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        TedPermission.with(this)
                     .setPermissionListener(permissionlistener)
                     .setRationaleMessage("본 앱을 사용하려면 권한이 필요합니다.")
                     .setDeniedMessage("거부하셔서 사용불가입니다.\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                     .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                                     Manifest.permission.READ_EXTERNAL_STORAGE,
                                     Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                     Manifest.permission.CAMERA)
                     .check();
    }

    // Security Checking 하는 부분
    protected void checkSecurity() {
        pm = getPackageManager();

        if (SecurityCheck.externalMemoryAvailable(MainActivity.this)) {
            //Toast.makeText(MainActivity.this, "external : 사용 가능(장착됨)", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(MainActivity.this, "external : 사용 불가능(마운트되지 않음)", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "Number of Cameras : " + checkCamera());
        Log.d(TAG, "hasGPS : " + SecurityCheck.checkGPSAvailability(pm));
        Log.d(TAG, "availableGSM : " + SecurityCheck.isMobileAvailable(MainActivity.this));

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // GPS 프로바이더 사용가능여부
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d(TAG, "isGPSEnabled=" + isGPSEnabled);
        Log.d(TAG, "isNetworkEnabled=" + isNetworkEnabled);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                //Toast.makeText(MainActivity.this, lat + " " + lng, Toast.LENGTH_SHORT).show();
                Log.d(TAG, lat + " " + lng);

                millisCount++;
                if(millisCount > 1) {
                    // 신호 주기가 3초가 넘으면 GPS 사용불가로 판단
                    if((SystemClock.elapsedRealtime() - mLastLocationMillis) > 3000) {
                        // NO GPS
                        // Progress Bar Stop

                        Toast.makeText(MainActivity.this, "GPS Removed", Toast.LENGTH_SHORT).show();
                    }
                }
                mLastLocationMillis = SystemClock.elapsedRealtime();
                mLastLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged: asdf");
            }

            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled: asdf");
            }

            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled: asdf");
            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // 수동으로 위치 구하기
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {
            double lng = lastKnownLocation.getLatitude();
            double lat = lastKnownLocation.getLatitude();
            Log.d(TAG, "longtitude=" + lng + ", latitude=" + lat);
        }
    }
}
