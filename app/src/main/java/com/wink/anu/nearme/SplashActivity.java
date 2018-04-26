package com.wink.anu.nearme;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wink.anu.nearme.login.LoginActivity;
import com.wink.anu.nearme.login.User;

public class SplashActivity extends AppCompatActivity {

     private final int SPLASH_DISPLAY_LENGTH = 1000;
     private static Boolean isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.hide();
        }

        /************************************************************************************************************/

        /************************************************************************************************************/

        //to check the connectivity status

        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=cm.getActiveNetworkInfo();
        isConnected=(info!=null)&&(info.isConnectedOrConnecting());
        if(isConnected) {

            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    //checking if there is a user already logged-in
                    if (firebaseUser == null) {

                        //no present user----open the login page
                        Intent startActivityIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(startActivityIntent);
                        SplashActivity.this.finish();
                    }
                    else {

                        //Open the Main page for the already-logged-in user
                        String uid = firebaseUser.getUid();
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.putExtra("username", user.getUsername());
                                i.putExtra("email", user.getEmail());
                                i.putExtra("phone number", user.getPhone());
                                i.putExtra("password", user.getPassword());
                                finish();
                                startActivity(i);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });

                    }

                }
            }, SPLASH_DISPLAY_LENGTH);
        }
        else {


            //prompt the user about unavailabilty of network connection
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Snackbar.make((View)findViewById(R.id.splash),R.string.no_internet_conn,4000).show();
            }
}
}
