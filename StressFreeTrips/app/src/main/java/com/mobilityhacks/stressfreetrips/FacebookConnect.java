package com.mobilityhacks.stressfreetrips;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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

/**
 * Created by Owner on 03.12.2016.
 */

public class FacebookConnect {

    public static LatLng[] getEvents() throws IOException, URISyntaxException {
        return new LatLng[] {new LatLng(52.521918, 13.413215)};
    }
}
