package com.mobilityhacks.stressfreetrips;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

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

    private static final String[] STARTSTATIONS = new String[] {
            "Westhafen", "Westkreuz", "Wuhlheide"
    };

    private static final String[] ENDSTATIONS = new String[] {
            "Ostkreuz", "Ostbahnhof", "Ostsee"
    };

    private FacebookQueryThread mFacebookQueryThread = new FacebookQueryThread();

    private Random mRandom = new Random();


    {
        mRandom.setSeed(200);
    }

    protected Circle mNotificationCircle;

    private final static int[] colors = new int[] {Color.YELLOW | 0x88000000, Color.RED | 0x88000000, Color.argb(88, 255, 128,0)};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_page, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        new FacebookConnect();

        Button button = (Button)rootView.findViewById(R.id.later_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SlideActivity.mainActivity,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        LatLng[] latLngs = FacebookConnect.getEvents();
                        int color = 0xF0FF8800;
                        for(LatLng latLng : latLngs) {
                            SlideActivity.mRouteFragment.setCircle(latLng, 500, color);
                            SlideActivity.mMapFragment.setCircle(latLng, 500, color);
                        }
                        SlideActivity.mainActivity.setState();
                        SlideActivity.mPager.setCurrentItem(1);
                    }
                }, 2016, 12, 4).show();
            }
        });
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;


                LatLng berlin = new LatLng(52.519684, 13.382435);

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(berlin).zoom(11).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                for(int i = 0; i < 17; i ++) {
                    addCircle(randomPoint(), mRandom.nextInt(250) + 250, colors[mRandom.nextInt(3)]);
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SlideActivity.mainActivity, android.R.layout.simple_dropdown_item_1line, STARTSTATIONS);
        AutoCompleteTextView textView = (AutoCompleteTextView) rootView.findViewById(R.id.start_text);
        textView.setAdapter(adapter);


        ArrayAdapter<String> adapterEnd = new ArrayAdapter<String>(SlideActivity.mainActivity, android.R.layout.simple_dropdown_item_1line, ENDSTATIONS);
        AutoCompleteTextView textViewEnd = (AutoCompleteTextView) rootView.findViewById(R.id.end_text);
        textViewEnd.setAdapter(adapterEnd);



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

    public void setCircle(LatLng latLng, final int radius, int color) {
        if(mNotificationCircle != null) {
            mNotificationCircle.remove();
        }
        final Circle circle = googleMap.addCircle(new CircleOptions().center(latLng)
                .radius(radius)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(color));
        mNotificationCircle = circle;
        ValueAnimator vAnimator = new ValueAnimator();
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.REVERSE);  /* PULSE */
        vAnimator.setIntValues(radius/2, radius);
        vAnimator.setDuration(500);
        vAnimator.setEvaluator(new IntEvaluator());
        vAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                circle.setRadius(radius + animatedFraction * 100);
            }
        });
        vAnimator.start();
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
