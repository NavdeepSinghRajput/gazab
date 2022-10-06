package com.gazabapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.gazabapp.actionlisteners.CommunityFollow;
import com.gazabapp.actionlisteners.PersonFollow;
import com.gazabapp.actionlisteners.PostDataChanged;
import com.gazabapp.adapter.Adapter_DrawerList;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.fragments.FragmentCommunity;
import com.gazabapp.fragments.FragmentExplorer;
import com.gazabapp.fragments.FragmentNewPostImage;
import com.gazabapp.fragments.FragmentNewPostLink;
import com.gazabapp.fragments.FragmentNewPostText;
import com.gazabapp.fragments.FragmentNewPostVideo;
import com.gazabapp.fragments.FragmentPost;
import com.gazabapp.fragments.FragmentPostDetails;
import com.gazabapp.fragments.FragmentPostTap;
import com.gazabapp.fragments.FragmentProfile_Base;
import com.gazabapp.fragments.FragmentSetting;
import com.gazabapp.fragments.FragmentVideo;
import com.gazabapp.pojo.CommunityList;
import com.gazabapp.pojo.CommunityListData;
import com.gazabapp.pojo.CommunityMyList;
import com.gazabapp.pojo.DeletePostModel;
import com.gazabapp.pojo.Follow;
import com.gazabapp.pojo.FollowData;
import com.gazabapp.pojo.PostListData;
import com.gazabapp.pojo.ReactionAdd;
import com.gazabapp.pojo.Unfollow;
import com.gazabapp.pojo.UnfollowData;
import com.gazabapp.serverapi.ApiClient;
import com.gazabapp.serverapi.RetroApis;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

import static com.gazabapp.ActivityRegistration.CHANGING_LOGIN;
import static com.gazabapp.Constants.COPY_INTENT;
import static com.gazabapp.Constants.PRINT;

public class ActivityHome extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private ArrayList<CommunityListData> arylstTRENDING = new ArrayList<>();
    private ArrayList<CommunityListData> arylstMY_COMMUNITY = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private RadioButton rbtPost, rbtExplorer, rbtCreate, rbtVideo, rbtSetting;
    private SharedPreferences sharedPref = null;
    private CircleImageView ivProfilePhoto;
    private TextView_custom tvProfileName, tvViewAll, tvMyCommunity;
    private ListView liDrwTrendCommunity, liDrwMyCommunity;

    private Adapter_DrawerList adapter_drw_MyComm, adapter_drw_TrenComm;

    private CommunityFollow communityFollow_PROFILE = null;
    private LinearLayoutCompat liProfile, liDrawer;
    private PersonFollow personFollow = null;

    private FragmentCommunity fragmentCommunity = null;
    private FragmentProfile_Base fragmentProfile_base = null;
    FragmentPost fragmentPost = null;
    FragmentExplorer fragmentExplorer = null;
    FragmentVideo fragmentVideo = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPref = getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        MyApplication application = (MyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Activity Home");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        if (sharedPref.getBoolean(Constants.shk_DARK_THEME, false))
            setTheme(R.style.DarkTheme);
        else
            setTheme(R.style.LightTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        PRINT("TOKEN " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""));
        PRINT("USER ID " + sharedPref.getString(Constants.shk_ID, ""));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Constants.HEIGHT = displayMetrics.heightPixels;
        Constants.WIDTH = displayMetrics.widthPixels;


        tvProfileName = (TextView_custom) findViewById(R.id.tvProfileName);
        tvMyCommunity = (TextView_custom) findViewById(R.id.tvMyCommunity);

        tvViewAll = (TextView_custom) findViewById(R.id.tvViewAll);
        ivProfilePhoto = (CircleImageView) findViewById(R.id.ivProfilePhoto);
        liProfile = (LinearLayoutCompat) findViewById(R.id.liProfile);
        liDrawer = (LinearLayoutCompat) findViewById(R.id.liDrawer);

        tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                rbtExplorer.setChecked(true);
            }
        });
        liProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                fragmentProfile_base = new FragmentProfile_Base();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame_whole, fragmentProfile_base).addToBackStack(fragmentProfile_base.getClass().getSimpleName()).commit();
            }
        });

        updateUserDetails();


        liDrwMyCommunity = (ListView) findViewById(R.id.liDrwMyCommunity);
        liDrwTrendCommunity = (ListView) findViewById(R.id.liDrwTrendCommunity);


        liDrwMyCommunity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeDrawer();
                startCommunityDetails(arylstMY_COMMUNITY.get(position).getId(), null);
            }
        });
        liDrwTrendCommunity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeDrawer();
                startCommunityDetails(arylstTRENDING.get(position).getId(), null);
            }
        });


        adapter_drw_MyComm = new Adapter_DrawerList(this, arylstMY_COMMUNITY);
        adapter_drw_TrenComm = new Adapter_DrawerList(this, arylstTRENDING);
        liDrwMyCommunity.setAdapter(adapter_drw_MyComm);
        liDrwTrendCommunity.setAdapter(adapter_drw_TrenComm);


        rbtPost = (RadioButton) findViewById(R.id.rbtPost);

        rbtExplorer = (RadioButton) findViewById(R.id.rbtExplorer);
        rbtCreate = (RadioButton) findViewById(R.id.rbtCreate);
        rbtVideo = (RadioButton) findViewById(R.id.rbtVideo);
        rbtSetting = (RadioButton) findViewById(R.id.rbtSetting);

        rbtPost.setOnCheckedChangeListener(this);
        rbtExplorer.setOnCheckedChangeListener(this);
        rbtCreate.setOnCheckedChangeListener(this);
        rbtVideo.setOnCheckedChangeListener(this);
        rbtSetting.setOnCheckedChangeListener(this);

        if (arylstTRENDING.size() < 1) {
            getCommunityMyList();
        }
        startAppScreens();
