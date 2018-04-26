package com.wink.anu.nearme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.wink.anu.nearme.login.LoginActivity;

import android.support.design.widget.NavigationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final String LOG_TAG = MainActivity.class.getName();
    public static double lon, lat;
    public static String current_add;
    public static boolean granted = false;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomAdapter adapter;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesAsyncTask task;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private static TextView email,username;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //setting up the swipeRefreshLayout to integrate 'refresh on swipe' feature
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(granted) {
                    getLatLon();
                    swipeRefreshLayout.setRefreshing(false);
                    new PlacesAsyncTask().execute();
                }
                else {
                    Snackbar.make(getWindow().getDecorView(), R.string.empty_view_text, Snackbar.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }


            }
        });

        /************************************************************************************************************/

        /************************************************************************************************************/
        //setting up the navigation drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView=(NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        /************************************************************************************************************/

        /************************************************************************************************************/

        //Retrieving data from Intent and displaying it in navigation drwaer
        email=(TextView)header.findViewById(R.id.tv_nav_email);
        username=(TextView)header.findViewById(R.id.tv_nav_username);

        Intent i=getIntent();
        email.setText(i.getStringExtra("email"));
        username.setText(i.getStringExtra("username"));

        /************************************************************************************************************/

        /************************************************************************************************************/


        //setting up the ListView
        listView = (ListView) findViewById(R.id.list_view);
        emptyView=findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        final Vibrator vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        adapter = new CustomAdapter(this, new ArrayList<Place>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent=new Intent(MainActivity.this,PlaceDisplay.class);
                intent.putExtra("PlaceDisplay", (Serializable) adapterView.getItemAtPosition(position));
                vibrator.vibrate(100);
                startActivity(intent);
            }
        });


        //initializing a new thread via AsyncTask
        task = new PlacesAsyncTask();


        /************************************************************************************************************/

        /************************************************************************************************************/

        //check for permission to access location
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

                                //getting address from co-ordinates using google maps Geolocation API
                                Geocoder geo = new Geocoder(MainActivity.this.getApplicationContext(), Locale.getDefault());
                                List<Address> addresses = geo.getFromLocation(lat, lon, 1);
                                if (addresses.isEmpty()) {
                                    current_add = ("Waiting for Location");
                                } else {
                                    if (addresses.size() > 0) {
                                        current_add = (addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace(); // getFromLocation() may sometimes fail
                            }
                            Toast.makeText(getApplicationContext(), "Current Location"+ " " + current_add, Toast.LENGTH_LONG).show();
                            //executing the new thread
                            task.execute();

                        }
                    }
                });
            } catch (SecurityException e) {
                    e.printStackTrace();
            }


        } else {
            Snackbar.make(getWindow().getDecorView(),R.string.empty_view_text,Snackbar.LENGTH_LONG).show();
        }


    }//end of OnCreate()
    /************************************************************************************************************/

    /************************************************************************************************************/



    private void getLatLon()
    {
        //to get longitude and latitude values of the present location
        try {

            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lon = location.getLongitude();
                        lat = location.getLatitude();


                        Toast.makeText(getApplicationContext(), "Current Location:"+ " " + current_add, Toast.LENGTH_SHORT).show();


                    }
                }
            });
        } catch (SecurityException e) {
                e.printStackTrace();
        }

    }//end of getLatLon()




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
                if(granted)
                    return;


                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            granted = true;


        }
    }//end of checkPermission()




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
                    granted=true;
                        startApp();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(getWindow().getDecorView(),R.string.empty_view_text,Snackbar.LENGTH_LONG).show();
                    return ;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void startApp() {
        Intent intent=getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.menu_refresh)
        {   if(granted) {
            getLatLon();
            swipeRefreshLayout.setRefreshing(true);
            new PlacesAsyncTask().execute();
        }
        else
        {
            Snackbar.make(getWindow().getDecorView(),R.string.empty_view_text,Snackbar.LENGTH_LONG).show();

        }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

         if (id == R.id.nav_profile) {
             Snackbar.make(getWindow().getDecorView(), "Replace with your own action", Snackbar.LENGTH_LONG)
                     .setAction("Action", null).show();


         } else if (id == R.id.nav_settings) {
             Intent intent = new Intent();
             intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
             Uri uri = Uri.fromParts("package", getPackageName(), null);
             intent.setData(uri);
             startActivity(intent);

        }else if(id==R.id.nav_logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    /************************************************************************************************************/

    /************************************************************************************************************/

    //AsyncTask class  definition
    //Used to perform Network request and other related tasks on a background thread

    private class PlacesAsyncTask extends AsyncTask<String, Void, List<Place>> {


        @Override
        protected List<Place> doInBackground(String... strings) {
            Log.d(LOG_TAG,"Loading in background");
            if (isCancelled())
                return null;

            return PlaceService.findPlaces(lon, lat, getString(R.string.type));
        }

        @Override
        protected void onPostExecute(List<Place> places) {
            adapter.clear();
            if (places != null&&!places.isEmpty()) {
                adapter.addAll(places);
                swipeRefreshLayout.setRefreshing(false);

            } else {
                Log.e("post_execute: ", "null list returned");
                emptyView.setText("Nothing to display!");
            }


        }
    }



    /************************************************************************************************************/

    /************************************************************************************************************/

}