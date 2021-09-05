package com.android.twaddle.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.twaddle.Models.User;
import com.android.twaddle.R;
import com.android.twaddle.databinding.ActivityChatSettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ChatSettings extends AppCompatActivity {

    ActivityChatSettingsBinding binding;    //here name of ActivityMainBinding will be Settings name in other file
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(ChatSettings.this,Login.class));
                finish();
            }
        });

        //Back Arrow Code
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChatSettings.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        binding.savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String about=binding.etAbout.getText().toString();
                String name=binding.etName.getText().toString();
                String phone=binding.etPhone.getText().toString();

                HashMap<String,Object>obj = new HashMap<>();
                obj.put("name",name);
                obj.put("phoneNumber",phone);
                obj.put("about",about);

                database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(obj);
                Toast.makeText(ChatSettings.this, "Saved", Toast.LENGTH_SHORT).show();

            }
        });

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User users =snapshot.getValue(User.class);
                        Picasso.get()
                                .load(users.getProfileImage())
                                .placeholder(R.drawable.avatar)
                                .into(binding.profileImage);

                        binding.etAbout.setText(users.getAbout());
                        binding.etName.setText(users.getName());
                        binding.etPhone.setText(users.getPhoneNumber());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //plus code
        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,33);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData() !=null){
            Uri sFile=data.getData();
            binding.profileImage.setImageURI(sFile);
            final StorageReference reference= storage.getReference().child("profile pictures")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                                    .child("profileImage").setValue(uri.toString());
                            Toast.makeText(ChatSettings.this, "Dp uploaded Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}