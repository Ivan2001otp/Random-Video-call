package com.ivan.omegleclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ivan.omegleclone.R;
import com.ivan.omegleclone.databinding.ActivityConnectBinding;

import java.util.HashMap;
import java.util.Objects;

public class ConnectActivity extends AppCompatActivity {
    private ActivityConnectBinding binding;
    private FirebaseAuth mauth;
    private FirebaseDatabase database;
    boolean isOkay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConnectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mauth = FirebaseAuth.getInstance();
        FirebaseUser user = mauth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        String username = mauth.getUid();

        Intent receiveIntent = getIntent();
        String profileImgUrl = receiveIntent.getStringExtra("profileImg");

        Glide.with(ConnectActivity.this)
                .load(profileImgUrl)
                .into(binding.connectProfileImg);


        //logic of creating room
        database.getReference()
                .child("USERS")
                .orderByChild("status")
                .equalTo(0).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount()>0){
                            //room is available
                            isOkay=true;
                            for(DataSnapshot childSnap : snapshot.getChildren()){
                                database.getReference().child("USERS")
                                        .child(childSnap.getKey())
                                        .child("incoming")
                                        .setValue(username);

                                database.getReference().child("USERS")
                                        .child(childSnap.getKey())
                                        .child("status")
                                        .setValue(1);

                                boolean isAvail = childSnap.child("isAvailable").getValue(Boolean.class);
                                String incoming = childSnap.child("incoming").getValue(String.class);
                                String createdBy = childSnap.child("createdBy").getValue(String.class);

                                Intent callIntent = new Intent(ConnectActivity.this,CallActivity.class);
                                callIntent.putExtra("incoming",incoming);
                                callIntent.putExtra("createdBy",createdBy);
                                callIntent.putExtra("isAvailable",isAvail);
                                //status will be zero as no second person will be available during room creation.
                                startActivity(callIntent);
                                finish();

                            }
                        }else{
                            //create the room as its not available
                            HashMap<String,Object>room = new HashMap<>();
                            room.put("incoming",username);
                            room.put("createdBy",username);
                            room.put("isAvailable",true);
                            room.put("status",0);

                            //now we have to listen to this room ,to track anybody to get in.
                            database.getReference().child("USERS")
                                    .child(Objects.requireNonNull(username))
                                    .setValue(room)
                                    .addOnSuccessListener(ConnectActivity.this, new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            database.getReference().child("USERS")
                                                    .child(Objects.requireNonNull(username))
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if(snapshot.child("status").exists()){
                                                                if(snapshot.child("status").getValue(Integer.class)==1){

                                                                    if(isOkay)return;

                                                                    isOkay=true;
                                                                    boolean isAvail = snapshot.child("isAvailable").getValue(Boolean.class);

                                                                    Intent callIntent = new Intent(ConnectActivity.this,CallActivity.class);
                                                                    callIntent.putExtra("incoming",username);
                                                                    callIntent.putExtra("createdBy",username);
                                                                    callIntent.putExtra("isAvailable",isAvail);
                                                                    //status will be zero as no second person will be available during room creation.
                                                                    startActivity(callIntent);
                                                                    finish();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}