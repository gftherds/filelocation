package com.example.therdsak.filelocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.CollationElementIterator;

/**
 * A placeholder fragment containing a simple view.
 */
public class FileLocationActivityFragment extends Fragment {
    private static final String TAG = "FileActivityFragment";
    private static final int REQUEST_PERM_ACCESS_LOC = 999;
    private static final int REQUEST_PERM_FINE_ACCESS_LOC = 888;

    public FileLocationActivityFragment() {
    }


    private TextView mLongitudeText;
    private TextView mLatitudeText;
    private boolean mHasPermission;
    private boolean mHasFinePermission;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_file_location,container,false);

        mLongitudeText = (TextView) v.findViewById(R.id.longtitude_text);
        mLatitudeText = (TextView) v.findViewById(R.id.latitude_text);

        updateLocation();
        return v;
    }

    private void updateLocation(){
        if(hasPermission()){
            requestLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


            if(requestCode == REQUEST_PERM_ACCESS_LOC
                    || requestCode == REQUEST_PERM_FINE_ACCESS_LOC) {

                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "Fine-Location-Permission accepted");

                    // update when done
                    updateLocation();
                }
            }
        }

    private boolean hasPermission(){

      int permissionStatus =   ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

        int permissionFineStatus =
                ContextCompat.checkSelfPermission(
                        getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);


      mHasPermission = permissionStatus == PackageManager.PERMISSION_GRANTED;
        mHasFinePermission = permissionFineStatus == PackageManager.PERMISSION_GRANTED;
        if(!mHasPermission) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERM_ACCESS_LOC);
        }

        if(!mHasFinePermission) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERM_FINE_ACCESS_LOC);
        }

        Log.d(TAG, "hasPermission: " + mHasPermission);
        Log.d(TAG, "hasFinePermission: " + mHasFinePermission);
        return mHasPermission|| mHasFinePermission;
    }

    @SuppressWarnings("all")
    protected void requestLocation() {
        Log.d(TAG, "REQUEST Location");
        LocationManager locationManager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);

        if(mHasFinePermission && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "Request fine location");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    mLocationListener);
        }

        if(mHasPermission && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d(TAG, "Request network location");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    mLocationListener);
        }
    }


    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Got location!");
            mLatitudeText.setText(String.valueOf(location.getLatitude()));
            mLongitudeText.setText(String.valueOf(location.getLongitude()));

        }

        @Override
        public void onStatusChanged(String provideName, int status, Bundle bundle) {
            String locationStatus = "";

            switch (status) {
                case LocationProvider.AVAILABLE :
                    locationStatus = "available";
                    break;

                case LocationProvider.OUT_OF_SERVICE:
                    locationStatus = "out_of_service";
                    break;

                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    locationStatus = "temporarily_unavailable";
                    break;
            }
            Log.d(TAG, "Provider: " + provideName + " : status changed -> " + locationStatus);
        }

        @Override
        public void onProviderEnabled(String provideName) {
            Log.d(TAG, "Provider: " + provideName + " has been enabled");
        }

        @Override
        public void onProviderDisabled(String provideName) {
            Log.d(TAG, "Provider: " + provideName + " has been disabled");
        }
    };

}
