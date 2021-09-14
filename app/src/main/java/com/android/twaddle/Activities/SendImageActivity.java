package com.android.twaddle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.android.twaddle.Adapters.MessagesAdapter;
import com.android.twaddle.Models.Message;
import com.android.twaddle.databinding.ActivityMessageBinding;
import com.android.twaddle.databinding.ActivitySendImageBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class SendImageActivity extends AppCompatActivity {

    ActivitySendImageBinding binding;
    String url, receiver_uid, sender_uid;
    String selectedImage;
    UploadTask uploadTask;
    Uri uri;
    MessagesAdapter adapter;
    ArrayList<Message> messages;
    StorageReference storageReference;
    String senderRoom, receiverRoom;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        selectedImage = getIntent().getStringExtra("u");

        sender_uid = getIntent().getStringExtra("suid");
        receiver_uid = getIntent().getStringExtra("ruid");

        senderRoom = sender_uid + receiver_uid;
        receiverRoom = receiver_uid + sender_uid;

        binding.ivSendImage.setImageURI(Uri.parse(selectedImage));

        database.getReference().child("chats")
                .child(senderRoom)

                .child("image");


        binding.btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("image")
                        .push()
                        .setValue(selectedImage).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        database.getReference().child("chats")
                                .child(receiverRoom)

                                .child("image")
                                .push()
                                .setValue(selectedImage).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                finish();
                            }
                        });

                    }
                });
            }
        });



    }
}