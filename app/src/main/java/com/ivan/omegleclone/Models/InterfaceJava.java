package com.ivan.omegleclone.Models;

import android.telecom.Call;
import android.webkit.JavascriptInterface;

import com.ivan.omegleclone.Activities.CallActivity;

public class  InterfaceJava {
    final CallActivity callActivity;

    public InterfaceJava(CallActivity callActivity){
        this.callActivity = callActivity;
    }


    //exposes the android native code to the javascript.
    @JavascriptInterface
    public void PeerConnected(){
        callActivity.onPeerConnected();
    }
}
