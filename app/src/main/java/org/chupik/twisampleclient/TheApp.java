package org.chupik.twisampleclient;

import android.app.Application;

import com.twitter.sdk.android.core.Twitter;


public class TheApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Twitter.initialize(this);
    }
}
