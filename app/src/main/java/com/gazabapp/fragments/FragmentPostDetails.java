package com.gazabapp.fragments;

import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.actionlisteners.GiveComment;
import com.gazabapp.actionlisteners.PostDataChanged;
import com.gazabapp.adapter.AdapterRV_Comments;
import com.gazabapp.customeview.EditText_custom;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.AddComment;
import com.gazabapp.pojo.PostCommentList;
import com.gazabapp.pojo.PostCommentListData;
import com.gazabapp.pojo.PostDetails;
import com.gazabapp.pojo.PostListData;
import com.gazabapp.pojo.ReactionAdd;
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

import static com.gazabapp.Constants.DP_LINK_POST;

public class FragmentPostDetails extends Fragment implements GiveComment
{
    private String COMMENT_ID = "";
    private int iREPLYING = -1;
    private int iPage = 1,iCount = 10,iTotalCount = 0;
    public static String POST_ID = "";
    public static PostListData postDetailsData = null;

    private SharedPreferences sharedPref = null;
    private EditText_custom etComment;

    private TextView_custom tvPost;
    private ImageView ivBack,ivShare,ivCurrentProfile;
    private Context context;



    private ArrayList<PostCommentListData> arrayList = new ArrayList<>();
    private ProgressBar progress_bar;
    private RecyclerView rcvComment;
    private AdapterRV_Comments adapterRV_comments;
    private LinearLayoutManager layoutManager = null;

    private PostDataChanged postDataChanged;
    private int Position ;
    public FragmentPostDetails(PostDataChanged postDataChanged,int Position)
    {
        this.postDataChanged = postDataChanged;
        this.Position = Position;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_post_details, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        context = getActivity();

        iPage = 1;iCount = 10;

        View viewHeader = inflater.inflate(R.layout.header_post_details_image, null, false);
        progress_bar = (ProgressBar) rootView.findViewById(R.id.progress_bar);


        ivBack = (ImageView) rootView.findViewById(R.id.ivBack);
        ivShare = (ImageView) rootView.findViewById(R.id.ivShare);
        ivCurrentProfile = (ImageView) rootView.findViewById(R.id.ivCurrentProfile);

        rcvComment = (RecyclerView) rootView.findViewById(R.id.rcvComment);
        adapterRV_comments = new AdapterRV_Comments(arrayList,getActivity(),this);
        rcvComment.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rcvComment.setLayoutManager(layoutManager);
        rcvComment.setAdapter(adapterRV_comments);
        rcvComment.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && iCount > 0 && iTotalCount > arrayList.size())
                {
                    iPage++;
                    getPostComments();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
            }
        });



        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getActivity().getResources().getDrawable(R.drawable.rcv_divider_line));
        rcvComment.addItemDecoration(divider);



        Glide.with(this).load(""+sharedPref.getString(Constants.shk_PICTURE,"")).into(ivCurrentProfile);

        ivShare.setOnClickListener(view -> ((ActivityHome)getActivity()).sharePost(DP_LINK_POST+postDetailsData.getId()));
        ivBack.setOnClickListener(view -> getActivity().onBackPressed());


        tvPost = (TextView_custom) rootView.findViewById(R.id.tvPost);
        etComment = (EditText_custom) rootView.findViewById(R.id.etComment);
        etComment.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEND)
                {
                    sendComment();
                }
                return false;
            }
        });

        etComment.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if(COMMENT_ID.length() > 0)
                {

                }
            }
        });

        tvPost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendComment();
            }
        });
