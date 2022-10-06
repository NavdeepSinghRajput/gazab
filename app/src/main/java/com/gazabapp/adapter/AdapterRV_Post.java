package com.gazabapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.actionlisteners.PostDataChanged;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.PostListData;
import com.gazabapp.utils.ScaleImageView;
import com.gazabapp.webview.WebviewActivity;

import java.util.ArrayList;

import static com.gazabapp.Constants.DP_LINK_POST;
import static com.gazabapp.Constants.PRINT;
import static com.gazabapp.Constants.VIEW_TYPE_GIF;
import static com.gazabapp.Constants.VIEW_TYPE_IMAGE;
import static com.gazabapp.Constants.VIEW_TYPE_TEXT;
import static com.gazabapp.Constants.VIEW_TYPE_URL;
import static com.gazabapp.Constants.VIEW_TYPE_VIDEO;
import static com.gazabapp.Constants.shk_ID;


public class AdapterRV_Post extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<PostListData> listdata;
    private Context context;
    private SharedPreferences sharedPref = null;
    boolean deleteOption;

    public AdapterRV_Post(ArrayList<PostListData> listdata, Context context,boolean deleteOption) {
        this.listdata = listdata;
        this.context = context;
        this.deleteOption = deleteOption;
        sharedPref = ((ActivityHome) context).getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv_post_text, parent, false);
                return new TextTypeViewHolder(view, context);
            case VIEW_TYPE_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv_post_image, parent, false);
                return new ImageTypeViewHolder(view, context);
            case VIEW_TYPE_GIF:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv_post_gif, parent, false);
                return new GifTypeViewHolder(view, context);
            case VIEW_TYPE_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv_post_video_wv, parent, false);
                return new VideoTypeViewHolder(view, context);
            case VIEW_TYPE_URL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv_post_url, parent, false);
                return new URLTypeViewHolder(view, context);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final PostListData myListData = listdata.get(position);
        switch (getItemViewType(position)) {
            case VIEW_TYPE_TEXT:
                ((TextTypeViewHolder) holder).loadData(myListData, position);
                break;
            case VIEW_TYPE_IMAGE:
                ((ImageTypeViewHolder) holder).loadData(myListData, position);
                break;
            case VIEW_TYPE_GIF:
                ((GifTypeViewHolder) holder).loadData(myListData, position);
                break;
            case VIEW_TYPE_VIDEO:
                ((VideoTypeViewHolder) holder).loadData(myListData, position);
                break;
            case VIEW_TYPE_URL:
                ((URLTypeViewHolder) holder).loadData(myListData, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (listdata.get(position).getType().equalsIgnoreCase("video")) {
            return VIEW_TYPE_VIDEO;
        } else if (listdata.get(position).getType().equalsIgnoreCase("text")) {
            return VIEW_TYPE_TEXT;
        } else if (listdata.get(position).getType().equalsIgnoreCase("image") && listdata.get(position).getMedia().endsWith("gif")) {
            return VIEW_TYPE_GIF;
        } else if (listdata.get(position).getType().equalsIgnoreCase("image")) {
            return VIEW_TYPE_IMAGE;
        } else if (listdata.get(position).getType().equalsIgnoreCase("link")) {
            return VIEW_TYPE_URL;
        } else {
            return -1;
        }
    }

    public class TextTypeViewHolder extends RecyclerView.ViewHolder implements PostDataChanged {
        public ImageView ivProfile, ivOption, ivDot;
        public TextView_custom tvFollow, tvTitle, tvWriter, tvDetail, tvCommentCount, tvShare, tvTemp;
        TextView tvUp, tvDown;
        public Context context;
        public LinearLayout comments;
        //        public RadioButton rbtUpVote , rbtDownVote;
        private int iPosition = -1;
        private PostListData myListData;

        public TextTypeViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            this.tvFollow = (TextView_custom) itemView.findViewById(R.id.tvFollow);
            this.tvTemp = (TextView_custom) itemView.findViewById(R.id.tvTemp);
            this.tvTitle = (TextView_custom) itemView.findViewById(R.id.tvTitle);
            this.tvDetail = (TextView_custom) itemView.findViewById(R.id.tvDetail);
            this.tvWriter = (TextView_custom) itemView.findViewById(R.id.tvWriter);
            this.tvCommentCount = (TextView_custom) itemView.findViewById(R.id.tvCommentCount);
            this.tvShare = (TextView_custom) itemView.findViewById(R.id.tvShare);
            this.ivProfile = (ImageView) itemView.findViewById(R.id.ivProfile);
            this.ivOption = (ImageView) itemView.findViewById(R.id.ivOption);
            this.ivDot = (ImageView) itemView.findViewById(R.id.ivDot);
            this.comments = (LinearLayout) itemView.findViewById(R.id.comments);

            this.tvUp = (TextView) itemView.findViewById(R.id.tvUp);
            this.tvDown = (TextView) itemView.findViewById(R.id.tvDown);
//            this.rbtUpVote = (RadioButton) itemView.findViewById(R.id.rbtUpVote);
//            this.rbtDownVote = (RadioButton) itemView.findViewById(R.id.rbtDownVote);

        }

        public void loadData(PostListData myData, int position) {

            this.iPosition = position;
            this.myListData = myData;

            if(sharedPref.getBoolean(Constants.shk_DARK_THEME,false))
                ivOption.setImageDrawable(context.getResources().getDrawable(R.drawable.three_dots));
            else
                ivOption.setImageDrawable(context.getResources().getDrawable(R.drawable.three_dots_black));

            ivOption.setOnClickListener(view -> ((ActivityHome) context).popupPostReport(myListData,deleteOption,position));
            tvTitle.setText("" + myListData.getCommunityName());
            tvWriter.setText("" + myListData.getUserName());
            tvDetail.setText("" + myListData.getDescription());
            tvCommentCount.setText("" + myListData.getCommentCount());


            if (myListData.getLink().length() > 2 && myListData.getLink().length() < 8) {
                try {
                    if (myListData.getLink().equalsIgnoreCase("#ffffff") || myListData.getLink().equalsIgnoreCase("#ffd71f"))
                        tvDetail.setTextColor(((ActivityHome) context).getAttributeColor(R.attr.new_post_field_in));
                    else
                        tvDetail.setTextColor(((ActivityHome) context).getAttributeColor(R.attr.new_post_field));


                    tvDetail.setBackgroundColor(Color.parseColor("" + myListData.getLink()));
                } catch (Exception e) {
                }
            }

            tvUp.setText("" + myListData.getReactionCount().getUp());
            tvDown.setText("" + myListData.getReactionCount().getDown());


            /*rbtUpVote.setOnCheckedChangeListener(null);
            rbtDownVote.setOnCheckedChangeListener(null);
            if(myListData.getIsReacted().equalsIgnoreCase("up"))
            {
                rbtUpVote.setChecked(true);
            }
            else if(myListData.getIsReacted().equalsIgnoreCase("down"))
            {
                rbtDownVote.setChecked(true);
            }*/
            tvUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!myListData.getIsReacted().equalsIgnoreCase("up")) {
                        if (myListData.getIsReacted().equalsIgnoreCase("down")) {
                            myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() - 1);
                        }

                        myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() + 1);
                        tvUp.setText("" + myListData.getReactionCount().getUp());
                        tvDown.setText("" + myListData.getReactionCount().getDown());

                        myListData.setIsReacted("up");
                        ((ActivityHome) context).addReaction(myListData, true);
                    } else {
                        Log.e("post", "alreadylikedByUser");
                    }


                }
            });
            tvDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!myListData.getIsReacted().equalsIgnoreCase("down")) {
                        if (myListData.getIsReacted().equalsIgnoreCase("up")) {
                            myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() - 1);
                        }
                        myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() + 1);
                        tvUp.setText("" + myListData.getReactionCount().getUp());
                        tvDown.setText("" + myListData.getReactionCount().getDown());

                        myListData.setIsReacted("down");
                        ((ActivityHome) context).addReaction(myListData, false);
                    } else {
                        Log.e("post", "alreadydislikedByUser");
                    }
                }
            });

          /*  rbtUpVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked)
                {
                    if(myListData.getIsReacted().equalsIgnoreCase("down"))
                    {
                        myListData.getReactionCount().setDown(myListData.getReactionCount().getDown()-1);
                    }
                    myListData.getReactionCount().setUp(myListData.getReactionCount().getUp()+1);
                    tvUp.setText(""+myListData.getReactionCount().getUp());
                    tvDown.setText(""+myListData.getReactionCount().getDown());
                    myListData.setIsReacted("up");
                    ((ActivityHome) context).addReaction(myListData,true);
                }
            });
            rbtDownVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked)
                {
                    if(myListData.getIsReacted().equalsIgnoreCase("up"))
                    {
                        myListData.getReactionCount().setUp(myListData.getReactionCount().getUp()-1);
                    }
                    myListData.getReactionCount().setDown(myListData.getReactionCount().getDown()+1);
                    tvUp.setText(""+myListData.getReactionCount().getUp());
                    tvDown.setText(""+myListData.getReactionCount().getDown());
                    myListData.setIsReacted("down");
                    ((ActivityHome) context).addReaction(myListData,false);
                }
            });*/

            tvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ActivityHome) context).sharePost(DP_LINK_POST + myListData.getId());
                }
            });

