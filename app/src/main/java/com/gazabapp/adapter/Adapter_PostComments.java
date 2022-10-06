package com.gazabapp.adapter;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gazabapp.R;
import com.gazabapp.actionlisteners.GiveComment;
import com.gazabapp.customeview.ExpandableTextView_custom;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.PostCommentListData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Adapter_PostComments extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<PostCommentListData> arrayList = new ArrayList<>();
    private GiveComment giveComment;
    private int iParentPosition = -1;

    public Adapter_PostComments(Context context, ArrayList<PostCommentListData> arrayList, GiveComment giveComment, int iParentPosition) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(this.context);
        this.giveComment = giveComment;
        this.iParentPosition = iParentPosition;
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public PostCommentListData getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    MyViewHolder holder;

    public View getView(final int position, View convertView, ViewGroup parent) {
//        if (convertView == null)
//        {
//            convertView = inflater.inflate(R.layout.row_post_comments, parent, false);
//            holder = new MyViewHolder(convertView);
//            convertView.setTag(holder);
//        }
//        else
//        {
//            holder = (MyViewHolder) convertView.getTag();
//        }

        convertView = inflater.inflate(R.layout.row_post_comments, parent, false);
        holder = new MyViewHolder(convertView);
        convertView.setTag(holder);


        final PostCommentListData data = arrayList.get(position);
        try {
            Date date = dateFormatCommon.parse("" + data.getCreated().substring(0, data.getCreated().indexOf('.')));
            holder.tvAgo.setText("" + getTimeAgo(date, context));
        } catch (Exception e) {

        }
        holder.tvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveComment.clickOnReply(data, iParentPosition, false, position, data.getParentId());
            }
        });


        holder.tvComment.setText("" + data.getComment());
        holder.tvUser.setText("" + data.getUserName());

//        int iH = getTextViewHeight(holder.tvComment);
//        holder.tvComment.setHeight(iH);

//


        Glide.with(context).load(data.getUserPic()).into(holder.ivProfile);
        return convertView;
    }

    private class MyViewHolder {
        private TextView_custom tvUser, tvAgo, tvReply;
        private ImageView ivProfile;
        private ExpandableTextView_custom tvComment;

        public MyViewHolder(View item) {
            tvUser = (TextView_custom) item.findViewById(R.id.tvUser);
            tvAgo = (TextView_custom) item.findViewById(R.id.tvAgo);
            tvComment = (ExpandableTextView_custom) item.findViewById(R.id.tvComment);
            ivProfile = (ImageView) item.findViewById(R.id.ivProfile);
            tvReply = (TextView_custom) item.findViewById(R.id.tvReply);

        }
    }

    final private SimpleDateFormat dateFormatCommon = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
            timeAgo = " " + ctx.getResources().getString(R.string.now);
            //            timeAgo = " " +  ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_minute)+" "+ctx.getResources().getString(R.string.date_util_term_less);
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

    public static int getTextViewHeight(TextView_custom textView) {
        WindowManager wm =
                (WindowManager) textView.getContext().getSystemService(Context.WINDOW_SERVICE);
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
