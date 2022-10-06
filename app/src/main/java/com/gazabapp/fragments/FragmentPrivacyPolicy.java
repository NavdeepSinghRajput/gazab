package com.gazabapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.pojo.PostDetails;
import com.gazabapp.pojo.PrivacyPolicy;
import com.gazabapp.pojo.PrivacyPolicyData;
import com.gazabapp.serverapi.RetroApis;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentPrivacyPolicy extends Fragment
{


    private SharedPreferences sharedPref = null;
    private WebView webview;
    public FragmentPrivacyPolicy()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);


        ImageView ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        webview = (WebView) rootView.findViewById(R.id.webview);

        getPrivacyPolicy();
        return rootView;
    }

    private void getPrivacyPolicy()
    {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<PrivacyPolicy> apiRequest = getApi.getPrivacyPolicy();
        apiRequest.enqueue(new Callback<PrivacyPolicy>()
        {
            @Override
            public void onResponse(Call<PrivacyPolicy> call, Response<PrivacyPolicy> response)
            {
                if(response.body() != null && response.body().getData() != null)
                {
                    PrivacyPolicyData data  = response.body().getData();
//                    webview.setBackgroundColor(Color.TRANSPARENT);
                    webview.getSettings().setJavaScriptEnabled(true);
                    webview.loadData(data.getContent(),"text/html", "UTF-8");
                }
            }
            @Override
            public void onFailure(Call<PrivacyPolicy> call, Throwable t)
            {

            }
        });
    }

}
