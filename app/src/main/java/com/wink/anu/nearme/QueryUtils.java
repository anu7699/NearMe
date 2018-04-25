package com.wink.anu.nearme;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

class QueryUtils {
    public static final String PLACES_REQUEST_URL="https://maps.googleapis.com/maps/api/place/search/json?";

    private static final String LOG_TAG ="QueryUtils" ;


    public static String makeUrl(double longitude, double latitude, String placeSpecified,String API_KEY) {
        Uri baseUri=Uri.parse(PLACES_REQUEST_URL);
        StringBuilder location=new StringBuilder("");
        location.append(latitude);
        location.append(",");
        location.append(longitude);
        Uri.Builder builder = baseUri.buildUpon();
        builder.appendQueryParameter("types",placeSpecified);
        builder.appendQueryParameter("location",location.toString());
        builder.appendQueryParameter("sensor","false");
        builder.appendQueryParameter("rankby","distance");
        builder.appendQueryParameter("key",API_KEY);
        
        Log.v("final uri : ",builder.toString());
        return builder.toString();


    }

    public static URL getUrlContents(String urlString) {
        try {
            URL url=new URL(urlString);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String makeHttpConn(URL url)throws IOException {
        String JSON_resp="";
        if(url==null)
            return JSON_resp;

        HttpURLConnection urlconnetion=null;
        InputStream is=null;
        try{
            urlconnetion=(HttpURLConnection)url.openConnection();
            urlconnetion.setRequestMethod("GET");
            urlconnetion.setReadTimeout(10000);
            urlconnetion.setConnectTimeout(15000);
            urlconnetion.connect();
            if(urlconnetion.getResponseCode()!=200)
            {
                Log.e(LOG_TAG,"http request code not 200");
                return ("");
            }

            is=urlconnetion.getInputStream();
            JSON_resp=readFromStream(is);
        }catch (IOException e){
            Log.e(LOG_TAG,"error occured while connecting ",e);
        }
        finally {
            if(urlconnetion!=null)
                urlconnetion.disconnect();
            if(is!=null)
                is.close();
        }
        return JSON_resp;
    }

    private static String readFromStream(InputStream is) throws IOException {
        StringBuilder output=new StringBuilder();
        if(is!=null) {
            InputStreamReader reader = new InputStreamReader(is, Charset.forName("UTF-8"));
            BufferedReader buffer_reader = new BufferedReader(reader);


            String line = buffer_reader.readLine();
            while (line != null) {
                output.append(line);
                line = buffer_reader.readLine();
            }
        }
        return output.toString();


    }
}
