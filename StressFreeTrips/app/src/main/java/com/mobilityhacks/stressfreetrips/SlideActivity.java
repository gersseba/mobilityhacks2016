package com.mobilityhacks.stressfreetrips;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.PopupWindowCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toolbar;

import com.google.android.gms.games.internal.PopupManager;
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

    protected SlidePageFragement mSlidePageFragement;

    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setActionBar(mToolbar);

        getMenuInflater().inflate(R.menu.toolbar, mToolbar.getMenu());

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mainActivity = this;
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
                    if(mSlidePageFragement == null) {
                        mSlidePageFragement = new SlidePageFragement();
                    }
                    return mSlidePageFragement;
                default:
                    if(mSlidePageFragement == null) {
                        mSlidePageFragement = new SlidePageFragement();
                    }
                    return mSlidePageFragement;
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
        View popupView = getLayoutInflater().inflate(R.layout.popup, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.showAsDropDown(mToolbar);
        Button cancel = (Button) popupView.findViewById(R.id.cancel);
        Button accept = (Button) popupView.findViewById(R.id.accept);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final LatLng[] stops = BvgConnect.getTrip(BvgConnect.WESTKREUZ,BvgConnect.SUEDKREUZ,BvgConnect.OSTKREUZ);
                            SlideActivity.mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SlideActivity.mMapFragment.drawPrimaryLinePath(stops, Color.GREEN);
                                    popupWindow.dismiss();
                                }
                            });
                        } catch (Exception e){
                            Log.e("bvg",e.toString());
                        }
                    }
                }).start();
            }
        });
        return true;
    }
}
