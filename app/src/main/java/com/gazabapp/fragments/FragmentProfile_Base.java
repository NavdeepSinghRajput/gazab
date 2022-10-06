package com.gazabapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.actionlisteners.ProfileData;
import com.gazabapp.adapter.PageAdapter_Profile;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.UserDetail;
import com.gazabapp.pojo.UserDetailData;
import com.gazabapp.serverapi.ApiClient;
import com.gazabapp.serverapi.RetroApis;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentProfile_Base extends Fragment implements ProfileData {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SharedPreferences sharedPref = null;
    private PageAdapter_Profile adapter_profile = null;
    private TextView_custom tvProfileName, tvFollowers, tvEditProfile;
    private ImageView ivEditProfile, ivBack;
    private CircleImageView ivProfilePhoto;
    private LinearLayout liAddBio;
    private TextView_custom tvAddBio;
    private UserDetailData userDetailData = null;
    String id;

    public FragmentProfile_Base() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_base, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);


        tvProfileName = (TextView_custom) rootView.findViewById(R.id.tvProfileName);
        tvAddBio = (TextView_custom) rootView.findViewById(R.id.tvAddBio);
        tvFollowers = (TextView_custom) rootView.findViewById(R.id.tvFollowers);
        tvEditProfile = (TextView_custom) rootView.findViewById(R.id.tvEditProfile);

        ivProfilePhoto = (CircleImageView) rootView.findViewById(R.id.ivProfilePhoto);
        ivEditProfile = (ImageView) rootView.findViewById(R.id.ivEditProfile);

        if (getArguments() != null) {
            id = getArguments().getString("id");
        }else{
            id =sharedPref.getString(Constants.shk_ID,"");
        }


        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        adapter_profile = new PageAdapter_Profile(getActivity().getSupportFragmentManager(), getActivity(), id);
        viewPager.setAdapter(adapter_profile);
//        viewPager.setOffscreenPageLimit(0);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        liAddBio = (LinearLayout) rootView.findViewById(R.id.liAddBio);
        liAddBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditProfile();
            }
        });
        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditProfile();
            }
        });
        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditProfile();
            }
        });

        getUserDetails();


        return rootView;
    }

    private void startEditProfile() {
        Fragment fragment = new FragmentProfile_Edit(this);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_frame_whole, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
    }


    private void getUserDetails() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<UserDetail> apiRequest = getApi.getUserDetail(""+sharedPref.getString(Constants.shk_ID,""),"Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""));
//        Call<UserDetail> apiRequest = ApiClient.getGazabApi().getUserDetail(id, "Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""));
        apiRequest.enqueue(new Callback<UserDetail>() {
            @Override
            public void onResponse(Call<UserDetail> call, Response<UserDetail> response) {
                if (response.body() != null && response.body().getData() != null) {
                    userDetailData = response.body().getData();

                   tvProfileName.setText("" + userDetailData.getDisplayName());
                    if (userDetailData.getBio()!= null && !userDetailData.getBio().equalsIgnoreCase("null") && userDetailData.getBio().length() > 0) {
                        tvAddBio.setText("" + userDetailData.getBio());
                    } else {
                        tvAddBio.setText("" + getActivity().getResources().getString(R.string.add_bio));
                    }

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(getContext().getResources().getDrawable(R.drawable.profile_pic_placeholder));
                    requestOptions.error(getContext().getResources().getDrawable(R.drawable.profile_pic_placeholder));
                    Glide.with(getActivity()).load(userDetailData.getPicture()).apply(requestOptions).into(ivProfilePhoto);

                    adapter_profile.setData(userDetailData);

                    adapter_profile.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<UserDetail> call, Throwable t) {

            }
        });
    }


    @Override
    public void onProfileChange(boolean IsBioChanged, boolean IsImageChanged) {
        if (IsBioChanged) {
            if (sharedPref.getString(Constants.shk_BIO, "") != null && !sharedPref.getString(Constants.shk_BIO, "").equalsIgnoreCase("null") && sharedPref.getString(Constants.shk_BIO, "").length() > 0) {
                tvAddBio.setText("" + sharedPref.getString(Constants.shk_BIO, ""));
            } else {
                tvAddBio.setText("" + getActivity().getResources().getString(R.string.add_bio));
            }
        }
        if (IsImageChanged) {
            Glide.with(getActivity()).load("" + sharedPref.getString(Constants.shk_PICTURE, "")).into(ivProfilePhoto);
        }
    }
}
