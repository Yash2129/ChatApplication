package com.android.twaddle.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.twaddle.Adapters.MessagesAdapter;
import com.android.twaddle.Models.Message;
import com.android.twaddle.R;
import com.android.twaddle.databinding.ActivityMessageBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

    ActivityMessageBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;

    FirebaseDatabase database;
    String senderRoom, receiverRoom;
    String receiverUid;
    String senderUid;
    private static final int PICK_IMAGE=1;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this,messages);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.smoothScrollToPosition(messages.size());



        String name = getIntent().getStringExtra("name");
        receiverUid = getIntent().getStringExtra("uid");
        senderUid = FirebaseAuth.getInstance().getUid();
        Picasso.get()
                .load(getIntent().getStringExtra("profile"))
                .placeholder(R.drawable.avatar)
                .into(binding.profileChatImage);

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        database = FirebaseDatabase.getInstance();

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren()){
                            Message message = snapshot1.getValue(Message.class);
                            messages.add(message);
                        }
                        adapter.notifyItemInserted(messages.size());
                        binding.recyclerView.smoothScrollToPosition(messages.size());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageTxt = binding.msgBox.getText().toString();

                Date date = new Date();
                Message message = new Message(messageTxt, senderUid, date.getTime());
                binding.msgBox.setText("");

                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg",message.getMessage());
                lastMsgObj.put("lastMsgTime", date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .push()
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });

                    }
                });
            }
        });

        binding.userChatName.setText(name);

        binding.msgCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        binding.backChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE || requestCode == RESULT_OK || data!=null || data.getData()!=null){
            uri = data.getData();
            String url = uri.toString();
            Intent intent = new Intent(MessageActivity.this,SendImageActivity.class);
            intent.putExtra("u",url);

            intent.putExtra("ruid",receiverUid);
            intent.putExtra("suid",senderUid);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "No file Selected", Toast.LENGTH_SHORT).show();
        }

    }
}