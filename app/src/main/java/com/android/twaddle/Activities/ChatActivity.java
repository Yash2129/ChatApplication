package com.android.twaddle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.android.twaddle.BottomNavigation.CameraActivity;
import com.android.twaddle.Models.User;
import com.android.twaddle.Adapters.UserAdapter;
import com.android.twaddle.R;
import com.android.twaddle.databinding.ActivityChatBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    FirebaseDatabase database;
    ArrayList<User> users;
    UserAdapter userAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       binding.bottomNavigationView.setSelectedItemId(R.id.chat_btn);
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.chat_btn:

                        //Toast.makeText(ChatActivity.this,"Chat Selected",Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.call_btn:
                        //Intent intent = new Intent(getApplicationContext(), StoryActivity.class);
                        //startActivity(intent);
                        //Toast.makeText(ChatActivity.this,"Call Selected",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings_btn:

                        Intent intent4 = new Intent(getApplicationContext(), ChatSettings.class);
                        startActivity(intent4);
                        //Toast.makeText(ChatActivity.this,"Settings Selected",Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();




        userAdapter = new UserAdapter(this,users);
       //    binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(userAdapter);

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    if (!user.getUid().equals(FirebaseAuth.getInstance().getUid())){

                        users.add(user);
                    }

                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @Override
    public void onBackPressed() {

// make sure you have this outcommented
// super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}