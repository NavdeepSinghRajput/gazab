package com.gazabapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.actionlisteners.PostDataChanged;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.PostListData;

import static com.gazabapp.Constants.DP_LINK_POST;

public class FragmentPostTap extends Fragment
{


    private SharedPreferences sharedPref = null;
    public static PostListData postDetailsData = null;

    private TextView_custom tvTitle,tvCommunityName,tvCommentCount,tvShare,tvUp,tvDown,tvDownload;
    private ImageView ivProfile,ivPoster,ivShare,ivClose,ivDownload;
    private LinearLayout liMain,liContainer,liComment;
    public RadioButton rbtUpVote , rbtDownVote;
    public WebView webview;

    private PostDataChanged postDataChanged;
    private int Position ;

    public FragmentPostTap(PostDataChanged postDataChanged,int Position)
    {
        this.postDataChanged = postDataChanged;
        this.Position = Position;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_post_tap, container, false);
        sharedPref = getActivity().getSharedPreferences(Constants.PACKAGE_NAME, getActivity().MODE_PRIVATE);


        tvTitle = (TextView_custom) rootView.findViewById(R.id.tvTitle);
        tvCommunityName = (TextView_custom) rootView.findViewById(R.id.tvCommunityName);
        tvCommentCount = (TextView_custom) rootView.findViewById(R.id.tvCommentCount);
        tvShare = (TextView_custom) rootView.findViewById(R.id.tvShare);
        tvUp = (TextView_custom) rootView.findViewById(R.id.tvUp);
        tvDown = (TextView_custom) rootView.findViewById(R.id.tvDown);
        tvDownload = (TextView_custom) rootView.findViewById(R.id.tvDownload);

        ivProfile = (ImageView) rootView.findViewById(R.id.ivProfile);
        ivPoster = (ImageView) rootView.findViewById(R.id.ivPoster);
        ivDownload = (ImageView) rootView.findViewById(R.id.ivDownload);
        ivClose = (ImageView) rootView.findViewById(R.id.ivClose);
        ivShare = (ImageView) rootView.findViewById(R.id.ivShare);

        this.webview = (WebView) rootView.findViewById(R.id.webview);

        liContainer = (LinearLayout) rootView.findViewById(R.id.liContainer);
        liMain = (LinearLayout) rootView.findViewById(R.id.liMain);
        liComment = (LinearLayout) rootView.findViewById(R.id.liComment);


