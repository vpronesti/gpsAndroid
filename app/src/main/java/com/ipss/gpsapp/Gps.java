package com.ipss.gpsapp;

import android.content.Context;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class Gps {
    private GpsListener gpsListener;
    private LocationManager locationManager;
    private MainActivity mainActivity;
    private boolean isOn;

    public Gps(GpsListener gpsListener, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.gpsListener = gpsListener;
        locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        isOn = false;
    }

    /**
     * Enable the GPS tracking
     */
    public void startTracking() {
        Log.d("startTracking", "startTracking has been called");
        if (isOn) {
            return;
        }

        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
            } else {
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }

            gpsListener.onLocationChanged(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, gpsListener);
            isOn = true;
        } catch (SecurityException e) {
            isOn = false;
            e.printStackTrace();
            mainActivity.displayPermissionError();
        } catch (IllegalArgumentException e) {
            isOn = false;
            e.printStackTrace();
            mainActivity.displayProviderError();
        }
    }

    /**
     * Disable the GPS tracking
     * @param reason why the app is stopping the tracking
     */
    public void stopTracking(String reason) {
        Log.d("stopTracking", "stopTracking has been called because " + reason);
        if (!isOn) {
            return;
        }

        locationManager.removeUpdates(gpsListener);
        isOn = false;
    }
}
