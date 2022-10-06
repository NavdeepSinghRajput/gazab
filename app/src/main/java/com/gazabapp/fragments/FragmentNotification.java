package com.gazabapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.adapter.Adapter_ChooseCommunityToPost;
import com.gazabapp.customeview.EditText_custom;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.CommunityList;
import com.gazabapp.pojo.CommunityListData;
import com.gazabapp.pojo.CreatePost;
import com.gazabapp.pojo.UploadFile;
import com.gazabapp.serverapi.RetroApis;
import com.gazabapp.utils.ImageFilePath;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentNotification extends Fragment
{


    private SharedPreferences sharedPref = null;
    public FragmentNotification()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        ImageView ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return rootView;
    }

}