//            Glide.with(context).load(myListData.getCommunityImage()).into(ivProfile);
            Glide.with(context).load(myListData.getCommunityImage()).into(ivProfile);
            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityHome) context).startCommunityDetails(myListData.getCommunityId(), null);
                }
            });
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityHome) context).startCommunityDetails(myListData.getCommunityId(), null);
                }
            });
            tvTemp.setText(myListData.getUserId() + "\n" + sharedPref.getString(shk_ID, ""));
            if (myListData.getUserId().equalsIgnoreCase(sharedPref.getString(shk_ID, ""))) {
                ivDot.setVisibility(View.INVISIBLE);
                tvFollow.setVisibility(View.INVISIBLE);
            } else {
                ivDot.setVisibility(View.VISIBLE);
                tvFollow.setVisibility(View.VISIBLE);
            }

            if (myListData.getIs_person_follow()) {
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
                        myListData.setIs_person_follow(true);
                        tvFollow.setText(context.getResources().getString(R.string.following));
                        tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.BOLD);
                        ((ActivityHome) context).followPerson("" + myListData.getUserId());
                    } else {
                        myListData.setIs_person_follow(false);
                        tvFollow.setText(context.getResources().getString(R.string.follow));
                        tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.NORMAL);
                        ((ActivityHome) context).unfollowPerson("" + myListData.getUserId());
                    }

                }
            });
            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ActivityHome) context).showDetail(myListData, position, TextTypeViewHolder.this);
                }
            });
        }

        @Override
        public void onPostDataChanged(PostListData data, int Position) {
            PRINT(" >>> TXT  onPostDataChanged ");
            if (iPosition == Position) {
                PRINT(" >>> TXT  onPostDataChanged IN ");
//                rbtUpVote.setOnCheckedChangeListener(null);
//                rbtDownVote.setOnCheckedChangeListener(null);
                myListData = data;
                AdapterRV_Post.this.notifyItemChanged(Position);
            }
        }
    }

    public class ImageTypeViewHolder extends RecyclerView.ViewHolder implements PostDataChanged {
        public TextView_custom tvFollow, tvTitle, tvWriter, tvDetail, tvCommentCount, tvShare, tvTemp;
        TextView tvUp, tvDown;
        public ImageView ivProfile, ivOption, ivDot,ivPoster;
        public Context context;
        public LinearLayout liContainer, comments;
        //        public RadioButton rbtUpVote , rbtDownVote;
        private int iPosition = -1;
        private PostListData myListData;

        public ImageTypeViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            this.tvTemp = (TextView_custom) itemView.findViewById(R.id.tvTemp);
            this.tvFollow = (TextView_custom) itemView.findViewById(R.id.tvFollow);
            this.tvTitle = (TextView_custom) itemView.findViewById(R.id.tvTitle);
            this.tvDetail = (TextView_custom) itemView.findViewById(R.id.tvDetail);
            this.tvWriter = (TextView_custom) itemView.findViewById(R.id.tvWriter);
            this.tvCommentCount = (TextView_custom) itemView.findViewById(R.id.tvCommentCount);
            this.tvShare = (TextView_custom) itemView.findViewById(R.id.tvShare);
            this.ivProfile = (ImageView) itemView.findViewById(R.id.ivProfile);
            this.ivPoster = (ImageView) itemView.findViewById(R.id.ivPoster);
            this.ivOption = (ImageView) itemView.findViewById(R.id.ivOption);
            this.ivDot = (ImageView) itemView.findViewById(R.id.ivDot);
            this.liContainer = (LinearLayout) itemView.findViewById(R.id.liContainer);
            this.comments = (LinearLayout) itemView.findViewById(R.id.comments);

            this.tvUp = (TextView) itemView.findViewById(R.id.tvUp);
            this.tvDown = (TextView) itemView.findViewById(R.id.tvDown);
//            this.rbtUpVote = (RadioButton) itemView.findViewById(R.id.rbtUpVote);
//            this.rbtDownVote = (RadioButton) itemView.findViewById(R.id.rbtDownVote);


        }

        public void loadData(PostListData myData, int position) {

            iPosition = position;
            this.myListData = myData;

            tvTitle.setText("" + myListData.getCommunityName());
            tvWriter.setText("" + myListData.getUserName());
            tvDetail.setText("" + myListData.getDescription());
            tvCommentCount.setText("" + myListData.getCommentCount());
            tvUp.setText("" + myListData.getReactionCount().getUp());
            tvDown.setText("" + myListData.getReactionCount().getDown());

            if(sharedPref.getBoolean(Constants.shk_DARK_THEME,false))
                ivOption.setImageDrawable(context.getResources().getDrawable(R.drawable.three_dots));
            else
                ivOption.setImageDrawable(context.getResources().getDrawable(R.drawable.three_dots_black));


            ivOption.setOnClickListener(view -> ((ActivityHome) context).popupPostReport(myListData,deleteOption,position));
            int iContWidth = Constants.WIDTH - 10;
            int iContHeight = Constants.HEIGHT - 300;
            if (myListData.getDimension() != null && myListData.getDimension().getWidth() != null && myListData.getDimension().getHeight() != null) {
                iContWidth = myListData.getDimension().getWidth();
                iContHeight = myListData.getDimension().getHeight();
            } else {

            }


            int iScrWidth = Constants.WIDTH - 10;
            int iScrHeight = Constants.HEIGHT - 300;
            int iNewWidth = 100, iNewHeight = 100;
            if (myListData.getDimension() == null) {
                iNewWidth = iScrWidth;
                iNewHeight = (int) ((iNewWidth / 4) * 3);
            } else if (iContWidth >= iContHeight) //LandScape
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
            liContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    liContainer.getLayoutParams().width, iNewHeight));

