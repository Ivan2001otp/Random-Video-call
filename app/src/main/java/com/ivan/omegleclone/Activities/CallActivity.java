package com.ivan.omegleclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ivan.omegleclone.Models.InterfaceJava;
import com.ivan.omegleclone.Models.User;
import com.ivan.omegleclone.R;
import com.ivan.omegleclone.databinding.ActivityCallBinding;

import java.util.Objects;
import java.util.UUID;

public class CallActivity extends AppCompatActivity {
private ActivityCallBinding binding;
private WebView webViewBg;
private String uniqueId="";
private DatabaseReference firebaseRef;
private String username="";
private String friendsName="";
private String createdBy;
private FirebaseAuth mauth;

private boolean isAudio=false;
private boolean isVideo=false;
private boolean isConnected = false;
boolean pageExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        webViewBg = (WebView) findViewById(R.id.webViewBg);
        firebaseRef = FirebaseDatabase.getInstance().getReference().child("USERS");
        mauth = FirebaseAuth.getInstance();

        username = getIntent().getStringExtra("username");
        String incoming = getIntent().getStringExtra("incoming");
        createdBy = getIntent().getStringExtra("createdBy");
        friendsName = incoming;

        //config for audio btn
        binding.micBtn.setOnClickListener(v->{
            isAudio = !isAudio;

            //activate the audio ot speak;
            String path = ("javascript:toggleAudio(\""+isAudio+"\")");
            callJavascriptRoutine(path,webViewBg);

            if(isAudio){
                binding.micBtn.setImageResource(R.drawable.btn_unmute_normal);
            }else{
                binding.micBtn.setImageResource(R.drawable.btn_mute_normal);
            }

        });

        //config for video btn
        binding.videoBtn.setOnClickListener(v->{
            isVideo = !isVideo;
            String path = ("javascript:toggleAudio(\""+isVideo+"\")");

            if(isVideo){
                binding.videoBtn.setImageResource(R.drawable.btn_video_normal);
            }else{
                binding.videoBtn.setImageResource(R.drawable.btn_video_muted);
            }
        });

        //config for end call
         binding.callDropBtn.setOnClickListener(v->{
             finish();
         });


        webViewBg.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    request.grant(request.getResources());
                }
            }
        });

        webViewBg.getSettings().setJavaScriptEnabled(true);
        webViewBg.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webViewBg.addJavascriptInterface(new InterfaceJava(CallActivity.this),"Android");

        loadVideoCall(webViewBg);
    }

    private void loadVideoCall(WebView webViewBg){

        String path = "file:android_asset/call.html";
        webViewBg.loadUrl(path);

        webViewBg.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //initialize the peer;
                InitializePeer(webViewBg);
            }
        });
    }

    private void callJavascriptRoutine(String function,WebView webViewBg){
        webViewBg.post(new Runnable() {
            @Override
            public void run() {
                webViewBg.evaluateJavascript(function,null);
            }
        });
    }

    private String getUid(){
        return UUID.randomUUID().toString();
    }

    private void InitializePeer(WebView webViewBg){
        uniqueId = getUid();
        callJavascriptRoutine("javascript:init(\""+uniqueId+"\")",webViewBg);
        if(createdBy.equalsIgnoreCase(username)){
            if(pageExit)return;
            //add the connection Id into the one who created the room.
            firebaseRef.child(username).child("connId").setValue(uniqueId);
            firebaseRef.child(username).child("isAvailable").setValue(true);

            binding.loading.setVisibility(View.GONE);
            binding.controls.setVisibility(View.VISIBLE);


            //setting the profile photo of friend
            FirebaseDatabase.getInstance().getReference()
                    .child("PROFILES")
                    .child(friendsName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User friendUser = snapshot.getValue(User.class);

                            if(getLifeCycleState()!=6){
                                Glide.with(CallActivity.this).load(Objects.requireNonNull(friendUser).getProfileImageUrl())
                                        .into(binding.profileImage);

                                binding.userFirstNametv.setText(friendUser.getName());
                                binding.place.setText(friendUser.getPlace());
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        }else{
           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {

                   friendsName = createdBy;
                   FirebaseDatabase.getInstance().getReference()
                                   .child("PROFILES")
                                   .child(friendsName)
                                   .addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                                           User user = snapshot.getValue(User.class);
                                           //upload photo of other user
                                           if(getLifeCycleState()!=6){
                                               Glide.with(CallActivity.this).load(Objects.requireNonNull(user).getProfileImageUrl())
                                                       .into(binding.profileImage);

                                               binding.userFirstNametv.setText(user.getName());
                                               binding.place.setText(user.getPlace());
                                           }

                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError error) {

                                       }
                                   });



                    FirebaseDatabase.getInstance().getReference()
                            .child("USERS")
                            .child(friendsName)
                            .child("connId")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.getValue()!=null){
                                        sendCallRequest(webViewBg);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
               }
           },3000);
        }

    }

    private int getLifeCycleState() {
        return this.getLifecycle().getCurrentState().ordinal();
    }

    public  void onPeerConnected(){
        isConnected=true;
    }

    private void sendCallRequest(WebView webViewBg){
        if(!isConnected){
            Toast.makeText(CallActivity.this,"You are not Connected.Please check Your internet!",Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        listenConnectionId(webViewBg);
    }

    private void listenConnectionId(WebView webViewBg) {
        firebaseRef.child(friendsName).child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    return;
                }

                binding.loading.setVisibility(View.GONE);
                binding.controls.setVisibility(View.VISIBLE);
                String connId = snapshot.getValue(String.class);
                String cmd="javascript:startCall(\""+connId+"\")";
                callJavascriptRoutine(cmd,webViewBg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageExit = true;
        firebaseRef.child(createdBy).setValue(null);
        finish();
    }
}