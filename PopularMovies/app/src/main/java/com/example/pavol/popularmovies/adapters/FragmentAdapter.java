package com.example.pavol.popularmovies.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.pavol.popularmovies.fragments.FragmentFavouriteMovies;
import com.example.pavol.popularmovies.fragments.FragmentPopularity;
import com.example.pavol.popularmovies.fragments.FragmentRating;

/**
 * Created by pavol on 17/03/2018.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public FragmentAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FragmentPopularity();
        } else if (position == 1) {
            return new FragmentRating();
        } else {
            return new FragmentFavouriteMovies();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Most popular movies";
        } else if (position == 1) {
            return "Top rated movies";
        } else {
            return "Favourite Movies";
        }
    }
}
