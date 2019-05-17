package com.ipss.gpsapp;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GpsListener implements LocationListener {
    private int i;
    private MainActivity mainActivity;
    private Location lastLocation;
    private boolean available;

    public GpsListener(MainActivity mainActivity) {
        this.i = 0;
        this.mainActivity = mainActivity;
        available = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "Called, location value = " + location);
        if (location == null) {
            if (lastLocation == null) {
                return;
            } else {
                location = lastLocation;
            }
        }

        lastLocation = location;
        @SuppressLint("DefaultLocale") String lat = String.format("%.06f", location.getLatitude());
        @SuppressLint("DefaultLocale") String lon = String.format("%.06f", location.getLongitude());
        @SuppressLint("DefaultLocale") String accuracy = String.format("%.02f", location.getAccuracy());
        @SuppressLint("DefaultLocale") String altitude = String.format("%.02f", location.getAltitude());
        @SuppressLint("DefaultLocale") String speed = String.format("%.02f", location.getSpeed());
        mainActivity.updateGps(lat, lon, accuracy, altitude, speed);
        Log.d("GPS update", "onLocationChanged called " + i + " times");
        i++;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String message;
        boolean locationProviderAvailable = false;

        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("onStatusChanged", "The " + provider + " is out of service");
                message = "The " + provider + " is out of service";
                available = false;
                break;

            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("onStatusChanged", "The " + provider + " is temporarily unavailable");
                message = "The " + provider + " is temporarily unavailable";
                available = false;
                break;

            case LocationProvider.AVAILABLE:
                Log.d("onStatusChanged", "The " + provider + " is newly available");
                message = "The " + provider + " is newly available";
                locationProviderAvailable = true;
                break;

            default:
                message = "";
        }

        if (locationProviderAvailable && !available) {
            Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
            available = true;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        mainActivity.setOkText();
        Toast.makeText(mainActivity, "The " + provider + " is enabled", Toast.LENGTH_SHORT).show();
        Log.d("onProviderEnabled", "The " + provider + " has been enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        mainActivity.setOffText();
        Toast.makeText(mainActivity, "The " + provider + " is disabled", Toast.LENGTH_SHORT).show();
        Log.d("onProviderDisabled", "The " + provider + " has been disabled");
    }
}
