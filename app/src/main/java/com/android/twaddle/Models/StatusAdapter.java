package com.android.twaddle.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.twaddle.R;
import com.android.twaddle.databinding.ItemStatusBinding;

import java.util.ArrayList;


public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

        Context context;
        ArrayList<UserStatus> userStatuses;

        public StatusAdapter(Context context, ArrayList<UserStatus> userStatuses){
            this.context = context;
            this.userStatuses= userStatuses;
        }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_status,parent,false);
        return new StatusViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {

            UserStatus userStatus = userStatuses.get(position);
            holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder{

        ItemStatusBinding binding;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemStatusBinding.bind(itemView);
        }
    }

}
