package com.anthonycaliendo.twitterclient.ui.timeline;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.anthonycaliendo.twitterclient.R;

import static com.anthonycaliendo.twitterclient.Instrumentation.debug;

/**
 * Extended {@link FragmentPagerAdapter} which handles the {@link TimelineActivity}.
 */
public class TimelineFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int[] TAB_TITLE_RESOURCE_IDS = new int[]{ R.string.timeline_home_title, R.string.timeline_mentions_title};

    private final Context context;

    public TimelineFragmentPagerAdapter(final FragmentManager fragmentManager, final Context context) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public int getCount() {
        return TAB_TITLE_RESOURCE_IDS.length;
    }

    @Override
    public Fragment getItem(int position) {
        debug(this, "method=getItem position=" + position);
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new MentionsFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position > TAB_TITLE_RESOURCE_IDS.length) {
            return null;
        }
        return context.getString(TAB_TITLE_RESOURCE_IDS[position]);
    }

}
