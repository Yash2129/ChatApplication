package com.android.twaddle.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.twaddle.Models.User;
import com.android.twaddle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SetupProfileActivity extends AppCompatActivity {

    EditText UserName;
    EditText About;
    ImageView ProfileImage;
    FirebaseAuth auth;
    Button SaveProfile;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Profile");
        dialog.setCancelable(false);


        UserName = findViewById(R.id.userName);
        About = findViewById(R.id.about);
        ProfileImage = findViewById(R.id.profile_image);
        SaveProfile = findViewById(R.id.profilesave_btn);

        SaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = UserName.getText().toString();

                dialog.show();
                if (name.isEmpty()){
                    UserName.setError("Enter The Name");
                    dialog.dismiss();
                    return;
                }

                if (selectedImage!=null){
                    StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String uid = auth.getUid();
                                        String imageUrl = uri.toString();
                                        String about = About.getText().toString();
                                        String phoneNumber = auth.getCurrentUser().getPhoneNumber();
                                        String userName = UserName.getText().toString();

                                        User user = new User(uid, userName,about,phoneNumber,imageUrl);

                                        database.getReference()
                                                .child("users")
                                                .child(uid)
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }
                else {

                    String uid = auth.getUid();

                    String about = About.getText().toString();
                    String phoneNumber = auth.getCurrentUser().getPhoneNumber();
                    String userName = UserName.getText().toString();

                    User user = new User(uid, userName,about,phoneNumber,"No Image");

                    database.getReference()
                            .child("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                }


            }
        });

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth= FirebaseAuth.getInstance();



        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,45);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null ){
            if(data.getData()!=null){
                ProfileImage.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }
}