        rbtUpVote = (RadioButton) rootView.findViewById(R.id.rbtUpVote);
        rbtDownVote = (RadioButton) rootView.findViewById(R.id.rbtDownVote);



        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ((ActivityHome) getActivity()).downloadContent(postDetailsData.getMedia());
            }
        });

        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityHome)getActivity()).sharePost(DP_LINK_POST+postDetailsData.getId());
            }
        });


        ivClose.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        int iContWidth = Constants.WIDTH - 10;
        int iContHeight = (int) (Constants.HEIGHT/3.1);

        if(postDetailsData.getDimension() != null && postDetailsData.getDimension().getWidth() != null && postDetailsData.getDimension().getHeight() != null)
        {
            iContWidth = postDetailsData.getDimension().getWidth();
            iContHeight = postDetailsData.getDimension().getHeight();
        }
        int iScrWidth = Constants.WIDTH - 10;
        int iScrHeight = Constants.HEIGHT - 300;
        int iNewWidth = 100,iNewHeight = 100;
        if(iContWidth >= iContHeight) //LandScape
        {
            float fl =  (float) iContWidth / (float) iContHeight;
            iNewHeight = (int) ((float)iScrWidth / fl);
            iNewWidth = iScrWidth;
        }
        else                         // Portrait
        {
            float fl =  (float) iContHeight / (float) iContWidth;
            iNewWidth = (int) ((float)iScrHeight / fl);
            iNewHeight = iScrHeight;
        }



        liContainer.setLayoutParams(new LinearLayout.LayoutParams(iNewWidth,iNewHeight));

        if(postDetailsData.getType().equals("image"))
        {
            ivPoster.setVisibility(View.VISIBLE);
            webview.setVisibility(View.GONE);
            Glide.with(getActivity()).load(postDetailsData.getMedia()).into(ivPoster);
        }
        else if(postDetailsData.getType().equals("video"))
        {
            ivPoster.setVisibility(View.GONE);
            webview.setVisibility(View.VISIBLE);
            webview.setWebViewClient(new WebViewClient());
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webview.getSettings().setPluginState(WebSettings.PluginState.ON);
            webview.getSettings().setMediaPlaybackRequiresUserGesture(false);
            webview.setWebChromeClient(new WebChromeClient());

            final String sHTML =  "<style>html,body{padding:0;margin:0;}video{width:100%;}video::-webkit-media-controls-volume-slider {display:none;}video::-webkit-media-controls-fullscreen-button {display:none;}</style><video Id=\"myVideo\"controls preload=\"none\" width=\"100%\" height=\"100%\"  poster=\"https://i.stack.imgur.com/PdlGo.gif\" disablepictureinpicture controlslist=\"nodownload\" style=\"background:transparent no-repeat center center url('"+postDetailsData.getVideoThumbnail()+"');background-size:cover\"><source src=\""+postDetailsData.getMedia()+"\" type=\"video/mp4\" /></video>";

            webview.loadData(sHTML,"text/html; charset=UTF-8", null);

            webview.setOnTouchListener(new View.OnTouchListener()
            {
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        float iMdW = v.getWidth() / 2;
                        float iMdH = v.getHeight() / 2;

                        float x1 = iMdW - 100;
                        float x2 = iMdW + 100;

                        float y1 = iMdH - 100;
                        float y2 = iMdH + 100;

                        float X = event.getX();
                        float Y = event.getY();
                        if(x1 <= X && x2 >= X && y1 <= Y && y2 >= Y)
                        {
                            return false;
                        }else if(Y > (v.getHeight() - 75))
                        {
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
            });


        }

        tvCommunityName.setText(""+postDetailsData.getCommunityName());
        tvTitle.setText(""+postDetailsData.getDescription());
        tvCommentCount.setText(""+postDetailsData.getCommentCount());



        tvUp.setText(""+postDetailsData.getReactionCount().getUp());
        tvDown.setText(""+postDetailsData.getReactionCount().getDown());

        if(postDetailsData.getIsReacted().equalsIgnoreCase("up"))
        {
            rbtUpVote.setChecked(true);
        }
        else if(postDetailsData.getIsReacted().equalsIgnoreCase("down"))
        {
            rbtDownVote.setChecked(true);
        }
        rbtUpVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
            {
                if(postDetailsData.getIsReacted().equalsIgnoreCase("down"))
                {
                    postDetailsData.getReactionCount().setDown(postDetailsData.getReactionCount().getDown()-1);
                }

                postDetailsData.getReactionCount().setUp(postDetailsData.getReactionCount().getUp()+1);
                tvUp.setText(""+postDetailsData.getReactionCount().getUp());
                tvDown.setText(""+postDetailsData.getReactionCount().getDown());

                postDetailsData.setIsReacted("up");
                postDataChanged.onPostDataChanged(postDetailsData,Position);
                ((ActivityHome) getActivity()).addReaction(postDetailsData,true);
            }
        });
        rbtDownVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
            {
                if(postDetailsData.getIsReacted().equalsIgnoreCase("up"))
                {
                    postDetailsData.getReactionCount().setUp(postDetailsData.getReactionCount().getUp()-1);
                }
                postDetailsData.getReactionCount().setDown(postDetailsData.getReactionCount().getDown()+1);
                tvUp.setText(""+postDetailsData.getReactionCount().getUp());
                tvDown.setText(""+postDetailsData.getReactionCount().getDown());

                postDetailsData.setIsReacted("down");
                postDataChanged.onPostDataChanged(postDetailsData,Position);
                ((ActivityHome) getActivity()).addReaction(postDetailsData,false);
            }
        });

        tvShare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                ((ActivityHome)getActivity()).sharePost(DP_LINK_POST+postDetailsData.getId());
            }
        });
        Glide.with(getActivity()).load(postDetailsData.getCommunityImage()).into(ivProfile);

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityHome) getActivity()).onBackPressed();
                ((ActivityHome) getActivity()).startCommunityDetails(postDetailsData.getCommunityId(),null);
            }
        });
        tvCommunityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityHome) getActivity()).onBackPressed();
                ((ActivityHome) getActivity()).startCommunityDetails(postDetailsData.getCommunityId(),null);
            }
        });
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityHome) getActivity()).onBackPressed();
                ((ActivityHome) getActivity()).startCommunityDetails(postDetailsData.getCommunityId(),null);
            }
        });

        liComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                ((ActivityHome)getActivity()).showDetail(postDetailsData,Position,postDataChanged);
            }
        });
        
        
        
        return rootView;
    }

}
