package com.gazabapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gazabapp.R;
import com.gazabapp.customeview.Checkbox_custom;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.CommentList;
import com.gazabapp.pojo.CommentListData;
import com.gazabapp.pojo.CommunityListData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Adapter_ProfileComments extends BaseAdapter
{
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<CommentListData> arrayList = new ArrayList<>();

    public Adapter_ProfileComments(Context context, ArrayList<CommentListData> arrayList)
    {
        this.context = context;
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(this.context);

    }


    @Override
    public int getCount()
    {
        return arrayList.size();
    }

    @Override
    public CommentListData getItem(int i)
    {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }
    MyViewHolder holder;

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.row_profile_comments, parent, false);
            holder = new MyViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (MyViewHolder) convertView.getTag();
        }


        convertView.setTag(holder);


        final CommentListData data = arrayList.get(position);



        try
        {
            Date date = dateFormatCommon.parse(""+data.getCreated().substring(0,data.getCreated().indexOf('.')));
            holder.tvAgo.setText(""+getTimeAgo(date,context));
        }catch (Exception e)
        {

        }


//        holder.tvAgo.setText(""+data.getCreated());
        holder.tvComment.setText(""+data.getComment());
        holder.tvCommunity.setText(""+data.getCommunityName());
        Glide.with(context).load(data.getCommunityPic()).into(holder.ivPhoto);


        return convertView;
    }

    private class MyViewHolder
    {
        private TextView_custom tvAgo,tvComment,tvCommunity;
        private ImageView ivPhoto;


        public MyViewHolder(View item)
        {
            tvCommunity = (TextView_custom) item.findViewById(R.id.tvCommunity);
            tvComment = (TextView_custom) item.findViewById(R.id.tvComment);
            tvAgo = (TextView_custom) item.findViewById(R.id.tvAgo);
            ivPhoto = (ImageView) item.findViewById(R.id.ivPhoto);


        }
    }



    final private SimpleDateFormat dateFormatCommon = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
