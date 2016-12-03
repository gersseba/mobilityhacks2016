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
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
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
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(new URI("https://graph.facebook.com/v2.8/search?access_token=EAACVsj4GyKgBAH5KQql48WAtoKrldvFPicIhi1aq9ZBInvk13SRd4aljzZCZAqwR4m5bZB9i9lOf8ZBRHsxvB4FFZBydw9arr5C74w1DBQdC1ZCXpHrjXJqCU3hg5vLuYzMgiINZBbs8t3ycntHRryutSsWaO1ZCTqrG7Po2k6akRSQZDZD&debug=all&format=json&method=get&pretty=0&q=hertha&suppress_http_code=1&type=event&since=2016-12-10&limit=1"));
        HttpResponse serverResponse = client.execute(httpGet);
        BasicResponseHandler handler = new BasicResponseHandler();

        String sResponse = handler.handleResponse(serverResponse);
        List<LatLng> route = new LinkedList<>();
        try {
            JSONObject parser = new JSONObject(sResponse);
            JSONArray events  = parser.getJSONArray("data");
            for(int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                JSONObject location = event.getJSONObject("place").getJSONObject("location");
                route.add(new LatLng(location.getDouble("latitude"), location.getDouble("longitude")));
            }
        } catch (JSONException e) {
            Log.e("BvgConnect",e.toString());
        }
        return route.toArray(new LatLng[route.size()]);
    }
}
