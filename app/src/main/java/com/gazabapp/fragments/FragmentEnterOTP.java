package com.gazabapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gazabapp.ActivityHome;
import com.gazabapp.ActivityRegistration;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.customeview.Button_custom;
import com.gazabapp.customeview.EditText_custom;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.Login;
import com.gazabapp.pojo.LoginData;
import com.gazabapp.pojo.ResendOTP;
import com.gazabapp.pojo.VerifyOTP;
import com.gazabapp.pojo.VerifyOTPData;
import com.gazabapp.serverapi.ApiClient;
import com.gazabapp.serverapi.RetroApis;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentEnterOTP extends Fragment
{
    private EditText_custom et1,et2,et3,et4;
    private TextView_custom tvStatus;
    private Button_custom btNext,btBack,btResendOTP;

    public static LoginData loginData = null;
    public static String MOBILE_NO = "";
    private SharedPreferences sharedPref = null;
    private ImageView ivNext;
    public FragmentEnterOTP()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_enter_otp, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        et1 = (EditText_custom) rootView.findViewById(R.id.et1);
        et2 = (EditText_custom) rootView.findViewById(R.id.et2);
        et3 = (EditText_custom) rootView.findViewById(R.id.et3);
        et4 = (EditText_custom) rootView.findViewById(R.id.et4);

        et1.requestFocus();

        ivNext = (ImageView) rootView.findViewById(R.id.ivNext);
        ivNext.setEnabled(false);
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });


        tvStatus = (TextView_custom) rootView.findViewById(R.id.tvStatus);
        tvStatus.setText("");

        et1.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){}

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(editable.toString().length() == 1)
                {
                    et2.requestFocus();
                }
                else
                {
                    ivNext.setEnabled(false);
                }
            }
        });
        et2.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){}

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(editable.toString().length() == 1)
                {
                    et3.requestFocus();
                }
                else if(editable.toString().length() == 0)
                {
                    et1.requestFocus();
                    ivNext.setEnabled(false);
                }
            }
        });
        et3.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){}

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(editable.toString().length() == 1)
                {
                    et4.requestFocus();
                    ivNext.setEnabled(false);
                }
                else if(editable.toString().length() == 0)
                {
                    et2.requestFocus();
                }
            }
        });
        et4.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){}

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(editable.toString().length() == 0)
                {
                    et3.requestFocus();
                    ivNext.setEnabled(false);
                }
                else if(editable.toString().length() == 1)
                {
                    ivNext.setEnabled(true);
                }

            }
        });

        et4.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
        @Override
            public boolean onEditorAction(TextView v, int actionId,KeyEvent event)
            {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    done();
                    handled = true;
                }
                return handled;
            }
        });

        btBack = (Button_custom) rootView.findViewById(R.id.btBack);
        btNext = (Button_custom) rootView.findViewById(R.id.btNext);
        btResendOTP = (Button_custom) rootView.findViewById(R.id.btResendOTP);

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

        btResendOTP.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                tvStatus.setText("");
                et1.setText("");
                et2.setText("");
                et3.setText("");
                et4.setText("");
                resendOTP(loginData.getMobile());
            }
        });


        return rootView;
    }

    private void done()
    {
        if(et1.getText().length() == 1 && et2.getText().length() == 1 && et3.getText().length() == 1 && et4.getText().length() == 1)
            verifyOTP(et1.getText()+""+et2.getText()+""+et3.getText()+et4.getText());
        else
            tvStatus.setText(getResources().getString(R.string.enter_valid_otp));
    }

    private void verifyOTP(String sOTP)
    {
        ((ActivityRegistration)getActivity()).showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("otp", sOTP);
        jsonObject.addProperty("device_id", loginData.getId());
        jsonObject.addProperty("device_type", "android");


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<VerifyOTP> apiRequest = ApiClient.getGazabApi().verifyOTP("Bearer "+loginData.getAccessToken(),"application/json",jsonObject);
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



                    sharedPref.edit().putString(Constants.shk_ACCESS_TOKEN,""+verifyOTPData.getAccessToken()).commit();
                    sharedPref.edit().putString(Constants.shk_ID,""+verifyOTPData.getId()).commit();
                    sharedPref.edit().putString(Constants.shk_MOBILE_NO,""+verifyOTPData.getMobile()).commit();
                    sharedPref.edit().putString(Constants.shk_DISPLAY_NAME,""+verifyOTPData.getDisplayName()).commit();
                    sharedPref.edit().putString(Constants.shk_EMAIL,""+verifyOTPData.getEmail()).commit();
                    sharedPref.edit().putString(Constants.shk_NAME,""+verifyOTPData.getName()).commit();
                    sharedPref.edit().putString(Constants.shk_PICTURE,""+verifyOTPData.getPicture()).commit();
                    sharedPref.edit().putString(Constants.shk_AGE_GROUP,""+verifyOTPData.getAgeGroup()).commit();
                    sharedPref.edit().putString(Constants.shk_BIO,""+verifyOTPData.getBio()).commit();

                    Fragment fragment = null;
                    if(verifyOTPData.getAgeGroup().length() < 1)
                    {
                        fragment = new FragmentBirthdaySelect();
                    }
                    else  if(verifyOTPData.getGender().length() < 1)
                    {
                        fragment = new FragmentGenderSelect();
                    }
                    else  if(verifyOTPData.getName().length() < 1)
                    {
                        fragment = new FragmentCreateUserName();
                    }
                    else
                    {
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), ActivityHome.class));
                    }

                    if(fragment != null)
                    {
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
                        transaction.replace(R.id.content_frame, fragment);
                        transaction.addToBackStack(fragment.getClass().getSimpleName());
                        transaction.commit();
                    }

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

    private void resendOTP(String sMobileNo)
    {
        ((ActivityRegistration)getActivity()).showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile", sMobileNo);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<ResendOTP> apiRequest = getApi.resendOTP(jsonObject);
        apiRequest.enqueue(new Callback<ResendOTP>()
        {
            @Override
            public void onResponse(Call<ResendOTP> call, Response<ResendOTP> response)
            {
                ((ActivityRegistration)getActivity()).hideProgress();
                if(response.body() == null || response.body().getStatus() == null)
                {
                    ((ActivityRegistration)getActivity()).showToast(""+getResources().getString(R.string.communication_error));
                    return;
                }
                if(response.body().getStatus().equalsIgnoreCase("success"))
                {
                    tvStatus.setText("");
                }
                else
                {

                }
            }
            @Override
            public void onFailure(Call<ResendOTP> call, Throwable t)
            {
                ((ActivityRegistration)getActivity()).hideProgress();
            }
        });
    }
}
