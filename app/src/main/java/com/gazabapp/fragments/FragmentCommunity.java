package com.gazabapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.actionlisteners.CommunityFollow;
import com.gazabapp.adapter.AdapterRV_Post;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.CommunityDetails;
import com.gazabapp.pojo.CommunityDetailsData;
import com.gazabapp.pojo.PostList;
import com.gazabapp.pojo.PostListData;
import com.gazabapp.serverapi.RetroApis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gazabapp.Constants.DP_LINK_COMMUNITY;
import static com.gazabapp.Constants.PRINT;

public class FragmentCommunity extends Fragment {
    private ProgressBar progress_bar;

    private SharedPreferences sharedPref = null;
    private CommunityDetailsData data = null;
    public static String COMMUNITY_ID = "";

    private boolean blFresh = false;
    private TextView_custom tvName, tvPost, tvFollowers;//tvHot, tvFresh,,tvHotBottom, tvFreshBottom;
    //    private Checkbox_custom cbFollow;
    private ImageView ivBack, ivShare;

//    private LinearLayout liTopbar;

    private RecyclerView rcvPost;
    private int iPage_fresh = 1, iPage_hot = 1, iCount = 10;
    private ArrayList<PostListData> arrayList_fresh = new ArrayList<>();
    private ArrayList<PostListData> arrayList_hot = new ArrayList<>();

    private AdapterRV_Post adapterRVPost_fresh = null;
    private AdapterRV_Post adapterRVPost_hot = null;

    private LinearLayoutManager layoutManager = null;
    private boolean blReadyForNetwork = true;
    private CommunityFollow communityFollow;
    CircleImageView community_image;

    LinearLayout communityFollowll;
    ImageView communityFollowSelected;
    TextView communityFollowUnSelected;
    private ShimmerFrameLayout mShimmerViewContainer;
    boolean changed = false;

    public FragmentCommunity(CommunityFollow communityFollow) {
        this.communityFollow = communityFollow;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (changed) {
            Intent intent2 = new Intent();
            intent2.setAction(getString(R.string.app_name) + "post.refresh");
            intent2.putExtra("refresh", "true");
            getActivity().sendBroadcast(intent2);
          }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_community, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.GONE);


//        liTopbar = (LinearLayout) rootView.findViewById(R.id.liTopbar);
        community_image = (CircleImageView) rootView.findViewById(R.id.community_image);
        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container);
