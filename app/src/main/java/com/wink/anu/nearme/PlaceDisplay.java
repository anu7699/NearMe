package com.wink.anu.nearme;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class PlaceDisplay extends AppCompatActivity {
    private TextView name,Address;
    private RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_display);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Place place=(Place)getIntent().getSerializableExtra("PlaceDisplay");
        name=findViewById(R.id.tv_disp_place_name);
        Address=findViewById(R.id.tv_address_val);
        ratingBar=findViewById(R.id.ratingBar);
        ratingBar.setRating(place.getRating());
        ratingBar.setIsIndicator(true);
        name.setText(place.getName());
        Address.setText(place.getVicinity());

    }
}
