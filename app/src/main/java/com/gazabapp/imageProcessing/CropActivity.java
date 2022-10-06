package com.gazabapp.imageProcessing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.pojo.UploadFile;
import com.gazabapp.serverapi.ApiClient;
import com.gazabapp.serverapi.RetroApis;

import java.io.File;
import java.io.IOException;
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


public class CropActivity extends AppCompatActivity {
    public Uri uri;
//    public static Bitmap bitmap;
    String value;

    public CropActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_main);
        value = getIntent().getStringExtra("sign");

        uri = Uri.parse(getIntent().getStringExtra("bitmap"));
        Log.e("uri...", "h" + uri.toString());

     /*   try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        setTaskBarColored(this);

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString("uri", uri.toString());
            CropFragment cropFragment = new CropFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            cropFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.container, cropFragment).commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        try {
            getOutputMediaFile().delete();
        } catch (Exception e) {
            Log.e("exception", e.getMessage());
        }
        finish();
    }

    public void startResultActivity(Uri uri) {
//        if (isFinishing()) return;


        Intent intent2 = new Intent();
        intent2.setAction(getString(R.string.app_name) +"photo.croped");
        intent2.putExtra("imageUri",uri.getPath());
        sendBroadcast(intent2);
        finish();

//        setResult(200, getIntent().putExtra("imageUri", uri));

//            notifyImagePath.imagePathIs(uri);
        Log.e("croppedImage",getOutputMediaFile().getPath()+"       asda");

        try {
            File file = new File(getOutputMediaFile().getPath());
            file.delete();
        } catch (Exception e) {
            Log.e("exception", e.getMessage());
        }

        File file = new File(uri.getPath());
        Bitmap bm = BitmapFactory.decodeFile(file.getPath());
//        uploadFile(uri);

    }

    private void uploadFile(Uri uri) {
        ProgressDialog progressDialog = new ProgressDialog(CropActivity.this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(true);
        progressDialog.show();
        File file = new File(uri.getPath());
        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "profile");
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        SharedPreferences sharedPref = getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
//        Call<UploadFile> apiRequest = getApi.uploadFile("Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), fileToUpload, type);
        Call<UploadFile> apiRequest = ApiClient.getGazabApi().uploadFile("Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), fileToUpload, type);

        apiRequest.enqueue(new Callback<UploadFile>() {
            @Override
            public void onResponse(Call<UploadFile> call, Response<UploadFile> response) {

                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success") && response.body().getData().getFilepath().length() > 10) {
                    String sFileImage1 = response.body().getData().getFilepath();
                    Log.e("sas", "sas" + sFileImage1);
                    progressDialog.dismiss();
                   /* RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(getContext().getResources().getDrawable(R.drawable.profile_pic_placeholder));
                    requestOptions.error(getContext().getResources().getDrawable(R.drawable.profile_pic_placeholder));

                    Glide.with(getActivity()).load(sFileImage1).apply(requestOptions).into(ivProfilePhoto);
                    ((ActivityHome) getActivity()).hideProgress();
                    ((ActivityHome) getActivity()).hideProgress();
                    ((ActivityHome) getActivity()).hideProgress();*/
                } else {   progressDialog.dismiss();
//                    ((ActivityHome) getActivity()).hideProgress();
                }
            }

            @Override
            public void onFailure(Call<UploadFile> call, Throwable t) {
//                ((ActivityHome) getActivity()).hideProgress();
                progressDialog.dismiss();
            }
        });
    }


    public void setTaskBarColored(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = context.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = getStatusBarHeight();

            View view = new View(context);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public File getOutputMediaFile() {

        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName = "MY_PROFILEPIC.jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

}
