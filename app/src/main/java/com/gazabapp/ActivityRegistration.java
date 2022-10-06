package com.gazabapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gazabapp.fragments.FragmentLogin;
import com.gazabapp.fragments.FragmentSplashScreen;
import com.gazabapp.pojo.Follow;
import com.gazabapp.pojo.FollowData;
import com.gazabapp.pojo.Unfollow;
import com.gazabapp.pojo.UnfollowData;
import com.gazabapp.serverapi.RetroApis;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityRegistration extends AppCompatActivity
{
    private FirebaseAnalytics mFirebaseAnalytics;
    private SharedPreferences sharedPref = null;
    public static boolean CHANGING_LOGIN = false;
    private int iVersionCode = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        setTheme(R.style.DarkTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);



        MyApplication application = (MyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Activity Registration");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        sharedPref = getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task)
            {
                sharedPref.edit().putString(Constants.shk_FCM_REGISTRATION,""+task.getResult().getToken()).commit();
            }
        });

        try
        {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            iVersionCode = Integer.parseInt(""+pInfo.versionCode);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("force_update");
        myRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(Integer.parseInt(""+dataSnapshot.child("version_code").getValue()) > iVersionCode)
                {
                    popupUpdate(""+dataSnapshot.child("title").getValue(),""+dataSnapshot.child("message").getValue(), Boolean.parseBoolean(""+dataSnapshot.child("force_update").getValue()));
                }
                else
                {
                    afterPermission();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void popupUpdate(String sTitle,String sDescription, boolean blForce)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case DialogInterface.BUTTON_POSITIVE:
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=com.gazabapp"));
                            startActivity(intent);
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        afterPermission();
                        break;
                }
            }
        };

        if(blForce)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegistration.this);
            builder.setTitle(""+sTitle).setMessage(""+sDescription).setPositiveButton("Yes, Sure !", dialogClickListener).setCancelable(false).show();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegistration.this);
            builder.setTitle(""+sTitle).setMessage(""+sDescription).setPositiveButton("Yes, Sure !", dialogClickListener)
                    .setNegativeButton("No, Thanks !", dialogClickListener).setCancelable(false).show();

        }


    }

    @Override
    public void onBackPressed()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount() == 1)
        {
            finish();
        }
        else
        {
            super.onBackPressed();

        }
    }

    private void afterPermission()
    {



//        Fragment fragment = new FragmentGenderSelect();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
//        transaction.replace(R.id.content_frame, fragment);
//        transaction.addToBackStack(fragment.getClass().getSimpleName());
//        transaction.commit();

        if(sharedPref.getString(Constants.shk_ACCESS_TOKEN,"").length() < 5)
        {
            Fragment fragment = null;
            if(CHANGING_LOGIN)
            {
                fragment = new FragmentLogin();
            }
            else
            {
                fragment = new FragmentSplashScreen();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.content_frame, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
        }
        else
        {
            finish();
            startActivity(new Intent(this,ActivityHome.class));
        }

//        printKeyHash(this);
    }
    public void showToast(String sMessage)
    {
        Toast.makeText(this,""+sMessage,Toast.LENGTH_LONG).show();
    }

    public static String printKeyHash(Activity context)
    {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);


            for (Signature signature : packageInfo.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
//                System.out.println("Key Hash="+key);
            }
        }
        catch (PackageManager.NameNotFoundException e1)
        {

        }
        catch (NoSuchAlgorithmException e)
        {
        } catch (Exception e)
        {
        }
        return key;
    }



    private Handler handler = new Handler();
    private ProgressDialog progressDialog;
    public void showProgress()
    {
        handler.post(new Runnable()
        {
            public void run()
            {
                progressDialog = ProgressDialog.show(ActivityRegistration.this, "Loading", "Please wait ......");
            }
        });
    }
    public void hideProgress()
    {
        if(progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }


    public void followCommunity(String sCommunityID)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", ""+sharedPref.getString(Constants.shk_ID,""));
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(""+sCommunityID);
        jsonObject.add("ids", jsonArray);


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);

        Call<Follow> apiRequest = getApi.followCommunities("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),"application/json",jsonObject);

        apiRequest.enqueue(new Callback<Follow>()
        {
            @Override
            public void onResponse(Call<Follow> call, Response<Follow> response)
            {
                if(response.body().getStatus().equalsIgnoreCase("success"))
                {
                    FollowData followData = response.body().getData().get(0);
                }
                else
                {

                }
            }
            @Override
            public void onFailure(Call<Follow> call, Throwable t)
            {

            }
        });
    }

    public void unfollowCommunity(String sCommunityID)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", ""+sharedPref.getString(Constants.shk_ID,""));
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(""+sCommunityID);
        jsonObject.add("ids", jsonArray);


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);

        Call<Unfollow> apiRequest = getApi.unfollowCommunities("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),"application/json",jsonObject);
        apiRequest.enqueue(new Callback<Unfollow>()
        {
            @Override
            public void onResponse(Call<Unfollow> call, Response<Unfollow> response)
            {
                if(response.body().getStatus().equalsIgnoreCase("success"))
                {
                    UnfollowData unfollowData = response.body().getData().get(0);
                }
                else
                {

                }
            }
            @Override
            public void onFailure(Call<Unfollow> call, Throwable t)
            {

            }
        });
    }
    public int getAttributeColor(int attributeId)
    {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attributeId, typedValue, true);
        @ColorInt int colorRes = typedValue.data;
        return colorRes;
    }
}
