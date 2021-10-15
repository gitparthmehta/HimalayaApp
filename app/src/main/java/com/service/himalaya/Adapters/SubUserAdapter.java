package com.service.himalaya.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.himalaya.Models.Sub_UserListModel;
import com.service.himalaya.Models.UserListModel;
import com.service.himalaya.R;

import java.util.List;


public class SubUserAdapter extends RecyclerView.Adapter<SubUserAdapter.MyViewHolder> {

    Context context;
    int count = 0;
    private List<Sub_UserListModel> userAddedLists;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView user_name_txt;
        LinearLayout linear_layout, bg_border;
        ImageView add_icon;

        public MyViewHolder(View view) {
            super(view);

            user_name_txt = view.findViewById(R.id.user_name_txt);
            add_icon = view.findViewById(R.id.add_icon);


        }
    }

    public SubUserAdapter(List<Sub_UserListModel> listdata, Activity context) {
        this.userAddedLists = listdata;
        this.context = context;

    }


    @Override
    public SubUserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sub_uer_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SubUserAdapter.MyViewHolder holder, int position) {
        final Sub_UserListModel scollListModel = userAddedLists.get(position);


        holder.user_name_txt.setText(scollListModel.getSub_userName());

        if (scollListModel.getSub_isParent().equals("1")) {
            holder.add_icon.setVisibility(View.VISIBLE);
        } else {
            holder.add_icon.setVisibility(View.GONE);

        }


    }

    @Override
    public int getItemCount() {
        return userAddedLists.size();
    }

}
