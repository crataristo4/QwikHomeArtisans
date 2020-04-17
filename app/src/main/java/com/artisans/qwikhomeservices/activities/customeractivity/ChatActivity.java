package com.artisans.qwikhomeservices.activities.customeractivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.adapters.ChatAdapter;
import com.artisans.qwikhomeservices.models.Chat;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView handyManPhoto;
    private TextView txtName, txtContent;
    private TextInputLayout edtComment;
    private ChatAdapter adapter;
    private DatabaseReference requestsDbRef, chatsDbRef;
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String uid, getHandyManName, getHandyManPhoto, senderName, senderPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Toolbar toolbar = findViewById(R.id.chatToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.btnReplyBack).setOnClickListener(v -> addChat());

        initViews();
        setUpRecycler();

    }

    private void initViews() {

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        assert mFirebaseUser != null;
        uid = mFirebaseUser.getUid();

        //get data from the view holder
        getHandyManPhoto = getIntent().getStringExtra("photo");//itemImage
        String getAdapterPosition = getIntent().getStringExtra("position");//adapter position of the item
        getHandyManName = getIntent().getStringExtra("name");//name of handyMan
        String getContent = getIntent().getStringExtra("content");//content of the report
        senderName = getIntent().getStringExtra("senderName");//name of sender
        senderPhoto = getIntent().getStringExtra("senderPhoto");//sender photo

        handyManPhoto = findViewById(R.id.imgHandyManPhoto);
        txtName = findViewById(R.id.txtHandyManName);
        txtContent = findViewById(R.id.txtShowReason);
        edtComment = findViewById(R.id.edtChatMsg);

        txtName.setText(getHandyManName);
        txtContent.setText(getContent);
        Glide.with(this).load(getHandyManPhoto).into(handyManPhoto);


        chatsDbRef = FirebaseDatabase.getInstance().getReference().child("Requests").child(getAdapterPosition);


    }

    private void setUpRecycler() {
        final RecyclerView recyclerView = findViewById(R.id.recyclerViewChats);
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
                    ContextCompat.getDrawable(ChatActivity.this, R.drawable.recycler_divider)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseReference postChatsDbRef = chatsDbRef.child("Chats");
        chatsDbRef.keepSynced(true);

        //querying the database base of the time posted
        Query query = postChatsDbRef.orderByChild("timeStamp");

        FirebaseRecyclerOptions<Chat> options = new FirebaseRecyclerOptions.Builder<Chat>().
                setQuery(query, Chat.class).build();

        adapter = new ChatAdapter(options);


        //add decorator
        recyclerView.addItemDecoration(itemDecoration);
        //attach adapter to recycler view
        recyclerView.setAdapter(adapter);
        //notify data change
        adapter.notifyDataSetChanged();

    }

    private void addChat() {

        String postChat = edtComment.getEditText().getText().toString();

        if (!postChat.isEmpty()) {
            HashMap<String, Object> chats = new HashMap<>();
            chats.put("chats", postChat);
            chats.put("userId", uid);
            chats.put("timeStamp", ServerValue.TIMESTAMP);
            chats.put("fullName", senderName);
            chats.put("image", senderPhoto);

            String chatId = chatsDbRef.push().getKey();
            assert chatId != null;

            chatsDbRef.child("Chats").child(chatId).setValue(chats).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    makeToast("Successfully sent");
                    edtComment.getEditText().setText("");
                }

                // setUpRecycler();


            }).addOnFailureListener(e -> {
                makeToast("Error: " + e.getMessage());
                //edtComment.getEditText().setText("");
            });
        } else {
            edtComment.setError("Cannot send empty message");
            //  makeToast("Comment cannot be empty");
        }

    }


    void makeToast(String s) {
        Toast toast = Toast.makeText(ChatActivity.this, s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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
