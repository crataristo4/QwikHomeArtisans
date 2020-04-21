package com.artisans.qwikhomeservices.activities.home.bottomsheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.databinding.LayoutAcceptOrRejectBottomSheetBinding;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AcceptOrRejectBtSheet extends BottomSheetDialogFragment {
    private static final String TAG = "AcceptOrRejectBtSheet";
    private LayoutAcceptOrRejectBottomSheetBinding layoutAcceptOrRejectBottomSheetBinding;
    private String notApproved, accepted, rejected;
    private DatabaseReference requestDbref;
    private String uid, response, getItemPrice, getUserName, getItemName, getReason, getUserPhoto, getItemPhoto, adapterPosition;
    private Button btnAccept, btnReject;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        layoutAcceptOrRejectBottomSheetBinding = DataBindingUtil.inflate(inflater, R.layout.layout_accept_or_reject_bottom_sheet, container, false);

        return layoutAcceptOrRejectBottomSheetBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notApproved = "not approved";
        accepted = "Request Accepted";
        rejected = "Request Rejected";


        Bundle getData = getArguments();
        if (getData != null) {

            getUserName = getData.getString(MyConstants.FULL_NAME);
            getUserPhoto = getData.getString(MyConstants.USER_IMAGE_URL);
            getItemPhoto = getData.getString(MyConstants.ITEM_PHOTO);
            getItemName = getData.getString(MyConstants.ITEM_NAME);
            getItemPrice = getData.getString(MyConstants.ITEM_PRICE);
            getReason = getData.getString(MyConstants.ITEM_REASON);
            adapterPosition = getData.getString(MyConstants.ADAPTER_POSITION);

            layoutAcceptOrRejectBottomSheetBinding.txtUserName.setText(getUserName);
            if (getUserPhoto == null) {
                Glide.with(Objects.requireNonNull(getActivity())).load(getActivity().getResources().getDrawable(R.drawable.photoe))
                        .into(layoutAcceptOrRejectBottomSheetBinding.imgUserPhoto);
            } else {
                Glide.with(Objects.requireNonNull(getActivity())).load(getItemPhoto)
                        .into(layoutAcceptOrRejectBottomSheetBinding.imgUserPhoto);
            }
            Glide.with(Objects.requireNonNull(getActivity())).load(getItemPhoto)
                    .into(layoutAcceptOrRejectBottomSheetBinding.imgItemImage);


        }

        btnAccept = layoutAcceptOrRejectBottomSheetBinding.btnAccept;
        btnReject = layoutAcceptOrRejectBottomSheetBinding.btnDecline;

        requestDbref =
                FirebaseDatabase.getInstance().getReference().child("Requests").child(adapterPosition);


        retrieveRequestDetails();
        btnAccept.setOnClickListener(this::processRequest);
        btnReject.setOnClickListener(this::processRequest);


    }

    private void processRequest(View view) {

        if (view.getId() == R.id.btnAccept) {
            btnAccept.setEnabled(false);

            if (notApproved.equals("not approved")) {

                Objects.requireNonNull(getActivity()).runOnUiThread(() -> new AlertDialog.Builder(getActivity())
                        .setTitle("Accept request")
                        .setMessage("Do you really want to accept REQUEST")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            dialog.dismiss();
                            acceptRequest();
                            // loading.show();

                            // ShowLeaveListener.onAcceptPressed();


                        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show());

            }
            if (view.getId() == R.id.btnReject) {
                btnReject.setEnabled(false);
                if (notApproved.equals("not approved")) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> new AlertDialog.Builder(getActivity())
                            .setTitle("Reject request")
                            .setMessage("Do you really want to cancel REQUEST")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                rejectRequest();
                                //loading.setVisibility(View.VISIBLE);

                                //ShowLeaveListener.onRejectPressed();


                            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show());

                }

            }

        }

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
        approvedLeave.put("name", getUserName);
        approvedLeave.put("timeStamp", ServerValue.TIMESTAMP);
        //random key for leave accepted
        final String Id = requestDbref.push().getKey();

        requestDbref.updateChildren(approve).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                assert Id != null;
                Approved.child(Id).setValue(approvedLeave).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DisplayViewUI.displayToast(getActivity(), "Request successfully accepted ");
                        //loading.dismiss();

                    }
                });

            }

        }).addOnFailureListener(e -> {
            // loading.dismiss();
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
        reject.put("name", getUserName);
        reject.put("timeStamp", ServerValue.TIMESTAMP);
        final String Id = requestDbref.push().getKey();
        //random key for leave accepted
        requestDbref.updateChildren(rejectxx).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                assert Id != null;
                //create and update the node
                Rejected.child(Id).setValue(reject).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DisplayViewUI.displayToast(getActivity(), "Request successfully rejected ");
                        //loading.dismiss();

                    }
                });

            }

        }).addOnFailureListener(e -> {
            // loading.dismiss();
            e.printStackTrace();
        });

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
                        btnAccept.setText(response);
                        btnAccept.setEnabled(false);
                        btnReject.setVisibility(View.GONE);

                    } else if (!response.isEmpty() && response.equals("Request Rejected")) {
                        btnReject.setText(response);
                        btnReject.setEnabled(false);
                        btnAccept.setVisibility(View.GONE);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                DisplayViewUI.displayToast(getActivity(), "Error " + databaseError.getDetails());
                //  Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                //Toast.makeText(ApproveLeaveActivity.this, databaseError.getDetails(), Toast.LENGTH_LONG).show();
            }
        });


    }


}
