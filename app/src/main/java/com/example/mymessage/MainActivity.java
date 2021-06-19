package com.example.mymessage;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.ContactsContract;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private EditText number, code;
    private Button send;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mverification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        FirebaseApp.initializeApp(this);
        userIsLoggedIn();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        setContentView(R.layout.activity_main);
        number = findViewById(R.id.number);
        send = findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s="";
                if(!String.valueOf(number.getText().toString().charAt(0)).equals("+"))
                {
                    s+="+91"+number.getText().toString();
                }
                Intent intent = new Intent(MainActivity.this, otpverifiation.class);
                intent.putExtra("number", s);
                startActivity(intent);
                finish();

//                if (mverification != null) {
////                    verifyPhoneNumberWithCode(mverification, code.getText().toString());
//                    Intent intent = new Intent(MainActivity.this, otpverifiation.class);
//                    intent.putExtra("number", mverification);
//                    startActivity(intent);
//                    finish();
//
//                } else
//                {
//                    sendVerification();
//                    Intent intent = new Intent(MainActivity.this, otpverifiation.class);
//                    intent.putExtra("number", mverification);
//                    startActivity(intent);
//                    finish();
//
//                }

//                Intent intent = new Intent(MainActivity.this, otpverifiation.class);
//                intent.putExtra("number", number.getText().toString());
//                startActivity(intent);


            }

        });


//        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            @Override
//            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                signwithPhoneCredential(phoneAuthCredential);
//            }
//
//            @Override
//            public void onVerificationFailed(@NonNull FirebaseException e) {
//                Toast.makeText(getApplicationContext(), "Vefication Failed", Toast.LENGTH_SHORT);
//            }
//
//            @Override
//            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                super.onCodeSent(s, forceResendingToken);
//                mverification = s;
//                Toast.makeText(getApplicationContext(), "OTP was sent", Toast.LENGTH_SHORT);
//            }
//        };
    }


//        private void verifyPhoneNumberWithCode (String verification, String mCode)
//        {
//            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification, mCode);
//            signwithPhoneCredential(credential);
//        }
//
//        private void signwithPhoneCredential (PhoneAuthCredential phoneAuthCredential){
//            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    if (task.isSuccessful()) {
//
//                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                        if (user != null) {
//                            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
//                            send.setText(user.getUid());
//                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    if (!snapshot.exists()) {
//                                        Map<String, Object> usermap = new HashMap<>();
//                                        usermap.put("phone", user.getPhoneNumber());
//                                        usermap.put("name", user.getPhoneNumber());
//                                        usermap.put("ImageURL", "default");
//                                        usermap.put("about", "Hey! I am Using My message app");
//                                        mDatabase.updateChildren(usermap);
//                                    }
//                                    userIsLoggedIn();
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                        }
//                    }
//
//                }
//            });
//        }

        private void userIsLoggedIn () {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                startActivity(new Intent(getApplicationContext(), navigationtryal.class));
                finish();
                return;
            }
        }
        ;


//        private void sendVerification () {
//            PhoneAuthProvider.getInstance().verifyPhoneNumber(number.getText().toString(), 60, TimeUnit.SECONDS, this, mCallbacks);
//        }



}