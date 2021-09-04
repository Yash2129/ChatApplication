package com.android.twaddle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.twaddle.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final ProgressBar progressBar = findViewById(R.id.progress_bar);
        final EditText inputMobile = findViewById(R.id.inputmobile);
        Button buttonGetOtp = findViewById(R.id.submit_btn);

        buttonGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputMobile.getText().toString().trim().isEmpty()){
                    Toast.makeText(Login.this,"Enter Mobile Number",Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                buttonGetOtp.setVisibility(View.INVISIBLE);

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = null;
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(inputMobile.getText().toString())       // Phone number to verify
                                .setTimeout(60L , TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(Login.this)                 // Activity (for callback binding)
                                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


                                        progressBar.setVisibility(View.GONE);
                                        buttonGetOtp.setVisibility(View.VISIBLE);

                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {

                                        progressBar.setVisibility(View.GONE);
                                        buttonGetOtp.setVisibility(View.VISIBLE);
                                        Toast.makeText(Login.this,"Retry",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String verficationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        Intent intent = new Intent(getApplicationContext(),VerifyOTP.class);
                                        intent.putExtra("mobile",inputMobile.getText().toString());
                                        intent.putExtra("verificationId",verficationId);
                                        startActivity(intent);
                                        finish();
                                    }
                                })          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);

            }
        });
    }
}