//        printUrlMatadata("https://www.youtube.com/watch?v=O3Z_LKxPJXQ");


    }


//    public Window getWindow()
//    {
//        return getWindow();
//    }

    private void startAppScreens() {

        PRINT("Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""));
        PRINT("USER " + sharedPref.getString(Constants.shk_ID, ""));

//        rbtPost.setChecked(true);
        rbtExplorer.setChecked(true);
//        FragmentPostDetails_IMAGE.POST_ID = "5d81e13f5b59f21ebd95495a";
//        Fragment fragment = new FragmentPostDetails_IMAGE();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
    }

    public void setCheckedPages(int iState) {
        switch (iState) {
            case 1:
                rbtPost.setSelected(true);
                rbtExplorer.setSelected(false);
                rbtVideo.setSelected(false);
                rbtSetting.setSelected(false);
                break;
            case 2:
                rbtPost.setSelected(false);
                rbtExplorer.setSelected(true);
                rbtVideo.setSelected(false);
                rbtSetting.setSelected(false);
                break;
            case 4:
                rbtPost.setSelected(false);
                rbtExplorer.setSelected(false);
                rbtVideo.setSelected(true);
                rbtSetting.setSelected(false);
                break;
            case 5:
                rbtPost.setSelected(false);
                rbtExplorer.setSelected(false);
                rbtVideo.setSelected(false);
                rbtSetting.setSelected(true);
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume","onresume");

    }

    public void startCommunityDetails(String sCategoryID, CommunityFollow communityFollow) {
        FragmentCommunity.COMMUNITY_ID = sCategoryID;
        fragmentCommunity = new FragmentCommunity(communityFollow);
        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().add(R.id.content_frame_whole, fragmentCommunity).addToBackStack(fragmentCommunity.getClass().getSimpleName()).commit();
        fragmentManager.beginTransaction().add(R.id.content_frame, fragmentCommunity).addToBackStack(fragmentCommunity.getClass().getSimpleName()).commit();
    }


    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(liDrawer)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 1) {
                super.onBackPressed();
            } else {
                finish();
            }
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean flag) {
        if (compoundButton == rbtPost && flag) {
            if (fragmentPost == null) {
                fragmentPost = new FragmentPost();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragmentPost).addToBackStack(fragmentPost.getClass().getSimpleName()).commit();
        } else if (compoundButton == rbtExplorer && flag) {
            if (fragmentExplorer == null) {
                fragmentExplorer = new FragmentExplorer();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragmentExplorer).addToBackStack(fragmentExplorer.getClass().getSimpleName()).commit();
        } else if (compoundButton == rbtCreate && flag) {
            popup_createPost();
        } else if (compoundButton == rbtVideo && flag) {
            if (fragmentVideo == null) {
                fragmentVideo = new FragmentVideo();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragmentVideo).addToBackStack(fragmentVideo.getClass().getSimpleName()).commit();
        } else if (compoundButton == rbtSetting && flag) {
            Fragment fragment = new FragmentSetting();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
        }

    }

    private void popup_createPost() {
        final Dialog dialogChoice = new Dialog(this);
        dialogChoice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChoice.setContentView(R.layout.dialog_create_post);
        dialogChoice.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogChoice.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogChoice.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialogChoice.getWindow().setGravity(Gravity.BOTTOM);
        dialogChoice.show();
        dialogChoice.setCancelable(true);
        dialogChoice.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                rbtCreate.setChecked(false);
            }
        });

        LinearLayout liText = (LinearLayout) dialogChoice.findViewById(R.id.liText);
        LinearLayout liImage = (LinearLayout) dialogChoice.findViewById(R.id.liImage);
        LinearLayout liVideo = (LinearLayout) dialogChoice.findViewById(R.id.liVideo);
        LinearLayout liLink = (LinearLayout) dialogChoice.findViewById(R.id.liLink);

        liText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogChoice.dismiss();
                Fragment fragment = new FragmentNewPostText();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
                transaction.add(R.id.content_frame_whole, fragment);
                transaction.addToBackStack(fragment.getClass().getSimpleName());
                transaction.commit();
            }
        });
        liImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogChoice.dismiss();
                Fragment fragment = new FragmentNewPostImage();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
                transaction.add(R.id.content_frame_whole, fragment);
                transaction.addToBackStack(fragment.getClass().getSimpleName());
                transaction.commit();
            }
        });
        liLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogChoice.dismiss();
                Fragment fragment = new FragmentNewPostLink();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
                transaction.add(R.id.content_frame_whole, fragment);
                transaction.addToBackStack(fragment.getClass().getSimpleName());
                transaction.commit();
            }
        });
        liVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogChoice.dismiss();
                Fragment fragment = new FragmentNewPostVideo();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
                transaction.add(R.id.content_frame_whole, fragment);
                transaction.addToBackStack(fragment.getClass().getSimpleName());
                transaction.commit();
            }
        });
    }

    public void showToast(String sMessage) {
        Toast.makeText(this, "" + sMessage, Toast.LENGTH_LONG).show();
    }

    private Handler handler = new Handler();
    private ProgressDialog progressDialog;

    public void showProgress() {
        handler.post(new Runnable() {
            public void run() {
                progressDialog = ProgressDialog.show(ActivityHome.this, "Loading", "Please wait ......");
            }
        });
    }


    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void printUrlMatadata(String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(url).get();
                    String keywords = doc.select("meta[name=keywords]").first().attr("content");
                    String description = doc.select("meta[name=description]").get(0).attr("content");
//                        System.out.println("keywordsAA:::"+keywords);
//                        System.out.println("description:::"+description);
//                        System.out.println("data:::"+doc.outerHtml());
                } catch (Exception e) {
                }
            }
        }).start();
    }

    public void followCommunity(String sCommunityID, CommunityFollow communityFollow) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", "" + sharedPref.getString(Constants.shk_ID, ""));
        JsonArray jsonArray = new JsonArray();
        jsonArray.add("" + sCommunityID);
        jsonObject.add("ids", jsonArray);


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<Follow> apiRequest = getApi.followCommunities("Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "application/json", jsonObject);
        apiRequest.enqueue(new Callback<Follow>() {
            @Override
            public void onResponse(Call<Follow> call, Response<Follow> response) {
                if (response.body().getStatus().equalsIgnoreCase("success")) {
                    FollowData followData = response.body().getData().get(0);
                    if (communityFollow != null) {
                        communityFollow.onCommunityFollow(true, -1, true);
                    }
                    if (communityFollow_PROFILE != null) {
                        communityFollow_PROFILE.onCommunityFollow(true, -1, true);
                    }
                    Intent intent2 = new Intent();
                    intent2.setAction(getString(R.string.app_name) + "feed.refresh");
                    intent2.putExtra("refresh", "true");
                    sendBroadcast(intent2);

                    getCommunityMyList();
                } else {

                }
            }

            @Override
            public void onFailure(Call<Follow> call, Throwable t) {

            }
        });
    }

    public void unfollowCommunity(String sCommunityID, CommunityFollow communityFollow) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", "" + sharedPref.getString(Constants.shk_ID, ""));
        JsonArray jsonArray = new JsonArray();
        jsonArray.add("" + sCommunityID);
        jsonObject.add("ids", jsonArray);


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<Unfollow> apiRequest = getApi.unfollowCommunities("Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "application/json", jsonObject);

        apiRequest.enqueue(new Callback<Unfollow>() {
            @Override
            public void onResponse(Call<Unfollow> call, Response<Unfollow> response) {
                if (response.body().getStatus().equalsIgnoreCase("success")) {
                    UnfollowData unfollowData = response.body().getData().get(0);

                    if (communityFollow != null) {
                        communityFollow.onCommunityFollow(false, -1, true);
                    }
                    if (communityFollow_PROFILE != null) {
                        communityFollow_PROFILE.onCommunityFollow(false, -1, true);
                    }
                    getCommunityMyList();
                    Intent intent2 = new Intent();
                    intent2.setAction(getString(R.string.app_name) + "feed.refresh");
                    intent2.putExtra("refresh", "true");
                    sendBroadcast(intent2);

                } else {

                }
            }

            @Override
            public void onFailure(Call<Unfollow> call, Throwable t) {

            }
        });
    }

    private void setUpdateFollowing(String sID, boolean blFollowing) {
        if (fragmentPost != null) {
            fragmentPost.updateFollowing(sID, blFollowing);
        }
        if (fragmentVideo != null) {
            fragmentVideo.updateFollowing(sID, blFollowing);
        }
        if (fragmentCommunity != null) {
            fragmentCommunity.updateFollowing(sID, blFollowing);
        }
    }


    public void setPersonFollow(PersonFollow personFollow) {
        this.personFollow = personFollow;
    }

    public void setCommunityFollow_PROFILE(CommunityFollow communityFollow_PROFILE) {
        this.communityFollow_PROFILE = communityFollow_PROFILE;
    }

    public void followPerson(String sPersonID) {
        setUpdateFollowing(sPersonID, true);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", "" + sharedPref.getString(Constants.shk_ID, ""));
        JsonArray jsonArray = new JsonArray();
        jsonArray.add("" + sPersonID);
        jsonObject.add("ids", jsonArray);


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);

        Call<Follow> apiRequest = getApi.followPersons("Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "application/json", jsonObject);

        apiRequest.enqueue(new Callback<Follow>() {
            @Override
            public void onResponse(Call<Follow> call, Response<Follow> response) {
                if (response.body().getStatus().equalsIgnoreCase("success")) {
                    FollowData followData = response.body().getData().get(0);
                    if (personFollow != null) {
                        personFollow.onPersonFollow(true, -1, true);
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<Follow> call, Throwable t) {

            }
        });
    }

    public void unfollowPerson(String sPersonID) {
        setUpdateFollowing(sPersonID, false);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", "" + sharedPref.getString(Constants.shk_ID, ""));
        JsonArray jsonArray = new JsonArray();
        jsonArray.add("" + sPersonID);
        jsonObject.add("ids", jsonArray);


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);

        Call<Unfollow> apiRequest = getApi.unfollowPersons("Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "application/json", jsonObject);
        apiRequest.enqueue(new Callback<Unfollow>() {
            @Override
            public void onResponse(Call<Unfollow> call, Response<Unfollow> response) {
                if (response.body().getStatus().equalsIgnoreCase("success")) {
                    UnfollowData unfollowData = response.body().getData().get(0);
                    if (personFollow != null) {
                        personFollow.onPersonFollow(false, -1, true);
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<Unfollow> call, Throwable t) {

            }
        });
    }

    private void getCommunityTrendingList() {
        int iCount = 10 - arylstMY_COMMUNITY.size();
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<CommunityList> apiRequest = getApi.getTrendingCommunity("Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "1", "" + iCount);
        apiRequest.enqueue(new Callback<CommunityList>() {
            @Override
            public void onResponse(Call<CommunityList> call, Response<CommunityList> response) {
                if (response.body() != null && response.body().getData() != null) {
                    List<CommunityListData> list = response.body().getData();
                    arylstTRENDING.clear();
                    arylstTRENDING.addAll(list);
                    adapter_drw_TrenComm.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<CommunityList> call, Throwable t) {
            }
        });
    }

    public void getCommunityMyList() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<CommunityMyList> apiRequest = getApi.getMyProfileCommunity(sharedPref.getString(Constants.shk_ID, ""), "Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "0", "5");
        apiRequest.enqueue(new Callback<CommunityMyList>() {
            @Override
            public void onResponse(Call<CommunityMyList> call, Response<CommunityMyList> response) {
                if (response.body() != null && response.body().getData() != null) {
                    List<CommunityListData> list = response.body().getData();
                    arylstMY_COMMUNITY.clear();
                    arylstMY_COMMUNITY.addAll(list);
                    adapter_drw_MyComm.notifyDataSetChanged();
                }
                if (arylstMY_COMMUNITY.size() > 0)
                    tvMyCommunity.setVisibility(View.VISIBLE);
                else
                    tvMyCommunity.setVisibility(View.GONE);
                getCommunityTrendingList();
            }

            @Override
            public void onFailure(Call<CommunityMyList> call, Throwable t) {
                getCommunityTrendingList();
            }
        });
    }

    public void popupPostReport(PostListData myListData, boolean delete,int position) {
        final Dialog dialogChoice = new Dialog(this);
        dialogChoice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChoice.setContentView(R.layout.dialog_post_report);
        dialogChoice.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogChoice.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogChoice.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialogChoice.getWindow().setGravity(Gravity.CENTER);
        dialogChoice.show();
        dialogChoice.setCancelable(true);


        dialogChoice.setOnKeyListener(new Dialog.OnKeyListener() {
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialogChoice.dismiss();
                }
                return true;
            }
        });


        ImageView ivClose = (ImageView) dialogChoice.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogChoice.dismiss();
            }
        });

        TextView_custom tvCopyLink = (TextView_custom) dialogChoice.findViewById(R.id.tvCopyLink);
        TextView_custom tvReport = (TextView_custom) dialogChoice.findViewById(R.id.tvReport);
        TextView_custom tvDownload = (TextView_custom) dialogChoice.findViewById(R.id.tvDownload);
        TextView_custom tvDelete = (TextView_custom) dialogChoice.findViewById(R.id.tvDelete);
        View v1 = (View) dialogChoice.findViewById(R.id.v1);
        View v2 = (View) dialogChoice.findViewById(R.id.v2);
        View v3 = (View) dialogChoice.findViewById(R.id.v3);

        if (delete) {
            tvDelete.setVisibility(View.VISIBLE);
            v3.setVisibility(View.VISIBLE);
        } else {
            tvDelete.setVisibility(View.GONE);
            v3.setVisibility(View.GONE);
        }

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("community_id", myListData.getCommunityId());
                jsonObject.addProperty("status", 0);
                Call<DeletePostModel> apiRequest = ApiClient.getGazabApi().deleteUserPost(myListData.getId(), "Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), jsonObject);
                apiRequest.enqueue(new Callback<DeletePostModel>() {
                    @Override
                    public void onResponse(Call<DeletePostModel> call, Response<DeletePostModel> response) {
                        Log.e("onResponse", new Gson().toJson(response.body().toString()));
                        if (response.body().status.equalsIgnoreCase("success")) {
                            dialogChoice.dismiss();
                            Intent intent2 = new Intent();
                            intent2.setAction(getString(R.string.app_name) +"post.delete");
                            intent2.putExtra("position",position);
                            sendBroadcast(intent2);
                        } else {
                            showToast("" + getResources().getString(R.string.communication_error));

                        }
                        hideProgress();
                    }

                    @Override
                    public void onFailure(Call<DeletePostModel> call, Throwable t) {
                        Log.e("onFailure", t.getMessage());
                        hideProgress();
                    }
                });

            }
        });

        if (myListData.getType().equalsIgnoreCase("video") || myListData.getType().equalsIgnoreCase("link")) {
            tvDownload.setVisibility(View.GONE);
            v2.setVisibility(View.GONE);
        } else if (myListData.getType().equalsIgnoreCase("image") || myListData.getType().equalsIgnoreCase("gif")) {
            tvCopyLink.setVisibility(View.GONE);
            v1.setVisibility(View.GONE);
        } else if (myListData.getType().equalsIgnoreCase("text")) {
            tvDownload.setVisibility(View.GONE);
            v2.setVisibility(View.GONE);
        }


        tvCopyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sText = "";


                if (myListData.getType().equalsIgnoreCase("text")) {
                    sText = myListData.getDescription();
                } else {
                    sText = myListData.getMedia();
                }
                if (sText.length() > 0) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(COPY_INTENT, sText);
                    clipboard.setPrimaryClip(clip);
                    dialogChoice.dismiss();
                    showToast(getResources().getString(R.string.content_copied));
                } else {
                    showToast(getResources().getString(R.string.no_content_to_copy));
                }
            }
        });
        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sText = "";
                String sType = "";

                sText = myListData.getMedia();
                sType = myListData.getType();

                if ((sType.equalsIgnoreCase("image") || sType.equalsIgnoreCase("video") || sType.equalsIgnoreCase("gif") || sType.equalsIgnoreCase("link")) && sText.length() > 7) {
                    dialogChoice.dismiss();
                    downloadContent(sText);

                } else {
                    dialogChoice.dismiss();
                    showToast(getResources().getString(R.string.no_content_to_download));
                }


            }
        });

        tvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChoice.dismiss();
                showToast(getResources().getString(R.string.post_reported));
            }
        });

    }


    public void downloadContent(String sText) {
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri Download_Uri = Uri.parse("" + sText);
                            String sFileName = sText.substring(sText.lastIndexOf('/') + 1);
                            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                            request.setAllowedOverRoaming(true);
                            request.setTitle("Gazab Downloading " + sFileName);
                            request.setDescription("Gazab Downloading " + sFileName);
                            request.setVisibleInDownloadsUi(true);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Gazab/" + sFileName);
                            downloadManager.enqueue(request);
                            showToast(getResources().getString(R.string.download_started));
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    public void showDetail(PostListData myListData, int position, PostDataChanged postDataChanged) {
        FragmentPostDetails.POST_ID = myListData.getId();
        FragmentPostDetails.postDetailsData = myListData;

        Fragment fragment = new FragmentPostDetails(postDataChanged, position);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
        transaction.add(R.id.content_frame_whole, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }


    public void viewPostTap(PostListData myListData, int position, PostDataChanged postDataChanged) {
        FragmentPostTap.postDetailsData = myListData;

        Fragment fragment = new FragmentPostTap(postDataChanged, position);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
        transaction.add(R.id.content_frame_whole, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    public void addReaction(PostListData postListData, boolean UpVote) {

        String sType = "down";
        if (UpVote)
            sType = "up";


        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("content_type", "post");
        jsonObject.addProperty("community_id", "" + postListData.getCommunityId());
        jsonObject.addProperty("post_id", "" + postListData.getId());
        jsonObject.addProperty("content_id", "" + postListData.getId());
        jsonObject.addProperty("type", "" + sType);


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<ReactionAdd> apiRequest = getApi.addReaction("Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "application/json", jsonObject);

        apiRequest.enqueue(new Callback<ReactionAdd>() {
            @Override
            public void onResponse(Call<ReactionAdd> call, Response<ReactionAdd> response) {
//                showToast(""+response.body().getMessage());
            }

            @Override
            public void onFailure(Call<ReactionAdd> call, Throwable t) {

            }
        });
    }

    public void sharePost(String URL) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Share with your friends at: " + URL);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    public void logout() {

        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logOut();

        sharedPref.edit().putString(Constants.shk_FCM_REGISTRATION, "").commit();
        sharedPref.edit().putString(Constants.shk_ACCESS_TOKEN, "").commit();
        sharedPref.edit().putString(Constants.shk_ID, "").commit();
        sharedPref.edit().putString(Constants.shk_MOBILE_NO, "").commit();
        sharedPref.edit().putString(Constants.shk_DISPLAY_NAME, "").commit();
        sharedPref.edit().putString(Constants.shk_EMAIL, "").commit();
        sharedPref.edit().putString(Constants.shk_NAME, "").commit();
        sharedPref.edit().putString(Constants.shk_PICTURE, "").commit();
        sharedPref.edit().putString(Constants.shk_AGE_GROUP, "").commit();
        sharedPref.edit().putString(Constants.shk_BIO, "").commit();


        CHANGING_LOGIN = true;
        finish();
        Intent intent = new Intent(this, ActivityRegistration.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void updateUserDetails() {
        tvProfileName.setText("" + sharedPref.getString(Constants.shk_DISPLAY_NAME, ""));
        Glide.with(this).load("" + sharedPref.getString(Constants.shk_PICTURE, "")).into(ivProfilePhoto);
    }


    public void setTheme(boolean isDark) {
//        if(isDark)
//            setTheme(R.style.DarkTheme);
//        else
//            setTheme(R.style.LightTheme);
        recreate();
    }

    public int getAttributeColor(int attributeId) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attributeId, typedValue, true);
        @ColorInt int colorRes = typedValue.data;
        return colorRes;
    }

    public void startProfile(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        FragmentProfile_Base fragmentProfile_base = new FragmentProfile_Base();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentProfile_base.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.content_frame_whole, fragmentProfile_base).addToBackStack(fragmentProfile_base.getClass().getSimpleName()).commit();
    }

}