//            rbtUpVote.setOnCheckedChangeListener(null);
//            rbtDownVote.setOnCheckedChangeListener(null);
         /*   if(myListData.getIsReacted().equalsIgnoreCase("up"))
            {
                rbtUpVote.setChecked(true);
            }
            else if(myListData.getIsReacted().equalsIgnoreCase("down"))
            {
                rbtDownVote.setChecked(true);
            }*/
            tvUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!myListData.getIsReacted().equalsIgnoreCase("up")) {
                        if (myListData.getIsReacted().equalsIgnoreCase("down")) {
                            myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() - 1);
                        }

                        myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() + 1);
                        tvUp.setText("" + myListData.getReactionCount().getUp());
                        tvDown.setText("" + myListData.getReactionCount().getDown());

                        myListData.setIsReacted("up");
                        ((ActivityHome) context).addReaction(myListData, true);
                    } else {
                        Log.e("post", "alreadylikedByUser");
                    }


                }
            });
            tvDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!myListData.getIsReacted().equalsIgnoreCase("down")) {
                        if (myListData.getIsReacted().equalsIgnoreCase("up")) {
                            myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() - 1);
                        }
                        myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() + 1);
                        tvUp.setText("" + myListData.getReactionCount().getUp());
                        tvDown.setText("" + myListData.getReactionCount().getDown());

                        myListData.setIsReacted("down");
                        ((ActivityHome) context).addReaction(myListData, false);
                    } else {
                        Log.e("post", "alreadydislikedByUser");
                    }
                }
            });
           /* rbtUpVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked)
                {
                    if(myListData.getIsReacted().equalsIgnoreCase("down"))
                    {
                        myListData.getReactionCount().setDown(myListData.getReactionCount().getDown()-1);
                    }

                    myListData.getReactionCount().setUp(myListData.getReactionCount().getUp()+1);
                    tvUp.setText(""+myListData.getReactionCount().getUp());
                    tvDown.setText(""+myListData.getReactionCount().getDown());

                    myListData.setIsReacted("up");
                    ((ActivityHome) context).addReaction(myListData,true);
                }
            });
            rbtDownVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked)
                {
                    if(myListData.getIsReacted().equalsIgnoreCase("up"))
                    {
                        myListData.getReactionCount().setUp(myListData.getReactionCount().getUp()-1);
                    }
                    myListData.getReactionCount().setDown(myListData.getReactionCount().getDown()+1);
                    tvUp.setText(""+myListData.getReactionCount().getUp());
                    tvDown.setText(""+myListData.getReactionCount().getDown());

                    myListData.setIsReacted("down");
                    ((ActivityHome) context).addReaction(myListData,false);
                }
            });*/

            tvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ActivityHome) context).sharePost(DP_LINK_POST + myListData.getId());
                }
            });
            tvTemp.setText(myListData.getUserId() + "\n" + sharedPref.getString(shk_ID, ""));
            if (myListData.getUserId().equalsIgnoreCase(sharedPref.getString(shk_ID, ""))) {
                ivDot.setVisibility(View.INVISIBLE);
                tvFollow.setVisibility(View.INVISIBLE);
            } else {
                ivDot.setVisibility(View.VISIBLE);
                tvFollow.setVisibility(View.VISIBLE);
            }
            if (myListData.getIs_person_follow()) {
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
                        myListData.setIs_person_follow(true);
                        tvFollow.setText(context.getResources().getString(R.string.following));
                        tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.BOLD);
                        ((ActivityHome) context).followPerson("" + myListData.getUserId());
                    } else {
                        myListData.setIs_person_follow(false);
                        tvFollow.setText(context.getResources().getString(R.string.follow));
                        tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.NORMAL);
                        ((ActivityHome) context).unfollowPerson("" + myListData.getUserId());
                    }

                }
            });

            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ActivityHome) context).showDetail(myListData, position, ImageTypeViewHolder.this);
                }
            });


            Glide.with(context).load(myListData.getMedia()).into(ivPoster);
            Glide.with(context).load(myListData.getCommunityImage()).into(ivProfile);
          /*  ivPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ActivityHome) context).viewPostTap(myListData, position, ImageTypeViewHolder.this);
                }
            });*/
            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityHome) context).startCommunityDetails(myListData.getCommunityId(), null);
                }
            });
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityHome) context).startCommunityDetails(myListData.getCommunityId(), null);
                }
            });
        }

        @Override
        public void onPostDataChanged(PostListData data, int Position) {
            if (iPosition == Position) {
//                rbtUpVote.setOnCheckedChangeListener(null);
//                rbtDownVote.setOnCheckedChangeListener(null);
                myListData = data;
                AdapterRV_Post.this.notifyItemChanged(Position);
            }
        }
    }

    public class GifTypeViewHolder extends RecyclerView.ViewHolder implements PostDataChanged {

        public TextView_custom tvFollow, tvTitle, tvWriter, tvDetail, tvCommentCount, tvShare, tvTemp;
        TextView tvUp, tvDown;
        public ImageView ivProfile, ivGif, ivOption, ivDot;
        public Context context;
        public LinearLayout liContainer;
        public LinearLayout comments;
        //        public RadioButton rbtUpVote, rbtDownVote;
        private int iPosition = -1;
        private PostListData myListData;

        public GifTypeViewHolder(View itemView, Context context) {
            super(itemView);

            this.context = context;
            this.tvTemp = (TextView_custom) itemView.findViewById(R.id.tvTemp);
            this.tvFollow = (TextView_custom) itemView.findViewById(R.id.tvFollow);
            this.tvTitle = (TextView_custom) itemView.findViewById(R.id.tvTitle);
            this.tvDetail = (TextView_custom) itemView.findViewById(R.id.tvDetail);
            this.tvWriter = (TextView_custom) itemView.findViewById(R.id.tvWriter);
            this.tvCommentCount = (TextView_custom) itemView.findViewById(R.id.tvCommentCount);
            this.tvShare = (TextView_custom) itemView.findViewById(R.id.tvShare);
            this.ivGif = (ImageView) itemView.findViewById(R.id.ivGif);
            this.ivProfile = (ImageView) itemView.findViewById(R.id.ivProfile);
            this.ivDot = (ImageView) itemView.findViewById(R.id.ivDot);
            this.ivOption = (ImageView) itemView.findViewById(R.id.ivOption);
            this.liContainer = (LinearLayout) itemView.findViewById(R.id.liContainer);
            this.comments = (LinearLayout) itemView.findViewById(R.id.comments);

            this.tvUp = (TextView) itemView.findViewById(R.id.tvUp);
            this.tvDown = (TextView) itemView.findViewById(R.id.tvDown);
//            this.rbtUpVote = (RadioButton) itemView.findViewById(R.id.rbtUpVote);
//            this.rbtDownVote = (RadioButton) itemView.findViewById(R.id.rbtDownVote);


        }

        public void loadData(PostListData myData, int position) {

            this.iPosition = position;
            this.myListData = myData;

            if(sharedPref.getBoolean(Constants.shk_DARK_THEME,false))
                ivOption.setImageDrawable(context.getResources().getDrawable(R.drawable.three_dots));
            else
                ivOption.setImageDrawable(context.getResources().getDrawable(R.drawable.three_dots_black));


            ivOption.setOnClickListener(view -> ((ActivityHome) context).popupPostReport(myListData,deleteOption,position));

            int iContWidth = Constants.WIDTH - 10;
            int iContHeight = Constants.HEIGHT - 300;
            if (myListData.getDimension() != null && myListData.getDimension().getWidth() != null && myListData.getDimension().getHeight() != null) {
                iContWidth = myListData.getDimension().getWidth();
                iContHeight = myListData.getDimension().getHeight();
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
            liContainer.setLayoutParams(new LinearLayout.LayoutParams(iNewWidth, iNewHeight));

            tvTitle.setText("" + myListData.getCommunityName());
            tvWriter.setText("" + myListData.getUserName());
            tvDetail.setText("" + myListData.getDescription());
            tvCommentCount.setText("" + myListData.getCommentCount());


            tvUp.setText("" + myListData.getReactionCount().getUp());
            tvDown.setText("" + myListData.getReactionCount().getDown());
           /* rbtUpVote.setOnCheckedChangeListener(null);
            rbtDownVote.setOnCheckedChangeListener(null);
            if (myListData.getIsReacted().equalsIgnoreCase("up")) {
                rbtUpVote.setChecked(true);
            } else if (myListData.getIsReacted().equalsIgnoreCase("down")) {
                rbtDownVote.setChecked(true);
            }*/
            tvUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!myListData.getIsReacted().equalsIgnoreCase("up")) {
                        if (myListData.getIsReacted().equalsIgnoreCase("down")) {
                            myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() - 1);
                        }

                        myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() + 1);
                        tvUp.setText("" + myListData.getReactionCount().getUp());
                        tvDown.setText("" + myListData.getReactionCount().getDown());

                        myListData.setIsReacted("up");
                        ((ActivityHome) context).addReaction(myListData, true);
                    } else {
                        Log.e("post", "alreadylikedByUser");
                    }


                }
            });
            tvDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!myListData.getIsReacted().equalsIgnoreCase("down")) {
                        if (myListData.getIsReacted().equalsIgnoreCase("up")) {
                            myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() - 1);
                        }
                        myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() + 1);
                        tvUp.setText("" + myListData.getReactionCount().getUp());
                        tvDown.setText("" + myListData.getReactionCount().getDown());

                        myListData.setIsReacted("down");
                        ((ActivityHome) context).addReaction(myListData, false);
                    } else {
                        Log.e("post", "alreadydislikedByUser");
                    }
                }
            });

          /*  rbtUpVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (myListData.getIsReacted().equalsIgnoreCase("down")) {
                        myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() - 1);
                    }

                    myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() + 1);
                    tvUp.setText("" + myListData.getReactionCount().getUp());
                    tvDown.setText("" + myListData.getReactionCount().getDown());

                    myListData.setIsReacted("up");
                    ((ActivityHome) context).addReaction(myListData, true);
                }
            });
            rbtDownVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (myListData.getIsReacted().equalsIgnoreCase("up")) {
                        myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() - 1);
                    }
                    myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() + 1);
                    tvUp.setText("" + myListData.getReactionCount().getUp());
                    tvDown.setText("" + myListData.getReactionCount().getDown());

                    myListData.setIsReacted("down");
                    ((ActivityHome) context).addReaction(myListData, false);
                }
            });
       */
            tvTemp.setText(myListData.getUserId() + "\n" + sharedPref.getString(shk_ID, ""));
            if (myListData.getUserId().equalsIgnoreCase(sharedPref.getString(shk_ID, ""))) {
                ivDot.setVisibility(View.INVISIBLE);
                tvFollow.setVisibility(View.INVISIBLE);
            } else {
                ivDot.setVisibility(View.VISIBLE);
                tvFollow.setVisibility(View.VISIBLE);
            }
            if (myListData.getIs_person_follow()) {
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
                        myListData.setIs_person_follow(true);
                        tvFollow.setText(context.getResources().getString(R.string.following));
                        tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.BOLD);
                        ((ActivityHome) context).followPerson("" + myListData.getUserId());
                    } else {
                        myListData.setIs_person_follow(false);
                        tvFollow.setText(context.getResources().getString(R.string.follow));
                        tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.NORMAL);
                        ((ActivityHome) context).unfollowPerson("" + myListData.getUserId());
                    }

                }
            });
            tvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ActivityHome) context).sharePost(DP_LINK_POST + myListData.getId());
                }
            });
            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ActivityHome) context).showDetail(myListData, position, GifTypeViewHolder.this);
                }
            });
            Glide.with(context).load(myListData.getMedia()).into(ivGif);
            Glide.with(context).load(myListData.getCommunityImage()).into(ivProfile);

         /*   ivGif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ActivityHome) context).viewPostTap(myListData, position, GifTypeViewHolder.this);
                }
            });*/

            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityHome) context).startCommunityDetails(myListData.getCommunityId(), null);
                }
            });
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityHome) context).startCommunityDetails(myListData.getCommunityId(), null);
                }
            });
        }

        @Override
        public void onPostDataChanged(PostListData data, int Position) {
            if (iPosition == Position) {
//                rbtUpVote.setOnCheckedChangeListener(null);
//                rbtDownVote.setOnCheckedChangeListener(null);
                myListData = data;
                AdapterRV_Post.this.notifyItemChanged(Position);
            }
        }
    }

    public class VideoTypeViewHolder extends RecyclerView.ViewHolder implements PostDataChanged {
        public ImageView ivProfile, ivOption, ivDot;
        public TextView_custom tvFollow, tvTitle, tvWriter, tvDetail, tvCommentCount, tvShare, tvTemp;
        TextView tvUp, tvDown;
        public LinearLayout comments;
        RelativeLayout liContainer;
        FrameLayout playLayout;
        boolean play = true;
        public Context context;
        public View itemView;
        public final WebView webview;
//        public RadioButton rbtUpVote, rbtDownVote;

        private int iPosition = -1;
        private PostListData myListData;
        private boolean FIRST_TIME = true;

        public VideoTypeViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            this.itemView = itemView;
            this.tvTemp = (TextView_custom) itemView.findViewById(R.id.tvTemp);
            this.tvFollow = (TextView_custom) itemView.findViewById(R.id.tvFollow);
            this.tvTitle = (TextView_custom) itemView.findViewById(R.id.tvTitle);
            this.tvDetail = (TextView_custom) itemView.findViewById(R.id.tvDetail);
            this.tvWriter = (TextView_custom) itemView.findViewById(R.id.tvWriter);
            this.tvCommentCount = (TextView_custom) itemView.findViewById(R.id.tvCommentCount);
            this.tvShare = (TextView_custom) itemView.findViewById(R.id.tvShare);


            this.webview = (WebView) itemView.findViewById(R.id.webview);

            this.ivProfile = (ImageView) itemView.findViewById(R.id.ivProfile);

            this.comments = (LinearLayout) itemView.findViewById(R.id.comments);
            this.liContainer = (RelativeLayout) itemView.findViewById(R.id.liContainer);
            this.playLayout = (FrameLayout) itemView.findViewById(R.id.playLayout);


            this.ivOption = (ImageView) itemView.findViewById(R.id.ivOption);
            this.ivDot = (ImageView) itemView.findViewById(R.id.ivDot);
            this.tvUp = (TextView) itemView.findViewById(R.id.tvUp);
            this.tvDown = (TextView) itemView.findViewById(R.id.tvDown);
//            this.rbtUpVote = (RadioButton) itemView.findViewById(R.id.rbtUpVote);
//            this.rbtDownVote = (RadioButton) itemView.findViewById(R.id.rbtDownVote);


        }

        public void loadData(PostListData myData, int position) {
            this.iPosition = position;
            this.myListData = myData;


            ivOption.setOnClickListener(view -> ((ActivityHome) context).popupPostReport(myListData,deleteOption,position));

//            if(FIRST_TIME)
            {


                PRINT("NAME::" + myData.getDescription());
                PRINT("ID::" + myData.getId());
                PRINT("DIME::" + myListData.getDimension().getHeight() + "==" + myListData.getDimension().getWidth());
                PRINT("THUMB::" + myListData.getVideoThumbnail());
                PRINT("MEDIA::" + myListData.getMedia());

                FIRST_TIME = false;

                int iContWidth = Constants.WIDTH - 10;
                int iContHeight = Constants.HEIGHT - 300;
                if (myListData.getDimension() != null && myListData.getDimension().getWidth() != null && myListData.getDimension().getHeight() != null) {
                    iContWidth = myListData.getDimension().getWidth();
                    iContHeight = myListData.getDimension().getHeight();
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

                comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ActivityHome) context).showDetail(myListData, position, VideoTypeViewHolder.this);
                    }
                });
                liContainer.setLayoutParams(new LinearLayout.LayoutParams(liContainer.getLayoutParams().width, iNewHeight - 30));
                playLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("url",myData.getLink());
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


                webview.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        //                WebView.HitTestResult hr = ((WebView)v).getHitTestResult();
