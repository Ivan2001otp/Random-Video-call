package com.ivan.omegleclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ivan.omegleclone.Activities.ConnectActivity;
import com.ivan.omegleclone.Models.User;
import com.ivan.omegleclone.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mauth;
    private FirebaseDatabase database;
    private DatabaseReference dataRef;
    private long coins = 0;
    private static final int requestCode = 1001;


    User user1;

    String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,Manifest.permission.MODIFY_AUDIO_SETTINGS};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize mobAdds
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });


        database = FirebaseDatabase.getInstance();
        mauth = FirebaseAuth.getInstance();
        FirebaseUser user = mauth.getCurrentUser();
        dataRef = database.getReference("PROFILES");

        binding.progressLoading.setVisibility(View.VISIBLE);
        binding.seeAdsBtn.setOnClickListener(v->{
            Intent rewardIntent = new Intent(MainActivity.this,RewardActivity.class);
            //since we are proceeding to an activity where in which it has third party
            //dependencies , it may hack our intent data.
            startActivity(rewardIntent);
        });


        dataRef.getRef()
                        .orderByChild("status")
                        .equalTo(0)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        long countUsers = snapshot.getChildrenCount();
                                        binding.usersCountTv.setText(Long.toString(countUsers));

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


        database.getReference().child("PROFILES")
                        .child(user.getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //to make changes.
                                        binding.progressLoading.setVisibility(View.GONE);
                                        user1 = snapshot.getValue(User.class);
                                        Glide.with(MainActivity.this)
                                                .load(user.getPhotoUrl())
                                                .into(binding.profileImage);


                                        coins = user1.getCoins();
                                        binding.userCoinsTv.setText("You have : "+coins+" ");


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



        binding.connectBtn.setOnClickListener(v->{



           if(getRuntimeManifestPermissions()) {
                if (coins > 5) {
                    coins = coins-50;
                    database.getReference().child("PROFILES")
                                    .child(mauth.getUid())
                                            .child("coins")
                                                    .setValue(coins);
                    Toast.makeText(MainActivity.this, "Finding your friend...", Toast.LENGTH_SHORT)
                            .show();

                    Intent connectingIntent = new Intent(MainActivity.this, ConnectActivity.class);
                    connectingIntent.putExtra("profileImg", user1.getProfileImageUrl());
                    Log.e("tag", "onCreate: Intent exe");
                    startActivity(connectingIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Insufficient Coins .Please watch ads to boost your coins.", Toast.LENGTH_SHORT)
                            .show();
                }
            }else{
                    askPermissions();
            }

        });

    }


    private boolean getRuntimeManifestPermissions(){
        boolean allPermissionGranted = true;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            for(String permission : permissions){
                if(ActivityCompat.checkSelfPermission(this,permission)!=PackageManager.PERMISSION_GRANTED){
                    allPermissionGranted = false;
                    break;
                }
            }
        }
        return allPermissionGranted;
    }

     void askPermissions(){
        ActivityCompat.requestPermissions(this,permissions,requestCode);
        Log.e("tag", "onCreate: perm stats2");
        return;
    }

     boolean isPermissionGranted(){
        for(String permission : permissions){
            if(ActivityCompat.checkSelfPermission(this,permission)!=PackageManager.GET_PERMISSIONS){
                return false;
            }
        }
        Log.e("tag", "onCreate: perm stats");

        return true;
    }
}