package com.ulnamsong.terry.rokafsecuritycheck;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;

import static com.ulnamsong.terry.rokafsecuritycheck.SecurityCheck.checkCamera;
import static com.ulnamsong.terry.rokafsecuritycheck.SecurityCheck.rootedTest;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MASTER_LOG";
    private long         mLastLocationMillis;
    private int          millisCount = 0;
    private int           errorCount = 0;
    private boolean      cameraResult = false;
    private boolean         GSMResult = false;
    private boolean         GPSResult = false;
    private boolean      SDCardResult = false;
    private boolean      rootedResult = false;
    private boolean           locFlag = true;

    Location mLastLocation;
    PackageManager pm;
    ProgressDialog dialog;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvSubTitle)
    TextView tvSubTitle;
    @BindView(R.id.tvCameraContent)
    TextView tvCameraResult;
    @BindView(R.id.tvRootedContent)
    TextView tvRootedResult;
    @BindView(R.id.tvGSMContent)
    TextView tvGSMResult;
    @BindView(R.id.tvGPSContent)
    TextView tvGPSResult;
    @BindView(R.id.tvSDCardContent)
    TextView tvSDCardResult;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    @BindView(R.id.ivCameraResult)
    ImageView ivCameraResult;
    @BindView(R.id.ivRootedResult)
    ImageView ivRootedResult;
    @BindView(R.id.ivGSMResult)
    ImageView ivGSMResult;
    @BindView(R.id.ivGPSResult)
    ImageView ivGPSResult;
    @BindView(R.id.ivSDCardResult)
    ImageView ivSDCardResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        registerReceiver(new GpsLocationReceiver(), new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        errorCount = 0;

        tvTitle = findViewById(R.id.tvTitle);
        tvSubTitle = findViewById(R.id.tvSubTitle);
        tvCameraResult = findViewById(R.id.tvCameraContent);
        tvRootedResult = findViewById(R.id.tvRootedContent);
        tvGSMResult = findViewById(R.id.tvGSMContent);
        tvGPSResult = findViewById(R.id.tvGPSContent);
        tvSDCardResult = findViewById(R.id.tvSDCardContent);
        btnRetry = findViewById(R.id.btnRetry);

        ivCameraResult = findViewById(R.id.ivCameraResult);
        ivRootedResult = findViewById(R.id.ivRootedResult);
        ivGPSResult = findViewById(R.id.ivGPSResult);
        ivGSMResult = findViewById(R.id.ivGSMResult);
        ivSDCardResult = findViewById(R.id.ivSDCardResult);

        tvRootedResult.setText(getString(R.string.progressing));
        tvCameraResult.setText(getString(R.string.progressing));
        tvGSMResult.setText(getString(R.string.progressing));
        tvGPSResult.setText(getString(R.string.progressing));
        tvSDCardResult.setText(getString(R.string.progressing));

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle(getString(R.string.progress_title));
        dialog.setMessage(getString(R.string.progress_content));
        dialog.setCancelable(false);

        dialog.show();
        btnRetry.setActivated(false);
        checkSecurity();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locFlag = true;
                dialog.show();
                checkSecurity();
            }
        });

        Handler hand = new Handler();
        hand.postDelayed(new Runnable() {

            @Override
            public void run() {
                dialog.hide();

                // 12초 지나도 응답 없을 시 GPS는 제거된 것으로 판별
                if(locFlag) {
                    GPSResult = true;
                    locFlag = false;

                    btnRetry.setActivated(true);
                    showResult();
                }
            }
        }, 12000);
    }

    // Security Checking 하는 부분
    protected void checkSecurity() {
        pm = getPackageManager();

        if (SecurityCheck.externalMemoryAvailable(MainActivity.this)) {
            //Toast.makeText(MainActivity.this, "external : 사용 가능(장착됨)", Toast.LENGTH_SHORT).show();
            errorCount++;
            SDCardResult = false;
        } else {
            //Toast.makeText(MainActivity.this, "external : 사용 불가능(마운트되지 않음)", Toast.LENGTH_SHORT).show();
            SDCardResult = true;
        }

        // Camera
        if(checkCamera() > 0) {
            errorCount++;
            cameraResult = false;
        } else {
            cameraResult = true;
        }

        // Firmware Root Test
        if(rootedTest(MainActivity.this)) {
            errorCount++;
            rootedResult = false;
        } else {
            rootedResult = true;
        }

        // GSM
        GSMResult = !SecurityCheck.isMobileAvailable(MainActivity.this);

        if(!GSMResult) errorCount++;


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

                if(locFlag) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    //Toast.makeText(MainActivity.this, lat + " " + lng, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, lat + " " + lng);

                    millisCount++;
                    Log.d(TAG, "millisCount : " + millisCount);
                    if (millisCount > 1) {
                        // 신호 주기가 3초가 넘으면 GPS 사용불가로 판단
                        if ((SystemClock.elapsedRealtime() - mLastLocationMillis) > 3000) {
                            GPSResult = true;
                        } else {
                            errorCount++;
                            GPSResult = false;
                        }

                        Log.d(TAG, "onLocationChanged: Dialog HIDE");
                        // Dialog Stop
                        dialog.hide();
                        locFlag = false;

                        btnRetry.setActivated(true);
                        showResult();
                    }
                    mLastLocationMillis = SystemClock.elapsedRealtime();
                    mLastLocation = location;
                }
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

    protected void showResult() {
        if(GPSResult) {
            tvGPSResult.setText(getString(R.string.no_error));
            ivGPSResult.setImageDrawable(getResources().getDrawable(R.drawable.ok));
        } else {
            tvGPSResult.setText(getString(R.string.error));
            ivGPSResult.setImageDrawable(getResources().getDrawable(R.drawable.error));
        }

        if(cameraResult) {
            tvCameraResult.setText(getString(R.string.no_error));
            ivCameraResult.setImageDrawable(getResources().getDrawable(R.drawable.ok));
        } else {
            tvCameraResult.setText(getString(R.string.error));
            ivCameraResult.setImageDrawable(getResources().getDrawable(R.drawable.error));
        }

        if(rootedResult) {
            tvRootedResult.setText(getString(R.string.no_rooted));
            ivRootedResult.setImageDrawable(getResources().getDrawable(R.drawable.ok));
        } else {
            tvRootedResult.setText(getString(R.string.rooted));
            ivRootedResult.setImageDrawable(getResources().getDrawable(R.drawable.error));
        }

        if(GSMResult) {
            tvGSMResult.setText(getString(R.string.no_error));
            ivGSMResult.setImageDrawable(getResources().getDrawable(R.drawable.ok));
        } else {
            tvGSMResult.setText(getString(R.string.error));
            ivGSMResult.setImageDrawable(getResources().getDrawable(R.drawable.error));
        }

        if(SDCardResult) {
            tvSDCardResult.setText(getString(R.string.no_error));
            ivSDCardResult.setImageDrawable(getResources().getDrawable(R.drawable.ok));
        } else {
            tvSDCardResult.setText(getString(R.string.error));
            ivSDCardResult.setImageDrawable(getResources().getDrawable(R.drawable.error));
        }

        if(errorCount > 0) {
            tvTitle.setText(getString(R.string.no_pass_title));
            tvSubTitle.setText(getString(R.string.no_pass_content));
        } else {
            tvTitle.setText(getString(R.string.pass_title));
            tvSubTitle.setText(getString(R.string.pass_content));
        }
    }
}