//                System.out.println(";->>>>>>>>>>>>>>>>>>>dddddddddd   "+event.getX()+"  "+event.getY()+"  "+v.getWidth()+"  "+v.getHeight());


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

                webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                webview.getSettings().setAppCacheEnabled(true);
                webview.getSettings().setDomStorageEnabled(true);


                final String sHTML = "<style>html,body{padding:0;margin:0;}video{width:100%;}video::-webkit-media-controls-volume-slider {display:none;}video::-webkit-media-controls-fullscreen-button {display:none;}</style><video Id=\"myVideo\"controls preload=\"none\" width=\"100%\" height=\"100%\"  poster=\"https://i.stack.imgur.com/PdlGo.gif\" disablepictureinpicture controlslist=\"nodownload\" style=\"background:transparent no-repeat center center url('" + myListData.getVideoThumbnail() + "');background-size:cover\"><source src=\"" + myListData.getMedia() + "\" type=\"video/mp4\" /></video>";


//                webview.clearHistory();
//                webview.loadUrl("about:blank");

                webview.loadData(sHTML, "text/html; charset=UTF-8", null);
//                webview.loadDataWithBaseURL(null,sHTML,"text/html","utf-8","about:blank");
            }

            tvTitle.setText("" + myListData.getCommunityName());
            tvWriter.setText("" + myListData.getUserName());
            tvDetail.setText("" + myListData.getDescription());
            tvCommentCount.setText("" + myListData.getCommentCount());


            tvUp.setText("" + myListData.getReactionCount().getUp());
            tvDown.setText("" + myListData.getReactionCount().getDown());

          /*  rbtUpVote.setOnCheckedChangeListener(null);
            rbtDownVote.setOnCheckedChangeListener(null);
            if (myListData.getIsReacted().equalsIgnoreCase("up")) {
                rbtUpVote.setChecked(true);
            } else if (myListData.getIsReacted().equalsIgnoreCase("down")) {
                rbtDownVote.setChecked(true);
            }
            rbtUpVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (myListData.getIsReacted().equalsIgnoreCase("down")) {
                        myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() - 1);
                    }

                    myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() + 1);
                    tvUp.setText("" + myListData.getReactionCount().getUp());
                    tvDown.setText("" + myListData.getReactionCount().getDown());

                    myListData.setIsReacted("up");
                    ((ActivityHome) context).addReaction(myListData, true);
                }
            });
            rbtDownVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (myListData.getIsReacted().equalsIgnoreCase("up")) {
                        myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() - 1);
                    }
                    myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() + 1);
                    tvUp.setText("" + myListData.getReactionCount().getUp());
                    tvDown.setText("" + myListData.getReactionCount().getDown());

                    myListData.setIsReacted("down");
                    ((ActivityHome) context).addReaction(myListData, false);
                }
            });*/

            tvUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!myListData.getIsReacted().equalsIgnoreCase("up")) {
                        if (myListData.getIsReacted().equalsIgnoreCase("down")) {
                            myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() - 1);
                        }

                        myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() + 1);
                        tvUp.setText("" + myListData.getReactionCount().getUp());
                        tvDown.setText("" + myListData.getReactionCount().getDown());

                        myListData.setIsReacted("up");
                        ((ActivityHome) context).addReaction(myListData, true);
                    } else {
                        Log.e("post", "alreadylikedByUser");
                    }


                }
            });
            tvDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!myListData.getIsReacted().equalsIgnoreCase("down")) {
                        if (myListData.getIsReacted().equalsIgnoreCase("up")) {
                            myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() - 1);
                        }
                        myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() + 1);
                        tvUp.setText("" + myListData.getReactionCount().getUp());
                        tvDown.setText("" + myListData.getReactionCount().getDown());

                        myListData.setIsReacted("down");
                        ((ActivityHome) context).addReaction(myListData, false);
                    } else {
                        Log.e("post", "alreadydislikedByUser");
                    }
                }
            });

            tvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ActivityHome) context).sharePost(DP_LINK_POST + myListData.getId());
                }
            });
            Glide.with(context).load(myListData.getCommunityImage()).into(ivProfile);
