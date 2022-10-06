package com.gazabapp.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.actionlisteners.ProfileData;
import com.gazabapp.customeview.EditText_custom;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.imageProcessing.CropActivity;
import com.gazabapp.pojo.Deactivate;
import com.gazabapp.pojo.UploadFile;
import com.gazabapp.pojo.VerifyOTP;
import com.gazabapp.pojo.VerifyOTPData;
import com.gazabapp.serverapi.ApiClient;
import com.gazabapp.serverapi.RetroApis;
import com.gazabapp.utils.ImageFilePath;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gazabapp.Constants.PRINT;

public class FragmentProfile_Edit extends Fragment {

    private TextView_custom tvEditPhoto, tvUserName, tvEmail, tvSave;
    private EditText_custom etBio;
    private ImageView ivBack;
    CircleImageView ivProfilePhoto;
    private SharedPreferences sharedPref = null;

    private final int iCAMERA_REQUEST_CODE = 12, iGALLERY_REQUEST_CODE = 13;
    private String sFileImage1 = "", sPrFilePath = "";
    private int iCameraState = 0;
    private ProfileData profileData = null;
    Uri outputFileUri;
    BroadcastReceiver mReceiver;

    public FragmentProfile_Edit(ProfileData profileData) {
        this.profileData = profileData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        tvEditPhoto = (TextView_custom) rootView.findViewById(R.id.tvEditPhoto);
        etBio = (EditText_custom) rootView.findViewById(R.id.etBio);
        tvUserName = (TextView_custom) rootView.findViewById(R.id.tvUserName);
        tvEmail = (TextView_custom) rootView.findViewById(R.id.tvEmail);
        tvSave = (TextView_custom) rootView.findViewById(R.id.tvSave);

        etBio.setText("" + sharedPref.getString(Constants.shk_BIO, ""));

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sFileImage1.length() > 5) {
                    ((ActivityHome) getActivity()).showProgress();
                    putData("" + sFileImage1, 1);

                } else {
//                    ((ActivityHome)getActivity()).showToast(""+getActivity().getResources().getString(R.string.attached_image_to_profile));
                    ((ActivityHome) getActivity()).showProgress();
                    putData("" + etBio.getText(), 2);
                }
            }
        });

        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        ivProfilePhoto = (CircleImageView) rootView.findViewById(R.id.ivProfilePhoto);

        ivProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(getActivity()).withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    iCameraState = 1;
                                    sPrFilePath = "";
                                    photoOptionDialog();
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
        });

        LinearLayout liDeleteAccount = (LinearLayout) rootView.findViewById(R.id.liDeleteAccount);
        liDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivateAC();
            }
        });

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(getContext().getResources().getDrawable(R.drawable.profile_pic_placeholder));
        requestOptions.error(getContext().getResources().getDrawable(R.drawable.profile_pic_placeholder));
        Glide.with(getActivity()).load("" + sharedPref.getString(Constants.shk_PICTURE, "")).apply(requestOptions).into(ivProfilePhoto);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("Uri","uriiiiii");
                Log.e("Uri",intent.getStringExtra("imageUri")+"sdasdasdas");
                try {
                    File file = new File(outputFileUri.getPath());
                    file.delete();
                } catch (Exception e) {
                    Log.e("exception", e.getMessage());
                }
                sFileImage1 = intent.getStringExtra("imageUri");
                uploadFile();
            }
        };
        IntentFilter intentFilter = new IntentFilter(getString(R.string.app_name) + "photo.croped");
        getActivity().registerReceiver(mReceiver, intentFilter);
        return rootView;
    }

    private void photoOptionDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_photo_choice);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        dialog.setCancelable(true);

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
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
            public void onClick(View view) {
                dialog.hide();
                captureCameraImage(99);
//                dispatchTakePictureIntent();

            }
        });
        LinearLayout liGallery = (LinearLayout) dialog.findViewById(R.id.liGallery);
        liGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 13);
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select Picture"), iGALLERY_REQUEST_CODE);
            }
        });
    }

    void captureCameraImage(int code) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        outputFileUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, code);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), Constants.PACKAGE_NAME + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, iCAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                Constants.getTodayDateTime(),  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        sPrFilePath = image.getAbsolutePath();
        return image;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case 99: {
                    try {
                        String selectedContentPath = getRealPathFromURI(outputFileUri);
                        if (new File(selectedContentPath).exists()) {
                            Log.e("filepath!*", "" + selectedContentPath);
                            final File file = new File(selectedContentPath);
                            OutputStream os;
                            try {
                                Bitmap bitmap = null;
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                                bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
                                os = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                                os.flush();
                                os.close();
                            } catch (Exception e) {
                                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                            }
                            sFileImage1 = selectedContentPath;
//                            Intent intent = new Intent(getActivity(), CropActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra("sign", "profile");
//                            intent.putExtra("bitmap", outputFileUri.toString());
//                            startActivity(intent);

                            uploadFile();
                            setImage();
//                            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                            Glide.
//                            ivProfilePhoto.setImageBitmap(myBitmap);

                        }
                    } catch (Exception e) {
                        Log.e("exception", e.toString());
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                    break;
                }
                case 200: {
                    Log.e("imageProcessding","Sasasasasa");
                    if (data != null) {
                        sFileImage1 = data.getStringExtra("imageUri");

                    }
                    break;
                }
                case 13: {
                    if (data == null) {
                        Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            Uri selectedContentUri = data.getData();
                            String selectedContentPath = ImageFilePath.getPath(getContext(), selectedContentUri); //super.getPath(selectedContentUri);
                            Log.e("filepath!*", "" + selectedContentPath);
                            Log.e("filepath!*", "" + selectedContentPath);
                            File file = new File(selectedContentPath);
                            sFileImage1 = selectedContentPath;
                            uploadFile();
//                            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                            ivProfilePhoto.setImageBitmap(myBitmap);
//                            setImage();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                }
            }
           /* switch (iCameraState)
            {
                case 1:
                {
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
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(mImageBitmap, 0, 0, mImageBitmap.getWidth(), mImageBitmap.getHeight(), matrix, true);

                        ExifInterface exif = new ExifInterface(sPrFilePath);
                      *//*  if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6"))
                        {
                            mImageBitmap = ThumbnailUtils.extractThumbnail(rotatedBitmap,100, 100);
                        }
                        else
                        {
                            mImageBitmap = ThumbnailUtils.extractThumbnail(mImageBitmap,100, 100);
                        }*//*
                        sFileImage1 = sPrFilePath;
                        ivProfilePhoto.setImageBitmap(mImageBitmap);
                    }
                }
                break;

            }*/

        } catch (Exception e) {

        }
    }

    private void setImage() {
        Bitmap mImageBitmap = BitmapFactory.decodeFile(sPrFilePath);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(mImageBitmap, 0, 0, mImageBitmap.getWidth(), mImageBitmap.getHeight(), matrix, true);
        ivProfilePhoto.setImageBitmap(mImageBitmap);
    }

    private void uploadFile() {
        ((ActivityHome) getActivity()).showProgress();
        File file = new File(sFileImage1);
        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "profile");
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
//        Call<UploadFile> apiRequest = getApi.uploadFile("Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), fileToUpload, type);
        Call<UploadFile> apiRequest = ApiClient.getGazabApi().uploadFile("Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), fileToUpload, type);

        apiRequest.enqueue(new Callback<UploadFile>() {
            @Override
            public void onResponse(Call<UploadFile> call, Response<UploadFile> response) {

                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success") && response.body().getData().getFilepath().length() > 10) {
                    sFileImage1 = response.body().getData().getFilepath();
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(getContext().getResources().getDrawable(R.drawable.profile_pic_placeholder));
                    requestOptions.error(getContext().getResources().getDrawable(R.drawable.profile_pic_placeholder));

                    Glide.with(getActivity()).load(sFileImage1).apply(requestOptions).into(ivProfilePhoto);
                    ((ActivityHome) getActivity()).hideProgress();
                    ((ActivityHome) getActivity()).hideProgress();
                    ((ActivityHome) getActivity()).hideProgress();
                } else {
                    ((ActivityHome) getActivity()).hideProgress();
                }
            }

            @Override
            public void onFailure(Call<UploadFile> call, Throwable t) {
                ((ActivityHome) getActivity()).hideProgress();
            }
        });
    }

    private void putData(String sData, int iState) {
//
//        ((ActivityHome)getActivity()).showProgress();
        JsonObject jsonObject = new JsonObject();

        if (iState == 1) {
            jsonObject.addProperty("image", sData);
            jsonObject.addProperty("bio", "" + etBio.getText());
        } else if (iState == 2) {
            jsonObject.addProperty("bio", sData);
        }


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);

        Call<VerifyOTP> apiRequest = getApi.putUserDetail(sharedPref.getString(Constants.shk_ID, ""), "Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "application/json", jsonObject);
//        Call<VerifyOTP> apiRequest = ApiClient.getGazabApi().putUserDetail(sharedPref.getString(Constants.shk_ID, ""), "Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "application/json", jsonObject);

        apiRequest.enqueue(new Callback<VerifyOTP>() {
            @Override
            public void onResponse(Call<VerifyOTP> call, Response<VerifyOTP> response) {
                ((ActivityHome) getActivity()).hideProgress();


                PRINT("DONE ------------ ");

                if (response.body().getStatus() == null) {
                    ((ActivityHome) getActivity()).showToast("" + getResources().getString(R.string.communication_error));
                    return;
                }
                if (response.body().getStatus().equalsIgnoreCase("success")) {
                    VerifyOTPData verifyOTPData = response.body().getVerifyOTPData();

                    Gson gson = new GsonBuilder().create();
                    String json = gson.toJson(verifyOTPData);

                    sharedPref.edit().putString(Constants.shk_DISPLAY_NAME, "" + verifyOTPData.getDisplayName()).commit();
                    sharedPref.edit().putString(Constants.shk_EMAIL, "" + verifyOTPData.getEmail()).commit();
                    sharedPref.edit().putString(Constants.shk_NAME, "" + verifyOTPData.getName()).commit();
                    sharedPref.edit().putString(Constants.shk_PICTURE, "" + verifyOTPData.getPicture()).commit();
                    sharedPref.edit().putString(Constants.shk_AGE_GROUP, "" + verifyOTPData.getAgeGroup()).commit();
                    sharedPref.edit().putString(Constants.shk_BIO, "" + verifyOTPData.getBio()).commit();

                    profileData.onProfileChange(true, true);
                    Log.e("uploaded", "uploaded");
                    ((ActivityHome) getActivity()).updateUserDetails();
                    ((ActivityHome) getActivity()).onBackPressed();
                } else {
                }
            }

            @Override
            public void onFailure(Call<VerifyOTP> call, Throwable t) {
                ((ActivityHome) getActivity()).hideProgress();
            }
        });
    }

    private void deActivateAC() {

        ((ActivityHome) getActivity()).showProgress();
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);

        Call<Deactivate> apiRequest = getApi.deactivateAccount(sharedPref.getString(Constants.shk_ID, ""), "Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "application/json");
        apiRequest.enqueue(new Callback<Deactivate>() {
            @Override
            public void onResponse(Call<Deactivate> call, Response<Deactivate> response) {
                ((ActivityHome) getActivity()).hideProgress();
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    ((ActivityHome) getActivity()).logout();
                } else {
                    ((ActivityHome) getActivity()).showToast(getActivity().getString(R.string.communication_error));
                }
            }

            @Override
            public void onFailure(Call<Deactivate> call, Throwable t) {
                ((ActivityHome) getActivity()).hideProgress();
            }
        });
    }

   }
