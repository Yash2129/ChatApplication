package com.android.twaddle.BottomNavigation;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.twaddle.Activities.ChatActivity;
import com.android.twaddle.Models.Message;
import com.android.twaddle.R;
import com.android.twaddle.databinding.ActivityCameraBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CameraActivity extends AppCompatActivity {

    FirebaseDatabase database;
    FirebaseStorage storage;
    String senderRoom, receiverRoom;
    String receiverUid;
    String senderUid;
    ProgressDialog dialog;
    ArrayList<Message> messages;
    Bitmap Image;
    Uri captureImage;
    ActivityCameraBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Image....");
        dialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        senderUid = getIntent().getStringExtra("senderuid");
        receiverUid = getIntent().getStringExtra("receiveruid");
        senderRoom = senderUid+receiverUid;
        receiverRoom= receiverUid+senderUid;


        if (ContextCompat.checkSelfPermission(  CameraActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( CameraActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);

        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult (intent, 100);
        binding.btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(Image != null)
              {
                  dialog.show();
                  Uri selectedImage = captureImage;
                  Calendar calendar = Calendar.getInstance();
                  StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");

                  reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                      @Override
                      public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                          if (task.isSuccessful()){
                              reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                  @Override
                                  public void onSuccess(Uri uri) {
                                      String filePath = uri.toString();

                                      String messageTxt = binding.msgBox1.getText().toString();

                                      Date date = new Date();
                                      Message message = new Message(messageTxt, senderUid, date.getTime());
                                      message.setMessage("photo");
                                      message.setImageUrl(filePath);


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
                                              dialog.dismiss();
                                              database.getReference().child("chats")
                                                      .child(receiverRoom)
                                                      .child("messages")
                                                      .push()
                                                      .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                  });
              }
              }

        });





    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {

             Image= (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            Image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), Image, "Title", null);
            captureImage= Uri.parse(path);
             binding.imageView.setImageURI(captureImage);
        }
                }


            }

