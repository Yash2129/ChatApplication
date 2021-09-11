package com.android.twaddle.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.twaddle.Activities.MessageActivity;
import com.android.twaddle.Models.Message;
import com.android.twaddle.Models.User;
import com.android.twaddle.R;
import com.android.twaddle.databinding.RowConversationBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    Context context;
    ArrayList<User> users;
    Message message;


    public UserAdapter(Context context, ArrayList<User> users){

        this.context=context;
        this.users=users;
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        User user = users.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();

        String senderRoom = senderId + user.getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            Calendar c = Calendar.getInstance();
                            System.out.println("Current time => "+c.getTime());

                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formattedDate = df.format(c.getTime());
                            // formattedDate have current date/time




                            holder.binding.lastMsg.setText(lastMsg);
                            holder.binding.msgTime.setText(formattedDate);


                        }else {
                            holder.binding.lastMsg.setText("Tap to Chat");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.userProfilename.setText(user.getName());



        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.defaultuserdp)
                .into(holder.binding.profileDp);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("name",user.getName());

                intent.putExtra("uid",user.getUid());
                intent.putExtra("profile", user.getProfileImage());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        RowConversationBinding binding;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
