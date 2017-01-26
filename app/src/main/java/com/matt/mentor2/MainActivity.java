package com.matt.mentor2;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;



import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends ListActivity {
    ArrayList<GooglePlace> venuesList;

    final String GOOGLE_KEY = "AIzaSyBDwzhubIO0rNSzxgKxPHTwzOlodWEE7yU";

    final String latitude = "40.7463956";
    final String longitude = "-73.9852992";

    ArrayAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new googleplaces().execute();
    }

    private class googleplaces extends AsyncTask<View,Void,String>{
        String temp;
        @Override
        protected String doInBackground(View... urls) {
            temp = makeCall("https://maps.googleapis.com/maps/api/place/search/json?location=" + latitude + "," + longitude + "&radius=100&sensor=true&key=" + GOOGLE_KEY);
            System.out.println("https://maps.googleapis.com/maps/api/place/search/json?location=" + latitude + "," + longitude + "&radius=100&sensor=true&key=" + GOOGLE_KEY);
            return "";
        }

        @Override
        protected void onPreExecute(){
            //Progress Bar?
        }

        @Override
        protected void onPostExecute(String result){
            if (temp == null) {
                //Error
            } else {
                venuesList = (ArrayList) parseGoogleParse(temp);
                List listTitle = new ArrayList();
                for (int i = 0; i < venuesList.size(); i++) {
                    // make a list of the venus that are loaded in the list.
                    // show the name, the category and the city
                    listTitle.add(i, venuesList.get(i).getName() + "\nOpen Now: " + venuesList.get(i).getOpenNow() + "\n(" + venuesList.get(i).getCategory() + ")");
                }
                // set the results to the list
                // and show them in the xml

                myAdapter = new ArrayAdapter(MainActivity.this, R.layout.row_layout, android.R.id.text1, listTitle);
                setListAdapter(myAdapter);
            }

        }
    }

    private static String makeCall(String url) {
        // string buffers the url
        StringBuffer buffer_string = new StringBuffer(url);
        String replyString = "";
        // instanciate an HttpClient
        HttpClient httpclient = new DefaultHttpClient();

        // instanciate an HttpGet
        HttpGet httpget = new HttpGet(buffer_string.toString());

        try {
            // get the responce of the httpclient execution of the url
            HttpResponse response = httpclient.execute(httpget);
            InputStream is = response.getEntity().getContent();

            // buffer input stream the result
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(20);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            // the result as a string is ready for parsing
            replyString = new String(baf.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(replyString);

        // trim the whitespaces
        return replyString.trim();
    }

    private static ArrayList parseGoogleParse(final String response) {
        ArrayList<GooglePlace> temp = new ArrayList();
        try {
            // make an jsonObject in order to parse the response
            JSONObject jsonObject = new JSONObject(response);
            // make an jsonObject in order to parse the response
            if (jsonObject.has("results")) {
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    GooglePlace poi = new GooglePlace();
                    if (jsonArray.getJSONObject(i).has("name")) {
                        poi.setName(jsonArray.getJSONObject(i).optString("name"));
                        poi.setRating(jsonArray.getJSONObject(i).optString("rating", " "));
                        if (jsonArray.getJSONObject(i).has("opening_hours")) {
                            if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) {
                                if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now").equals("true")) {
                                    poi.setOpenNow("YES");
                                } else {
                                    poi.setOpenNow("NO");
                                }
                            }
                        } else {
                            poi.setOpenNow("Not Known");
                        }
                        if (jsonArray.getJSONObject(i).has("types")) {
                            JSONArray typesArray = jsonArray.getJSONObject(i).getJSONArray("types");

                            for (int j = 0; j < typesArray.length(); j++) {
                                poi.setCategory(typesArray.getString(j) + ", " + poi.getCategory());
                            }
                        }
                    }
                    temp.add(poi);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();
        }
        return temp;

    }
}
