package com.dalogax.sportevents;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;

public class MyApplication extends Application{

    private static Context mContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        this.mContext = this;

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "tFuu18fkg7rAqGQqN49HNrbOpkyn4J1yL5WfKI8R", "Gjkrleg3ULRsFPnpEShHpf5mjKDK9zJnxFLb8hcD");

    }

    public static Context getContext(){
        return mContext;
    }
}
