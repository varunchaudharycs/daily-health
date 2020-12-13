package com.example.dailyhealthcheckup;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;


public class LocationMonitor implements LocationListener {
    private double latitudeData;
    private double longitudeData;
    private Context context;
    private LocationManager locationManager;
    ArrayList<Double> locationData = new ArrayList<>();

    public LocationMonitor(LocationManager locationManager, Context context) {
        this.locationManager = locationManager;
        this.context = context;
    }

    public ArrayList<Double> getLocationData() {
        startGps();
        return locationData;
    }

    private void startGps() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        android.location.Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

        onLocationChanged(location);

        locationData.add(latitudeData);
        locationData.add(longitudeData);
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        latitudeData = latitude;
        longitudeData = longitude;
    }
}