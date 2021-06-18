package com.example.mymessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.onesignal.OneSignal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OtpTextView;

public class otpverifiation extends AppCompatActivity {
    private Button send;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private  String mverification,code="";
    public  OtpTextView otpTextView;
    public String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FirebaseApp.initializeApp(this);
        userIsLoggedIn();
        setContentView(R.layout.activity_otpverifiation);
        Intent intent=getIntent();
        number=intent.getStringExtra("number");

        otpTextView= findViewById(R.id.otp_view);;
        send=findViewById(R.id.sendotp);
//        send.setText(number);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mverification != null) {
                    code=otpTextView.getOTP();
                    verifyPhoneNumberWithCode(mverification, code.toString());
                }

            }

        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signwithPhoneCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getApplicationContext(), "Vefication Failed", Toast.LENGTH_SHORT);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mverification = s;
                otpTextView.setOTP(s);
                Toast.makeText(getApplicationContext(), "OTP was sent", Toast.LENGTH_SHORT);
            }
        };
        if(mverification==null)
            sendVerification();

    }

    private void verifyPhoneNumberWithCode(String verification, String mCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification, mCode);
        signwithPhoneCredential(credential);
    }

    private void signwithPhoneCredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    otpTextView.setOTP(mverification);
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) {
                                    Map<String, Object> usermap = new HashMap<>();
                                    usermap.put("phone", user.getPhoneNumber());
                                    usermap.put("name", user.getPhoneNumber());
                                    usermap.put("ImageURL", "default");
                                    usermap.put("about", "Hey! I am Using My message app");
                                    mDatabase.updateChildren(usermap);
                                }
                                userIsLoggedIn();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

            }
        });
    }

    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            OneSignal.startInit(this)
                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                    .unsubscribeWhenNotificationsAreDisabled(true)
                    .init();
            Toast.makeText(getApplicationContext(),"Account create succesfully",Toast.LENGTH_LONG);
            startActivity(new Intent(getApplicationContext(), navigationtryal.class));
            finish();
            return;
        }
    }


    private void sendVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, this, mCallbacks);
    }
}




