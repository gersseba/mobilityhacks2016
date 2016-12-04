package com.mobilityhacks.stressfreetrips;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by Owner on 03.12.2016.
 */

public class MapsFragment extends Fragment {

    protected final static double BERLIN_BR_LAT = 52.473901;
    protected final static double BERLIN_BR_LNG = 13.504213;

    MapView mMapView;
    private GoogleMap googleMap;

    private ArrayList<Circle> circles = new ArrayList<>();
    private ArrayList<Polyline> lines = new ArrayList<>();

    private FacebookQueryThread mFacebookQueryThread = new FacebookQueryThread();

    private Random mRandom = new Random();

    {
        mRandom.setSeed(200);
    }

    private final static int[] colors = new int[] {Color.YELLOW | 0x88000000, Color.RED | 0x88000000, Color.argb(88, 255, 128,0)};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_page, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        new FacebookConnect();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;


                LatLng berlin = new LatLng(52.521918, 13.413215);

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(berlin).zoom(11).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                for(int i = 0; i < 17; i ++) {
                    addCircle(randomPoint(), mRandom.nextInt(250) + 250, colors[mRandom.nextInt(3)]);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final LatLng[] stops = BvgConnect.getTrip(BvgConnect.WESTKREUZ,BvgConnect.ALEXANDERPLATZ,BvgConnect.OSTKREUZ);
                            SlideActivity.mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SlideActivity.mRouteFragment.drawLines(stops, new LatLng[0]);
                                }
                            });
                        } catch (Exception e){
                            Log.e("bvg",e.toString());
                        }
                    }
                }).start();
            }
        });


//        mFacebookQueryThread.start();

        return rootView;
    }

    public LatLng randomPoint() {
        return new LatLng(BERLIN_BR_LAT + mRandom.nextInt(10)/100.0, BERLIN_BR_LNG - mRandom.nextInt(25)/100.0);
    }

    public void addCircle(LatLng latLng, int radius, int color) {
        color |= 0xFF000000;
        color &= 0x88FFFFFF;
        circles.add(googleMap.addCircle(new CircleOptions().center(latLng)
                .radius(radius)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(color)));
    }

    public void clearCircles() {
        Iterator<Circle> circleIterator = circles.iterator();
        while(circleIterator.hasNext()) {
            circleIterator.next().remove();
            circleIterator.remove();
        }
    }

    public void clearLines() {
        Iterator<Polyline> lineIterator = lines.iterator();
        while(lineIterator.hasNext()) {
            lineIterator.next().remove();
            lineIterator.remove();
        }
    }

    public void drawPrimaryLinePath( LatLng[] locsToDraw, int color )    {
        if ( locsToDraw.length < 2 ) {
            return;
        }

        PolylineOptions options = new PolylineOptions();

        options.color( color );
        options.width( 5 );
        options.visible( true );

        for ( LatLng latLng : locsToDraw )
        {
            options.add( latLng );
        }

        lines.add(googleMap.addPolyline( options ));

    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mFacebookQueryThread.stop = true;
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
