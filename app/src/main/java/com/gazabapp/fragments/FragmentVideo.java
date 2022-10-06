package com.gazabapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.adapter.AdapterRV_Post;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.PostList;
import com.gazabapp.pojo.PostListData;
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

public class FragmentVideo extends Fragment
{

    private ProgressBar progress_bar;
    private RecyclerView rcvVideo;
    private int iPage_fresh = 1,iPage_hot = 1,iCount = 10;
    private ArrayList<PostListData> arrayList_fresh = new ArrayList<>();
    private ArrayList<PostListData> arrayList_hot = new ArrayList<>();

    private AdapterRV_Post adapterRVPost_fresh = null;
    private AdapterRV_Post adapterRVPost_hot = null;

    private LinearLayoutManager layoutManager = null;
    private SharedPreferences sharedPref = null;

    private boolean blFresh = false;
    private TextView_custom tvHot,tvFresh,tvHotBottom,tvFreshBottom;
    private ImageView ivNotification,ivProfile;

    private boolean blReadyForNetwork = true;
    public FragmentVideo()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.GONE);


        ((ActivityHome) getActivity()).setCheckedPages(4);

        rcvVideo = (RecyclerView) rootView.findViewById(R.id.rcvVideo);

        SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                swipeContainer.setRefreshing(false);
                if(blReadyForNetwork)
                {
                    if(blFresh)
                    {
                        iPage_fresh = 1;
                        arrayList_fresh.clear();
                        adapterRVPost_fresh.notifyDataSetChanged();

                        getPost_fresh();
                    }
                    else
                    {
                        iPage_hot = 1;
                        arrayList_hot.clear();
                        adapterRVPost_hot.notifyDataSetChanged();

                        getPost_hot();
                    }
                }

            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        tvHot = (TextView_custom) rootView.findViewById(R.id.tvHot);
        tvFresh = (TextView_custom) rootView.findViewById(R.id.tvFresh);
        tvHotBottom = (TextView_custom) rootView.findViewById(R.id.tvHotBottom);
        tvFreshBottom = (TextView_custom) rootView.findViewById(R.id.tvFreshBottom);

        tvFreshBottom.setVisibility(View.INVISIBLE);
        tvHotBottom.setVisibility(View.INVISIBLE);

        ivNotification = (ImageView) rootView.findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Fragment fragment = new FragmentNotification();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.content_frame, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
            }
        });
        ivProfile = (ImageView) rootView.findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((ActivityHome)getActivity()).openDrawer();
            }
        });


        tvFresh.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                tvFreshBottom.setVisibility(View.VISIBLE);
                tvHotBottom.setVisibility(View.INVISIBLE);
                changePostFeed(true);
            }
        });
        tvHot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                tvFreshBottom.setVisibility(View.INVISIBLE);
                tvHotBottom.setVisibility(View.VISIBLE);
                changePostFeed(false);
            }
        });

        rcvVideo = (RecyclerView) rootView.findViewById(R.id.rcvVideo);
        rcvVideo.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rcvVideo.setLayoutManager(layoutManager);
        if(arrayList_fresh == null || arrayList_fresh.size() == 0)
        {
            iPage_fresh = 1;
            adapterRVPost_fresh = new AdapterRV_Post(arrayList_fresh,getActivity(),false);
        }

        if(arrayList_hot == null || arrayList_hot.size() == 0)
        {
            iPage_hot = 1;
            adapterRVPost_hot = new AdapterRV_Post(arrayList_hot,getActivity(),false);
        }
        rcvVideo.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1))
                {
                    loadMore();
                }
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

//        tvHot.callOnClick();
        tvFresh.callOnClick();
        return rootView;
    }
    public void updateFollowing(String sID,boolean blFollowing)
    {
        for(PostListData postListData :arrayList_fresh)
        {
            if(postListData.getUserId().equals(sID))
            {
                postListData.setIs_person_follow(blFollowing);
            }

        }
        for(PostListData postListData :arrayList_hot)
        {
            if(postListData.getUserId().equals(sID))
            {
                postListData.setIs_person_follow(blFollowing);
            }

        }
        if(adapterRVPost_hot != null)
        {
            adapterRVPost_hot.notifyDataSetChanged();
        }
        if(adapterRVPost_fresh != null)
        {
            adapterRVPost_fresh.notifyDataSetChanged();
        }
    }
    public void changePostFeed(boolean blFresh)
    {
        blReadyForNetwork = true;
        this.blFresh = blFresh;
        if(blFresh)
        {
            rcvVideo.setAdapter(adapterRVPost_fresh);
            getPost_fresh();
        }
        else
        {
            rcvVideo.setAdapter(adapterRVPost_hot);
            getPost_hot();
        }
    }



    private void getPost_hot()
    {
        blReadyForNetwork = false;
        String sSort = "";
        progress_bar.setVisibility(View.VISIBLE);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<PostList> apiRequest = getApi.getVideo_Hot("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),""+sharedPref.getString(Constants.shk_ID,""),"" + iPage_hot,""+iCount);

        apiRequest.enqueue(new Callback<PostList>()
        {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response)
            {
                blReadyForNetwork = true;
                if(response.body() != null && response.body().getData() != null)
                {
                    List<PostListData> list = response.body().getData();
                    arrayList_hot.addAll(list);
                    adapterRVPost_hot.notifyDataSetChanged();
                    iPage_hot++;
                }
                progress_bar.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<PostList> call, Throwable t)
            {
                blReadyForNetwork = true;
                progress_bar.setVisibility(View.GONE);
            }
        });
    }
    private void getPost_fresh()
    {
        blReadyForNetwork = false;
        String sSort = "created";
        progress_bar.setVisibility(View.VISIBLE);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<PostList> apiRequest = getApi.getVideo_fresh("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),"" + iPage_fresh,""+iCount,"0",sSort);

        apiRequest.enqueue(new Callback<PostList>()
        {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response)
            {
                blReadyForNetwork = true;
                if(response.body() != null && response.body().getData() != null)
                {
                    List<PostListData> list = response.body().getData();
                    arrayList_fresh.addAll(list);
                    adapterRVPost_fresh.notifyDataSetChanged();
                    iPage_fresh++;
                }
                progress_bar.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<PostList> call, Throwable t)
            {
                blReadyForNetwork = true;
                progress_bar.setVisibility(View.GONE);
            }
        });


    }
    public void loadMore()
    {
        if(blReadyForNetwork)
        {
            if(blFresh)
            {
                getPost_fresh();
            }
            else
            {
                getPost_hot();
            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

}
