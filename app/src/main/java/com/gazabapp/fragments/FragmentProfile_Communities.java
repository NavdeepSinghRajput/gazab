package com.gazabapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.actionlisteners.CommunityFollow;
import com.gazabapp.adapter.Adapter_MyCommunity;
import com.gazabapp.pojo.CommunityListData;
import com.gazabapp.pojo.CommunityMyList;
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

import static com.gazabapp.Constants.PRINT;

public class FragmentProfile_Communities extends Fragment implements CommunityFollow {

    private ArrayList<CommunityListData> arrayList = new ArrayList<>();
    private GridView gridview;
    private Adapter_MyCommunity gridViewAdapter = null;
    private SharedPreferences sharedPref = null;
    private String sNextOffset = "0";
    private boolean API_CALLING = false;
    private ProgressBar progress_bar;
    ImageView noGroup;
    String id;

    public FragmentProfile_Communities() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_communities, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        noGroup = (ImageView) rootView.findViewById(R.id.noGroup);
        progress_bar.setVisibility(View.GONE);

        arrayList.clear();
        gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridViewAdapter = new Adapter_MyCommunity(getActivity(), arrayList);
        gridview.setAdapter(gridViewAdapter);


        API_CALLING = false;

        id = getArguments().getString("id");

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ActivityHome) getActivity()).startCommunityDetails(arrayList.get(position).getId(), FragmentProfile_Communities.this);
            }
        });

        gridview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && (gridview.getLastVisiblePosition()) >= (gridViewAdapter.getCount() - 1)) {
                    loadMore();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        if (!API_CALLING) {
            getCommunityList();
        }

        ((ActivityHome) getActivity()).setCommunityFollow_PROFILE(this);
        return rootView;
    }

    private void getCommunityList() {
        API_CALLING = true;

        progress_bar.setVisibility(View.VISIBLE);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<CommunityMyList> apiRequest = getApi.getMyProfileCommunity(id,"Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""), ""+ sNextOffset);
//        Call<CommunityMyList> apiRequest = ApiClient.getGazabApi().getMyProfileCommunity(id, "Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "" + sNextOffset);

        PRINT("" + apiRequest.request().url());
        apiRequest.enqueue(new Callback<CommunityMyList>() {
            @Override
            public void onResponse(Call<CommunityMyList> call, Response<CommunityMyList> response) {
                progress_bar.setVisibility(View.GONE);
                if (response.body() != null && response.body().getData() != null) {
                    if (response.body().getData().size() > 0) {
                        noGroup.setVisibility(View.GONE);
                        gridview.setVisibility(View.VISIBLE);
                        List<CommunityListData> list = response.body().getData();
                        arrayList.addAll(list);
                        gridViewAdapter.notifyDataSetChanged();
                        sNextOffset = "" + response.body().getNextOffset();
                    } else {
                        if (sNextOffset.equalsIgnoreCase("0")) {
                            noGroup.setVisibility(View.VISIBLE);
                            gridview.setVisibility(View.GONE);
                        }
                    }

                }
                API_CALLING = false;
            }

            @Override
            public void onFailure(Call<CommunityMyList> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
                API_CALLING = false;
            }
        });


    }

    public void loadMore() {
        if (Integer.parseInt(sNextOffset) > 0 && !API_CALLING) {
            getCommunityList();
        }

    }

    @Override
    public void onCommunityFollow(boolean Following, int Position, boolean Refresh) {
        if (Refresh) {
            sNextOffset = "0";
            arrayList.clear();
            gridViewAdapter.notifyDataSetChanged();
            gridViewAdapter.notifyDataSetInvalidated();
            if (!API_CALLING) {
                getCommunityList();
            }

        }
    }
}
