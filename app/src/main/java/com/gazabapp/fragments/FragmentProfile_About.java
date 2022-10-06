package com.gazabapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.customeview.TextView_custom;

public class FragmentProfile_About extends Fragment
{

    private SharedPreferences sharedPref = null;

    private TextView_custom tvFollowers,tvPosts,tvComments,tvAbout;
    private ImageView ivEditAboutUs;
    public FragmentProfile_About()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_profile_about, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        tvFollowers = (TextView_custom) rootView.findViewById(R.id.tvFollowers);
        tvPosts = (TextView_custom) rootView.findViewById(R.id.tvPosts);
        tvComments = (TextView_custom) rootView.findViewById(R.id.tvComments);
        tvAbout = (TextView_custom) rootView.findViewById(R.id.tvAbout);

        ivEditAboutUs = (ImageView) rootView.findViewById(R.id.ivEditAboutUs);




        return rootView;
    }

}