//            Glide.with(context).load(myListData.getUserPicture()).into(ivProfile);
            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityHome) context).startCommunityDetails(myListData.getCommunityId(), null);
                }
            });
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityHome) context).startCommunityDetails(myListData.getCommunityId(), null);
                }
            });
            tvTemp.setText(myListData.getUserId() + "\n" + sharedPref.getString(shk_ID, ""));
            if (myListData.getUserId().equalsIgnoreCase(sharedPref.getString(shk_ID, ""))) {
                ivDot.setVisibility(View.INVISIBLE);
                tvFollow.setVisibility(View.INVISIBLE);
            } else {
                ivDot.setVisibility(View.VISIBLE);
                tvFollow.setVisibility(View.VISIBLE);
            }
            if (myListData.getIs_person_follow()) {
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
                        myListData.setIs_person_follow(true);
                        tvFollow.setText(context.getResources().getString(R.string.following));
                        tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.BOLD);
                        ((ActivityHome) context).followPerson("" + myListData.getUserId());
                    } else {
                        myListData.setIs_person_follow(false);
                        tvFollow.setText(context.getResources().getString(R.string.follow));
                        tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.NORMAL);
                        ((ActivityHome) context).unfollowPerson("" + myListData.getUserId());
                    }
                }
            });
        }

        @Override
        public void onPostDataChanged(PostListData data, int Position) {
            if (iPosition == Position) {
//                rbtUpVote.setOnCheckedChangeListener(null);
//                rbtDownVote.setOnCheckedChangeListener(null);
                myListData = data;
                AdapterRV_Post.this.notifyItemChanged(Position);
            }
        }
    }

    public class URLTypeViewHolder extends RecyclerView.ViewHolder implements PostDataChanged {
        public TextView_custom tvFollow, tvTitle, tvWriter, tvDetail, tvCommentCount, tvShare, tvTemp,tvUrl;
        TextView tvUp, tvDown;
        public ImageView ivProfile, ivPoster, ivOption, ivDot;
        public Context context;
        public LinearLayout liContainer, comments,urlLayout;
        //        public RadioButton rbtUpVote, rbtDownVote;
        private int iPosition = -1;
        private PostListData myListData;

        public URLTypeViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            this.tvTemp = (TextView_custom) itemView.findViewById(R.id.tvTemp);
            this.tvUrl = (TextView_custom) itemView.findViewById(R.id.tvUrl);
            this.urlLayout = (LinearLayout) itemView.findViewById(R.id.urlLayout);
            this.tvFollow = (TextView_custom) itemView.findViewById(R.id.tvFollow);
            this.tvTitle = (TextView_custom) itemView.findViewById(R.id.tvTitle);
            this.tvDetail = (TextView_custom) itemView.findViewById(R.id.tvDetail);
            this.tvWriter = (TextView_custom) itemView.findViewById(R.id.tvWriter);
            this.tvCommentCount = (TextView_custom) itemView.findViewById(R.id.tvCommentCount);
            this.tvShare = (TextView_custom) itemView.findViewById(R.id.tvShare);
            this.ivProfile = (ImageView) itemView.findViewById(R.id.ivProfile);
            this.ivPoster = (ImageView) itemView.findViewById(R.id.ivPoster);
            this.ivOption = (ImageView) itemView.findViewById(R.id.ivOption);
            this.ivDot = (ImageView) itemView.findViewById(R.id.ivDot);
            this.liContainer = (LinearLayout) itemView.findViewById(R.id.liContainer);
            this.comments = (LinearLayout) itemView.findViewById(R.id.comments);

            this.tvUp = (TextView) itemView.findViewById(R.id.tvUp);
            this.tvDown = (TextView) itemView.findViewById(R.id.tvDown);
//            this.rbtUpVote = (RadioButton) itemView.findViewById(R.id.rbtUpVote);
//            this.rbtDownVote = (RadioButton) itemView.findViewById(R.id.rbtDownVote);


        }

        public void loadData(PostListData myData, int position) {
            this.iPosition = position;
            this.myListData = myData;

            tvTitle.setText("" + myListData.getCommunityName());
            tvWriter.setText("" + myListData.getUserName());
            tvDetail.setText("" + myListData.getDescription());
            tvCommentCount.setText("" + myListData.getCommentCount());
            if(sharedPref.getBoolean(Constants.shk_DARK_THEME,false))
                ivOption.setImageDrawable(context.getResources().getDrawable(R.drawable.three_dots));
            else
                ivOption.setImageDrawable(context.getResources().getDrawable(R.drawable.three_dots_black));



            ivOption.setOnClickListener(view -> ((ActivityHome) context).popupPostReport(myListData,deleteOption,position));
            int iContWidth = Constants.WIDTH - 10;
            int iContHeight = (int) (Constants.HEIGHT / 3.1);
            if (myListData.getDimension() != null && myListData.getDimension().getWidth() != null && myListData.getDimension().getHeight() != null) {
                iContWidth = myListData.getDimension().getWidth();
                iContHeight = myListData.getDimension().getHeight();
            } else {

            }


            int iScrWidth = Constants.WIDTH - 10;
            int iScrHeight = Constants.HEIGHT - 300;
            int iNewWidth = 100, iNewHeight = 100;
            if (myListData.getDimension() == null) {
                iNewWidth = iScrWidth;
                iNewHeight = (int) ((iNewWidth / 4) * 3);
            } else if (iContWidth >= iContHeight) //LandScape
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


            liContainer.setLayoutParams(new LinearLayout.LayoutParams(iNewWidth, iNewHeight));

            tvUp.setText("" + myListData.getReactionCount().getUp());
            tvDown.setText("" + myListData.getReactionCount().getDown());

           /* rbtUpVote.setOnCheckedChangeListener(null);
            rbtDownVote.setOnCheckedChangeListener(null);
            if (myListData.getIsReacted().equalsIgnoreCase("up")) {
                rbtUpVote.setChecked(true);
            } else if (myListData.getIsReacted().equalsIgnoreCase("down")) {
                rbtDownVote.setChecked(true);
            }
            rbtUpVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (myListData.getIsReacted().equalsIgnoreCase("down")) {
                        myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() - 1);
                    }

                    myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() + 1);
                    tvUp.setText("" + myListData.getReactionCount().getUp());
                    tvDown.setText("" + myListData.getReactionCount().getDown());

                    myListData.setIsReacted("up");
                    ((ActivityHome) context).addReaction(myListData, true);
                }
            });
            rbtDownVote.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (myListData.getIsReacted().equalsIgnoreCase("up")) {
                        myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() - 1);
                    }
                    myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() + 1);
                    tvUp.setText("" + myListData.getReactionCount().getUp());
                    tvDown.setText("" + myListData.getReactionCount().getDown());

                    myListData.setIsReacted("down");
                    ((ActivityHome) context).addReaction(myListData, false);
                }
            });*/

            tvUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!myListData.getIsReacted().equalsIgnoreCase("up")) {
                        if (myListData.getIsReacted().equalsIgnoreCase("down")) {
                            myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() - 1);
                        }

                        myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() + 1);
                        tvUp.setText("" + myListData.getReactionCount().getUp());
                        tvDown.setText("" + myListData.getReactionCount().getDown());

                        myListData.setIsReacted("up");
                        ((ActivityHome) context).addReaction(myListData, true);
                    } else {
                        Log.e("post", "alreadylikedByUser");
                    }


                }
            });
            tvDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!myListData.getIsReacted().equalsIgnoreCase("down")) {
                        if (myListData.getIsReacted().equalsIgnoreCase("up")) {
                            myListData.getReactionCount().setUp(myListData.getReactionCount().getUp() - 1);
                        }
                        myListData.getReactionCount().setDown(myListData.getReactionCount().getDown() + 1);
                        tvUp.setText("" + myListData.getReactionCount().getUp());
                        tvDown.setText("" + myListData.getReactionCount().getDown());

                        myListData.setIsReacted("down");
                        ((ActivityHome) context).addReaction(myListData, false);
                    } else {
                        Log.e("post", "alreadydislikedByUser");
                    }
                }
            });


            tvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ActivityHome) context).sharePost(DP_LINK_POST + myListData.getId());
                }
            });
            tvTemp.setText(myListData.getUserId() + "\n" + sharedPref.getString(shk_ID, ""));
            if (myListData.getUserId().equalsIgnoreCase(sharedPref.getString(shk_ID, ""))) {
                ivDot.setVisibility(View.INVISIBLE);
                tvFollow.setVisibility(View.INVISIBLE);
            } else {
                ivDot.setVisibility(View.VISIBLE);
                tvFollow.setVisibility(View.VISIBLE);
            }
            if (myListData.getIs_person_follow()) {
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
                        myListData.setIs_person_follow(true);
                        tvFollow.setText(context.getResources().getString(R.string.following));
                        tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.BOLD);
                        ((ActivityHome) context).followPerson("" + myListData.getUserId());
                    } else {
                        myListData.setIs_person_follow(false);
                        tvFollow.setText(context.getResources().getString(R.string.follow));
                        tvFollow.setTypeface(tvFollow.getTypeface(), Typeface.NORMAL);
                        ((ActivityHome) context).unfollowPerson("" + myListData.getUserId());
                    }

                }
            });

            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ActivityHome) context).showDetail(myListData, position, URLTypeViewHolder.this);
                }
            });



            if (myListData.getMedia().length() > 7) {
                Glide.with(context).load(myListData.getMedia()).into(ivPoster);
            } else {
                ivPoster.setImageResource(R.drawable.click_to_visit);
            }

