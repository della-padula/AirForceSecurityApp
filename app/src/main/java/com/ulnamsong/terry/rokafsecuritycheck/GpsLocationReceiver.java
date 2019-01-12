package com.ulnamsong.terry.rokafsecuritycheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GpsLocationReceiver extends BroadcastReceiver implements LocationListener {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            // react on GPS provider change action
            Intent iintent = new Intent("android.location.GPS_ENABLED_CHANGE");
            iintent.putExtra("enabled", true);
            context.sendBroadcast(iintent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
