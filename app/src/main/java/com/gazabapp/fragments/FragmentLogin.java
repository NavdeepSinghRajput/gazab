package com.gazabapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gazabapp.ActivityHome;
import com.gazabapp.ActivityRegistration;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.customeview.EditText_custom;
import com.gazabapp.pojo.Login;
import com.gazabapp.pojo.LoginData;
import com.gazabapp.pojo.VerifyOTP;
import com.gazabapp.pojo.VerifyOTPData;
import com.gazabapp.serverapi.ApiClient;
import com.gazabapp.serverapi.RetroApis;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentLogin extends Fragment
{
    private ImageView ivFaacebook,ivGoogle;
    private EditText_custom etMobileNo;
    private ImageView ivTick;
    private LoginButton facebooklogin ;
    private CallbackManager callbackManager;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btGoogle;
    private SharedPreferences sharedPref = null;

    public FragmentLogin()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        etMobileNo = (EditText_custom) rootView.findViewById(R.id.etMobileNo);
        ivTick = (ImageView) rootView.findViewById(R.id.ivTick);
        etMobileNo.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if(editable.length() == 10 )
                    ivTick.setEnabled(true);
                else
                    ivTick.setEnabled(false);
            }
        });
        ivTick.setEnabled(false);


        ivTick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                done();
            }
        });





        ivFaacebook = (ImageView) rootView.findViewById(R.id.ivFaacebook);
        ivGoogle = (ImageView) rootView.findViewById(R.id.ivGoogle);

        callbackManager = CallbackManager.Factory.create();
        facebooklogin = (LoginButton) rootView.findViewById(R.id.facebooklogin);
        facebooklogin.setReadPermissions(Arrays.asList("public_profile","email"));
        facebooklogin.setFragment(this);

        facebooklogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                getFBUserDetails(loginResult.getAccessToken());
            }
            @Override
            public void onCancel(){}
            @Override
            public void onError(FacebookException exception){}
        });
        ivFaacebook.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                facebooklogin.callOnClick();
            }
        });

        ivGoogle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(GoogleSignIn.getLastSignedInAccount(getActivity()) == null)
                    signInGoogle();
                else
                    signOutGoogle();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        btGoogle = rootView.findViewById(R.id.btGoogle);
        btGoogle.setSize(SignInButton.SIZE_WIDE);
        btGoogle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signInGoogle();
            }
        });

        etMobileNo.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    done();
                    handled = true;
                }
                return handled;
            }
        });

        return rootView;
    }
    private void signInGoogle()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOutGoogle()
    {
        mGoogleSignInClient.signOut();
    }
    private void doLogin(String sMobileNo)
    {
        ((ActivityRegistration)getActivity()).showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile", sMobileNo);


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
//        Call<Login> apiRequest = getApi.getLogin(jsonObject);
        Call<Login> apiRequest = ApiClient.getGazabApi().getLogin(jsonObject);

        apiRequest.enqueue(new Callback<Login>()
        {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response)
            {
                ((ActivityRegistration)getActivity()).hideProgress();
                if(response.body() == null || response.body().getStatus() == null)
                {
                    ((ActivityRegistration)getActivity()).showToast(""+getResources().getString(R.string.communication_error));
                    return;
                }
                if(response.body().getStatus().equalsIgnoreCase("success"))
                {
                    LoginData loginData = response.body().getLoginData();
                    showOTPScreen(loginData);
                }
                else
                {

                }
//                ((ActivityRegistration)getActivity()).showToast(""+response.body().getMessage());
            }
            @Override
            public void onFailure(Call<Login> call, Throwable t)
            {
                ((ActivityRegistration)getActivity()).hideProgress();
            }
        });


    }
    private void showOTPScreen(LoginData loginData)
    {
        FragmentEnterOTP.MOBILE_NO = loginData.getMobile();
        FragmentEnterOTP.loginData = loginData;
        Fragment fragment = new FragmentEnterOTP();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String androidId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("source", "Google");
            jsonObject.addProperty("id", ""+account.getId());
            jsonObject.addProperty("email", ""+account.getEmail());
            jsonObject.addProperty("display_name", ""+account.getDisplayName());
            jsonObject.addProperty("device_type", "android");
            jsonObject.addProperty("device_id", ""+androidId);
            jsonObject.addProperty("access_token", ""+account.getIdToken());
            socialLogin(jsonObject);

        } catch (ApiException e)
        {
//            ((ActivityRegistration)getActivity()).showToast("Error "+e);
        }
    }
    private void socialLogin(final JsonObject jsonObject)
    {
        ((ActivityRegistration)getActivity()).showProgress();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<VerifyOTP> apiRequest = getApi.socialLogin("application/json",jsonObject);
        apiRequest.enqueue(new Callback<VerifyOTP>()
        {
            @Override
            public void onResponse(Call<VerifyOTP> call, Response<VerifyOTP> response)
            {
                ((ActivityRegistration)getActivity()).hideProgress();
                if(response.body() == null || response.body().getStatus() == null)
                {
                    ((ActivityRegistration)getActivity()).showToast(""+getResources().getString(R.string.communication_error));
                    return;
                }
                if(response.body().getStatus().equalsIgnoreCase("success"))
                {
                    VerifyOTPData verifyOTPData = response.body().getVerifyOTPData();

                    sharedPref.edit().putString(Constants.shk_ACCESS_TOKEN,""+verifyOTPData.getAccessToken()).commit();
                    sharedPref.edit().putString(Constants.shk_ID,""+verifyOTPData.getId()).commit();
                    sharedPref.edit().putString(Constants.shk_MOBILE_NO,""+verifyOTPData.getMobile()).commit();
                    sharedPref.edit().putString(Constants.shk_DISPLAY_NAME,""+verifyOTPData.getDisplayName()).commit();
                    sharedPref.edit().putString(Constants.shk_EMAIL,""+verifyOTPData.getEmail()).commit();
                    sharedPref.edit().putString(Constants.shk_NAME,""+verifyOTPData.getName()).commit();
                    sharedPref.edit().putString(Constants.shk_PICTURE,""+verifyOTPData.getPicture()).commit();
                    sharedPref.edit().putString(Constants.shk_AGE_GROUP,""+verifyOTPData.getAgeGroup()).commit();
                    sharedPref.edit().putString(Constants.shk_BIO,""+verifyOTPData.getBio()).commit();


                    Fragment fragment = null;
                    if(verifyOTPData.getAgeGroup().length() < 1)
                    {
                        fragment = new FragmentBirthdaySelect();
                    }
                    else  if(verifyOTPData.getGender().length() < 1)
                    {
                        fragment = new FragmentGenderSelect();
                    }
                    else  if(verifyOTPData.getName().length() < 1)
                    {
                        fragment = new FragmentCreateUserName();
                    }
                    else
                    {
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), ActivityHome.class));
                    }

                    if(fragment != null)
                    {
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
                        transaction.replace(R.id.content_frame, fragment);
                        transaction.addToBackStack(fragment.getClass().getSimpleName());
                        transaction.commit();
                    }
                }
                else
                {
//                    ((ActivityRegistration)getActivity()).showToast(""+response.body().getMessage());
                }
            }
            @Override
            public void onFailure(Call<VerifyOTP> call, Throwable t)
            {
                ((ActivityRegistration)getActivity()).hideProgress();
            }
        });
    }


    private void getFBUserDetails(final AccessToken accessToken)
    {
        if (accessToken != null) {
            GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try
                    {
                        String androidId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("source", "Facebook");
                        jsonObject.addProperty("id", ""+object.getString("id"));
                        jsonObject.addProperty("email", ""+object.getString("email"));
                        jsonObject.addProperty("display_name", ""+object.getString("name"));
                        jsonObject.addProperty("device_type", "android");
                        jsonObject.addProperty("device_id", ""+androidId);
                        jsonObject.addProperty("access_token", ""+accessToken.getToken());

//                        ((ActivityRegistration)getActivity()).showToast(""+jsonObject.toString());

                        socialLogin(jsonObject);


                    } catch (Exception e) {

                    }
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name,last_name,email,gender,birthday,name");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    private void done()
    {
        doLogin("+91"+etMobileNo.getText());
    }
}
