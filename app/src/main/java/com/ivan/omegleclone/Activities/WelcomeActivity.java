package com.ivan.omegleclone.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.ivan.omegleclone.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {
private ActivityWelcomeBinding binding;
private FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        mauth= FirebaseAuth.getInstance();

        if(mauth.getCurrentUser()!=null){
            changeActivity();
        }


        binding.getStartedBtn.setOnClickListener(v->{
          changeActivity();
        });


    }

    private void changeActivity(){
        Intent loginActivity = new Intent(WelcomeActivity.this,LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }
}