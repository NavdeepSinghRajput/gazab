package com.gazabapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gazabapp.ActivityHome;
import com.gazabapp.R;
import com.gazabapp.customeview.Checkbox_custom;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.CommunityListData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Explorer extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<CommunityListData> arrayList = new ArrayList<>();


    public Adapter_Explorer(Context context, ArrayList<CommunityListData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(this.context);

    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public CommunityListData getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    MyViewHolder holder;

    public View getView(final int position, View convertView, ViewGroup parent) {
//        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.row_explorer, parent, false);
            holder = new MyViewHolder(convertView);
            convertView.setTag(holder);
        }
//        else
//        {
//            holder = (MyViewHolder) convertView.getTag();
//        }

//        convertView = inflater.inflate(R.layout.row_explorer, parent, false);
//        holder = new MyViewHolder(convertView);
//        convertView.setTag(holder);


        final CommunityListData data = arrayList.get(position);

        holder.crMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityHome) context).startCommunityDetails(data.getId(), null);
            }
        });


        holder.tvTitle.setText("" + data.getName());
        holder.tvUserCount.setText("" + data.getTotal_followers());

        Glide.with(context).load(data.getImage()).into(holder.ivCover);

        holder.cbFollow.setChecked(data.getIs_community_follow());
       /* if(data.getIs_community_follow())
        {
            holder.cbFollow.setText(context.getResources().getString(R.string.following));
            holder.cbFollow.setTextColor(((ActivityHome) context).getAttributeColor(R.attr.following));
            holder.tvTitle.setBackgroundColor(((ActivityHome) context).getAttributeColor(R.attr.community_choose_name_back_select));
        }
        else
        {
            holder.cbFollow.setText(context.getResources().getString(R.string.follow));
            holder.cbFollow.setTextColor(((ActivityHome) context).getAttributeColor(R.attr.follow));
            holder.tvTitle.setBackgroundColor(((ActivityHome) context).getAttributeColor(R.attr.community_choose_name_back));
        }*/
        holder.cbFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean flag) {
                data.setIs_community_follow(flag);
                notifyDataSetChanged();
                if (flag) {
                    ((ActivityHome) context).followCommunity(data.getId(), null);
                } else {
                    ((ActivityHome) context).unfollowCommunity(data.getId(), null);
                }
            }
        });
        if (data.getIs_community_follow()) {
            holder.communityFollowll.setBackgroundColor(((ActivityHome) context).getAttributeColor(R.attr.followCommunitySelectedColor));
            holder.communityFollowSelected.setVisibility(View.VISIBLE);
            holder.communityFollowUnSelected.setVisibility(View.GONE);
            holder.ivCover.setBorderWidth(5);
            holder.ivCover.setBorderColor(((ActivityHome) context).getAttributeColor(R.attr.followCommunitySelectedColor));

        } else {
            holder.communityFollowll.setBackgroundColor(((ActivityHome) context).getAttributeColor(R.attr.community_choose_following_background));
            holder.communityFollowSelected.setVisibility(View.GONE);
            holder.communityFollowUnSelected.setVisibility(View.VISIBLE);
            holder.ivCover.setBorderWidth(0);
            holder.ivCover.setBorderColor(((ActivityHome) context).getAttributeColor(R.attr.followCommunitySelectedColor));

        }
        holder.communityFollowll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getIs_community_follow()) {
                    ((ActivityHome) context).unfollowCommunity(data.getId(), null);

                } else {
                    ((ActivityHome) context).followCommunity(data.getId(), null);

                }
                if (holder.communityFollowUnSelected.getVisibility() == View.VISIBLE) {
                } else {
                }
                data.setIs_community_follow(!data.getIs_community_follow());
                notifyDataSetChanged();

            }
        });

        return convertView;
    }

    private class MyViewHolder {
        private TextView_custom tvTitle, tvUserCount;
        private CircleImageView ivCover;
        private Checkbox_custom cbFollow;
        private LinearLayout crMain;
        LinearLayout communityFollowll;
        ImageView communityFollowSelected;
        TextView communityFollowUnSelected;

        public MyViewHolder(View item) {
            tvTitle = (TextView_custom) item.findViewById(R.id.tvTitle);
            ivCover = (CircleImageView) item.findViewById(R.id.ivCover);
            tvUserCount = (TextView_custom) item.findViewById(R.id.tvUserCount);
            cbFollow = (Checkbox_custom) item.findViewById(R.id.cbFollow);
            crMain = (LinearLayout) item.findViewById(R.id.crMain);
            communityFollowll = (LinearLayout) item.findViewById(R.id.communityFollowll);
            communityFollowSelected = (ImageView) item.findViewById(R.id.communityFollowSelected);
            communityFollowUnSelected = (TextView) item.findViewById(R.id.communityFollowUnSelected);


        }
    }

}
