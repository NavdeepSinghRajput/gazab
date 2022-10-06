package com.gazabapp.fragments;

import android.Manifest;
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
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.fragment.app.FragmentTransaction;

import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.actionlisteners.SelectCommunity;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

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

public class FragmentNewPostImage extends Fragment implements SelectCommunity
{
    private ImageView ivBack,ivSelectedPhoto;
    private TextView_custom tvPost,tvCommunity;
    private LinearLayout liAddImage,liChooseCommunity;
    private EditText_custom etPostTitle,etDescription;
    private SharedPreferences sharedPref = null;

    private final int iCAMERA_REQUEST_CODE = 12,iGALLERY_REQUEST_CODE=13;
    private String sFileImage1="",sPrFilePath = "";
    private int iCameraState = 0;

    private int iPage = 1,iCount = 24;
    private ArrayList<CommunityListData> arrayList = new ArrayList<>();
    private Adapter_ChooseCommunityToPost adapter = null;
    private String sCommunityIDs = "";
    public FragmentNewPostImage()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_new_post_image, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        ivSelectedPhoto = (ImageView) rootView.findViewById(R.id.ivSelectedPhoto);
        ivSelectedPhoto.setVisibility(View.GONE);
        tvPost = (TextView_custom) rootView.findViewById(R.id.tvPost);
        tvCommunity = (TextView_custom) rootView.findViewById(R.id.tvCommunity);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();

            }
        });
        etPostTitle = (EditText_custom) rootView.findViewById(R.id.etPostTitle);
        etDescription = (EditText_custom) rootView.findViewById(R.id.etDescription);

        etPostTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                if(s.length() > 0)
                {
                    readyForPost();
                }
            }
        });


        liAddImage = (LinearLayout) rootView.findViewById(R.id.liAddImage);
        adapter = new Adapter_ChooseCommunityToPost(getActivity(),arrayList,this);
        liChooseCommunity = (LinearLayout) rootView.findViewById(R.id.liChooseCommunity);
        liChooseCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_chooseCommunity();
            }
        });

        getCommunityList();

        liAddImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Dexter.withActivity(getActivity()).withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report)
                            {
                                if (report.areAllPermissionsGranted())
                                {
                                    iCameraState = 1;
                                    sPrFilePath = "";
                                    photoOptionDialog();
                                }
                                if (report.isAnyPermissionPermanentlyDenied())
                                {
                                    // permission is denied permenantly, navigate user to app settings
                                }
                            }
                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
                            {
                                token.continuePermissionRequest();
                            }
                        })
                        .onSameThread()
                        .check();
            }
        });
        return rootView;
    }
    private void popup_chooseCommunity()
    {
        final Dialog dialogChoice = new Dialog(getActivity());
        dialogChoice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChoice.setContentView(R.layout.dialog_choose_community_to_post);
        dialogChoice.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogChoice.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogChoice.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialogChoice.getWindow().setGravity(Gravity.CENTER);

        dialogChoice.show();
        dialogChoice.setCancelable(true);
        TextView_custom tvNext = (TextView_custom) dialogChoice.findViewById(R.id.tvNext);
        tvNext.setOnClickListener(view -> dialogChoice.dismiss());
        ImageView ivBack = (ImageView) dialogChoice.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialogChoice.dismiss();
            }
        });

        dialogChoice.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialogInterface)
            {
//                sCommunityIDs = "";
//                tvCommunity.setText(getActivity().getResources().getString(R.string.choose_a_community));
//
//                for(CommunityListData data : arrayList)
//                {
//                    if(data.getIs_community_follow())
//                    {
//                        sCommunityIDs = data.getId();
//                        tvCommunity.setText(""+data.getCategoryName());
//                        readyForPost();
//                        return;
//                    }
//                }
            }
        });

        ListView listview = (ListView) dialogChoice.findViewById(R.id.listview);
        listview.setAdapter(adapter);

        View footer = getLayoutInflater().inflate(R.layout.footer_loader, listview, false);
        listview.addFooterView(footer);




        listview.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && (listview.getLastVisiblePosition() ) >= (adapter.getCount() - 1))
                {
                    loadMore();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });

    }
    public void loadMore()
    {
        iPage++;
        getCommunityList();
    }
    private void getCommunityList()
    {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<CommunityList> apiRequest = getApi.getCommunityList("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),"" + iPage,""+iCount);
        apiRequest.enqueue(new Callback<CommunityList>()
        {
            @Override
            public void onResponse(Call<CommunityList> call, Response<CommunityList> response)
            {
                if(response.body() != null && response.body().getData() != null)
                {
                    List<CommunityListData> list = response.body().getData();
                    arrayList.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<CommunityList> call, Throwable t)
            {}
        });
    }
    private void photoOptionDialog()
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_photo_choice);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        dialog.setCancelable(true);

        dialog.setOnKeyListener(new Dialog.OnKeyListener()
        {
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        LinearLayout liCamera = (LinearLayout) dialog.findViewById(R.id.liCamera);
        liCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.hide();
                dispatchTakePictureIntent();

            }
        });
        LinearLayout liGallery = (LinearLayout) dialog.findViewById(R.id.liGallery);
        liGallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.hide();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), iGALLERY_REQUEST_CODE);
            }
        });
    }
    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            }
            catch (Exception ex)
            {

            }
            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),Constants.PACKAGE_NAME+".fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, iCAMERA_REQUEST_CODE);



            }
        }
    }
    private File createImageFile() throws IOException
    {
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                Constants.getTodayDateTime(),  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        sPrFilePath = image.getAbsolutePath();
        return image;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);



        try
        {

            switch (iCameraState)
            {
                case 1:
                {
                    ivSelectedPhoto.setVisibility(View.VISIBLE);
                    Bitmap mImageBitmap = null;
                    if (requestCode == iCAMERA_REQUEST_CODE)
                    {

                    }
                    else if (requestCode == iGALLERY_REQUEST_CODE)
                    {

                        if (data != null)
                        {
                            sPrFilePath = ImageFilePath.getPath(getActivity(), data.getData());

                        }
                    }

                    if(sPrFilePath != null && sPrFilePath.length() > 5)
                    {

                        mImageBitmap = BitmapFactory.decodeFile(sPrFilePath);
                       /* Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(mImageBitmap, 0, 0, mImageBitmap.getWidth(), mImageBitmap.getHeight(), matrix, true);

                        ExifInterface exif = new ExifInterface(sPrFilePath);
                        if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6"))
                        {
                            mImageBitmap = ThumbnailUtils.extractThumbnail(rotatedBitmap,100, 100);
                        }
                        else
                        {
                            mImageBitmap = ThumbnailUtils.extractThumbnail(mImageBitmap,100, 100);
                        }*/
                        sFileImage1 = sPrFilePath;
                        ivSelectedPhoto.setImageBitmap(mImageBitmap);
                    }
                }
                readyForPost();
                break;

            }

        }
        catch (Exception e)
        {

        }
    }
    private boolean validation()
    {
        if(sCommunityIDs.length() < 1)
        {
            ((ActivityHome)getActivity()).showToast(""+getResources().getString(R.string.choose_community));
            return false;
        }
        else if(etPostTitle.getText().toString().length() < 1)
        {
            ((ActivityHome)getActivity()).showToast(""+getResources().getString(R.string.enter_title));
            return false;
        }
        else if(sFileImage1.length() < 6)
        {
            ((ActivityHome)getActivity()).showToast(""+getResources().getString(R.string.attached_image_to_post));
            return false;
        }
        else
        {
            uploadFile();
            return true;
        }
    }

    private void uploadFile()
    {
        ((ActivityHome)getActivity()).showProgress();
        File file = new File(sFileImage1);
        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "post");
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<UploadFile> apiRequest = getApi.uploadFile("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),fileToUpload,type);

        apiRequest.enqueue(new Callback<UploadFile>()
        {
            @Override
            public void onResponse(Call<UploadFile> call, Response<UploadFile> response)
            {
//                ((ActivityHome)getActivity()).showToast(""+response.body().getMessage());
                if(response.body().getStatus().equalsIgnoreCase("success"))
                {
                    createPost(response.body().getData().getFilepath());
                }
                else
                {
                    ((ActivityHome)getActivity()).hideProgress();
                }
            }
            @Override
            public void onFailure(Call<UploadFile> call, Throwable t)
            {
                ((ActivityHome)getActivity()).hideProgress();
            }
        });
    }

    private void createPost(String sURL)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "image");
        jsonObject.addProperty("description", ""+etPostTitle.getText());
        jsonObject.addProperty("media", ""+sURL);
        jsonObject.addProperty("community_id", ""+sCommunityIDs);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);

        Call<CreatePost> apiRequest = getApi.createPost("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),"application/json",jsonObject);
        apiRequest.enqueue(new Callback<CreatePost>()
        {
            @Override
            public void onResponse(Call<CreatePost> call, Response<CreatePost> response)
            {
                ((ActivityHome)getActivity()).hideProgress();

                if(response.body().getStatus().equalsIgnoreCase("success"))
                {
                    ((ActivityHome)getActivity()).showToast(""+getResources().getString(R.string.successful_posted));
                    getActivity().onBackPressed();

                    Fragment fragment = new FragmentProfile_Base();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame_whole, fragment);
                    transaction.addToBackStack(fragment.getClass().getSimpleName());
                    transaction.commit();

                }
                else
                {
                    ((ActivityHome)getActivity()).showToast(""+response.body().getMessage());
                }
            }
            @Override
            public void onFailure(Call<CreatePost> call, Throwable t)
            {
                ((ActivityHome)getActivity()).hideProgress();
            }
        });
    }
    private boolean readyForPost()
    {
        boolean IsReady = true;

        if(sCommunityIDs.length() < 1)
        {
            IsReady = false;
        }
        else if(etPostTitle.getText().toString().trim().length() < 1)
        {
            IsReady = false;
        }
        else if(sFileImage1.length() < 6)
        {
            IsReady = false;
        }


        if(IsReady)
        {
            tvPost.setTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_title));
        }
        else
        {
            tvPost.setTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_text2));
        }
        return IsReady;
    }

    @Override
    public void callbackSelected(String sCommunityName, String sCommunityID) {
        sCommunityIDs = sCommunityID;
        tvCommunity.setText(getActivity().getResources().getString(R.string.choose_a_community));

        if(sCommunityID.length() < 1)
        {
            sCommunityIDs = "";
            tvCommunity.setText(getActivity().getResources().getString(R.string.choose_a_community));
        }
        else
        {
            sCommunityIDs = sCommunityID;
            tvCommunity.setText(sCommunityName);
        }
        readyForPost();
    }
}
