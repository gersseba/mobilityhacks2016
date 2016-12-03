package com.mobilityhacks.stressfreetrips; /**
 * Created by danieldummer on 03.12.16.
 */


import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;


public class BvgConnect {

    public static final String WESTKREUZ = "A=1@O=S%20Westkreuz%20(Berlin)@X=13283036@Y=52501147@U=86@L=009024102@B=1@V=3.9,@p=1480610652@";
    public static final String OSTKREUZ = "A=1@O=S%20Ostkreuz%20Bhf%20(Berlin)@X=13469095@Y=52502846@U=86@L=009120003@B=1@V=3.9,@p=1480610652@";
    public static final String SUEDKREUZ = "A=1@O=S%20Südkreuz%20Bhf%20(Berlin)@X=13365575@Y=52475465@U=86@L=009058101@B=1@V=3.9,@p=1480610652@";
    public static final String ALEXANDERPLATZ = "A=1@O=S+U%20Alexanderplatz%20Bhf%20(Berlin)@X=13411267@Y=52521508@U=86@L=009100003@B=1@V=3.9,@p=1480610652@";

    public static LatLng[] getTrip(String from, String via, String to) throws IOException, URISyntaxException {
        String url = String.format("http://demo.hafas.de/openapi/vbb-proxy/trip?originId=%s&viaId=%s&passlist=1&destId=%s&format=json&accessId=BVG-VBB-Dezember&time=08:00&date=2016-12-12",from,via, to);
        return queryBvg(url);
    }

    private static LatLng[] queryBvg(String url) throws IOException, URISyntaxException {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(new URI(url));
        HttpResponse serverResponse = client.execute(httpGet);
        BasicResponseHandler handler = new BasicResponseHandler();
        String sResponse = handler.handleResponse(serverResponse);
        List<LatLng> route = new LinkedList<>();
        try {
            JSONObject parser = new JSONObject(sResponse);
            JSONArray trip  = parser.getJSONArray("Trip");
            JSONObject firstTrip = trip.getJSONObject(0);
            JSONObject legList = firstTrip.getJSONObject("LegList");
            JSONArray legs = legList.getJSONArray("Leg");
            JSONObject leg;

            for(int i = 0; i < legs.length(); i++){
                leg = legs.getJSONObject(i);
                JSONObject stops = leg.getJSONObject("Stops");
                JSONArray halt = stops.getJSONArray("Stop");
                for(int j = 0; j < halt.length(); j++) {
                    JSONObject stop = halt.getJSONObject(j);
                    double lon = stop.getDouble("lon");
                    double lat = stop.getDouble("lat");
                    String name = stop.getString("name");
                    Log.e("BvgConnect", name);
                    Log.e("BvgConnect",String.valueOf(lat));
                    Log.e("BvgConnect", String.valueOf(lon));
                    route.add(new LatLng(lat,lon));
                }
            }
        } catch (JSONException e) {
            Log.e("BvgConnect",e.toString());
        }
        LatLng[] aRoute = route.toArray(new LatLng[route.size()]);
        return aRoute;
    }

}