//        toolbar = rootView.findViewById(R.id.toolbar);
        mShimmerViewContainer.startShimmerAnimation();

        SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                if (blReadyForNetwork) {
                    if (blFresh) {
                        iPage_fresh = 1;
                        arrayList_fresh.clear();
                        adapterRVPost_fresh.notifyDataSetChanged();
                        getPost_fresh();
                    } else {
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

//        tvHot = (TextView_custom) rootView.findViewById(R.id.tvHot);
//        tvFresh = (TextView_custom) rootView.findViewById(R.id.tvFresh);
        tvName = (TextView_custom) rootView.findViewById(R.id.tvName);
        tvPost = (TextView_custom) rootView.findViewById(R.id.tvPost);
        tvFollowers = (TextView_custom) rootView.findViewById(R.id.tvFollowers);
//        tvHotBottom = (TextView_custom) rootView.findViewById(R.id.tvHotBottom);
//        tvFreshBottom = (TextView_custom) rootView.findViewById(R.id.tvFreshBottom);
//
//        tvFreshBottom.setVisibility(View.INVISIBLE);
//        tvHotBottom.setVisibility(View.INVISIBLE);
//        cbFollow = (Checkbox_custom) rootView.findViewById(R.id.cbFollow);
        communityFollowll = (LinearLayout) rootView.findViewById(R.id.communityFollowll);
        communityFollowSelected = (ImageView) rootView.findViewById(R.id.communityFollowSelected);
        communityFollowUnSelected = (TextView) rootView.findViewById(R.id.communityFollowUnSelected);

        communityFollowll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communityFollowUnSelected.getVisibility() == View.VISIBLE) {
                    ((ActivityHome) getActivity()).followCommunity(data.getId(), communityFollow);
                    communityFollowll.setBackgroundColor(((ActivityHome) getActivity()).getAttributeColor(R.attr.followCommunitySelectedColor));
                    communityFollowSelected.setVisibility(View.VISIBLE);
                    communityFollowUnSelected.setVisibility(View.GONE);
                    community_image.setBorderWidth(5);
                    community_image.setBorderColor(((ActivityHome) getActivity()).getAttributeColor(R.attr.followCommunitySelectedColor));
                } else {
                    ((ActivityHome) getActivity()).unfollowCommunity(data.getId(), communityFollow);
                    communityFollowll.setBackgroundColor(((ActivityHome) getActivity()).getAttributeColor(R.attr.community_choose_following_background));
                    communityFollowSelected.setVisibility(View.GONE);
                    communityFollowUnSelected.setVisibility(View.VISIBLE);
                    community_image.setBorderWidth(0);
                    community_image.setBorderColor(((ActivityHome) getActivity()).getAttributeColor(R.attr.followCommunitySelectedColor));
                    changed = true;
                }

            }
        });


      /*  cbFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    cbFollow.setText(getActivity().getResources().getString(R.string.following));
                    community_image.setBorderWidth(10);
                    community_image.setBorderColor(((ActivityHome) getActivity()).getAttributeColor(R.attr.followCommunitySelectedColor));


//                    cbFollow.setTextColor(((ActivityHome) getActivity()).getAttributeColor(R.attr.following));
                } else {
                    cbFollow.setText(getActivity().getResources().getString(R.string.follow));
//                    cbFollow.setTextColor(((ActivityHome) getActivity()).getAttributeColor(R.attr.follow));
                }
            }
        });

     */
        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        ivShare = (ImageView) rootView.findViewById(R.id.ivShare);
        ivShare.setOnClickListener(view -> ((ActivityHome) getActivity()).sharePost(DP_LINK_COMMUNITY + COMMUNITY_ID));
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


    /*    tvFresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvFreshBottom.setVisibility(View.VISIBLE);
                tvHotBottom.setVisibility(View.INVISIBLE);
                changePostFeed(true);
            }
        });
        tvHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvFreshBottom.setVisibility(View.INVISIBLE);
                tvHotBottom.setVisibility(View.VISIBLE);
                changePostFeed(false);
            }
        });*/


        rcvPost = (RecyclerView) rootView.findViewById(R.id.rcvPost);
        rcvPost.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rcvPost.setLayoutManager(layoutManager);
        if (arrayList_fresh == null || arrayList_fresh.size() == 0) {
            iPage_fresh = 1;
            adapterRVPost_fresh = new AdapterRV_Post(arrayList_fresh, getActivity(), false);
        }

        if (arrayList_hot == null || arrayList_hot.size() == 0) {
            iPage_hot = 1;
            adapterRVPost_hot = new AdapterRV_Post(arrayList_hot, getActivity(), false);
        }
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

        getCommunityDetails(COMMUNITY_ID);
        return rootView;
    }


    public void changePostFeed(boolean blFresh) {
        this.blFresh = blFresh;
        blReadyForNetwork = true;
        if (blFresh) {
            rcvPost.setAdapter(adapterRVPost_fresh);
            getPost_fresh();
        } else {
            rcvPost.setAdapter(adapterRVPost_hot);
            getPost_hot();
        }
    }

    public void updateFollowing(String sID, boolean blFollowing) {
        for (PostListData postListData : arrayList_fresh) {
            if (postListData.getUserId().equals(sID)) {
                postListData.setIs_person_follow(blFollowing);
            }

        }
        for (PostListData postListData : arrayList_hot) {
            if (postListData.getUserId().equals(sID)) {
                postListData.setIs_person_follow(blFollowing);
            }

        }
        if (adapterRVPost_hot != null) {
            adapterRVPost_hot.notifyDataSetChanged();
        }
        if (adapterRVPost_fresh != null) {
            adapterRVPost_fresh.notifyDataSetChanged();
        }
    }

    private void getPost_fresh() {
        blReadyForNetwork = false;
        String sSort = "created";
        progress_bar.setVisibility(View.VISIBLE);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<PostList> apiRequest = getApi.getCommunityPost_fresh("Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "" + iPage_fresh, "" + iCount, "0", sSort, COMMUNITY_ID);

        apiRequest.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                blReadyForNetwork = true;
                progress_bar.setVisibility(View.GONE);
                if (response.body() != null && response.body().getData() != null) {
                    List<PostListData> list = response.body().getData();
                    arrayList_fresh.addAll(list);
                    adapterRVPost_fresh.notifyDataSetChanged();
                    iPage_fresh++;
                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
                blReadyForNetwork = true;

                progress_bar.setVisibility(View.GONE);
            }
        });
    }

    private void getPost_hot() {
        blReadyForNetwork = false;
        String sSort = "";
        progress_bar.setVisibility(View.VISIBLE);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<PostList> apiRequest = getApi.getCommunityPost_hot("Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "" + COMMUNITY_ID, "" + iPage_hot, "" + iCount);

        apiRequest.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                blReadyForNetwork = true;
                progress_bar.setVisibility(View.GONE);
                if (response.body() != null && response.body().getData() != null) {
                    List<PostListData> list = response.body().getData();
                    arrayList_hot.addAll(list);
                    adapterRVPost_hot.notifyDataSetChanged();
                    iPage_hot++;
                }
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
                blReadyForNetwork = true;
                progress_bar.setVisibility(View.GONE);
            }
        });
    }

    public void loadMore() {
        if (blReadyForNetwork) {
            if (blFresh) {
                getPost_fresh();
            } else {
                getPost_hot();
            }
        }

    }

    private void getCommunityDetails(String sID) {
        progress_bar.setVisibility(View.VISIBLE);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<CommunityDetails> apiRequest = getApi.getCommunityDetails(sID, "application/json", "Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""));
        PRINT("" + apiRequest.request().url());
        apiRequest.enqueue(new Callback<CommunityDetails>() {
            @Override
            public void onResponse(Call<CommunityDetails> call, Response<CommunityDetails> response) {
                progress_bar.setVisibility(View.GONE);
                if (response.body() != null && response.body().getData() != null) {
                    data = response.body().getData();
                    try {
                        tvFollowers.setText("" + data.getTotalFollowers() + " " + getActivity().getResources().getString(R.string.followers));
                    } catch (Exception e) {

                    }


                    tvPost.setText("" + data.getTotalPosts() + " " + getActivity().getResources().getString(R.string.posts));
                    tvName.setText("" + data.getName());
                    if (data.getIs_community_follow()) {
                        communityFollowll.setBackgroundColor(((ActivityHome) getActivity()).getAttributeColor(R.attr.followCommunitySelectedColor));
                        communityFollowSelected.setVisibility(View.VISIBLE);
                        communityFollowUnSelected.setVisibility(View.GONE);
                        community_image.setBorderWidth(5);
                        community_image.setBorderColor(((ActivityHome) getActivity()).getAttributeColor(R.attr.followCommunitySelectedColor));

                    } else {
                        communityFollowll.setBackgroundColor(((ActivityHome) getActivity()).getAttributeColor(R.attr.community_choose_following_background));
                        communityFollowSelected.setVisibility(View.GONE);
                        communityFollowUnSelected.setVisibility(View.VISIBLE);
                        community_image.setBorderWidth(0);
                        community_image.setBorderColor(((ActivityHome) getActivity()).getAttributeColor(R.attr.followCommunitySelectedColor));

                    }
//                    cbFollow.setChecked(data.getIs_community_follow());

                    Glide.with(getActivity()).load(data.getImage()).into(community_image);
                   /* Glide.with(getActivity()).load(data.getImage()).into(new SimpleTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                liTopbar.setBackground(resource);
                            }
                        }
                    });*/
                  /*  cbFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                    {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                        {
                            if(isChecked)
                            {
                                cbFollow.setText(getActivity().getResources().getString(R.string.following));
                                cbFollow.setTextColor(((ActivityHome) getActivity()).getAttributeColor(R.attr.following));
                                ((ActivityHome) getActivity()).followCommunity(data.getId(),communityFollow);
                            }
                            else
                            {
                                cbFollow.setText(getActivity().getResources().getString(R.string.follow));
                                cbFollow.setTextColor(((ActivityHome) getActivity()).getAttributeColor(R.attr.follow));
                                ((ActivityHome) getActivity()).unfollowCommunity(data.getId(),communityFollow);
                            }
                        }
                    });*/

                    mShimmerViewContainer.stopShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);


                    System.out.println("=========>>>1");
                }
//                tvHot.callOnClick();
//              /  tvFresh.callOnClick();
                changePostFeed(true);
                System.out.println("=========>>>2");
            }

            @Override
            public void onFailure(Call<CommunityDetails> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
                changePostFeed(false);
            }
        });


    }
}
