package com.wink.anu.nearme;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final String LOG_TAG = MainActivity.class.getName();
    public static double lon, lat;
    public static String current_add;
    public static boolean granted = false;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomAdapter adapter;
    private LoaderManager loaderManager;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesAsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLatLon();
                new PlacesAsyncTask().execute();
            }
        });
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new CustomAdapter(this, new ArrayList<Place>());
        listView.setAdapter(adapter);
        task = new PlacesAsyncTask();


        //check for permission
        checkPermissions();
        if (granted) {
            try {

                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lon = location.getLongitude();
                            lat = location.getLatitude();

                            try {
                                Geocoder geo = new Geocoder(MainActivity.this.getApplicationContext(), Locale.getDefault());
                                List<Address> addresses = geo.getFromLocation(lat, lon, 1);
                                if (addresses.isEmpty()) {
                                    current_add = ("Waiting for Location");
                                } else {
                                    if (addresses.size() > 0) {
                                        current_add = (addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                                        //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace(); // getFromLocation() may sometimes fail
                            }
                            Toast.makeText(getApplicationContext(), "3 longitude: " + lon + " latitude: " + lon + " " + current_add, Toast.LENGTH_SHORT).show();
                            //loaderManager = getLoaderManager();
                            //loaderManager.initLoader(1, null, MainActivity.this);
                            task.execute();

                        }
                    }
                });
            } catch (SecurityException e) {

            }


        } else {

        }


    }
    private void getLatLon()
    {
        try {

            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lon = location.getLongitude();
                        lat = location.getLatitude();


                        Toast.makeText(getApplicationContext(), "3 longitude: " + lon + " latitude: " + lon + " " + current_add, Toast.LENGTH_SHORT).show();


                    }
                }
            });
        } catch (SecurityException e) {

        }



    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            granted = true;


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    granted = true;
                    startApp();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void startApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, 0);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private class PlacesAsyncTask extends AsyncTask<String, Void, List<Place>> {


        @Override
        protected List<Place> doInBackground(String... strings) {
            if (isCancelled())
                return null;

            return PlaceService.findPlaces(lon, lat, getString(R.string.type));
        }

        @Override
        protected void onPostExecute(List<Place> places) {
            adapter.clear();
            if (places != null) {
                adapter.addAll(places);
                swipeRefreshLayout.setRefreshing(false);

            } else
                Log.e("post_execute: ", "null list returned");
        }
    }
}