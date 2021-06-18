package com.example.mymessage.fragments.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymessage.R;
import com.example.mymessage.User;
import com.example.mymessage.fragments.MessageActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> mUsers;

    public UserAdapter(Context context, List<User> mUsers) {
        this.context = context;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_user1,parent,false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user=mUsers.get(position);
        holder.username.setText(user.getUser());
        holder.about.setText(user.getAbout());
        String currenttime=new SimpleDateFormat("dd/MM/yyyy/HH.mm aa").format(new Date());
        if(user.getLastseen().substring(0,10).equals(currenttime.substring(0,10)))
        {
            holder.lastseen.setText(user.getLastseen().substring(11,user.getLastseen().toString().length()));
        }
        else
        {
            holder.lastseen.setText(user.getLastseen().substring(0,10));
        }

        if(user.getImageURL().equals("default"))
        {
            holder.imageView.setImageResource(R.drawable.ic_user);
        }
        else
        {
            Glide.with(context).load(user.getImageURL()).circleCrop().into(holder.imageView);
        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context, MessageActivity.class);
                intent.putExtra("userId",user.getUid());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public  class  ViewHolder  extends RecyclerView.ViewHolder
    {
        public TextView username,about,lastseen;
        public ImageView imageView;
        public LinearLayout linearLayout;

        public  ViewHolder(View itemView)
        {
            super(itemView);
            username=itemView.findViewById(R.id.name1);
            imageView=itemView.findViewById(R.id.imageView);
            about=itemView.findViewById(R.id.about);
            lastseen=itemView.findViewById(R.id.seen);
            linearLayout=itemView.findViewById(R.id.liniarLayout);

        }

    }

}
