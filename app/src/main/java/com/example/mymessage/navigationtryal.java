package com.example.mymessage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.mymessage.fragments.Adapter.ProfileFragment;
import com.example.mymessage.fragments.ChatsFragment;
import com.example.mymessage.fragments.UserFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class navigationtryal extends AppCompatActivity {
    AnimatedBottomBar animatedBottomBar;
    FragmentManager fragmantmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_navigationtryal);
        animatedBottomBar =findViewById(R.id.bottom_bar);
        
        UpdateToken();
        if(savedInstanceState==null)
        {
            animatedBottomBar.selectTabById(R.id.tab_home,true);
            fragmantmanager=getSupportFragmentManager();
            ChatsFragment chatsFragment=new ChatsFragment();
            fragmantmanager.beginTransaction().replace(R.id.fragment_container,chatsFragment).commit();
        }
        animatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, @Nullable AnimatedBottomBar.Tab lasttab, int newIndex, @NotNull AnimatedBottomBar.Tab newTab) {
                Fragment fragment=null;
                switch (newTab.getId())
                {
                    case R.id.tab_home:
                        fragment=new ChatsFragment();
                        break;
                    case  R.id.tab_User:
                        fragment=new UserFragment();
                        break;
                    case  R.id.tab_profile:
                        fragment=new ProfileFragment();
                        break;
                }
                if(fragment!=null)
                {
                    fragmantmanager=getSupportFragmentManager();
                    fragmantmanager.beginTransaction().replace(R.id.fragment_container,fragment).commit();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Error in Load",Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {

            }
        });

    }

    private void UpdateToken() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshtoken= FirebaseInstanceId.getInstance().getToken();
        HashMap<String,Object> map=new HashMap<>();
        map.put("notificationKey",refreshtoken);
        FirebaseDatabase.getInstance().getReference("user").child(firebaseUser.getUid()).updateChildren(map);
    }
}