package com.android.twaddle.BottomNavigation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.twaddle.Activities.ChatActivity;
import com.android.twaddle.Models.Status;
import com.android.twaddle.Models.StatusAdapter;
import com.android.twaddle.Models.User;
import com.android.twaddle.Models.UserStatus;
import com.android.twaddle.R;
import com.android.twaddle.databinding.ActivityStoryBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StoryActivity extends AppCompatActivity {

    ActivityStoryBinding binding;
    StatusAdapter statusAdapter;

    ArrayList<UserStatus> userStatuses;

    FirebaseDatabase database;
    ProgressDialog dialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Status...");
        dialog.setCancelable(false);
        database = FirebaseDatabase.getInstance();

        userStatuses = new ArrayList<>();
        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        statusAdapter = new StatusAdapter(this,userStatuses);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        binding.recyclerView2.setLayoutManager(layoutManager);
        binding.recyclerView2.setAdapter(statusAdapter);
        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for(DataSnapshot storySnapshot : snapshot.getChildren()){
                        UserStatus status = new UserStatus();
                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();
                        for(DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()){
                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }

                        status.setStatuses(statuses);
                        userStatuses.add(status);
                    }


                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,75);
            }
        });

        binding.bottomNavigationView.setSelectedItemId(R.id.story_btn);
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.chat_btn:
                        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                        startActivity(intent);
                        finishAffinity();
                        //Toast.makeText(StoryActivity.this,"Chat Selected",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.story_btn:

                        //Toast.makeText(StoryActivity.this,"Story Selected",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.camera_btn:
                        Toast.makeText(StoryActivity.this,"Camera Selected",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.call_btn:
                        Toast.makeText(StoryActivity.this,"Call Selected",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings_btn:
                        Toast.makeText(StoryActivity.this,"Settings Selected",Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data!=null){
            if (data.getData()!=null){
                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                UserStatus userStatus = new UserStatus();
                                userStatus.setName(user.getName());
                                userStatus.setProfileImage(user.getProfileImage());
                                userStatus.getLastUpdated(date.getTime());


                                HashMap<String, Object> obj = new HashMap<>();
                                obj.put("name", userStatus.getName());
                                obj.put("profileImage", userStatus.getProfileImage());
                                obj.put("lastUpdated", userStatus.getLastUpdated(date.getTime()));

                                String imageUrl = uri.toString();
                                Status status = new Status(imageUrl, userStatus.getLastUpdated(date.getTime()));


                                database.getReference()
                                        .child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .updateChildren(obj);

                                database.getReference()
                                        .child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("statuses")
                                        .push()
                                        .setValue(status);

                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {

// make sure you have this outcommented
// super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}