package com.artisans.qwikhomeservices.activities.handymanactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.artisans.qwikhomeservices.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AcceptOrRejectActivity extends AppCompatActivity implements View.OnClickListener {
    TextView name, dtate, reason;
    ProgressDialog loading;
    CircleImageView mPhto;
    private Button btnAcpt, btnRejct;
    private String uid, response, getHandyManId, getLocation, getName, getDate, getReason, getPhoto, adapterPosition;
    private Intent intent;
    private String notApproved, accepted, rejected;
    private DatabaseReference requestDbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requestdesign);
        notApproved = "not approved";
        accepted = "Request Accepted";
        rejected = "Request Rejected";

        initViews();
        initListerners();
        retrieveRequestDetails();
    }

    private void retrieveRequestDetails() {

        requestDbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // String name = (String) dataSnapshot.child("fullName").getValue();
                    response = (String) dataSnapshot.child("response").getValue();
//check the status of the request sent
                    assert response != null;
                    if (!response.isEmpty() && response.equals("Request Accepted")) {
                        btnAcpt.setText(response);
                        btnAcpt.setEnabled(false);
                        btnRejct.setVisibility(View.GONE);

                    } else if (!response.isEmpty() && response.equals("Request Rejected")) {
                        btnRejct.setText(response);
                        btnRejct.setEnabled(false);
                        btnAcpt.setVisibility(View.GONE);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                makeToast("Error " + databaseError.getDetails());
                //  Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                //Toast.makeText(ApproveLeaveActivity.this, databaseError.getDetails(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void initListerners() {
        btnRejct.setOnClickListener(this);
        btnAcpt.setOnClickListener(this);
    }

    private void initViews() {

        intent = getIntent();
        name = findViewById(R.id.txtFromUser);
        dtate = findViewById(R.id.txtToBeDonAt);
        reason = findViewById(R.id.txtReasonForRequest);
        loading = new ProgressDialog(this);
        mPhto = findViewById(R.id.mPhto);
        btnAcpt = findViewById(R.id.btnAccept);
        btnRejct = findViewById(R.id.btnReject);

        if (intent != null) {
            adapterPosition = intent.getStringExtra("position");
            getName = intent.getStringExtra("name");
            getDate = intent.getStringExtra("date");
            getReason = intent.getStringExtra("reason");
            getPhoto = intent.getStringExtra("image");
            // getLocation = intent.getStringExtra("location");

            name.setText(getName);
            dtate.setText(getDate);
            reason.setText(getReason);
            Glide.with(this).load(getPhoto).into(mPhto);

            requestDbref =
                    FirebaseDatabase.getInstance().getReference().child("Requests").child(adapterPosition);

            loading.setMessage("Loading please wait");


        }


        //firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        assert mFirebaseUser != null;
        uid = mFirebaseUser.getUid();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAccept:
                btnAcpt.setEnabled(false);

                if (notApproved.equals("not approved")) {

                    runOnUiThread(() -> new AlertDialog.Builder(AcceptOrRejectActivity.this)
                            .setTitle("Accept request")
                            .setMessage("Do you really want to accept REQUEST")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                acceptRequest();
                                loading.show();

                                // ShowLeaveListener.onAcceptPressed();


                            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show());

                }
                break;

            case R.id.btnReject:
                btnRejct.setEnabled(false);
                if (notApproved.equals("not approved")) {
                    runOnUiThread(() -> new AlertDialog.Builder(AcceptOrRejectActivity.this)
                            .setTitle("Reject request")
                            .setMessage("Do you really want to cancel REQUEST")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                rejectRequest();
                                //loading.setVisibility(View.VISIBLE);

                                //ShowLeaveListener.onRejectPressed();


                            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show());

                }
                break;
        }

    }


    //Displays a toast
    void makeToast(String status) {
        Toast toast = Toast.makeText(AcceptOrRejectActivity.this, status,
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    //Method to approve l
    private void acceptRequest() {

//Updating the database
        Map<String, Object> approve = new HashMap<>();
        approve.put("response", accepted);
//node for approved leave
        final DatabaseReference Approved = requestDbref.child("Approved");
        //leave accepted node
        final Map<String, Object> approvedLeave = new HashMap<>();
        approvedLeave.put("name", getName);
        approvedLeave.put("timeStamp", ServerValue.TIMESTAMP);
        //random key for leave accepted
        final String Id = requestDbref.push().getKey();

        requestDbref.updateChildren(approve).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                assert Id != null;
                Approved.child(Id).setValue(approvedLeave).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        makeToast("Request successfully accepted ");
                        loading.dismiss();

                    }
                });

            }

        }).addOnFailureListener(e -> {
            loading.dismiss();
            e.printStackTrace();
        });
    }

    //method to reject
    private void rejectRequest() {


        Map<String, Object> rejectxx = new HashMap<>();
        rejectxx.put("response", rejected);
        //node for rejected leaves
        final DatabaseReference Rejected = requestDbref.child("Request Rejected");

//leave accepted node
        final Map<String, Object> reject = new HashMap<>();
        reject.put("name", getName);
        reject.put("timeStamp", ServerValue.TIMESTAMP);
        final String Id = requestDbref.push().getKey();
        //random key for leave accepted
        requestDbref.updateChildren(rejectxx).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                assert Id != null;
                //create and update the node
                Rejected.child(Id).setValue(reject).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        makeToast("Request successfully rejected ");
                        loading.dismiss();

                    }
                });

            }

        }).addOnFailureListener(e -> {
            loading.dismiss();
            e.printStackTrace();
        });

    }


}
