package com.gazabapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gazabapp.ActivityHome;
import com.gazabapp.ActivityRegistration;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.customeview.Button_custom;
import com.gazabapp.customeview.EditText_custom;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.VerifyOTP;
import com.gazabapp.pojo.VerifyOTPData;
import com.gazabapp.serverapi.RetroApis;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentBirthdaySelect extends Fragment {
    private EditText_custom etDay, etMonth, etYear;
    private TextView_custom tvStatus;
    private Button_custom btNext, btBack;
    private SharedPreferences sharedPref = null;
    private ImageView ivNext;

    public FragmentBirthdaySelect() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_birthday_select, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        etDay = (EditText_custom) rootView.findViewById(R.id.etDay);
        etMonth = (EditText_custom) rootView.findViewById(R.id.etMonth);
        etYear = (EditText_custom) rootView.findViewById(R.id.etYear);

        tvStatus = (TextView_custom) rootView.findViewById(R.id.tvStatus);
        tvStatus.setVisibility(View.INVISIBLE);
        tvStatus.setText("");

        ivNext = (ImageView) rootView.findViewById(R.id.ivNext);
        ivNext.setEnabled(false);
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });

        etDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 2) {
                    etMonth.requestFocus();
                }
            }
        });
        etMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 2) {
                    etYear.requestFocus();
                } else if (editable.toString().length() == 0) {
                    etDay.requestFocus();
                }
            }
        });
        etYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0) {
                    etMonth.requestFocus();
                }
                if (!validateDate()) {
//                    tvStatus.setText(getResources().getString(R.string.birthdate_enter_valid_date));
                    ivNext.setEnabled(false);
                } else {
                    ivNext.setEnabled(true);
                }
            }
        });
        btBack = (Button_custom) rootView.findViewById(R.id.btBack);
        btNext = (Button_custom) rootView.findViewById(R.id.btNext);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done();

            }
        });

        etYear.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    done();
                    handled = true;
                }
                return handled;
            }
        });
        return rootView;
    }

    private boolean validateDate() {

        if (etDay.getText().toString().isEmpty() || Integer.parseInt(etDay.getText().toString()) > 31)
            return false;
        else if (etMonth.getText().toString().isEmpty() || Integer.parseInt(etMonth.getText().toString()) > 12)
            return false;
        else if (etYear.getText().toString().isEmpty() || Integer.parseInt(etYear.getText().toString()) < 1900 || Integer.parseInt(etYear.getText().toString()) > 2100)
            return false;
        else if (!checkDateFormat(etDay.getText() + "/" + etMonth.getText() + "/" + etYear.getText()))
            return false;
        else
            return true;
    }

    private Boolean checkDateFormat(String date) {
        if (date == null || !date.matches("^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$"))
            return false;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            format.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }


    private void putData(String sData) {
        ((ActivityRegistration) getActivity()).showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("age_group", sData);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API + "").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);

        Call<VerifyOTP> apiRequest = getApi.putUserDetail(sharedPref.getString(Constants.shk_ID, ""), "Bearer " + sharedPref.getString(Constants.shk_ACCESS_TOKEN, ""), "application/json", jsonObject);

        apiRequest.enqueue(new Callback<VerifyOTP>() {
            @Override
            public void onResponse(Call<VerifyOTP> call, Response<VerifyOTP> response) {
                ((ActivityRegistration) getActivity()).hideProgress();
                if (response.body() == null || response.body().getStatus() == null) {
                    ((ActivityRegistration) getActivity()).showToast("" + getResources().getString(R.string.communication_error));
                    return;
                }
                if (response.body().getStatus().equalsIgnoreCase("success")) {
                    VerifyOTPData verifyOTPData = response.body().getVerifyOTPData();


                    sharedPref.edit().putString(Constants.shk_DISPLAY_NAME, "" + verifyOTPData.getDisplayName()).commit();
                    sharedPref.edit().putString(Constants.shk_EMAIL, "" + verifyOTPData.getEmail()).commit();
                    sharedPref.edit().putString(Constants.shk_NAME, "" + verifyOTPData.getName()).commit();
                    sharedPref.edit().putString(Constants.shk_PICTURE, "" + verifyOTPData.getPicture()).commit();
                    sharedPref.edit().putString(Constants.shk_AGE_GROUP, "" + verifyOTPData.getAgeGroup()).commit();
                    sharedPref.edit().putString(Constants.shk_BIO, "" + verifyOTPData.getBio()).commit();

                    Fragment fragment = null;
                    if (verifyOTPData.getGender().length() < 1) {
                        fragment = new FragmentGenderSelect();
                    } else if (verifyOTPData.getName().length() < 1) {
                        fragment = new FragmentCreateUserName();
                    } else {
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), ActivityHome.class));
                    }
                    fragment = new FragmentGenderSelect();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom, R.anim.slide_up, R.anim.slide_bottom);
                    transaction.replace(R.id.content_frame, fragment);
                    transaction.addToBackStack(fragment.getClass().getSimpleName());
                    transaction.commit();

                } else {
                    tvStatus.setText("" + response.body().getMessage());

                }
            }

            @Override
            public void onFailure(Call<VerifyOTP> call, Throwable t) {
                ((ActivityRegistration) getActivity()).hideProgress();
            }
        });
    }

    private void done() {
        if (validateDate()) {
            putData(etDay.getText() + "/" + etMonth.getText() + "/" + etYear.getText());
        } else {
            tvStatus.setText(getResources().getString(R.string.birthdate_enter_valid_date));
        }
    }
}
