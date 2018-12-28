package de.geosearchef.hnsdroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import de.geosearchef.hnsdroid.data.Location;

public class LocationService {

    private static LocationManager locationManager;
    private static volatile android.location.Location lastLocation;

    private static boolean initStarted = false;

    @SuppressLint("MissingPermission")
    public static void init(Context context) {
        if(initStarted) {
            return;
        }
        initStarted = true;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        ensureGPSEnabled(context);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public static void ensureGPSEnabled(Context context) {
        while(! locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);
        }
    }

    public static Location getMyLocation() {
        return lastLocation == null ?
                new Location(0.0, 0.0, 1.0) :
                new Location(lastLocation.getLatitude(), lastLocation.getLongitude(), lastLocation.getAccuracy());
    }

    private static LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            LocationService.lastLocation = location;

            if(GameService.gameMapFragment != null && HNSService.centerOnLocationUpdate) {
                GameService.gameMapFragment.moveCamera(location);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
