package com.gazabapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gazabapp.R;
import com.gazabapp.customeview.Button_custom;

public class FragmentSplashScreen extends Fragment
{

    private Button_custom btLogin;

    public FragmentSplashScreen()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_splash_screen, container, false);

        btLogin = (Button_custom) rootView.findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Fragment fragment = new FragmentLogin();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
                transaction.replace(R.id.content_frame, fragment);
                transaction.addToBackStack(fragment.getClass().getSimpleName());
                transaction.commit();
            }
        });


        return rootView;
    }

}
