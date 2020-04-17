package com.artisans.qwikhomeservices.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.artisans.qwikhomeservices.R;

import java.util.Objects;
import java.util.Random;

public class DisplayViewUI {


    private static ColorDrawable[] vibrantLightColorList =
            {
                    new ColorDrawable(Color.parseColor("#ffeead")),
                    new ColorDrawable(Color.parseColor("#93cfb3")),
                    new ColorDrawable(Color.parseColor("#fd7a7a")),
                    new ColorDrawable(Color.parseColor("#faca5f")),
                    new ColorDrawable(Color.parseColor("#1ba798")),
                    new ColorDrawable(Color.parseColor("#6aa9ae")),
                    new ColorDrawable(Color.parseColor("#ffbf27")),
                    new ColorDrawable(Color.parseColor("#d93947"))
            };

    public static ColorDrawable getRandomDrawableColor() {
        int idx = new Random().nextInt(vibrantLightColorList.length);
        return vibrantLightColorList[idx];
    }


    public static void displayToast(Context ctx, String s) {
        Toast toast = Toast.makeText(ctx, s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static ProgressDialog displayProgress(Context ctx, String message) {
        ProgressDialog loading = new ProgressDialog(ctx);
        loading.setCancelable(false);
        loading.setMessage(message);
        // loading.show();

        return loading;
    }

    static public void displayAlertDialog(Context context, String title, String msg, String btnPos, String btnNeg, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        if (btnPos != null) builder.setPositiveButton(btnPos, onClickListener);
        if (btnNeg != null) builder.setNegativeButton(btnNeg, onClickListener);
        builder.setIcon(context.getResources().getDrawable(R.drawable.applogo));
        builder.show();
    }

    static public void displayAlertDialog(Context context, String title, String msg, String btnPos, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        if (btnPos != null) builder.setPositiveButton(btnPos, onClickListener);
        builder.setIcon(R.drawable.ic_gps);
        builder.show();
    }

    static public void displayAlertDialogMsg(Context context, String msg, String btnPos, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(false);
        if (btnPos != null) builder.setPositiveButton(btnPos, onClickListener);
        builder.setIcon(context.getResources().getDrawable(R.drawable.sorry));
        builder.show();
    }

    static public boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(context).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        assert networkInfo != null;
        return networkInfo.isConnectedOrConnecting();

    }
}
