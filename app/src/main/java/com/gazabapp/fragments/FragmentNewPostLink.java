package com.gazabapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import com.gazabapp.serverapi.ApiClient;
import com.gazabapp.serverapi.RetroApis;
import com.google.gson.JsonObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentNewPostLink extends Fragment implements SelectCommunity
{
    private ImageView ivBack;
    private TextView_custom tvPost,tvCommunity;
    private EditText_custom etPostTitle,etDescription,etLink;

    private SharedPreferences sharedPref = null;
    private int iPage = 1,iCount = 24;
    private ArrayList<CommunityListData> arrayList = new ArrayList<>();
    private Adapter_ChooseCommunityToPost adapter = null;
    private String sCommunityIDs = "";
    private LinearLayout liChooseCommunity;

    public FragmentNewPostLink()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_new_post_link, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        tvPost = (TextView_custom) rootView.findViewById(R.id.tvPost);
        tvCommunity = (TextView_custom) rootView.findViewById(R.id.tvCommunity);


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
        etPostTitle = (EditText_custom) rootView.findViewById(R.id.etPostTitle);
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

        etDescription = (EditText_custom) rootView.findViewById(R.id.etDescription);
        etLink = (EditText_custom) rootView.findViewById(R.id.etLink);
        etLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 5)
                {
                    readyForPost();
                }
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
        ivBack.setOnClickListener(view -> dialogChoice.dismiss());
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
        else if(!Patterns.WEB_URL.matcher(etLink.getText().toString()).matches())
        {
            ((ActivityHome)getActivity()).showToast(""+getResources().getString(R.string.enter_url));
            return false;
        }
        else
        {
            createPost();
//            getWebLinkDetails(""+etLink.getText());
            return true;
        }
    }

    private void createPost()
    {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "link");
        jsonObject.addProperty("description", ""+etPostTitle.getText());
        jsonObject.addProperty("link", ""+etLink.getText());
        jsonObject.addProperty("community_id", ""+sCommunityIDs);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
//        Call<CreatePost> apiRequest = getApi.createPost("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),"application/json",jsonObject);
        Call<CreatePost> apiRequest = ApiClient.getGazabApi().createPost("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),"application/json",jsonObject);
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

    private void getWebLinkDetails(String sURL)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ((ActivityHome)getActivity()).showProgress();
                UnfurlResult result = new UnfurlResult();
                try
                {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    Document doc = Jsoup.connect(sURL).get();
                    Elements metaProperties = doc.select("meta[property]");
                    for (Element element : metaProperties)
                    {
                        String property = element.attr("property");
                        if (TextUtils.isEmpty(property))
                        {
                            continue;
                        }
                        switch (property)
                        {
                            case "og:title":
                                result.title = element.attr("content");
                                break;
                            case "og:description":
                                result.description= element.attr("content");
                                break;
                            case "og:image":
                                result.imageUrl = element.attr("content");
                                break;
                            case "og:image:width":
                                result.width = element.attr("content");
                                break;
                            case "og:image:height":
                                result.height = element.attr("content");
                                break;
                            default:
                                break;
                        }
                    }
//                    System.out.println("1META Data: "+result.imageUrl);
//                    System.out.println("2META Data: "+result.title);
//                    System.out.println("3META Data: "+result.description);
                }catch (Exception e)
                {
                }

//                createPost(result);
            }
        }).start();
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
        else if(!Patterns.WEB_URL.matcher(etLink.getText().toString()).matches())
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

    class UnfurlResult
    {
        String title;
        String description;
        String imageUrl;
        String width;
        String height;
        UnfurlResult() {}
    }
}
