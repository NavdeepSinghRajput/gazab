package com.gazabapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gazabapp.ActivityHome;
import com.gazabapp.Constants;
import com.gazabapp.R;
import com.gazabapp.actionlisteners.GiveComment;
import com.gazabapp.actionlisteners.PostDataChanged;
import com.gazabapp.customeview.ExpandableTextView_custom;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.PostCommentListData;
import com.gazabapp.pojo.PostListData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.gazabapp.Constants.VIEW_TYPE_COMMENTS;

public class AdapterRV_Comments_Child extends  RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static SharedPreferences sharedPref = null;
    private ArrayList<PostCommentListData> arrayList;
    private PostListData headerData;
    private Context context;
    private GiveComment giveComment;

    private static int iParentPosition = -1;

    public AdapterRV_Comments_Child(ArrayList<PostCommentListData> arrayList, Context context, GiveComment giveComment, int iParentPosition)
    {
        this.arrayList = arrayList;
        this.context = context;
        this.giveComment = giveComment;
        this.iParentPosition = iParentPosition;
        sharedPref = ((ActivityHome) context).getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post_comments, parent, false);
        return new CommentViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ((CommentViewHolder) holder).loadData(arrayList.get(position),position,giveComment);
    }
    @Override
    public int getItemViewType(int position)
    {
        return VIEW_TYPE_COMMENTS;
    }
    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }


    public static class CommentViewHolder extends RecyclerView.ViewHolder
    {

        private Context context;
        private View itemView;
        private TextView_custom tvUser,tvAgo,tvReply;
        private ImageView ivProfile;
        private ExpandableTextView_custom tvComment;

        public CommentViewHolder (View itemView, Context context)
        {
            super(itemView);
            this.context = context;
            this.itemView = itemView;

            tvUser = (TextView_custom) itemView.findViewById(R.id.tvUser);
            tvAgo = (TextView_custom) itemView.findViewById(R.id.tvAgo);
            tvComment = (ExpandableTextView_custom) itemView.findViewById(R.id.tvComment);
            ivProfile = (ImageView) itemView.findViewById(R.id.ivProfile);
            tvReply = (TextView_custom) itemView.findViewById(R.id.tvReply);
        }

        public void loadData(PostCommentListData myListData, int position,GiveComment giveComment)
        {
            try
            {
                Date date = dateFormatCommon.parse(""+myListData.getCreated().substring(0,myListData.getCreated().indexOf('.')));
                String sAgo = getTimeAgo(date,context);
                if(sAgo != null && !sAgo.equalsIgnoreCase("null") && sAgo.length() > 4)
                {
                    tvAgo.setText(""+sAgo);
                }
                else
                {
                    tvAgo.setText(context.getResources().getString(R.string.date_util_term_less) + " " +  context.getResources().getString(R.string.date_util_term_a) + " " + context.getResources().getString(R.string.date_util_unit_minute));
                }
            }catch (Exception e)
            {
                tvAgo.setText(context.getResources().getString(R.string.date_util_term_less) + " " +  context.getResources().getString(R.string.date_util_term_a) + " " + context.getResources().getString(R.string.date_util_unit_minute));
            }
            tvComment.setText(""+myListData.getComment());

            if(myListData.getUserName() == null || myListData.getUserName().equalsIgnoreCase("null"))
            {
                tvUser.setText("");
            }
            else
            {
                tvUser.setText(""+myListData.getUserName());
            }

            Glide.with(context).load(myListData.getUserPic()).into(ivProfile);
            tvReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    giveComment.clickOnReply(myListData,iParentPosition,false,position,myListData.getParentId());
                }
            });
        }

    }
    final static private SimpleDateFormat dateFormatCommon = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Date currentDate()
    {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }
    private static int getTimeDistanceInMinutes(long time)
    {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }
    public static String getTimeAgo(Date date, Context ctx)
    {

        if(date == null) {
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
            timeAgo = ctx.getResources().getString(R.string.date_util_term_less) + " " +  ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim == 1) {
            return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
        } else if (dim >= 45 && dim <= 89) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+ctx.getResources().getString(R.string.date_util_term_an)+ " " + ctx.getResources().getString(R.string.date_util_unit_hour);
        } else if (dim >= 90 && dim <= 1439) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 60)) + " " + ctx.getResources().getString(R.string.date_util_unit_hours);
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
        } else if (dim >= 2520 && dim <= 43199) {
            timeAgo = (Math.round(dim / 1440)) + " " + ctx.getResources().getString(R.string.date_util_unit_days);
        } else if (dim >= 43200 && dim <= 86399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+ctx.getResources().getString(R.string.date_util_term_a)+ " " + ctx.getResources().getString(R.string.date_util_unit_month);
        } else if (dim >= 86400 && dim <= 525599) {
            timeAgo = (Math.round(dim / 43200)) + " " + ctx.getResources().getString(R.string.date_util_unit_months);
        } else if (dim >= 525600 && dim <= 655199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+ctx.getResources().getString(R.string.date_util_term_a)+ " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " "+ctx.getResources().getString(R.string.date_util_term_a)+ " " + ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
        }

        return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }
}
