package com.android.twaddle.BottomNavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.twaddle.Activities.ChatActivity;
import com.android.twaddle.R;
import com.android.twaddle.databinding.ActivityCameraBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.annotations.Nullable;

public class CameraActivity extends AppCompatActivity {

    ActivityCameraBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ContextCompat.checkSelfPermission(  CameraActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( CameraActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }

        binding.btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult (intent, 100);
            }
        });



        binding.bottomNavigationView.setSelectedItemId(R.id.camera_btn);
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.chat_btn:
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        startActivity(intent);
                        finishAffinity();
                        //Toast.makeText(StoryActivity.this,"Chat Selected",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.story_btn:
                        Intent intent1 = new Intent(getApplicationContext(), StoryActivity.class);
                        startActivity(intent1);
                        finishAffinity();
                        //Toast.makeText(StoryActivity.this,"Story Selected",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.camera_btn:
                        Toast.makeText(CameraActivity.this,"Camera Selected",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.call_btn:
                        Toast.makeText(CameraActivity.this,"Call Selected",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings_btn:
                        Toast.makeText(CameraActivity.this,"Settings Selected",Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }
    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            binding.imageView.setImageBitmap(captureImage);
        }
    }
}