package com.example.whatsapp;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MessageHolder> {
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    int val;
    Context context;
    FirebaseUser firebaseUser;
    View view1,view2;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    private List<Chats> chatsList;

    public ChatsAdapter(Context context,List<Chats> chatsList)
    {
        this.context = context;
        this.chatsList = chatsList;
    }


    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_message_layout,parent,false);
        //mAuth = FirebaseAuth.getInstance();
        val = viewType;
        if(viewType == MSG_TYPE_RIGHT)
        {
            view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right,parent,false);
            return new MessageHolder(view1);
        }
        else
        {
            view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left,parent,false);
            return new MessageHolder(view2);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageHolder holder, int position)
    {
        if(val == 0)
        {
            final Chats sms = chatsList.get(position);
            holder.show_message_text.setText(sms.getMessage());
            reference = FirebaseDatabase.getInstance().getReference().child("users").child(sms.getIdsender());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.hasChild("profile"))
                    {
                        String imageprofile = dataSnapshot.child("profile").getValue().toString();
                        if(holder.profile != null)
                        {
                            Picasso.get().load(imageprofile).into(holder.profile);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if(val == 1)
        {
            final Chats sms = chatsList.get(position);
            holder.show_message_text.setText(sms.getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatsList.get(position).getIdsender().equals(firebaseUser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount()
    {
        return chatsList.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder{

        public TextView show_message_text;
        /*public TextView sender_text,receiver_text;*/
        public CircleImageView profile;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            show_message_text = itemView.findViewById(R.id.show_message_text);
            profile = itemView.findViewById(R.id.receiver_profile);
            /*sender_text = itemView.findViewById(R.id.text_sender);
            receiver_text = itemView.findViewById(R.id.text_receiver);
            mon_profile = itemView.findViewById(R.id.mon_profile);*/
        }
    }
}
