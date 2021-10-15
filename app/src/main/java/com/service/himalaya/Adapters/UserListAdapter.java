package com.service.himalaya.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.service.himalaya.Activities.UserListActivity;
import com.service.himalaya.Activities.UserLocationActivity;
import com.service.himalaya.ItemClickListner.SubUserClickListner;
import com.service.himalaya.Models.Sub_UserListModel;
import com.service.himalaya.Models.UserListModel;
import com.service.himalaya.R;
import com.service.himalaya.Utils.CustomLinearLayoutManager;

import java.util.List;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    Context context;
    private List<UserListModel> userAddedLists;
    Activity activity;
    SubUserClickListner subUserClickListner;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView user_name_txt;
        LinearLayout linear_layout;
        ImageView add_icon;

        public MyViewHolder(View view) {
            super(view);

            user_name_txt = view.findViewById(R.id.user_name_txt);
            add_icon = view.findViewById(R.id.add_icon);
            linear_layout = view.findViewById(R.id.linear_layout);


        }
    }

    public UserListAdapter(List<UserListModel> listdata, Activity context, SubUserClickListner subUserClickListner) {
        this.userAddedLists = listdata;
        this.context = context;
        this.activity = context;
        this.subUserClickListner = subUserClickListner;
    }


    @Override
    public UserListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userlist_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UserListAdapter.MyViewHolder holder, int position) {
        final UserListModel scollListModel = userAddedLists.get(position);


        holder.user_name_txt.setText(scollListModel.getUserName());

        if (scollListModel.getIsParent().equals("1")) {
            holder.add_icon.setVisibility(View.VISIBLE);
//            sub_user_recycleview.setVisibility(View.VISIBLE);


        } else {
            holder.add_icon.setVisibility(View.GONE);
//            sub_user_recycleview.setVisibility(View.GONE);

        }

        holder.add_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subUserClickListner.SubUserClickListner(scollListModel.getUserId(), scollListModel.getUserName());

            }
        });
        holder.linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.add_icon.getVisibility()==View.GONE){
                    Intent intent = new Intent(context, UserLocationActivity.class);
                    intent.putExtra("user_id", scollListModel.getUserId());
                    intent.putExtra("user_name", scollListModel.getUserName());
                    context.startActivity(intent);
                }else {
                    subUserClickListner.SubUserClickListner(scollListModel.getUserId(), scollListModel.getUserName());

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return userAddedLists.size();
    }

}
