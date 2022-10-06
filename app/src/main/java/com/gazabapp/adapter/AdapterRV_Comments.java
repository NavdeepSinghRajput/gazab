package com.gazabapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.actionlisteners.GiveComment;
import com.gazabapp.actionlisteners.PostDataChanged;
import com.gazabapp.customeview.Checkbox_custom;
import com.gazabapp.customeview.ExpandableTextView_custom;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.PostCommentListData;
import com.gazabapp.pojo.PostListData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.gazabapp.Constants.DP_LINK_POST;
import static com.gazabapp.Constants.PRINT;
import static com.gazabapp.Constants.VIEW_TYPE_COMMENTS;
import static com.gazabapp.Constants.VIEW_TYPE_HEADER;
import static com.gazabapp.Constants.shk_ID;

public class AdapterRV_Comments extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static SharedPreferences sharedPref = null;
    private ArrayList<PostCommentListData> arrayList;
    private PostListData headerData;
    private Context context;
    private GiveComment giveComment;
    private static int iExpand = -1;
    private static boolean IsHeaderLoaded = false;
    private PostDataChanged postDataChanged;
    private int Position;

    public void setHeaderData(PostListData headerData, PostDataChanged postDataChanged, int Position) {
        this.headerData = headerData;
        iExpand = -1;
        IsHeaderLoaded = false;
        this.postDataChanged = postDataChanged;
        this.Position = Position;
    }

    public void setCommentCount(int iCount) {
        headerData.setCommentCount(iCount);
        postDataChanged.onPostDataChanged(headerData, Position);
        headerViewHolder.commentCount(iCount);
    }

    public AdapterRV_Comments(ArrayList<PostCommentListData> arrayList, Context context, GiveComment giveComment) {
        this.arrayList = arrayList;
        this.context = context;
        this.giveComment = giveComment;
        sharedPref = ((ActivityHome) context).getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    HeaderViewHolder headerViewHolder = null;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_rcv_post_details, parent, false);
                headerViewHolder = new HeaderViewHolder(view, context);
                return headerViewHolder;

            case VIEW_TYPE_COMMENTS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv_post_comments, parent, false);
                return new CommentViewHolder(view, context);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            if (!IsHeaderLoaded) {

                IsHeaderLoaded = true;
                ((HeaderViewHolder) holder).loadData(headerData, position, giveComment, postDataChanged, Position);
            }
        } else {
            ((CommentViewHolder) holder).loadData(arrayList.get(position), position, giveComment);
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_HEADER;
        else
            return VIEW_TYPE_COMMENTS;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private View itemView;

        private TextView_custom tvFollow, tvTitle, tvWriter, tvDetail, tvCommentCount, tvShare,tvTemp;
        TextView tvUp, tvDown;
        private ImageView ivProfile, ivPoster, ivOption, ivBack, ivShare, ivDot;
        private LinearLayout liContainer;
        private LinearLayout liMain, liComment,urlLayout;
        FrameLayout playLayout;
        boolean play = true;

        //        public RadioButton rbtUpVote, rbtDownVote;
        public WebView webview;
        public TextView_custom tvUrl;

        public HeaderViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            this.itemView = itemView;


            tvFollow = (TextView_custom) itemView.findViewById(R.id.tvFollow);
            tvTemp = (TextView_custom) itemView.findViewById(R.id.tvTemp);
            tvTitle = (TextView_custom) itemView.findViewById(R.id.tvTitle);
            tvDetail = (TextView_custom) itemView.findViewById(R.id.tvDetail);
            tvWriter = (TextView_custom) itemView.findViewById(R.id.tvWriter);
            tvCommentCount = (TextView_custom) itemView.findViewById(R.id.tvCommentCount);
            tvUrl = (TextView_custom) itemView.findViewById(R.id.tvUrl);
            tvShare = (TextView_custom) itemView.findViewById(R.id.tvShare);
            ivProfile = (ImageView) itemView.findViewById(R.id.ivProfile);
            ivPoster = (ImageView) itemView.findViewById(R.id.ivPoster);
            ivDot = (ImageView) itemView.findViewById(R.id.ivDot);
            ivOption = (ImageView) itemView.findViewById(R.id.ivOption);
            liContainer = (LinearLayout) itemView.findViewById(R.id.liContainer);
            liComment = (LinearLayout) itemView.findViewById(R.id.liComment);
            urlLayout = (LinearLayout) itemView.findViewById(R.id.urlLayout);
            liMain = (LinearLayout) itemView.findViewById(R.id.liMain);

            this.webview = (WebView) itemView.findViewById(R.id.webview);
            this.playLayout = (FrameLayout) itemView.findViewById(R.id.playLayout);

            tvDown = (TextView) itemView.findViewById(R.id.tvDown);
            tvUp = (TextView) itemView.findViewById(R.id.tvUp);
//            rbtUpVote = (RadioButton) itemView.findViewById(R.id.rbtUpVote);
//            rbtDownVote = (RadioButton) itemView.findViewById(R.id.rbtDownVote);
        }

        public void commentCount(int iCount) {

            tvCommentCount.setText("" + iCount);
        }

        public void loadData(PostListData headerData, int position, GiveComment giveComment, PostDataChanged postDataChanged, int Position) {
            if (headerData != null) {

                PRINT("NAME A::" + headerData.getDescription());
                PRINT("ID A::" + headerData.getId());
                PRINT("DIME A::" + headerData.getDimension().getHeight() + "==" + headerData.getDimension().getWidth());
                PRINT("THUMB A::" + headerData.getVideoThumbnail());
                PRINT("MEDIA A::" + headerData.getMedia());

                liContainer.setVisibility(View.GONE);
                if (!headerData.getType().equals("text")) {
                    int iContWidth = Constants.WIDTH - 10;
                    int iContHeight = (int) (Constants.HEIGHT / 3.1);
                    if (headerData.getDimension() != null && headerData.getDimension().getWidth() != null && headerData.getDimension().getHeight() != null) {
                        iContWidth = headerData.getDimension().getWidth();
                        iContHeight = headerData.getDimension().getHeight();
                    }
                    int iScrWidth = Constants.WIDTH - 10;
                    int iScrHeight = Constants.HEIGHT - 300;
                    int iNewWidth = 100, iNewHeight = 100;
                    if (iContWidth >= iContHeight) //LandScape
                    {
                        float fl = (float) iContWidth / (float) iContHeight;
                        iNewHeight = (int) ((float) iScrWidth / fl);
                        iNewWidth = iScrWidth;
                    } else                         // Portrait
                    {
                        float fl = (float) iContHeight / (float) iContWidth;
                        iNewWidth = (int) ((float) iScrHeight / fl);
                        iNewHeight = iScrHeight;
                    }
                    liContainer.setVisibility(View.VISIBLE);
                    liContainer.setLayoutParams(new LinearLayout.LayoutParams(iNewWidth, iNewHeight));

                    if (headerData.getType().equals("image") || headerData.getType().equals("link")) {
                        ivPoster.setVisibility(View.VISIBLE);
                        webview.setVisibility(View.GONE);
                        playLayout.setVisibility(View.GONE);

                        if (headerData.getType().equals("link")) {
                            SpannableString content = new SpannableString(headerData.getLink());
                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            tvUrl.setVisibility(View.VISIBLE);
                            tvUrl.setText(headerData.getTitle());
                            tvUrl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String sURL = headerData.getLink().trim();
                                    if (sURL.length() > 5) {
                                        String sStart = sURL.substring(0, 4).toLowerCase();
                                        if (!sStart.equalsIgnoreCase("http")) {
                                            sURL = "http://" + sURL;
                                        }
                                        Uri webpage = Uri.parse(sURL);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                                        context.startActivity(intent);
                                    }
                                }
                            });
//                            if(headerData.getTitle().length()<0){
//                                urlLayout.setVisibility(View.GONE);
//                            }else{
//                                urlLayout.setVisibility(View.VISIBLE);
//                            }
                        }else{
                            tvUrl.setVisibility(View.GONE);

                        }

                        if (headerData.getMedia().length() > 7) {
                            Glide.with(context).load(headerData.getMedia()).into(ivPoster);
                        } else {
                            ivPoster.setImageResource(R.drawable.click_to_visit);
                        }


                    } else if (headerData.getType().equals("video")) {
                        ivPoster.setVisibility(View.GONE);
                        webview.setVisibility(View.VISIBLE);
                        playLayout.setVisibility(View.VISIBLE);
                        playLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (play) {
                                    webview.loadUrl("javascript:(function(){document.getElementById('myVideo').play();})()");
                                } else {
                                    webview.loadUrl("javascript:(function(){document.getElementById('myVideo').pause();})()");
                                }
                                play = !play;
                            }
                        });
                        playLayout.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {

                                if (event.getAction() == MotionEvent.ACTION_UP) {
                                    float iMdW = v.getWidth() / 2;
                                    float iMdH = v.getHeight() / 2;

                                    float x1 = iMdW - 100;
                                    float x2 = iMdW + 100;

                                    float y1 = iMdH - 100;
                                    float y2 = iMdH + 100;

                                    float X = event.getX();
                                    float Y = event.getY();
                                    if (x1 <= X && x2 >= X && y1 <= Y && y2 >= Y) {
                                        Log.e("play","plal");
                                        return false;
                                    } else if (Y > (v.getHeight() - 75)) {
                                        Log.e("play","plal");
                                        return false;
                                    } else {
                                        Log.e("play","plal");
                                        webview.loadUrl("javascript:(function(){document.getElementById('myVideo').pause();})()");
//                                ((ActivityHome) context).viewPostTap(myListData, position, VideoTypeViewHolder.this);
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            }
                        });
                        webview.setWebViewClient(new WebViewClient());
                        webview.getSettings().setJavaScriptEnabled(true);
                        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
                        webview.getSettings().setMediaPlaybackRequiresUserGesture(false);
                        webview.setWebChromeClient(new WebChromeClient());

                        final String sHTML = "<style>html,body{padding:0;margin:0;}video{width:100%;}video::-webkit-media-controls-volume-slider {display:none;}video::-webkit-media-controls-fullscreen-button {display:none;}</style><video Id=\"myVideo\"controls preload=\"none\" width=\"100%\" height=\"100%\"  poster=\"https://i.stack.imgur.com/PdlGo.gif\" disablepictureinpicture controlslist=\"nodownload\" style=\"background:transparent no-repeat center center url('" + headerData.getVideoThumbnail() + "');background-size:cover\"><source src=\"" + headerData.getMedia() + "\" type=\"video/mp4\" /></video>";
                        webview.loadData(sHTML, "text/html; charset=UTF-8", null);


                        webview.setOnTouchListener(new View.OnTouchListener() {
                            public boolean onTouch(View v, MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_UP) {
                                    float iMdW = v.getWidth() / 2;
                                    float iMdH = v.getHeight() / 2;

                                    float x1 = iMdW - 100;
                                    float x2 = iMdW + 100;

                                    float y1 = iMdH - 100;
                                    float y2 = iMdH + 100;

                                    float X = event.getX();
                                    float Y = event.getY();
                                    if (x1 <= X && x2 >= X && y1 <= Y && y2 >= Y) {
                                        return false;
                                    } else if (Y > (v.getHeight() - 75)) {
                                        return false;
                                    } else {
                                        return true;
                                    }
                                } else {
                                    return false;
                                }
                            }
                        });


                    }

                    if (headerData.getType().equalsIgnoreCase("link")) {
                        liContainer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String sURL = headerData.getLink().trim();
                                if (sURL.length() > 5) {
                                    String sStart = sURL.substring(0, 4).toLowerCase();
                                    if (!sStart.equalsIgnoreCase("http")) {
                                        sURL = "http://" + sURL;
                                    }
                                    Uri webpage = Uri.parse(sURL);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                                    context.startActivity(intent);
                                }
                            }
                        });
                    }
                } else {
                    if (headerData.getLink().length() > 2 && headerData.getLink().length() < 8) {
                        try {
                            tvDetail.setPadding(10, 10, 10, 10);
                            if (headerData.getLink().equalsIgnoreCase("#ffffff") || headerData.getLink().equalsIgnoreCase("#ffd71f"))
                                tvDetail.setTextColor(((ActivityHome) context).getAttributeColor(R.attr.new_post_field_in));
                            else
                                tvDetail.setTextColor(((ActivityHome) context).getAttributeColor(R.attr.new_post_field));
                            tvDetail.setBackgroundColor(Color.parseColor("" + headerData.getLink()));
                        } catch (Exception e) {
                        }
                    }
                }

                tvTitle.setText("" + headerData.getCommunityName());
                tvWriter.setText("" + headerData.getUserName());
                tvDetail.setText("" + headerData.getDescription());
                tvCommentCount.setText("" + headerData.getCommentCount());
                liComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        giveComment.clickOnReply(null, -1, false, -1, "");
                    }
                });


                tvTemp.setText(headerData.getUserId() + "\n" + sharedPref.getString(shk_ID, ""));

                if (headerData.getUserId().equalsIgnoreCase(sharedPref.getString(shk_ID, ""))) {
                    ivDot.setVisibility(View.INVISIBLE);
                    tvFollow.setVisibility(View.INVISIBLE);
                } else {
                    ivDot.setVisibility(View.VISIBLE);
                    tvFollow.setVisibility(View.VISIBLE);
                }
                if (headerData.getIs_person_follow()) {
                    tvFollow.setText(context.getResources().getString(R.string.following));
                    tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.BOLD);
                } else {
                    tvFollow.setText(context.getResources().getString(R.string.follow));
                    tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.NORMAL);
                }
                tvFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tvFollow.getText().toString().equalsIgnoreCase(context.getResources().getString(R.string.follow))) {
                            headerData.setIs_person_follow(true);
                            tvFollow.setText(context.getResources().getString(R.string.following));
                            tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.BOLD);
                            ((ActivityHome) context).followPerson("" + headerData.getUserId());
                        } else {
                            headerData.setIs_person_follow(false);
                            tvFollow.setText(context.getResources().getString(R.string.follow));
                            tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.NORMAL);
                            ((ActivityHome) context).unfollowPerson("" + headerData.getUserId());
                        }
                        postDataChanged.onPostDataChanged(headerData, Position);
                    }
                });

                ivOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((ActivityHome) context).popupPostReport(headerData,false,position);


                    }
                });

                tvUp.setText("" + headerData.getReactionCount().getUp());
                tvDown.setText("" + headerData.getReactionCount().getDown());
              /*  if (headerData.getIsReacted().equalsIgnoreCase("up")) {
                    rbtUpVote.setChecked(true);
                } else if (headerData.getIsReacted().equalsIgnoreCase("down")) {
                    rbtDownVote.setChecked(true);
                }
*/
                tvUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!headerData.getIsReacted().equalsIgnoreCase("up")) {
                            if (headerData.getIsReacted().equalsIgnoreCase("down")) {
                                headerData.getReactionCount().setDown(headerData.getReactionCount().getDown() - 1);
                            }
                            headerData.getReactionCount().setUp(headerData.getReactionCount().getUp() + 1);
                            tvUp.setText("" + headerData.getReactionCount().getUp());
                            tvDown.setText("" + headerData.getReactionCount().getDown());

                            headerData.setIsReacted("up");

                            postDataChanged.onPostDataChanged(headerData, Position);
                            ((ActivityHome) context).addReaction(headerData, true);

                        }
                    }
                });
                tvDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!headerData.getIsReacted().equalsIgnoreCase("down")) {
                            if (headerData.getIsReacted().equalsIgnoreCase("up")) {
                                headerData.getReactionCount().setUp(headerData.getReactionCount().getUp() - 1);
                            }
                            headerData.getReactionCount().setDown(headerData.getReactionCount().getDown() + 1);
                            tvUp.setText("" + headerData.getReactionCount().getUp());
                            tvDown.setText("" + headerData.getReactionCount().getDown());

                            headerData.setIsReacted("down");
                            postDataChanged.onPostDataChanged(headerData, Position);
                            ((ActivityHome) context).addReaction(headerData, false);
                        }
                    }
                });


             /*   rbtUpVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        if (headerData.getIsReacted().equalsIgnoreCase("down")) {
                            headerData.getReactionCount().setDown(headerData.getReactionCount().getDown() - 1);
                        }
                        headerData.getReactionCount().setUp(headerData.getReactionCount().getUp() + 1);
                        tvUp.setText("" + headerData.getReactionCount().getUp());
                        tvDown.setText("" + headerData.getReactionCount().getDown());

                        headerData.setIsReacted("up");

                        postDataChanged.onPostDataChanged(headerData, Position);
                        ((ActivityHome) context).addReaction(headerData, true);
                    }
                });
                rbtDownVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        if (headerData.getIsReacted().equalsIgnoreCase("up")) {
                            headerData.getReactionCount().setUp(headerData.getReactionCount().getUp() - 1);
                        }
                        headerData.getReactionCount().setDown(headerData.getReactionCount().getDown() + 1);
                        tvUp.setText("" + headerData.getReactionCount().getUp());
                        tvDown.setText("" + headerData.getReactionCount().getDown());

                        headerData.setIsReacted("down");
                        postDataChanged.onPostDataChanged(headerData, Position);
                        ((ActivityHome) context).addReaction(headerData, false);
                    }
                });
*/
                tvShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ActivityHome) context).sharePost(DP_LINK_POST + headerData.getId());
                    }
                });
                Glide.with(context).load(headerData.getCommunityImage()).into(ivProfile);

                ivProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ActivityHome) context).onBackPressed();
                        ((ActivityHome) context).startCommunityDetails(headerData.getCommunityId(), null);

                    }
                });
                tvTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ActivityHome) context).onBackPressed();
                        ((ActivityHome) context).startCommunityDetails(headerData.getCommunityId(), null);
                    }
                });
            }
        }


    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView_custom tvUser, tvAgo, tvReply;
        private ImageView ivProfile;
        private ListView listview;
        private Context context;
        private View itemView;
        private Checkbox_custom cbMoreReply;
        private ExpandableTextView_custom tvComment;
        private RecyclerView rcvCommentChild;
        private LinearLayoutManager layoutManager = null;

        public CommentViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            this.itemView = itemView;

            tvUser = (TextView_custom) itemView.findViewById(R.id.tvUser);
            tvAgo = (TextView_custom) itemView.findViewById(R.id.tvAgo);

            tvReply = (TextView_custom) itemView.findViewById(R.id.tvReply);

            ivProfile = (ImageView) itemView.findViewById(R.id.ivProfile);
            cbMoreReply = (Checkbox_custom) itemView.findViewById(R.id.cbMoreReply);

            tvComment = (ExpandableTextView_custom) itemView.findViewById(R.id.tvComment);

            listview = (ListView) itemView.findViewById(R.id.listview);
            rcvCommentChild = (RecyclerView) itemView.findViewById(R.id.rcvCommentChild);

        }

        public void loadData(PostCommentListData myListData, int position, GiveComment giveComment) {
            try {
                Date date = dateFormatCommon.parse("" + myListData.getCreated().substring(0, myListData.getCreated().indexOf('.')));
                String sAgo = getTimeAgo(date, context);
                if (sAgo != null && !sAgo.equalsIgnoreCase("null") && sAgo.length() > 4) {
                    tvAgo.setText("" + sAgo);
                } else {
                    tvAgo.setText(context.getResources().getString(R.string.date_util_term_less) + " " + context.getResources().getString(R.string.date_util_term_a) + " " + context.getResources().getString(R.string.date_util_unit_minute));
                }
            } catch (Exception e) {
                tvAgo.setText(context.getResources().getString(R.string.date_util_term_less) + " " + context.getResources().getString(R.string.date_util_term_a) + " " + context.getResources().getString(R.string.date_util_unit_minute));
            }
//            tvComment.setText(""+myListData.getComment());
//            int iH = getTextViewHeight(tvComment);
//            tvComment.setHeight(iH);


            tvComment.setText("" + myListData.getComment());

            if (myListData.getUserName() == null || myListData.getUserName().equalsIgnoreCase("null")) {
                tvUser.setText("");
            } else {
                tvUser.setText("" + myListData.getUserName());
            }

            Glide.with(context).load(myListData.getUserPic()).into(ivProfile);
            tvReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iExpand = position;
                    giveComment.clickOnReply(myListData, position, true, -1, myListData.getId());
                }
            });


            if (myListData.getChildern().size() > 0) {
                cbMoreReply.setVisibility(View.VISIBLE);
                cbMoreReply.setChecked(false);
            } else {
                cbMoreReply.setVisibility(View.INVISIBLE);
                listview.setVisibility(View.GONE);
                rcvCommentChild.setVisibility(View.GONE);
            }


            cbMoreReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        iExpand = position;
                        cbMoreReply.setText(context.getResources().getString(R.string.less_reply));
                        listview.setAdapter(new Adapter_PostComments(context, (ArrayList<PostCommentListData>) myListData.getChildern(), giveComment, position));
                        setListViewHeightBasedOnItems(listview);
                        listview.setVisibility(View.GONE);
                        rcvCommentChild.setVisibility(View.VISIBLE);


                        rcvCommentChild.setHasFixedSize(false);
                        layoutManager = new LinearLayoutManager(context);
                        rcvCommentChild.setLayoutManager(layoutManager);
                        rcvCommentChild.setAdapter(new AdapterRV_Comments_Child((ArrayList<PostCommentListData>) myListData.getChildern(), context, giveComment, position));
                    } else {
                        cbMoreReply.setText(context.getResources().getString(R.string.more_reply));
                        listview.setVisibility(View.GONE);
                        rcvCommentChild.setVisibility(View.GONE);
                    }
                }
            });


            if (myListData.getChildern().size() > 0 && iExpand == position) {
                cbMoreReply.setChecked(true);
            }


        }

        public static int getTextViewHeight(TextView_custom textView) {
            WindowManager wm = (WindowManager) textView.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();

            int deviceWidth;

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Point size = new Point();
                display.getSize(size);
                deviceWidth = size.x;
            } else {
                deviceWidth = display.getWidth();
            }

            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            textView.measure(widthMeasureSpec, heightMeasureSpec);
            return textView.getMeasuredHeight();
        }
    }

    final static private SimpleDateFormat dateFormatCommon = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    public static String getTimeAgo(Date date, Context ctx) {

        if (date == null) {
            return null;
        }
        long time = date.getTime();

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        int dim = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (dim == 0) {
            timeAgo = ctx.getResources().getString(R.string.date_util_term_less) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim == 1) {
            return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
        } else if (dim >= 45 && dim <= 89) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_an) + " " + ctx.getResources().getString(R.string.date_util_unit_hour);
        } else if (dim >= 90 && dim <= 1439) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 60)) + " " + ctx.getResources().getString(R.string.date_util_unit_hours);
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
        } else if (dim >= 2520 && dim <= 43199) {
            timeAgo = (Math.round(dim / 1440)) + " " + ctx.getResources().getString(R.string.date_util_unit_days);
        } else if (dim >= 43200 && dim <= 86399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_month);
        } else if (dim >= 86400 && dim <= 525599) {
            timeAgo = (Math.round(dim / 43200)) + " " + ctx.getResources().getString(R.string.date_util_unit_months);
        } else if (dim >= 525600 && dim <= 655199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
        }

        return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;

        } else {
            return false;
        }

    }

    public static void setListViewHeightBasedOnChildren(final ListView listView) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                ListAdapter listAdapter = listView.getAdapter();
                if (listAdapter == null) {
                    return;
                }
                int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
                int listWidth = listView.getMeasuredWidth();
                for (int i = 0; i < listAdapter.getCount(); i++) {
                    View listItem = listAdapter.getView(i, null, listView);
                    listItem.measure(
                            View.MeasureSpec.makeMeasureSpec(listWidth, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));


                    totalHeight += listItem.getMeasuredHeight();

                }
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = (totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)));
                listView.setLayoutParams(params);
                listView.requestLayout();

            }
        });
    }

}
