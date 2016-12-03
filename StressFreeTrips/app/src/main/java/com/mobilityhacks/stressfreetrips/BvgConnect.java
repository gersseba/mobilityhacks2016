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
        HttpGet httpGet = new HttpGet(new URI("http://demo.hafas.de/openapi/vbb-proxy/trip?originId=009110003&passlist=1&destId=009100042&format=json&accessId=BVG-VBB-Dezember&date=2016-12-12"));
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
