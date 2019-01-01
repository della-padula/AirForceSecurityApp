package com.example.terry.rokafsecuritycheck;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.*;

import static com.example.terry.rokafsecuritycheck.SecurityCheck.checkCamera;

public class MainActivity extends AppCompatActivity {

    PackageManager pm;
    private final String TAG = "MASTER_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                // 허락을 한 경우 실행할 부분
                pm = getPackageManager();

                if (externalMemoryAvailable(MainActivity.this)) {
                    // External SDcard 장착되었으며 마운트 완료. (인식됨)
                    Toast.makeText(MainActivity.this, "external : 사용 가능(장착됨)", Toast.LENGTH_SHORT).show();
                } else {
                    // External SDCard 마운트 안됨 혹은 장착되지 않음. (인식되지 않음)
                    Toast.makeText(MainActivity.this, "external : 사용 불가능(마운트되지 않음)", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "Number of Cameras : " + checkCamera());
                Log.d(TAG, "hasGPS : " + SecurityCheck.checkGPSAvailability(pm));
                Log.d(TAG, "availableGSM : " + SecurityCheck.isMobileAvailable(MainActivity.this));


                // Temporary Code
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
                        Toast.makeText(MainActivity.this, lat + " " + lng, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, lat + " " + lng);
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
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.d(TAG, "onPermissionGranted: asdfasfdsasfdsafdfsfdafsdf");
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
                    Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한이 없습니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                // 거절을 한 경우 실행할 부분
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("본 앱을 사용하려면 권한이 필요합니다.")
                .setDeniedMessage("거부하셔서 사용불가입니다.\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .check();

    }

    // SD카드가 인식되는지 안되는지 확인
    protected boolean externalMemoryAvailable(Activity context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        if (storages.length > 1 && storages[0] != null && storages[1] != null)
            return true;
        else
            return false;
    }
}
