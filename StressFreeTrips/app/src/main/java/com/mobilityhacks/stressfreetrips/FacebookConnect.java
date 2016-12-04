package com.mobilityhacks.stressfreetrips;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Owner on 03.12.2016.
 */

public class FacebookConnect {

    public static LatLng[] getEvents() throws IOException, URISyntaxException {
        HttpResponse response = null;
        String json;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("https://graph.facebook.com/v2.8/search?access_token=EAACVsj4GyKgBAEQym0TnlnKfB8znt4WqZBKnCjlSc4ZCrK3iSj5OFfAsF7ZBKrHY5ZCnhOBfBbW8wcjLHwrpgZBFar6vsRMGkN5C86pDH37z44F328SbZAO17rlj8rTZAVZCH3bIrDSOGQuHTx7DSf03IUf82hLZBeJ8ZD&debug=all&format=json&method=get&pretty=0&q=MassenVeranstaltung&suppress_http_code=1&type=event&limit=1"));
            response = client.execute(request);
            json = convertStreamToString(response.getEntity().getContent());
            List<LatLng> route = new LinkedList<>();
            JSONObject parser = new JSONObject(json);
            JSONArray events  = parser.getJSONArray("data");
            for(int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                JSONObject location = event.getJSONObject("place").getJSONObject("location");
                route.add(new LatLng(location.getDouble("latitude"), location.getDouble("longitude")));
            }
            return route.toArray(new LatLng[route.size()]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LatLng[] {new LatLng(52.521918, 13.413215)};
    }

    public static String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),1024);
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                inputStream.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}
