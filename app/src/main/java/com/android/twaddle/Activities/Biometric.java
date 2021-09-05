package com.android.twaddle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.twaddle.R;

import java.util.concurrent.Executor;

public class Biometric extends AppCompatActivity {

    //UI views


    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private int res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric);


        //initialize the  values
        executor= ContextCompat.getMainExecutor(this);
        biometricPrompt=new BiometricPrompt(Biometric.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                //if any error come while authentication
                Toast.makeText(Biometric.this, "Error", Toast.LENGTH_SHORT).show();
                res=0;
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //Authentication Succeded
                Toast.makeText(Biometric.this, "Authentication Success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Biometric.this,ChatActivity.class));
                res=1;
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //failed to auth
                Toast.makeText(Biometric.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                res=0;
            }
        });


        //setup title, description on authentication dialog
        promptInfo=new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Login using Fingerprint or FaceId ")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);

    }
}
