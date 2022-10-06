package com.gazabapp.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.ColorInt;

import com.bumptech.glide.Glide;
import com.gazabapp.ActivityHome;
import com.gazabapp.ActivityRegistration;
import com.gazabapp.R;
import com.gazabapp.customeview.Checkbox_custom;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.CommunityListData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_ChooseCommunity extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<CommunityListData> arrayList = new ArrayList<>();

    public Adapter_ChooseCommunity(Context context, ArrayList<CommunityListData> arrayList) {
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
//        {
//            convertView = inflater.inflate(R.layout.row_choose_community, parent, false);
//            holder = new MyViewHolder(convertView);
//            convertView.setTag(holder);
//        }
//        else
//        {
//            holder = (MyViewHolder) convertView.getTag();
//        }

        convertView = inflater.inflate(R.layout.row_choose_community, parent, false);
        holder = new MyViewHolder(convertView);
        convertView.setTag(holder);

        final CommunityListData data = arrayList.get(position);

        holder.cbFollow.setChecked(data.getIs_community_follow());

        if (data.getIs_community_follow()) {
            holder.follow.setVisibility(View.VISIBLE);
            holder.ivCover.setBorderWidth(5);
            holder.ivCover.setBorderColor(getAttributeColor(R.attr.followCommunitySelectedColor));

        } else {
            holder.follow.setVisibility(View.GONE);
            holder.ivCover.setBorderWidth(0);
            holder.ivCover.setBorderColor( getAttributeColor(R.attr.followCommunitySelectedColor));

        }

        /*if (data.getIs_community_follow()) {
            holder.cbFollow.setText(context.getResources().getString(R.string.following));
            holder.cbFollow.setTextColor(((ActivityRegistration) context).getAttributeColor(R.attr.following));
        } else {
            holder.cbFollow.setText(context.getResources().getString(R.string.follow));
            holder.cbFollow.setTextColor(((ActivityRegistration) context).getAttributeColor(R.attr.follow));
        }*/

        holder.tvTitle.setText("" + data.getName());
        Glide.with(context).load(data.getImage()).into(holder.ivCover);

        holder.ivCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.follow.getVisibility()==View.GONE){
                    ((ActivityRegistration) context).followCommunity(data.getId());
                } else {
                    ((ActivityRegistration) context).unfollowCommunity(data.getId());
                }
                data.setIs_community_follow(!data.getIs_community_follow());
                notifyDataSetChanged();

            }
        });
        holder.cbFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.setIs_community_follow(isChecked);
                notifyDataSetChanged();

                if (isChecked) {
                    ((ActivityRegistration) context).followCommunity(data.getId());
                } else {
                    ((ActivityRegistration) context).unfollowCommunity(data.getId());
                }


            }
        });


        return convertView;
    }

    private class MyViewHolder {
        private TextView_custom tvTitle;
        private CircleImageView ivCover;
        private Checkbox_custom cbFollow;
        private ImageView follow;

        public MyViewHolder(View item) {
            tvTitle = (TextView_custom) item.findViewById(R.id.tvTitle);
            ivCover = (CircleImageView) item.findViewById(R.id.ivCover);
            cbFollow = (Checkbox_custom) item.findViewById(R.id.cbFollow);
            follow = (ImageView) item.findViewById(R.id.follow);

        }
    }
    public int getAttributeColor(int attributeId)
    {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeId, typedValue, true);
        @ColorInt int colorRes = typedValue.data;
        return colorRes;
    }

}
