package com.gazabapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.adapter.Adapter_MyFollowers;
import com.gazabapp.pojo.FollowUsers;
import com.gazabapp.pojo.FollowUsersData;
import com.gazabapp.serverapi.ApiClient;
import com.gazabapp.serverapi.RetroApis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentProfile_MyFollowers extends Fragment {
    private ListView listview;
    private SharedPreferences sharedPref = null;
    private Adapter_MyFollowers adapter = null;
    private ArrayList<FollowUsersData> arrayList = new ArrayList<>();
    private String sNextOffset = "0";
    private ProgressBar progress_bar;
    ImageView noFollowers;
    String id;

    public FragmentProfile_MyFollowers() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_my_followers, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        noFollowers = (ImageView) rootView.findViewById(R.id.noFollowers);
        progress_bar.setVisibility(View.GONE);
        listview = (ListView) rootView.findViewById(R.id.listview);
        adapter = new Adapter_MyFollowers(getActivity(), arrayList);
        listview.setAdapter(adapter);

//        View footer = getLayoutInflater().inflate(R.layout.footer_loader, listview, false);
//        listview.addFooterView(footer);

        id = getArguments().getString("id");


        listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && (listview.getLastVisiblePosition()) >= (adapter.getCount() - 1)) {
                    if (progress_bar.getVisibility() == View.GONE) {
                        loadMore();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        getMyFollowerList();
        return rootView;
    }

    private void getMyFollowerList() {
        progress_bar.setVisibility(View.VISIBLE);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<FollowUsers> apiRequest = getApi.getMyFollowers(""+sharedPref.getString(Constants.shk_ID,""),"Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),  ""+sNextOffset);
//        Call<FollowUsers> apiRequest = ApiClient.getGazabApi().getMyFollowers(id, "Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "" + sNextOffset);

        apiRequest.enqueue(new Callback<FollowUsers>() {
            @Override
            public void onResponse(Call<FollowUsers> call, Response<FollowUsers> response) {
                progress_bar.setVisibility(View.GONE);
                if (response.body() != null && response.body().getData() != null) {
                    if (response.body().getData().size() > 0) {
                        List<FollowUsersData> list = response.body().getData();
                        arrayList.addAll(list);
                        adapter.notifyDataSetChanged();
                        sNextOffset = "" + response.body().getNextOffset();
                        noFollowers.setVisibility(View.GONE);
                        listview.setVisibility(View.VISIBLE);
                    } else {
                        if (sNextOffset.equalsIgnoreCase("0")) {
                            noFollowers.setVisibility(View.VISIBLE);
                            listview.setVisibility(View.GONE);
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<FollowUsers> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
            }
        });


    }

    public void loadMore() {
        if (Integer.parseInt(sNextOffset) > 0) {
            getMyFollowerList();
        }
    }
}
