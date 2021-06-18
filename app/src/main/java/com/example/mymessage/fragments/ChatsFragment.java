package com.example.mymessage.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mymessage.R;
import com.example.mymessage.User;
import com.example.mymessage.fragments.Adapter.ChatList;
import com.example.mymessage.fragments.Adapter.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private UserAdapter userAdapter;
    private List<User> mUser;

    FirebaseUser fuser;
    DatabaseReference mDb;
    private RecyclerView recyclerView;

    private List<ChatList> userList;


    public ChatsFragment() {
        // Required empty public constructor
    }


    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView=view.findViewById(R.id.recycler_View2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();
        mDb=FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());

        HashMap<String,Object> map=new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        map.put("lastseen", new SimpleDateFormat("dd/MM/yyyy/").format(new Date())+dateString);

        mDb.updateChildren(map);

        mDb= FirebaseDatabase.getInstance().getReference("ChatList").child(fuser.getUid());
        mDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                     String s= dataSnapshot.getKey().toString();
                     ChatList chatlist=new ChatList(s);
                     userList.add(chatlist);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return  view;
    }

    private void chatList() {

        mUser =new ArrayList<>();
        mDb=FirebaseDatabase.getInstance().getInstance().getReference("user");
        mDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    String phone = "";
                    String name = "";
                    String ImageURL = "";
                    String about="At work!";
                    String lastseen="Not avelable";

                    if (dataSnapshot.child("phone").getValue() != null) {
                        phone = dataSnapshot.child("phone").getValue().toString();
                    }
                    if (dataSnapshot.child("name").getValue() != null) {
                        name = dataSnapshot.child("name").getValue().toString();
                    }
                    if (dataSnapshot.child("ImageURL").getValue() != null) {
                        ImageURL = dataSnapshot.child("ImageURL").getValue().toString();
                    }
                    if(dataSnapshot.child("about").getValue()!=null)
                    {
                        about=dataSnapshot.child("about").getValue().toString();
                    }
                    if (dataSnapshot.child("lastseen").getValue() != null) {
                        lastseen = dataSnapshot.child("lastseen").getValue().toString();
                    }
                    User user=new User(dataSnapshot.getKey().toString(),name,phone,ImageURL,about,lastseen);
                    for (ChatList chatList: userList)
                    {
//                        User user1=new User(chatList.getId(),chatList.getId(),phone,ImageURL);
//                        mUser.add(user1);
                        if(user.getUid().equals(chatList.getId()))
                        {
                            mUser.add(user);
                        }
                    }

                }
                userAdapter =new UserAdapter(getContext(),mUser);
                recyclerView.setAdapter(userAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}