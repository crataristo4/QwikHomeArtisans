package com.artisans.qwikhomeservices.activities.handymanactivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.adapters.CustomerRequestSent;
import com.artisans.qwikhomeservices.interfaces.RecyclerItemTouchHelperDeleteRequest;
import com.artisans.qwikhomeservices.models.ServicePerson;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestHandyManActivity extends AppCompatActivity implements View.OnClickListener, RecyclerItemTouchHelperDeleteRequest.RecyclerItemTouchHelperDeleteListenerRequest {
    private static final String TAG = "RequestHandyManActivity";
    private final Calendar calendar = Calendar.getInstance();
    ProgressDialog loading;
    DatabaseReference request;
    SimpleDateFormat sfd;
    CustomerRequestSent adapter;
    RecyclerView recyclerView;
    private TextView txtName, txtOccupation, edtAbt;
    private CircleImageView mPhoto;
    private EditText edtReason, txtDate;
    private Button btnRequest, btnStartDate;
    private String uid, getHandyManId, getLocation, getName, getAbt, getOccupation, getPhoto, adapterPosition;
    private Intent intent;
    private String dayOfTheWeek, startDateSelected, ownName, ownerPhoto;
    private DatePickerDialog datePicker;
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);
    private String getStartDate;
    private Date date;
    private DatabaseReference UserRef, requestDbRef;
    private String notApproved = "Not yet Approved";
    private DatabaseReference mRequests;
    private String datePosted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_handy_man);

        date = calendar.getTime();
        sfd = new SimpleDateFormat("EEE dd-MMMM-yyyy ",
                Locale.US);
        mRequests = FirebaseDatabase.getInstance().getReference().child("Requests");
        mRequests.keepSynced(true);


        initViews();
        initListener();
        setUpRecycler();
    }

    private void setUpRecycler() {
        recyclerView = findViewById(R.id.recyclerRequestResponse);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        //now set the drawable of the item decorator
        try {
            itemDecoration.setDrawable(
                    ContextCompat.getDrawable(RequestHandyManActivity.this, R.drawable.recycler_divider)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }


        Query query = mRequests.orderByChild("senderId").equalTo(uid);


        FirebaseRecyclerOptions<ServicePerson> options = new FirebaseRecyclerOptions.Builder<ServicePerson>().
                setQuery(query, ServicePerson.class).build();

        adapter = new CustomerRequestSent(options);


        //add decorator
        recyclerView.addItemDecoration(itemDecoration);
        //attach adapter to recycler view
        recyclerView.setAdapter(adapter);
        //notify data change
        adapter.notifyDataSetChanged();

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperDeleteRequest(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        //swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                    }


                };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView);


    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        deleteItem(viewHolder.getAdapterPosition());
    }

    //Method to delete value in recycler
    private void deleteItem(int adapterPosition) {
        adapter.getSnapshots().getSnapshot(adapterPosition).getRef().removeValue();


    }


    private void initListener() {
        btnRequest.setOnClickListener(this);
        btnStartDate.setOnClickListener(this);
    }

    private void initViews() {
        intent = getIntent();
        txtName = findViewById(R.id.txtHandyManName);
        txtOccupation = findViewById(R.id.handyManOccupation);
        mPhoto = findViewById(R.id.servicePersonPhoto);
        loading = new ProgressDialog(this);
        edtAbt = findViewById(R.id.edtAbt);
        txtDate = findViewById(R.id.edtDate);
        edtReason = findViewById(R.id.edtReason);
        btnRequest = findViewById(R.id.btnRequest);
        btnStartDate = findViewById(R.id.btnStartDate);

        if (intent != null) {
            adapterPosition = intent.getStringExtra("position");
            getName = intent.getStringExtra("name");
            getAbt = intent.getStringExtra("details");
            getOccupation = intent.getStringExtra("occupation");
            getPhoto = intent.getStringExtra("image");
            getLocation = intent.getStringExtra("location");
            getHandyManId = intent.getStringExtra("handyManId");

            txtName.setText(getName);
            txtOccupation.setText(getOccupation);
            edtAbt.setText(getAbt);
            Glide.with(this).load(getPhoto).into(mPhoto);

//            dayOfTheWeek = new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis());
//            //request
//            request = FirebaseDatabase.getInstance().getReference("Request").child(dayOfTheWeek).child(adapterPosition);
        }


        //firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        assert mFirebaseUser != null;
        uid = mFirebaseUser.getUid();
        requestDbRef = FirebaseDatabase.getInstance().getReference("Requests");
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        UserRef.keepSynced(true);
        requestDbRef.keepSynced(true);


        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        //retrieve the details and set the on the users profile
                        ownName = (String) dataSnapshot.child("fullName").getValue();
                        ownerPhoto = (String) dataSnapshot.child("image").getValue();


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                try {
                    Log.d(TAG, "Error : " + databaseError.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartDate:
                callStartDate();
                break;

            case R.id.btnRequest:

                if (!btnRequest.isEnabled()) {
                    makeToast("Cannot request please select another date");
                } else {
                    sendRequestToHandyMan();
                }

                break;
        }

    }


    //Method to check the start date selected by the user and display the valid date into the view
    private void callStartDate() {
        datePicker = new DatePickerDialog(RequestHandyManActivity.this, (view, year, month, dayOfMonth) -> {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                startDateSelected = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

                Log.i(TAG, "onDateSet: " + startDateSelected);

                if (date.before(new Date(startDateSelected))) {
                    checkSuccessSelectStartDate();


                } else if (date.after(new Date(startDateSelected))) {
                    displayErrorOnStartDateSelected();

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }, year, month, day);

        datePicker.show();
    }


    //checks corresponding date from the user and allows them to proceed
    void checkSuccessSelectStartDate() {
        txtDate.setText(sfd.format(new Date(startDateSelected)));
        txtDate.setTextColor(getResources().getColor(R.color.colorGreen));
        btnRequest.setEnabled(true);
    }

    //if date selected is before the current date ... display error
    void displayErrorOnStartDateSelected() {
        makeToast("Please select a day after today");
        txtDate.setText("");
        btnRequest.setEnabled(false);

    }

    public void makeToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    void clear() {
        txtDate.setText("");
        edtReason.setText("");
    }


    private void sendRequestToHandyMan() {
        final String getReason = edtReason.getText().toString();
        sfd = new SimpleDateFormat("EEE dd-MM-yyyy '@' hh:mm aa",
                Locale.US);
        try {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            datePosted = sfd.format(new Date(today.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!edtReason.getText().toString().isEmpty() && !txtDate.getText().toString().isEmpty()) {
            btnRequest.setEnabled(true);
            loading.setMessage("Making the request please wait");
            loading.show();

            runOnUiThread(() -> {


                Map<String, Object> requestSent = new HashMap<>();
                requestSent.put("senderUserId", uid);
                requestSent.put("senderName", ownName);
                requestSent.put("senderPhoto", ownerPhoto);

                requestSent.put("handyManId", getHandyManId);
                requestSent.put("servicePersonName", getName);
                requestSent.put("servicePersonPhoto", getPhoto);

                requestSent.put("date", datePosted);
                requestSent.put("reason", getReason);
                requestSent.put("response", notApproved);
                requestSent.put("rating", 0);


                String requestId = requestDbRef.push().getKey();
                assert requestId != null;

                requestDbRef.child(requestId).setValue(requestSent).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        clear();
                        loading.dismiss();
                        makeToast("Request has been sent");
                    } else {
                        loading.dismiss();
                        makeToast("Request Failed try again");
                    }

                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                    makeToast("Request Failed try again");

                });
            });


        } else if (edtReason.getText().toString().isEmpty()) {
            makeToast("Please state your reason");
            btnRequest.setEnabled(false);
        } else if (txtDate.getText().toString().isEmpty()) {
            makeToast("Please select your start date");
            btnRequest.setEnabled(false);
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
