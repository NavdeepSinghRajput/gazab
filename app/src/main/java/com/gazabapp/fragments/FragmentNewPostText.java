    package com.gazabapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.actionlisteners.SelectCommunity;
import com.gazabapp.adapter.Adapter_ChooseCommunityToPost;
import com.gazabapp.customeview.AutoResizeEditText;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.CommunityList;
import com.gazabapp.pojo.CommunityListData;
import com.gazabapp.pojo.CreatePost;
import com.gazabapp.serverapi.RetroApis;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentNewPostText extends Fragment implements SelectCommunity
{
    private ImageView ivBack;
    private TextView_custom tvPost,tvCommunity;
    private AutoResizeEditText etPostTitle;

    private SharedPreferences sharedPref = null;
    private int iPage = 1,iCount = 24;
    private ArrayList<CommunityListData> arrayList = new ArrayList<>();
    private Adapter_ChooseCommunityToPost adapter = null;
    private String sCommunityIDs = "";
    private LinearLayout liChooseCommunity;
    private ImageView ivTextColor1,ivTextColor2,ivTextColor3,ivTextColor4,ivTextColor5,ivTextColor6;
    private LinearLayout liTitle;
    private String SELECT_COLOR = "";
    public FragmentNewPostText()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_new_post_text, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        liTitle = (LinearLayout) rootView.findViewById(R.id.liTitle);

        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        tvPost = (TextView_custom) rootView.findViewById(R.id.tvPost);
        tvCommunity = (TextView_custom) rootView.findViewById(R.id.tvCommunity);

        ivTextColor1 = (ImageView) rootView.findViewById(R.id.ivTextColor1);
        ivTextColor2 = (ImageView) rootView.findViewById(R.id.ivTextColor2);
        ivTextColor3 = (ImageView) rootView.findViewById(R.id.ivTextColor3);
        ivTextColor4 = (ImageView) rootView.findViewById(R.id.ivTextColor4);
        ivTextColor5 = (ImageView) rootView.findViewById(R.id.ivTextColor5);
        ivTextColor6 = (ImageView) rootView.findViewById(R.id.ivTextColor6);


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        tvPost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                validation();
            }
        });
        etPostTitle = (AutoResizeEditText) rootView.findViewById(R.id.etPostTitle);

        ivTextColor1.setOnClickListener(v -> selectColor(1));
        ivTextColor2.setOnClickListener(v -> selectColor(2));
        ivTextColor3.setOnClickListener(v -> selectColor(3));
        ivTextColor4.setOnClickListener(v -> selectColor(4));
        ivTextColor5.setOnClickListener(v -> selectColor(5));
        ivTextColor6.setOnClickListener(v -> selectColor(6));


        etPostTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                readyForPost();
            }
        });

        adapter = new Adapter_ChooseCommunityToPost(getActivity(),arrayList,this);

        liChooseCommunity = (LinearLayout) rootView.findViewById(R.id.liChooseCommunity);
        liChooseCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_chooseCommunity();
            }
        });

        getCommunityList();
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

        ImageView ivBack = (ImageView) dialogChoice.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialogChoice.dismiss();
            }
        });
        TextView_custom tvNext = (TextView_custom) dialogChoice.findViewById(R.id.tvNext);
        tvNext.setOnClickListener(view -> dialogChoice.dismiss());
        dialogChoice.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialogInterface)
            {
//                sCommunityIDs = "";
//                tvCommunity.setText(getActivity().getResources().getString(R.string.choose_a_community));
//
//                System.out.println("=>>>>>>>>>>>>>>>>>1111111111");
//                for(CommunityListData data : arrayList)
//                {
//
//                    if(data.getIs_community_follow())
//                    {
//                        System.out.println("=>>>>>>>>>>>>>>>>>1222222222");
//                        sCommunityIDs = data.getId();
//                        tvCommunity.setText(""+data.getCategoryName());
//
//                        return;
//                    }
//                }
//                readyForPost();
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
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
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
        else
        {
            createPost();
            return true;
        }
    }

    private void createPost()
    {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "text");
        jsonObject.addProperty("description", ""+etPostTitle.getText());
        jsonObject.addProperty("community_id", ""+sCommunityIDs);
        jsonObject.addProperty("media", ""+SELECT_COLOR);
        jsonObject.addProperty("link", ""+SELECT_COLOR);

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
                if(response.body().getStatus().equalsIgnoreCase("success"))
                {
                    ((ActivityHome)getActivity()).showToast(""+getResources().getString(R.string.successful_posted));
                    getActivity().onBackPressed();
                    Fragment fragment = new FragmentProfile_Base();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_frame_whole, fragment);
//                    transaction.detach(fragment);
//                    transaction.attach(fragment);
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
            {}
        });
    }


    private void selectColor(int iSelection)
    {
        switch (iSelection)
        {
            case 1:
            {
                SELECT_COLOR = "#" + Integer.toHexString(ContextCompat.getColor (getActivity(), R.color.create_text_post_choose_color_1) & 0x00ffffff);

                liTitle.setBackground(getActivity().getResources().getDrawable(R.drawable.back_new_post_text_title_1));
                etPostTitle.setTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_field));
                etPostTitle.setHintTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_field));
            }
            break;
            case 2:
            {
                SELECT_COLOR = "#" + Integer.toHexString(ContextCompat.getColor (getActivity(), R.color.create_text_post_choose_color_2) & 0x00ffffff);

                liTitle.setBackground(getActivity().getResources().getDrawable(R.drawable.back_new_post_text_title_2));
                etPostTitle.setTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_field));
                etPostTitle.setHintTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_field));
            }
            break;
            case 3:
            {


                SELECT_COLOR = "#" + Integer.toHexString(ContextCompat.getColor (getActivity(), R.color.create_text_post_choose_color_3) & 0x00ffffff);

                liTitle.setBackground(getActivity().getResources().getDrawable(R.drawable.back_new_post_text_title_3));
                etPostTitle.setTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_field));
                etPostTitle.setHintTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_field));

            }
            break;
            case 4:
            {
                SELECT_COLOR = "#" + Integer.toHexString(ContextCompat.getColor (getActivity(), R.color.create_text_post_choose_color_4) & 0x00ffffff);

                liTitle.setBackground(getActivity().getResources().getDrawable(R.drawable.back_new_post_text_title_4));
                etPostTitle.setTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_field_in));
                etPostTitle.setHintTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_field_in));
            }
            break;
            case 5:
            {
                SELECT_COLOR = "#" + Integer.toHexString(ContextCompat.getColor (getActivity(), R.color.create_text_post_choose_color_5) & 0x00ffffff);

                liTitle.setBackground(getActivity().getResources().getDrawable(R.drawable.back_new_post_text_title_5));
                etPostTitle.setTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_field_in));
                etPostTitle.setHintTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_field_in));

            }
            break;
            case 6:
            {

                SELECT_COLOR = "#" + Integer.toHexString(ContextCompat.getColor (getActivity(), R.color.create_text_post_choose_color_6) & 0x00ffffff);

                liTitle.setBackground(getActivity().getResources().getDrawable(R.drawable.back_new_post_text_title_6));
                etPostTitle.setTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_field));
                etPostTitle.setHintTextColor(((ActivityHome)getActivity()).getAttributeColor(R.attr.new_post_field));
            }
            break;
        }
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
    public void callbackSelected(String sCommunityName, String sCommunityID)
    {
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
