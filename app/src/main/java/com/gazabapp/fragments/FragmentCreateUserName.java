package com.gazabapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gazabapp.ActivityHome;
import com.gazabapp.ActivityRegistration;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.customeview.Button_custom;
import com.gazabapp.customeview.EditText_custom;
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

public class FragmentCreateUserName extends Fragment
{
    private EditText_custom etUserName;
    private TextView_custom tvStatus;
    private Button_custom btNext,btBack;
    private SharedPreferences sharedPref = null;
    private ImageView ivNext;
    public FragmentCreateUserName()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_create_user_name, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        etUserName = (EditText_custom) rootView.findViewById(R.id.etUserName);

        tvStatus = (TextView_custom) rootView.findViewById(R.id.tvStatus);
        tvStatus.setVisibility(View.INVISIBLE);


        ivNext = (ImageView) rootView.findViewById(R.id.ivNext);
        ivNext.setEnabled(false);
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });

        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.length() >= 2)
                {
                    tvStatus.setVisibility(View.INVISIBLE);
                    ivNext.setEnabled(true);
                }
                else if(s.length() < 2)
                {
                    ivNext.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

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
            }
        });

        return rootView;
    }

    private void putData(String sData)
    {
        ((ActivityRegistration)getActivity()).showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", sData);

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

                    Fragment fragment =  new FragmentChooseCommunity();

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
                    transaction.replace(R.id.content_frame, fragment);
                    transaction.addToBackStack(fragment.getClass().getSimpleName());
                    transaction.commit();

                }
                else
                {
                    tvStatus.setText(""+response.body().getMessage());
                    tvStatus.setVisibility(View.VISIBLE);

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
        if(etUserName.getText().toString().trim().length() > 3)
        {
            putData(etUserName.getText().toString());
        }
        else
        {
            tvStatus.setText(getResources().getString(R.string.enter_valid_username));
        }
    }
}
