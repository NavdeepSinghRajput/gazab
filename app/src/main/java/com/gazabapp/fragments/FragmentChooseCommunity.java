package com.gazabapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.gazabapp.ActivityHome;
import com.gazabapp.ActivityRegistration;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.adapter.Adapter_ChooseCommunity;
import com.gazabapp.customeview.Button_custom;
import com.gazabapp.pojo.CommunityList;
import com.gazabapp.pojo.CommunityListData;
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

public class FragmentChooseCommunity extends Fragment
{
    private int iPage = 1,iCount = 24;
    private ArrayList<CommunityListData> arrayList = new ArrayList<>();
    private Button_custom btNext,btBack;
    private GridView gridview;
    private Adapter_ChooseCommunity gridViewAdapter = null;
    private SharedPreferences sharedPref = null;
    private ProgressBar progress_bar;
    public FragmentChooseCommunity()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_choose_community, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        btBack = (Button_custom) rootView.findViewById(R.id.btBack);
        btNext = (Button_custom) rootView.findViewById(R.id.btNext);
        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

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
                getActivity().startActivity(new Intent(getActivity(), ActivityHome.class));
                getActivity().finish();

            }
        });



        gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridViewAdapter = new Adapter_ChooseCommunity(getActivity(),arrayList);
        gridview.setAdapter(gridViewAdapter);



        gridview.setOnScrollListener(new AbsListView.OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && (gridview.getLastVisiblePosition() ) >= (gridViewAdapter.getCount() - 1))
                {
                    loadMore();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        getCommunityList();

        return rootView;
    }

    private void getCommunityList()
    {
        progress_bar.setVisibility(View.VISIBLE);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<CommunityList> apiRequest = getApi.getCommunityList("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),"" + iPage,""+iCount);
        apiRequest.enqueue(new Callback<CommunityList>()
        {
            @Override
            public void onResponse(Call<CommunityList> call, Response<CommunityList> response)
            {
                if(response.body() != null && response.body().getData() != null)
                {
                    List<CommunityListData> list = response.body().getData();
                    arrayList.addAll(list);
                    gridViewAdapter.notifyDataSetChanged();
                }
                progress_bar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<CommunityList> call, Throwable t)
            {
                progress_bar.setVisibility(View.GONE);
            }
        });


    }

    public void loadMore()
    {
        iPage++;
        getCommunityList();
    }
}
