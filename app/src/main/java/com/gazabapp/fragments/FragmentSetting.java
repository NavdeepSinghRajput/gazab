package com.gazabapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.customeview.TextView_custom;

public class FragmentSetting extends Fragment
{

    private SwitchCompat swNotification,swDarkMode;
    private SharedPreferences sharedPref = null;

    public FragmentSetting()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        ((ActivityHome) getActivity()).setCheckedPages(5);

        TextView_custom tvUserName = ((TextView_custom) rootView.findViewById(R.id.tvUserName));
        tvUserName.setText(sharedPref.getString(Constants.shk_NAME,""));

        String sVersionCode = "";
        try
        {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            sVersionCode = ""+pInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        TextView_custom tvVersionCode = ((TextView_custom) rootView.findViewById(R.id.tvVersionCode));
        tvVersionCode.setText(""+sVersionCode);

        TextView_custom tvPrivacyPolicy = (TextView_custom) rootView.findViewById(R.id.tvPrivacyPolicy);
        tvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Fragment fragment = new FragmentPrivacyPolicy();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.content_frame_whole, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
            }
        });

        TextView_custom tvUserArrangement = (TextView_custom) rootView.findViewById(R.id.tvUserArrangement);
        tvUserArrangement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Fragment fragment = new FragmentUserArrangement();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.content_frame_whole, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();


            }
        });
        swNotification = (SwitchCompat) rootView.findViewById(R.id.swNotification);
        swDarkMode = (SwitchCompat) rootView.findViewById(R.id.swDarkMode);


        swNotification.setChecked(sharedPref.getBoolean(Constants.shk_NOTIFICATION,true));
        swDarkMode.setChecked(sharedPref.getBoolean(Constants.shk_DARK_THEME,false));


        tvUserName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Fragment fragment = new FragmentProfile_Base();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame_whole, fragment);
                transaction.addToBackStack(fragment.getClass().getSimpleName());
                transaction.commit();
            }
        });

        swNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                sharedPref.edit().putBoolean(Constants.shk_NOTIFICATION,isChecked).commit();
            }




        });
        swDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                sharedPref.edit().putBoolean(Constants.shk_DARK_THEME,isChecked).commit();
                ((ActivityHome) getActivity()).setTheme(isChecked);
            }



        });


        TextView_custom tvLogout = (TextView_custom) rootView.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((ActivityHome)getActivity()).logout();
            }
        });


        return rootView;
    }

}
