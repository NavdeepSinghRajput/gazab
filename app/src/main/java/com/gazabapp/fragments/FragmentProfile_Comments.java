package com.gazabapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.adapter.Adapter_ProfileComments;
import com.gazabapp.pojo.CommentList;
import com.gazabapp.pojo.CommentListData;
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

public class FragmentProfile_Comments extends Fragment
{
    private int iPage = 1,iCount = 8;
    private SharedPreferences sharedPref = null;
    private ListView listView;
    private Adapter_ProfileComments adapter = null;
    private ArrayList<CommentListData> arrayList = new ArrayList<>();
    public FragmentProfile_Comments()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_profile_comments, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        listView = (ListView) rootView.findViewById(R.id.listview);
        adapter = new Adapter_ProfileComments(getActivity(),arrayList);
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && (listView.getLastVisiblePosition() ) >= (adapter.getCount() - 1))
                {
                    loadMore();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        getCommentList();
        return rootView;
    }


    private void getCommentList()
    {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<CommentList> apiRequest = getApi.getCommentList("" + iPage,""+iCount);
        apiRequest.enqueue(new Callback<CommentList>()
        {
            @Override
            public void onResponse(Call<CommentList> call, Response<CommentList> response)
            {
                if(response.body() != null && response.body().getData() != null)
                {
                    List<CommentListData> list = response.body().getData();
                    arrayList.addAll(list);
                    adapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onFailure(Call<CommentList> call, Throwable t)
            {}
        });


    }

    public void loadMore()
    {
        iPage++;
        getCommentList();
    }

}