//        getPostDetails();



        adapterRV_comments.setHeaderData(postDetailsData,postDataChanged,Position);
        arrayList.clear();
        iPage = 1;iCount = 10;
        arrayList.add(null);
        getPostComments();



        return rootView;
    }
    private void sendComment()
    {
        ((ActivityHome)getActivity()).hideSoftKeyboard();

        if(!etComment.getText().toString().isEmpty())
        {
            if(iREPLYING > -1)
            {
                try
                {
                    String sText = etComment.getText().toString().substring(iREPLYING).trim();
                    if(sText.length() > 0)
                    {
                        addComment(""+sText);
                    }
                    else
                    {
                        ((ActivityHome)getActivity()).showToast(getResources().getString(R.string.please_enter_comment));
                    }
                }
                catch (Exception e)
                {
                    addComment(""+etComment.getText());
                }
            }
            else
            {
                addComment(""+etComment.getText());
            }
        }
        else
        {
            ((ActivityHome)getActivity()).showToast(getResources().getString(R.string.please_enter_comment));
        }
    }

    private void updatePostDetails()
    {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<PostDetails> apiRequest = getApi.getPostDetails("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),POST_ID);
        apiRequest.enqueue(new Callback<PostDetails>()
        {
            @Override
            public void onResponse(Call<PostDetails> call, Response<PostDetails> response)
            {
                postDetailsData = response.body().getData();
            }
            @Override
            public void onFailure(Call<PostDetails> call, Throwable t)
            {

            }
        });
    }
    private void showLoader()
    {
        progress_bar.setVisibility(View.VISIBLE);
    }
    private void hideLoader()
    {
        progress_bar.setVisibility(View.GONE);
    }
    private void getPostComments()
    {
        showLoader();
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);
        Call<PostCommentList> apiRequest = getApi.getPostCommentList("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),postDetailsData.getId(),""+iPage,  ""+iCount);

        apiRequest.enqueue(new Callback<PostCommentList>()
        {
            @Override
            public void onResponse(Call<PostCommentList> call, Response<PostCommentList> response)
            {
                hideLoader();

                List<PostCommentListData> list = response.body().getData();
                arrayList.addAll(list);
                adapterRV_comments.notifyDataSetChanged();
                iTotalCount = response.body().getTotal();
                adapterRV_comments.setCommentCount(iTotalCount);
            }
            @Override
            public void onFailure(Call<PostCommentList> call, Throwable t)
            {
                hideLoader();
            }
        });


    }
    private void addComment(String sComment)
    {
        ((ActivityHome)getActivity()).showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("community_id", ""+postDetailsData.getCommunityId());
        jsonObject.addProperty("post_id", ""+postDetailsData.getId());
        jsonObject.addProperty("comment", ""+sComment);
        if(COMMENT_ID.length() > 0)
        {
            jsonObject.addProperty("parent_id", ""+COMMENT_ID);
        }


        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.API+"").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        RetroApis getApi = retrofit.create(RetroApis.class);

        Call<AddComment> apiRequest = getApi.addComment("Bearer "+sharedPref.getString(Constants.shk_ACCESS_TOKEN,""),"application/json",jsonObject);

        apiRequest.enqueue(new Callback<AddComment>()
        {
            @Override
            public void onResponse(Call<AddComment> call, Response<AddComment> response)
            {
                ((ActivityHome)getActivity()).hideProgress();
//                ((ActivityHome)getActivity()).showToast(""+response.body().getMessage());
                ((ActivityHome)getActivity()).showToast(""+getActivity().getResources().getString(R.string.comment_posted_successfully));
                etComment.setText("");
                COMMENT_ID = "";
                updatePostDetails();

                iCount = 10;iPage=1;
                arrayList.clear();
                arrayList.add(null);
                adapterRV_comments.notifyDataSetChanged();
                getPostComments();
            }
            @Override
            public void onFailure(Call<AddComment> call, Throwable t)
            {
                ((ActivityHome)getActivity()).hideProgress();
            }
        });
    }

    private void hideSystemUI()
    {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    private void showSystemUI()
    {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void clickOnReply(PostCommentListData data, int Position, boolean IsParent, int ChildPosition,String ParentCommentID)
    {
        if(data == null)
        {
            COMMENT_ID = "";
            etComment.setText("");
            iREPLYING = -1;
        }
        else
        {
            COMMENT_ID = ParentCommentID;
            etComment.setText(getResources().getString(R.string.replying)+" "+data.getUserName()+" ");
            etComment.setSelection(etComment.getText().toString().length());
            iREPLYING = etComment.getText().toString().length();
        }
        etComment.requestFocus();
    }
}
