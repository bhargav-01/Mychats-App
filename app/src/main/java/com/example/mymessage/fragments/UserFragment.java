package com.example.mymessage.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mymessage.R;
import com.example.mymessage.User;
import com.example.mymessage.fragments.Adapter.UserAdapter;
import com.example.mymessage.fragments.Adapter.divider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUser;
    private  ArrayList<User> userslist,contactlist;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    public UserFragment() {


    }


    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        View view= inflater.inflate(R.layout.fragment_user,container,false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));


        userslist=new ArrayList<>();
        contactlist=new ArrayList<>();
        userAdapter=new UserAdapter(getContext(),userslist);
        recyclerView.setAdapter(userAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            getContactList();
        }

        return  view;
    }




    private  void getContactList()
    {
        Cursor phone=getActivity().getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while(phone.moveToNext())
        {
            String Name=phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number=phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(!String.valueOf(number.charAt(0)).equals("+"))
            {
                number="+91"+number;
            }
            User u=new User("",Name,number,"default");

            contactlist.add(u);
            getsUserDetails(u);


        }
    }

    private void getsUserDetails(User u) {
        DatabaseReference mDb= FirebaseDatabase.getInstance().getReference().child("user");
        Query query=mDb.orderByChild("phone").equalTo(u.getPhoneNumber());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!=null)
                {
                    String phone="";
                    String name="";
                    String ImageURL="";
                    String about="At work!";
                    String lastseen="Not avelable";
                    for(DataSnapshot childsnapshot:snapshot.getChildren() )
                    {
                        if(childsnapshot.child("phone").getValue()!=null)
                        {
                            phone=childsnapshot.child("phone").getValue().toString();
                        }
                        if(childsnapshot.child("name").getValue()!=null)
                        {
                            name=childsnapshot.child("name").getValue().toString();
                        }
                        if(childsnapshot.child("ImageURL").getValue()!=null)
                        {
                            ImageURL=childsnapshot.child("ImageURL").getValue().toString();
                        }
                        if(childsnapshot.child("about").getValue()!=null)
                        {
                            about=childsnapshot.child("about").getValue().toString();
                        }
                        if(childsnapshot.child("lastseen").getValue()!=null)
                        {
                            lastseen=childsnapshot.child("lastseen").getValue().toString();
                        }
                        User mUser=new User(childsnapshot.getKey(),name,phone,ImageURL,about,lastseen);
                        if(name.equals(phone))
                        {
                            for(User mC: contactlist)
                            {
                                if(mC.getPhoneNumber().equals(mUser.getPhoneNumber()))
                                {
                                    mUser.setUser(mC.getUser());
                                }
                            }
                        }
                        userslist.add(mUser);
                        userAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getContactList();
            } else {
                Toast.makeText(getContext(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkPermission(Context context, String Permission) {
        if (ContextCompat.checkSelfPermission(context,
                Permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}