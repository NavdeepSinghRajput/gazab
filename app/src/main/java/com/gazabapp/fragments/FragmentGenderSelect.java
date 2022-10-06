package com.gazabapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gazabapp.ActivityHome;
import com.gazabapp.ActivityRegistration;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.customeview.Button_custom;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.VerifyOTP;
import com.gazabapp.pojo.VerifyOTPData;
import com.gazabapp.serverapi.RetroApis;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentGenderSelect extends Fragment
{
    private TextView_custom tvStatus;
    private RadioButton rbtFemale,rbtMale,rbtOther;
    private TextView_custom tvFemale,tvMale,tvOther;
    private Button_custom btNext,btBack;
    private SharedPreferences sharedPref = null;
    private ImageView ivNext;
    public FragmentGenderSelect()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_gender_select, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        rbtFemale = (RadioButton) rootView.findViewById(R.id.rbtFemale);
        rbtMale = (RadioButton) rootView.findViewById(R.id.rbtMale);
        rbtOther = (RadioButton) rootView.findViewById(R.id.rbtOther);

        tvStatus = (TextView_custom) rootView.findViewById(R.id.tvStatus);
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText("");

        tvFemale = (TextView_custom) rootView.findViewById(R.id.tvFemale);
        tvMale = (TextView_custom) rootView.findViewById(R.id.tvMale);
        tvOther = (TextView_custom) rootView.findViewById(R.id.tvOther);

        ivNext = (ImageView) rootView.findViewById(R.id.ivNext);
        ivNext.setEnabled(false);
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });

        rbtFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean flag)
            {
                ivNext.setEnabled(true);
                if(flag)
                    tvFemale.setTextColor(((ActivityRegistration)getActivity()).getAttributeColor(R.attr.gender_title_text));
                else
                    tvFemale.setTextColor(((ActivityRegistration)getActivity()).getAttributeColor(R.attr.gender_text_unselect));
            }
        });

        rbtMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean flag)
            {
                ivNext.setEnabled(true);
                if(flag)
                    tvMale.setTextColor(((ActivityRegistration)getActivity()).getAttributeColor(R.attr.gender_title_text));
                else
                    tvMale.setTextColor(((ActivityRegistration)getActivity()).getAttributeColor(R.attr.gender_text_unselect));
            }
        });

        rbtOther.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean flag)
            {
                ivNext.setEnabled(true);
                if(flag)
                    tvOther.setTextColor(((ActivityRegistration)getActivity()).getAttributeColor(R.attr.gender_title_text));
                else
                    tvOther.setTextColor(((ActivityRegistration)getActivity()).getAttributeColor(R.attr.gender_text_unselect));
            }
        });
        btBack = (Button_custom) rootView.findViewById(R.id.btBack);
        btNext = (Button_custom) rootView.findViewById(R.id.btNext);
        btBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getActivity().onBackPressed();
            }
        });
        btNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                done();
            }
        });

        return rootView;
    }


    private void putData(String sData)
    {
        ((ActivityRegistration)getActivity()).showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gender", sData);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);

        Call<VerifyOTP> apiRequest = getApi.putUserDetail(sharedPref.getString(Constants.shk_ID,""),"Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),"application/json",jsonObject);
        apiRequest.enqueue(new Callback<VerifyOTP>()
        {
            @Override
            public void onResponse(Call<VerifyOTP> call, Response<VerifyOTP> response)
            {
                ((ActivityRegistration)getActivity()).hideProgress();
                if(response.body() == null || response.body().getStatus() == null)
                {
                    ((ActivityRegistration)getActivity()).showToast(""+getResources().getString(R.string.communication_error));
                    return;
                }

                if(response.body().getStatus().equalsIgnoreCase("success"))
                {
                    VerifyOTPData verifyOTPData = response.body().getVerifyOTPData();


                    sharedPref.edit().putString(Constants.shk_DISPLAY_NAME,""+verifyOTPData.getDisplayName()).commit();
                    sharedPref.edit().putString(Constants.shk_EMAIL,""+verifyOTPData.getEmail()).commit();
                    sharedPref.edit().putString(Constants.shk_NAME,""+verifyOTPData.getName()).commit();
                    sharedPref.edit().putString(Constants.shk_PICTURE,""+verifyOTPData.getPicture()).commit();
                    sharedPref.edit().putString(Constants.shk_AGE_GROUP,""+verifyOTPData.getAgeGroup()).commit();
                    sharedPref.edit().putString(Constants.shk_BIO,""+verifyOTPData.getBio()).commit();


                    Fragment fragment = null;
                    if(verifyOTPData.getName().length() < 1)
                    {
                        fragment = new FragmentCreateUserName();
                    }
                    else
                    {
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), ActivityHome.class));
                    }

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
                    transaction.replace(R.id.content_frame, fragment);
                    transaction.addToBackStack(fragment.getClass().getSimpleName());
                    transaction.commit();

                }
                else
                {
                    tvStatus.setText(""+response.body().getMessage());

                }
            }
            @Override
            public void onFailure(Call<VerifyOTP> call, Throwable t)
            {
                ((ActivityRegistration)getActivity()).hideProgress();
            }
        });
    }

    private void done()
    {
        if(rbtFemale.isChecked())
            putData("F");
        else if(rbtMale.isChecked())
            putData("M");
        else if(rbtOther.isChecked())
            putData("O");
        else
            tvStatus.setText(getResources().getString(R.string.select_gender));
    }
}
