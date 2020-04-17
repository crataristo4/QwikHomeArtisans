package com.artisans.qwikhomeservices.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.models.Chat;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends FirebaseRecyclerAdapter<Chat, ChatAdapter.ShowChatHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChatAdapter(@NonNull FirebaseRecyclerOptions<Chat> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ShowChatHolder holder, int position, @NonNull Chat model) {
        holder.showUserPhoto(model.getImage());
        holder.showUserName(model.getFullName());
        holder.showDate(model.getTimeStamp());
        holder.showComment(model.getChats());
    }

    @NonNull
    @Override
    public ShowChatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ShowChatHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_chat_list_items, viewGroup, false));
    }

    public class ShowChatHolder extends RecyclerView.ViewHolder {
        View view;

        ShowChatHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;


        }

        //display the user name of the person who posted
        void showUserName(String name) {
            TextView nameOfUser = view.findViewById(R.id.comment_username);
            nameOfUser.setText(name);
        }

        //display the content
        void showComment(String description) {
            TextView crimeDescription = view.findViewById(R.id.comment_message);
            crimeDescription.setText(description);
        }


        //display the users photo
        void showUserPhoto(String urlOfUserPhoto) {
            CircleImageView userProfilePhoto = view.findViewById(R.id.comment_image);
            Glide.with(view).load(urlOfUserPhoto).into(userProfilePhoto);
        }


        void showDate(Long date) {

            TextView time = view.findViewById(R.id.commentDate);
            SimpleDateFormat sfd = new SimpleDateFormat("EEE dd-MMMM-yyyy '@' hh:mm aa ",
                    Locale.US);

            try {
                time.setText(sfd.format((new Date(date))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
