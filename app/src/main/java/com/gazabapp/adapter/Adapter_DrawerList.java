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
import com.gazabapp.pojo.CommunityListData;
import com.gazabapp.pojo.Parcelable_Drawer;

import java.util.ArrayList;

public class Adapter_DrawerList extends BaseAdapter
{
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<CommunityListData> arrayList = new ArrayList<>();

    public Adapter_DrawerList(Context context, ArrayList<CommunityListData> arrayList)
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

        convertView = inflater.inflate(R.layout.row_drw_community, parent, false);
        holder = new MyViewHolder(convertView);
        convertView.setTag(holder);


        final CommunityListData data = arrayList.get(position);

        holder.tvTitle.setText(""+data.getName());
        Glide.with(context).load(data.getImage()).into(holder.ivIcon);

        return convertView;
    }

    private class MyViewHolder
    {
        private TextView_custom tvTitle;
        private ImageView ivIcon;


        public MyViewHolder(View item)
        {
            tvTitle = (TextView_custom) item.findViewById(R.id.tvTitle);
            ivIcon = (ImageView) item.findViewById(R.id.ivIcon);
        }
    }

}
