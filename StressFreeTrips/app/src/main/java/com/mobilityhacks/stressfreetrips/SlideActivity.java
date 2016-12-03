package com.mobilityhacks.stressfreetrips;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

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

    protected MapsFragment mMapFragment;

    protected SlidePageFragement mSlidePageFragement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mainActivity = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                BvgConnect bvgConnect = new BvgConnect();
                try {
                    bvgConnect.getTrip();
                } catch (Exception e){
                    Log.e("bvg",e.toString());
                }
            }
        }).start();

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
}
