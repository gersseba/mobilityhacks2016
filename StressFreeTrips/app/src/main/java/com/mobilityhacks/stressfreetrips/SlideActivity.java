package com.mobilityhacks.stressfreetrips;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Owner on 03.12.2016.
 */

public class SlideActivity extends FragmentActivity {

    public static Activity mainActivity;

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    public static MapsFragment mMapFragment;

    public static RouteFragment mRouteFragment;

    protected Toolbar mToolbar;

    protected boolean mPopupShown = false;

    protected int mState = 0; // 0 = normal, 1 = small congestion, 2 = large congestion

    protected boolean mShowNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng[] latLngs = FacebookConnect.getEvents();
                if(mRouteFragment == null) {
                    mRouteFragment = new RouteFragment();
                }
                for(LatLng latLng : latLngs) {
                    mRouteFragment.setCircle(latLng, 500, Color.RED);
                }
                setState(mState + 1);
            }
        });
        setActionBar(mToolbar);

        getMenuInflater().inflate(R.menu.toolbar, mToolbar.getMenu());

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mainActivity = this;
    }

    private void showNotification(boolean show) {
        mShowNotification = show;
        Drawable drawable;
        if(show) {
            drawable = getResources().getDrawable(R.drawable.ic_action_new_notification, null);
        } else {
            drawable = getResources().getDrawable(R.drawable.ic_action_no_notification, null);
        }
        // todo show notification
        mToolbar.getMenu().getItem(0).setIcon(drawable);
    }

    private void setState(int state) {
        this.mState = state < 3 ? state : 3;
        Drawable drawable;
        switch (mState) {
            case 0:
                drawable = getResources().getDrawable(R.drawable.ic_action_no_notification, null);
                break;
            case 1:
            case 2:
                drawable = getResources().getDrawable(R.drawable.ic_action_new_notification, null);
                break;
            case 3:
            default:
                drawable = getResources().getDrawable(R.drawable.ic_action_no_notification, null);
                break;
        }
        mToolbar.getMenu().getItem(0).setIcon(drawable);
    }

    private void changePopupForState(View popupView) {
        switch (mState) {
            default:
            case 0:
            case 1:
                break;
            case 2:
                ((ImageView) popupView.findViewById(R.id.bonus_points)).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_action_report_big_problem, null));
                ((TextView) popupView.findViewById(R.id.expected_delay)).setText(getResources().getString(R.string.big_delay));
                ((TextView) popupView.findViewById(R.id.extra_time)).setText(getResources().getString(R.string.additionalTimeBig));
                ((TextView) popupView.findViewById(R.id.extra_bonus_points)).setText(getResources().getString(R.string.additionalBonusBig));
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    if(mMapFragment == null) {
                        mMapFragment = new MapsFragment();
                    }
                    return mMapFragment;
                case 1:
                    if(mRouteFragment == null) {
                        mRouteFragment = new RouteFragment();
                    }
                    return mRouteFragment;
                default:
                    if(mRouteFragment == null) {
                        mRouteFragment = new RouteFragment();
                    }
                    return mRouteFragment;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


    /**
     * Handle creation of options menu.
     * @param menu the option menu handle.
     * @return state of parent handling
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onNotificationClick(MenuItem item) {
        if(mState > 0 && !mPopupShown) {
            mPopupShown = true;
            View popupView = getLayoutInflater().inflate(R.layout.popup, null);
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            changePopupForState(popupView);

            popupWindow.showAsDropDown(mToolbar);;
            Button cancel = (Button) popupView.findViewById(R.id.cancel);
            Button accept = (Button) popupView.findViewById(R.id.accept);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    mPopupShown = false;
                    showNotification(false);
                }
            });
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setState(3);
                    mPager.setCurrentItem(1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final LatLng[] oldstops = BvgConnect.getTrip(BvgConnect.WESTKREUZ,BvgConnect.ALEXANDERPLATZ,BvgConnect.OSTKREUZ);
                                final LatLng[] stops = BvgConnect.getTrip(BvgConnect.WESTKREUZ,BvgConnect.SUEDKREUZ,BvgConnect.OSTKREUZ);
                                SlideActivity.mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SlideActivity.mRouteFragment.drawLines(stops, oldstops);
                                        popupWindow.dismiss();
                                        ((TextView)mRouteFragment.getActivity().findViewById(R.id.travel_time)).setText(getResources().getString(R.string.new_duration));
                                        ((TextView)mRouteFragment.getActivity().findViewById(R.id.source_line)).setText(getResources().getString(R.string.new_line));
                                        ((TextView)mRouteFragment.getActivity().findViewById(R.id.dest_line)).setText(getResources().getString(R.string.new_line));
                                        ((TextView)mRouteFragment.getActivity().findViewById(R.id.ride_bonus_points)).setText(getResources().getString(R.string.new_bonus));
                                        mPopupShown = false;
                                        showNotification(false);
                                    }
                                });
                            } catch (Exception e){
                                Log.e("bvg",e.toString());
                            }
                        }
                    }).start();
                }
            });
        }

        return true;
    }
}
