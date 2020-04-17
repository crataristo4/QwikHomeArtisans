package com.artisans.qwikhomeservices;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.artisans.qwikhomeservices.utils.AppSignatureHelper;
import com.google.firebase.database.FirebaseDatabase;

public class QwikHomeArtisans extends Application {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            appSignatureHelper.getAppSignatures();
        }
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference().keepSynced(true);


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }
}
