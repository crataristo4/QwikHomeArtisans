package com.artisans.qwikhomeservices.activities;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.artisans.qwikhomeservices.activities.auth.signup.SignInWithPhoneNumberActivity;

public class ItemViewClickEvents {
    private Context context;

    public ItemViewClickEvents(Context context) {
        this.context = context;
    }

    //simple click
    public void startRegistrationActivity(View view) {
        context.startActivity(new Intent(context, SignInWithPhoneNumberActivity.class));
    }

}
