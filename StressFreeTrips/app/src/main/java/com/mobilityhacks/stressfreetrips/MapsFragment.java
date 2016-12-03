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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Owner on 03.12.2016.
 */

public class MapsFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;

    private final static LatLng ALEX = new LatLng(52.521918, 13.413215);
    private final static LatLng WOANDERS = new LatLng(52.5, 13.4);
    private final static LatLng NOCHWOANDERS = new LatLng(52.6, 13.43);



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_page, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;


                LatLng berlin = new LatLng(52.52, 13.41);

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(berlin).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                addCircle(52.521918, 13.413215, 200, Color.RED); // alexanderplatz
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final LatLng[] stops = BvgConnect.getTrip(BvgConnect.WESTKREUZ,BvgConnect.ALEXANDERPLATZ,BvgConnect.OSTKREUZ);
                            SlideActivity.mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    drawPrimaryLinePath(stops, Color.GREEN);
                                }
                            });
                        } catch (Exception e){
                            Log.e("bvg",e.toString());
                        }
                    }
                }).start();
            }
        });

        return rootView;
    }

    private void addCircle(double lat, double lng, int radius, int color) {
        color |= 0xFF000000;
        color &= 0x88FFFFFF;
        googleMap.addCircle(new CircleOptions().center(new LatLng(lat, lng))
                .radius(radius)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(color));
    }

    private void drawPrimaryLinePath( LatLng[] locsToDraw, int color )    {
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

        googleMap.addPolyline( options );

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
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
