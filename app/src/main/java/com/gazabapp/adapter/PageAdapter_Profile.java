package com.gazabapp.adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gazabapp.R;
import com.gazabapp.fragments.FragmentProfile_About;
import com.gazabapp.fragments.FragmentProfile_Comments;
import com.gazabapp.fragments.FragmentProfile_Communities;
import com.gazabapp.fragments.FragmentProfile_Following;
import com.gazabapp.fragments.FragmentProfile_MyFollowers;
import com.gazabapp.fragments.FragmentProfile_Posts;
import com.gazabapp.pojo.UserDetailData;

public class PageAdapter_Profile extends FragmentStatePagerAdapter {
    private UserDetailData userDetailData = null;
    private Context context;
    private String id;

    public PageAdapter_Profile(FragmentManager fm, Context context, String id) {
        super(fm);
        this.id = id;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
         switch (position) {

            case 0:
                fragment = new FragmentProfile_Posts();
                break;
            case 1:
                fragment = new FragmentProfile_Communities();
                break;
            case 2:
                fragment = new FragmentProfile_MyFollowers();
                break;
            case 3:
                fragment = new FragmentProfile_Following();

                break;
            case 4:
                fragment = new FragmentProfile_Comments();
                break;
            case 5:
                fragment = new FragmentProfile_About();
                break;
        }
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case 0:
                title = "" + context.getResources().getString(R.string.posts);
                break;
            case 1:
                title = "" + context.getResources().getString(R.string.communities);
                break;
            case 2:
                if (userDetailData != null) {
                    title = "" + context.getResources().getString(R.string.followers) + "  " + userDetailData.getTotalFollowers();
                } else {
                    title = "" + context.getResources().getString(R.string.followers);
                }

                break;
            case 3:
                title = "" + context.getResources().getString(R.string.following);
                break;
            case 4:
                title = "" + context.getResources().getString(R.string.comments);
                break;
            case 5:
                title = "" + context.getResources().getString(R.string.about);
                break;
        }

        return title;
    }

    public void setData(UserDetailData userDetailData) {
        this.userDetailData = userDetailData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
