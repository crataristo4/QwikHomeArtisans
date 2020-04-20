package com.artisans.qwikhomeservices.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.auth.login.SignInWithPhoneNumberActivity;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;

import java.util.Objects;

public class ItemViewClickEvents {
    private Context context;

    public ItemViewClickEvents(Context context) {
        this.context = context;
    }


    public void startRegistrationActivity(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                context.startActivity(new Intent(context, SignInWithPhoneNumberActivity.class));

            } else {
                DisplayViewUI.displayAlertDialogMsg(context, context.getString(R.string.noInternet), "ok",
                        (dialog, which) -> dialog.dismiss());
            }

        }
    }

}
