package com.wink.anu.nearme;

import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WELLCOME on 20-04-2018.
 */

public class PlaceService {
    //private static  String API_KEY;
    private static final String API_KEY="AIzaSyC-pjXjDJMpscgPFBkEj7Xc-kBy812CB78 ";


    //method that returns the list of places of a given type near the current location
  public static List<com.wink.anu.nearme.Place> findPlaces(double longitude, double latitude, String placeSpecified)
    {
        String urlString =QueryUtils.makeUrl(longitude,latitude,placeSpecified,API_KEY);
        try{

            URL url=QueryUtils.getUrlContents(urlString);
            String Json_Response=QueryUtils.makeHttpConn(url);
            JSONObject root=new JSONObject(Json_Response);
            JSONArray array =root.getJSONArray("results");
            ArrayList<com.wink.anu.nearme.Place> places_list =new ArrayList<>();
            for(int i=0;i<array.length();i++)
            {
                com.wink.anu.nearme.Place place= com.wink.anu.nearme.Place.jsonToPontoReferencia(array.getJSONObject(i));
                Log.v("Places Services ", ""+place);
                places_list.add(place);


            }

            return places_list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }


}
