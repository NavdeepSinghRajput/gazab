package com.gazabapp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.gazabapp.ActivityHome;
import com.gazabapp.R;
import com.gazabapp.customeview.Button_custom;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.fragments.FragmentProfile_Base;
import com.gazabapp.pojo.FollowUsersData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Adapter_Following extends BaseAdapter
{
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<FollowUsersData> arrayList = new ArrayList<>();

    public Adapter_Following(Context context, ArrayList<FollowUsersData> arrayList)
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
    public FollowUsersData getItem(int i)
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
            convertView = inflater.inflate(R.layout.row_profile_following, parent, false);
            holder = new MyViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (MyViewHolder) convertView.getTag();
        }

        convertView.setTag(holder);



        final FollowUsersData data = arrayList.get(position);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //set Fragmentclass Arguments
//                ((ActivityHome) context).startProfile(data.getId());

            }
        });



        holder.tvName.setText(""+data.getDisplayName());
        Glide.with(context).load(data.getPicture()).into(holder.ivPhoto);

        return convertView;
    }

    private class MyViewHolder
    {
        private TextView_custom tvName;
        private ImageView ivPhoto;
        private Button_custom btFollow;

        public MyViewHolder(View item)
        {
            btFollow = (Button_custom) item.findViewById(R.id.btFollow);
            tvName = (TextView_custom) item.findViewById(R.id.tvName);
            ivPhoto = (ImageView) item.findViewById(R.id.ivPhoto);
        }
    }



    final private SimpleDateFormat dateFormatCommon = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Date currentDate()
    {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }
    private static int getTimeDistanceInMinutes(long time) {
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
