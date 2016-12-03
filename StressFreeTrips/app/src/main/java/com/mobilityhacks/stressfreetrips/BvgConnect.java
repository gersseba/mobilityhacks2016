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


    public static LatLng[] getTrip() throws IOException, URISyntaxException {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(new URI("http://demo.hafas.de/openapi/vbb-proxy/trip?originId=009110003&destId=009100042&format=json&accessId=BVG-VBB-Dezember&date=2016-12-12"));
        HttpResponse serverResponse = client.execute(httpGet);
        BasicResponseHandler handler = new BasicResponseHandler();

        String sResponse = handler.handleResponse(serverResponse);
        Log.e("BvgConnect",sResponse);
        List<LatLng> route = new LinkedList<LatLng>();
        try {
            JSONObject parser = new JSONObject(sResponse);
            JSONArray trip  = parser.getJSONArray("Trip");
            JSONObject firstTrip = trip.getJSONObject(0);
            JSONObject legList = firstTrip.getJSONObject("LegList");
            JSONArray legs = legList.getJSONArray("Leg");
            JSONObject leg;

            for(int i = 0; i < legs.length(); i++){
                leg = legs.getJSONObject(i);
                JSONObject origin = leg.getJSONObject("Origin");
                String originName = origin.getString("name");
                double originLong = origin.getDouble("lon");
                double originLat = origin.getDouble("lat");
                Log.e("BvgConnect", originName);
                Log.e("BvgConnect",String.valueOf(originLat));
                Log.e("BvgConnect", String.valueOf(originLong));
                route.add(new LatLng(originLat,originLong));
                JSONObject destination = leg.getJSONObject("Destination");
                String destinationName = destination.getString("name");
                double destinationLong = destination.getDouble("lon");
                double destinationLat = destination.getDouble("lat");
                Log.e("BvgConnect", destinationName);
                Log.e("BvgConnect",String.valueOf(destinationLat));
                Log.e("BvgConnect", String.valueOf(destinationLong));
                route.add(new LatLng(destinationLat,destinationLong));
            }
        } catch (JSONException e) {
            Log.e("BvgConnect",e.toString());
        }
        LatLng[] aRoute = route.toArray(new LatLng[route.size()]);
        return aRoute;
    }
}
