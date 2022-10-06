package com.gazabapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.gazabapp.R;
import com.gazabapp.actionlisteners.SelectCommunity;
import com.gazabapp.customeview.TextView_custom;
import com.gazabapp.pojo.CommunityListData;

import java.util.ArrayList;

public class Adapter_ChooseCommunityToPost extends BaseAdapter
{
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<CommunityListData> arrayList = new ArrayList<>();
    private int iSelected = -1;
    private SelectCommunity selectComm;
    public Adapter_ChooseCommunityToPost(Context context, ArrayList<CommunityListData> arrayList, SelectCommunity selectComm)
    {
        this.context = context;
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(this.context);
        this.selectComm = selectComm;
    }


    @Override
    public int getCount()
    {
        return arrayList.size();
    }

    @Override
    public CommunityListData getItem(int i)
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
        convertView = inflater.inflate(R.layout.row_choose_community_to_post, parent, false);
        holder = new MyViewHolder(convertView);
        convertView.setTag(holder);


        final CommunityListData data = arrayList.get(position);

        holder.tvCommunityName.setText(""+data.getName());
        Glide.with(context).load(data.getImage()).apply(RequestOptions.bitmapTransform(new RoundedCorners(25))).into(holder.ivCommIcon);


        if(iSelected == position)
        {
            holder.cbSelect.setChecked(true);
            data.setIs_community_follow(true);
        }
        else
        {
            holder.cbSelect.setChecked(false);
            data.setIs_community_follow(false);
        }





        holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean flag)
            {
                if(flag)
                {
                    iSelected = position;
                    selectComm.callbackSelected(data.getName(),data.getId());
                }
                else
                {
                    iSelected = -2;
                    selectComm.callbackSelected("","");
                }
                data.setIs_community_follow(flag);
                notifyDataSetChanged();

            }
        });



        return convertView;
    }

    private class MyViewHolder
    {
        private TextView_custom tvCommunityName;
        private ImageView ivCommIcon;
        private CheckBox cbSelect;

        public MyViewHolder(View item)
        {
            tvCommunityName = (TextView_custom) item.findViewById(R.id.tvCommunityName);
            ivCommIcon = (ImageView) item.findViewById(R.id.ivCommIcon);
            cbSelect = (CheckBox) item.findViewById(R.id.cbSelect);

        }
    }

}
