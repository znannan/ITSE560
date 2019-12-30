package com.example.android.tourguide;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class CategoryAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public CategoryAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChapelFragment();
            case 1:
                return new AcademyBuildingFragment();
            case 2:
                return new AdminBuildingFragment();
            case 3:
                return new CafeteriaFragment();
            default:
                return null;
        }
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.category_chapel);
            case 1:
                return mContext.getString(R.string.category_admin);
            case 2:
                return mContext.getString(R.string.category_academy);
            case 3:
                return mContext.getString(R.string.category_cafeteria);
            case 4:
                return mContext.getString(R.string.category_play);
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return 4;
    }
}
