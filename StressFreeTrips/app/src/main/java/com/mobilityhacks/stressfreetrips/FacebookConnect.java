package com.mobilityhacks.stressfreetrips;

import android.util.Log;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.*;

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

public class FacebookConnect  {

    public FacebookConnect () {
        FacebookSdk.sdkInitialize(SlideActivity.mainActivity);
        AppEventsLogger.activateApp(SlideActivity.mainActivity);
        AccessToken accessToken = new AccessToken(
                SlideActivity.mainActivity.getResources().getString(R.string.facebook_app_access_token),
                SlideActivity.mainActivity.getResources().getString(R.string.facebook_app_id),
                SlideActivity.mainActivity.getResources().getString(R.string.facebook_user_id), null, null, null, null, null);


        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/search",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Log.e("Test Facebook API ", response.toString());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("q", "Hertha");
        parameters.putString("type", "event");
        parameters.putString("since", "2016-12-09");
        parameters.putString("limit", "1");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static LatLng[] getEvents() throws IOException, URISyntaxException {
        return new LatLng[] {new LatLng(52.521918, 13.413215)};
    }
}
