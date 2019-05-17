package com.ipss.gpsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView gpsLatTextView;
    private TextView gpsLongTextView;
    private TextView onOffTextView;
    private TextView gpsSpeedTextView;
    private TextView gpsAccuracyTextView;
    private TextView gpsAltitudeTextView;
    private String lat;
    private String lon;
    private String accuracy;
    private String speed;
    private String altitude;
    private Gps gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsLatTextView = findViewById(R.id.gpsLatTextView);
        gpsLongTextView = findViewById(R.id.gpsLongTextView);
        onOffTextView = findViewById(R.id.OnOffTextView);
        gpsSpeedTextView = findViewById(R.id.gpsSpeedTextView);
        gpsAltitudeTextView = findViewById(R.id.gpsAltitudeTextView);
        gpsAccuracyTextView = findViewById(R.id.gpsAccuracyTextView);

        // checkIfGpsIsEnabled();
        gps = new Gps(new GpsListener(this), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfGpsIsEnabled();
        gps.startTracking();
    }

    @Override
    protected void onPause() {
        gps.stopTracking("the app is going to be in pause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        gps.stopTracking("the app is going to be in stop");
        super.onStop();
    }

    /**
     * Show an alert dialog to the user, telling him to give to the app the permission for using
     * the localization service
     */
    public void displayPermissionError() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_error);
        builder.setMessage(R.string.permission_error_body);
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("Dialog", "Permission DialogBox closed");
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.grant_permission, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        builder.show();
    }

    /**
     * Show an alert dialog to the user, telling him that the GPS is turned off
     */
    private void displayGpsStatusError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.gps_error);
        builder.setMessage(R.string.gps_error_body);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("Dialog", "Gps Status DialogBox closed");
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.turn_on, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        builder.show();
    }

    /**
     * Show an alert dialog to the user, telling him that his device doesn't have a GPS sensor, then
     * close the app
     */
    public void displayProviderError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.gps_Sensors);
        builder.setMessage(R.string.gps_sensors_body);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.show();
    }

    /**
     * Check that the GPS is enabled. If so set the status to on, otherwise show an alert dialog to
     * the user
     */
    public void checkIfGpsIsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return;
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsLongTextView.setText(R.string.Unavailable);
            gpsLatTextView.setText(R.string.Unavailable);
            gpsAccuracyTextView.setText(R.string.Unavailable);
            gpsAltitudeTextView.setText(R.string.Unavailable);
            gpsSpeedTextView.setText(R.string.Unavailable);
            onOffTextView.setText(R.string.Off);
            onOffTextView.setTextColor(Color.RED);
            displayGpsStatusError();
        } else {
            onOffTextView.setText(R.string.On);
            onOffTextView.setTextColor(Color.GREEN);
        }
    }

    /**
     * Allow non-UI threads to set the GPS status to off
     */
    public void setOffText() {
        final Handler UIHandler = new Handler(Looper.getMainLooper());
        UIHandler.post(setOffTextView);
    }

    /**
     * Set the GPS status to off
     */
    private Runnable setOffTextView = new Runnable() {
        @Override
        public void run() {
            gpsLongTextView.setText(R.string.Unavailable);
            gpsLatTextView.setText(R.string.Unavailable);
            gpsAccuracyTextView.setText(R.string.Unavailable);
            gpsAltitudeTextView.setText(R.string.Unavailable);
            gpsSpeedTextView.setText(R.string.Unavailable);
            onOffTextView.setText(R.string.Off);
            onOffTextView.setTextColor(Color.RED);
        }
    };

    /**
     * Allow non-UI threads to set the GPS status to on
     */
    public void setOkText() {
        final Handler UIHandler = new Handler(Looper.getMainLooper());
        UIHandler.post(setOkTextView);
    }

    /**
     * Set the GPS status to on
     */
    private Runnable setOkTextView = new Runnable() {
        @Override
        public void run() {
            onOffTextView.setText(R.string.On);
            onOffTextView.setTextColor(Color.GREEN);
        }
    };

    /**
     * Allow non-UI threads to update the GPS status
     * @param lat the current latitude
     * @param lon the current longitude
     * @param accuracy the current accuracy
     * @param altitude the current altitude
     * @param speed the current speed
     */
    public void updateGps(String lat, String lon, String accuracy, String altitude, String speed) {
        this.lat = lat;
        this.lon = lon;
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.speed = speed;
        final Handler UIHandler = new Handler(Looper.getMainLooper());
        UIHandler.post(updateGpsTextView);
    }

    /**
     * Update the GPS status
     */
    private Runnable updateGpsTextView = new Runnable() {
        @Override
        public void run() {
            onOffTextView.setText(R.string.On);
            onOffTextView.setTextColor(Color.GREEN);
            gpsLatTextView.setText(lat);
            gpsLatTextView.append("°");
            gpsLongTextView.setText(lon);
            gpsLongTextView.append("°");
            gpsAccuracyTextView.setText(accuracy);
            gpsAccuracyTextView.append(" m");
            gpsAltitudeTextView.setText(altitude);
            gpsAltitudeTextView.append(" m");
            gpsSpeedTextView.setText(speed);
            gpsSpeedTextView.append(" m/s");
        }
    };
}