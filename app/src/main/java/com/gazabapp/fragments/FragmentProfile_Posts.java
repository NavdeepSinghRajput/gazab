package com.gazabapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.adapter.AdapterRV_Post;
import com.gazabapp.pojo.PostList;
import com.gazabapp.pojo.PostListData;
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

public class FragmentProfile_Posts extends Fragment {
    private int iPage = 1, iCount = 7;
    private ArrayList<PostListData> arrayList = new ArrayList<>();

    private SharedPreferences sharedPref = null;
    private AdapterRV_Post adapterRVPost = null;
    private LinearLayoutManager layoutManager = null;
    private RecyclerView rcvPost;
    private int iTotal = 0;
    private ProgressBar progress_bar;
    String id;
    ImageView nopost;
    BroadcastReceiver mReceiver;

    public FragmentProfile_Posts() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_post, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        nopost = (ImageView) rootView.findViewById(R.id.nopost);
        progress_bar.setVisibility(View.GONE);
        rcvPost = (RecyclerView) rootView.findViewById(R.id.rcvPost);
        adapterRVPost = new AdapterRV_Post(arrayList, getActivity(), true);
        rcvPost.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rcvPost.setLayoutManager(layoutManager);
        rcvPost.setAdapter(adapterRVPost);
        rcvPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    loadMore();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        id = getArguments().getString("id");

        if (arrayList.size() == 0) {
            getPostList();
        }

        Log.e("Uri", "uriiiiii");
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e("Uri", "uriiiiii");
                    Log.e("Uri", intent.getIntExtra("position", -1) + "sdasdasdas");
                    int position = intent.getIntExtra("position", -1);
                    arrayList.remove(position);
                    adapterRVPost.notifyDataSetChanged();
                    if (arrayList.size() < 1) {
                        nopost.setVisibility(View.VISIBLE);
                        rcvPost.setVisibility(View.GONE);
                    }

                }
            };
            IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "post.delete");
            getActivity().registerReceiver(mReceiver, intentFilter);
        }
        return rootView;
    }


    private void getPostList() {
        progress_bar.setVisibility(View.VISIBLE);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
//        Call<PostList> apiRequest = getApi.getMyPost(sharedPref.getString(Constants.shk_ID,""),"Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),"" + iPage,""+iCount);
        Call<PostList> apiRequest = ApiClient.getGazabApi().getMyPost(id, "Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "" + iPage, "" + iCount);

        apiRequest.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                progress_bar.setVisibility(View.GONE);
                if (response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                    List<PostListData> list = response.body().getData();
                    arrayList.addAll(list);
                    adapterRVPost.notifyDataSetChanged();
                    iTotal = response.body().getTotal();
                    nopost.setVisibility(View.GONE);
                    rcvPost.setVisibility(View.VISIBLE);
                } else {
                    if (iTotal == 0) {
                        nopost.setVisibility(View.VISIBLE);
                        rcvPost.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
            }
        });
    }

    public void loadMore() {
        if (iTotal > arrayList.size()) {
            iPage++;
            getPostList();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
