package com.ivan.omegleclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ivan.omegleclone.databinding.ActivityRewardBinding;

public class RewardActivity extends AppCompatActivity {
    private ActivityRewardBinding binding;
    private RewardedAd rewardedAd;
    FirebaseAuth mauth;
    FirebaseDatabase database;
    private int coins=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRewardBinding.inflate(getLayoutInflater());
        mauth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        String userId = mauth.getUid();

        setContentView(binding.getRoot());

        //loading the current coins before playing ads
        database.getReference().child("PROFILES")
                        .child(userId)
                                .child("coins")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                coins = snapshot.getValue(Integer.class);
                                                binding.userCurrCoinsTv.setText(String.valueOf(coins));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

        loadAd();



        binding.ll1.setOnClickListener(v->{
            if (rewardedAd != null) {
                Activity activityContext = RewardActivity.this;
                rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        coins+=50;
                        database.getReference().child("PROFILES")
                                .child(userId)
                                .child("coins")
                                .setValue(coins);
                        //change the right drawable
                        binding.ad1Img.setImageResource(R.drawable.green_tick);
                    }
                });
            } else {
                Toast.makeText(RewardActivity.this,"Check your Internet connection.",Toast.LENGTH_SHORT)
                        .show();
            }
        });

        binding.ll2.setOnClickListener(v->{
            if (rewardedAd != null) {
                Activity activityContext = RewardActivity.this;
                rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        coins+=100;
                        database.getReference().child("PROFILES")
                                .child(userId)
                                .child("coins")
                                .setValue(coins);
                        //change the right drawable
                        binding.ad2.setImageResource(R.drawable.green_tick);
                    }
                });
            } else {
                Toast.makeText(RewardActivity.this,"Check your Internet connection.",Toast.LENGTH_SHORT)
                        .show();
            }
        });

        binding.ll3.setOnClickListener(v->{
            if (rewardedAd != null) {
                Activity activityContext = RewardActivity.this;
                rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        coins+=150;
                        database.getReference().child("PROFILES")
                                .child(userId)
                                .child("coins")
                                .setValue(coins);
                        //change the right drawable
                        binding.ad3.setImageResource(R.drawable.green_tick);
                    }
                });
            } else {
                Toast.makeText(RewardActivity.this,"Check your Internet connection.",Toast.LENGTH_SHORT)
                        .show();
            }
        });

        binding.ll4.setOnClickListener(v->{
            if (rewardedAd != null) {
                Activity activityContext = RewardActivity.this;
                rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        coins+=200;
                        database.getReference().child("PROFILES")
                                .child(userId)
                                .child("coins")
                                .setValue(coins);
                        //change the right drawable
                        binding.ad4.setImageResource(R.drawable.green_tick);
                    }
                });
            } else {
                Toast.makeText(RewardActivity.this,"Check your Internet connection.",Toast.LENGTH_SHORT)
                        .show();
            }
        });




    }

    private void loadAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rewardedAd=null;
    }
}