//            SpannableString content = new SpannableString(myListData.getLink());
//            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            tvUrl.setVisibility(View.VISIBLE);
            tvUrl.setText(myListData.getTitle());
            tvUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sURL = myListData.getLink().trim();
                    if(sURL.length() > 5)
                    {
                        String sStart = sURL.substring(0,4).toLowerCase();
                        if(!sStart.equalsIgnoreCase("http"))
                        {
                            sURL = "http://"+sURL;
                        }
                        Uri webpage = Uri.parse(sURL);
                        Intent intent = new Intent(context,WebviewActivity.class);
                        intent.putExtra("url",sURL);
                        context.startActivity(intent);
                    }
                }
            });

            Glide.with(context).load(myListData.getCommunityImage()).into(ivProfile);
            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityHome) context).startCommunityDetails(myListData.getCommunityId(), null);
                }
            });
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityHome) context).startCommunityDetails(myListData.getCommunityId(), null);
                }
            });

        }

        @Override
        public void onPostDataChanged(PostListData data, int Position) {
            if (iPosition == Position) {
//                rbtUpVote.setOnCheckedChangeListener(null);
//                rbtDownVote.setOnCheckedChangeListener(null);
                myListData = data;
                AdapterRV_Post.this.notifyItemChanged(Position);
            }
        }


        class UnfurlResult {
            String title;
            String description;
            String imageUrl;
            String width;
            String height;

            UnfurlResult() {
            }
        }
    }

    public static int convertPxToDp(Context context, float px) {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }

}