package com.example.mymessage.fragments.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymessage.R;
import com.example.mymessage.User;
import com.example.mymessage.chat.ChatObject;
import com.example.mymessage.fragments.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private List<ChatObject> mChat;
    private  String imageURL;
    public  static  final int MSG_TYPE_LEFT=0;
    public  static  final int MSG_TYPE_RIGHT=1;
    FirebaseUser fuser;

    public MessageAdapter(Context context, List<ChatObject> mChat,String imageURL) {
        this.context = context;
        this.mChat = mChat;
        this.imageURL=imageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);

            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);

            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull  MessageAdapter.ViewHolder holder, int position) {
        final ChatObject mchat=mChat.get(position);
        if(mchat.getType().equals("text"))
        {

            holder.show_message.setVisibility(View.VISIBLE);
            holder.messageImage.setVisibility(View.GONE);

            holder.show_message.setText(mchat.getMessage());
        }
        else
        {
            holder.show_message.setVisibility(View.GONE);
            holder.messageImage.setVisibility(View.VISIBLE);
            holder.show_message.setText(mchat.getMessage());
            Glide.with(context).load(mchat.getMessage()).into(holder.messageImage);

        }

        if(imageURL.equals("default"))
        {
            holder.profile_image.setImageResource(R.drawable.ic_user);

        }
        else
        {
            Glide.with(context).load(imageURL).circleCrop().into(holder.profile_image);
        }



    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public  class  ViewHolder  extends RecyclerView.ViewHolder
    {
        public TextView show_message;
        public ImageView profile_image,messageImage;

        public  ViewHolder(View itemView)
        {
            super(itemView);
            show_message=itemView.findViewById(R.id.show_message);
            profile_image=itemView.findViewById(R.id.profile_Image);
            messageImage=itemView.findViewById(R.id.messageIv);

        }

    }

    @Override
    public int getItemViewType(int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid()))
        {
            return  MSG_TYPE_RIGHT;
        }
        else
        {
            return  MSG_TYPE_LEFT;
        }

    }
}
