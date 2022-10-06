package com.gazabapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.adapter.Adapter_Explorer;
import com.gazabapp.customeview.Button_custom;
import com.gazabapp.customeview.EditText_custom;
import com.gazabapp.customeview.TextView_custom;
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

public class FragmentExplorer extends Fragment
{
    private ProgressBar progress_bar;
    private int iPage = 1,iCount = 24;
    private ArrayList<CommunityListData> arrayList = new ArrayList<>();
    private Toolbar toolbar;
    private GridView gridview;
    private Adapter_Explorer gridViewAdapter = null;
    private EditText_custom etSearch;
    private ImageView ivSearch;
    private SharedPreferences sharedPref = null;
    private boolean isSearchingByName = false;
    private TextView_custom tvNotFound;
    BroadcastReceiver mReceiver;

    public FragmentExplorer()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_explorer, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        ((ActivityHome) getActivity()).setCheckedPages(2);
        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.GONE);

        tvNotFound = (TextView_custom) rootView.findViewById(R.id.tvNotFound);
        tvNotFound.setVisibility(View.GONE);

        etSearch = (EditText_custom) rootView.findViewById(R.id.etSearch);
        ivSearch = (ImageView) rootView.findViewById(R.id.ivSearch);
        iPage = 1;
        isSearchingByName = false;
        ivSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(etSearch.getText().toString().length() > 0)
                {
                    search();
                }

            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if(s.length() == 0)
                {
                    search();
                }

            }
        });
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    if(etSearch.getText().toString().length() > 0)
                    {
                        search();
                    }

                }
                return handled;
            }
        });

        gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridViewAdapter = new Adapter_Explorer(getActivity(),arrayList);
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

        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e("asas","sda");
                    arrayList.clear();
                    getCommunityList();
                }
            };
            IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "post.refresh");
            getActivity().registerReceiver(mReceiver, intentFilter);
        }

        return rootView;
    }





    private void search()
    {
        tvNotFound.setVisibility(View.GONE);
        iPage = 1;iCount = 24;
        arrayList.clear();
        gridViewAdapter.notifyDataSetChanged();
        if(etSearch.getText().toString().length() > 0)
        {
            isSearchingByName = true;
        }
        else
        {
            isSearchingByName = false;
        }
        ((ActivityHome)getActivity()).hideSoftKeyboard();

        if(progress_bar.getVisibility() == View.GONE)
        {
            getCommunityList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getCommunityList()
    {

        progress_bar.setVisibility(View.VISIBLE);
        String sType = "";
        String sSearchText = "";
        if(isSearchingByName)
        {
            sType = "";
            sSearchText = String.valueOf(etSearch.getText());
        }
        else
        {
            sType = "popular";
            sSearchText = "";
        }

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<CommunityList> apiRequest = getApi.getPopularCommunity("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),"" + iPage,""+iCount,sType,sSearchText);

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
                if(arrayList.size() == 0)
                {
                    tvNotFound.setVisibility(View.VISIBLE);
                }
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
