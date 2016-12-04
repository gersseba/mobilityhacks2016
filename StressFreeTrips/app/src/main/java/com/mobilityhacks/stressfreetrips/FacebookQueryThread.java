package com.mobilityhacks.stressfreetrips;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Owner on 03.12.2016.
 */

public class FacebookQueryThread extends Thread {

    public boolean stop;

    public void run() {
        while(!stop) {
            try {
                final LatLng[] stops;
                stops = FacebookConnect.getEvents();
                SlideActivity.mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        SlideActivity.mMapFragment.clearCircles();
                        for (int i = 0; i < stops.length; i++) {
                            SlideActivity.mMapFragment.addCircle(stops[i], 200, Color.RED);
                        }
                    }
                });
                Thread.sleep(2000);
            } catch (Exception e) {
                Log.e("FacebookThread", e.getMessage());
            }
        }
    }
}
