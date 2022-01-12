package com.example.whatsapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class MessageGroupAdapter extends RecyclerView.Adapter<MessageGroupAdapter.MessageHolder> {
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    DatabaseReference groupref,reference;
    private List<MessageGroup> messageGrouplist;

    public MessageGroupAdapter(List<MessageGroup> messageGrouplist)
    {
        this.messageGrouplist = messageGrouplist;
    }


    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_message_layout,parent,false);
        //mAuth = FirebaseAuth.getInstance();

        if(viewType == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right,parent,false);
            return new MessageHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left,parent,false);
            return new MessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageHolder holder, int position)
    {
        MessageGroup sms = messageGrouplist.get(position);
        if(sms.getCurrentusername()!=null)
            holder.show_message_text.setText(sms.getCurrentusername()+"\n\n"+sms.getMessage());

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(sms.getCurrentid());
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
       /* String currentuserID = mAuth.getCurrentUser().getUid();
        MessageGroup sms = messageGrouplist.get(position);
        String username = sms.getCurrentusername();
        String userid = sms.getCurrentid();

        holder.receiver_text.setVisibility(View.INVISIBLE);
        holder.mon_profile.setVisibility(View.INVISIBLE);
        if(userid.equals(currentuserID))
        {
            holder.sender_text.setBackgroundResource(R.drawable.sender_message_layout);
            holder.sender_text.setText(username+"\n"+sms.getMessage());
        }
        else
        {
            holder.sender_text.setVisibility(View.INVISIBLE);
            holder.receiver_text.setVisibility(View.VISIBLE);
            holder.mon_profile.setVisibility(View.VISIBLE);
            holder.receiver_text.setBackgroundResource(R.drawable.receiver_message_layout);
            holder.receiver_text.setText(sms.getCurrentusername()+"\n"+sms.getMessage());
        }*/

        //holder.show_message_text.setText(sms.getCurrentusername()+"\n"+sms.getMessage()+"\n"+sms.getCurrentdate()+" "+sms.getCurrenttime());
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(messageGrouplist.get(position).getCurrentid().equals(firebaseUser.getUid()))
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
        return messageGrouplist.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder{

        public TextView show_message_text;
        public CircleImageView profile;
        /*public TextView sender_text,receiver_text;
        public CircleImageView mon_profile;*/

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
