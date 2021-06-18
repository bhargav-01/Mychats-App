package com.example.mymessage.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mymessage.BuildConfig;
import com.example.mymessage.R;
import com.example.mymessage.User;
import com.example.mymessage.chat.ChatObject;
import com.example.mymessage.fragments.Adapter.MessageAdapter;
import com.example.mymessage.fragments.Adapter.sendNotification;
import com.example.mymessage.navigationtryal;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;
import com.sothree.slidinguppanel.ScrollableViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class MessageActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 101;
    SharedPreferences sharedpreferences;
    public static final int CAMERA_PER_CODE = 101;
    private static String currentPhotoPath;
    ImageView imageView;
    TextView textView,lastseen;
    Intent intent;
    RecyclerView recyclerView;
    FirebaseUser fuser;
    EditText msg_editText;
    String useid;
    Button sendBtn;
    ImageButton back;
    ImageButton attachBtn;
    DatabaseReference mDb;
    MessageAdapter messageAdapter;
    List<ChatObject> mChat;
    Uri selectedImage=null;
    public File photoFile;
    private StorageTask upLoadTask;
    //Encryption
    KeyGenerator keyGenerator;
    SecretKey secretKey;
    byte[] IV = new byte[16];
    SecureRandom random;
    String publicKey="";




//    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_message);


        imageView=findViewById(R.id.imageView2);
        textView=findViewById(R.id.textView);
        lastseen=findViewById(R.id.lastseen);
        sendBtn=findViewById(R.id.btn_send);
        attachBtn=findViewById(R.id.sendImage);
        msg_editText=findViewById(R.id.text_send);
        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, navigationtryal.class));
                finish();
            }
        });

        //encryption:
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGenerator.init(256);
        secretKey = keyGenerator.generateKey();
        sharedpreferences = getSharedPreferences("RSAKEY",Context.MODE_PRIVATE);
        if(!sharedpreferences.contains("privateKey"))
        {
            KeyPairGenerator keyPairGenerator = null;
            try {
                keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            keyPairGenerator.initialize(1024); // key length
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            String privateKeyString;
            String publicKeyString;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                privateKeyString=  Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
                publicKeyString=Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            } else {
                privateKeyString = android.util.Base64.encodeToString(keyPair.getPrivate().getEncoded(), android.util.Base64.DEFAULT);
                publicKeyString = android.util.Base64.encodeToString(keyPair.getPublic().getEncoded(), android.util.Base64.DEFAULT);
            }
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("privateKey",privateKeyString);
            editor.putString("publicKey",publicKeyString);
            editor.apply();

            DatabaseReference dr=FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            HashMap<String,Object> map=new HashMap<>();
            map.put("publicKey",publicKeyString);
            dr.updateChildren(map);
        }
//        random = new SecureRandom();
//        random.nextBytes(IV);
        //end encryption intialization

        recyclerView=findViewById(R.id.recycler_View);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent=getIntent();
        useid=intent.getStringExtra("userId");

        fuser=FirebaseAuth.getInstance().getCurrentUser();
        mDb= FirebaseDatabase.getInstance().getReference("user").child(useid);
        mDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    String phone = "";
                    String name = "";
                    String ImageURL = "";


                    if (snapshot.child("name").getValue() != null) {
                        name = snapshot.child("name").getValue().toString();
                    }
                    if (snapshot.child("ImageURL").getValue() != null) {
                        ImageURL = snapshot.child("ImageURL").getValue().toString();
                    }
                    if (snapshot.child("publicKey").getValue() != null) {
                        publicKey = snapshot.child("publicKey").getValue().toString();
                    }
                    if (snapshot.child("lastseen").getValue() != null) {
                        String s=snapshot.child("lastseen").getValue().toString();
                        String currenttime=new SimpleDateFormat("dd/MM/yyyy/hh.mm aa" ).format(new Date());
                        if(s.substring(0,10).equals(currenttime.substring(0,10)))
                        {
//
                            lastseen.setText(s.substring(11,s.length()));
                        }
                        else
                        {
                            lastseen.setText(s.substring(0,10));
                        }
//                        lastseen.setText(snapshot.child("lastseen").getValue().toString());
                    }

                    textView.setText(name);
                    if (ImageURL.equals("default")) {
                        imageView.setImageResource(R.drawable.ic_user);
                    } else {
                        Glide.with(MessageActivity.this).load(ImageURL).circleCrop().into(imageView);
                    }

                    readmessage(fuser.getUid(),useid,snapshot.child("ImageURL").getValue().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                 String msg1=msg_editText.getText().toString();
                byte[] bs = new byte[0];
                try {
                    bs = msg1.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        msg1=  Base64.getEncoder().encodeToString(encrypt(bs,secretKey));
                    } else {
                        msg1=android.util.Base64.encodeToString(encrypt(bs,secretKey), android.util.Base64.DEFAULT);
                    }
                    //msg1 = Base64.getEncoder().encodeToString(encrypt(bs,secretKey));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final String msg=msg1;
                ;


                if(!msg.equals(""))
                 {
                     sendMessage(fuser.getUid(),useid,msg);
                     final DatabaseReference mdb=FirebaseDatabase.getInstance().getReference("user");
                     mdb.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             if(snapshot!=null)
                             {
                                 Toast.makeText(getApplicationContext(),snapshot.child("notificationKey").getValue().toString(),Toast.LENGTH_SHORT).show();

                                 try {
                                     sendNotifications(msg, snapshot.child(fuser.getUid()).child("name").getValue().toString(), snapshot.child(useid).child("notificationKey").getValue().toString());
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }
                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {

                         }
                     });

                 }
                 else
                 {

                 }
                 msg_editText.setText("");
            }
        });

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(MessageActivity.this);
                final DatabaseReference mdb=FirebaseDatabase.getInstance().getReference("user");
                mdb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot!=null)
                        {
                            //Toast.makeText(getApplicationContext(),snapshot.child("notificationKey").getValue().toString(),Toast.LENGTH_SHORT).show();

                            try {
                                sendNotifications("Send you image", snapshot.child(fuser.getUid()).child("name").getValue().toString(), snapshot.child(useid).child("notificationKey").getValue().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    askpermission();

                }
                else if (options[item].equals("Choose from Gallery")) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, 1);
                }
                else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }

            }
        });
                builder.show();
    }

    private void askpermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, CAMERA_PER_CODE);
        }
        else
        {
            openCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CAMERA_PER_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                openCamera();
            }
            else {
                Toast.makeText(this,"Camera permission is REquired to use Camera",Toast.LENGTH_SHORT).show();
            }
        }
    }



//    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage(String uid, final String useid, final String msg) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> newMeaaseMap=new HashMap<>();
        newMeaaseMap.put("sender",uid);
        newMeaaseMap.put("receiver",useid);
        newMeaaseMap.put("message",msg);
        newMeaaseMap.put("type","text");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           newMeaaseMap.put("AES", Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        } else {
            newMeaaseMap.put("AES",android.util.Base64.encodeToString(secretKey.getEncoded(), android.util.Base64.DEFAULT));
        }
        //newMeaaseMap.put("AES", Base64.getEncoder().encodeToString(secretKey.getEncoded()));

        reference.child("Chats").push().setValue(newMeaaseMap);
        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("ChatList").child(fuser.getUid()).child(useid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    chatRef.child("id").setValue(useid);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private  void readmessage(final String myid, final String userid, final String imageurl)
    {
        mChat=new ArrayList<>();
        mDb=FirebaseDatabase.getInstance().getReference("Chats");
        mDb.addValueEventListener(new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                String sender="";
                String receiver="";
                String message="";
                String type="text";
                    for(DataSnapshot childsnapshot:dataSnapshot.getChildren() )
                    {
                        if(childsnapshot.child("sender").getValue()!=null)
                        {
                            sender=childsnapshot.child("sender").getValue().toString();
                        }
                        if(childsnapshot.child("receiver").getValue()!=null)
                        {
                            receiver=childsnapshot.child("receiver").getValue().toString();
                        }
                        if(childsnapshot.child("message").getValue()!=null)
                        {
                            if(childsnapshot.child("AES").getValue()!=null)
                            {
//
                                byte[] br= new byte[0];
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                {
                                    try {
                                        br = Base64.getDecoder().decode(childsnapshot.child("message").getValue().toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                       br=android.util.Base64.decode(childsnapshot.child("message").getValue().toString(), android.util.Base64.DEFAULT);
                                }
                                byte[] decodedKey;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                {
                                    decodedKey = Base64.getDecoder().decode(childsnapshot.child("AES").getValue().toString());
                                }
                                else {
                                    decodedKey=android.util.Base64.decode(childsnapshot.child("AES").getValue().toString(),android.util.Base64.DEFAULT);
                                }

                                //byte[] decodedKey = Base64.getDecoder().decode(childsnapshot.child("AES").getValue().toString());
                                SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");


                                try {
                                    message = decrypt(br, originalKey);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            else
                            {
                                message=childsnapshot.child("message").getValue().toString();
                            }

                        }
                        if(childsnapshot.child("type").getValue()!=null)
                        {
                            type=childsnapshot.child("type").getValue().toString();
                        }

                        ChatObject chat=new ChatObject(sender,receiver,message,type);

                            if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid) )
                            {
                               mChat.add(chat);
                            }
                            messageAdapter =new MessageAdapter(MessageActivity.this,mChat,imageurl);
                            recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void sendNotifications(String message, String heading, String notificationKey) throws JSONException {
        JSONObject notificationObject=new JSONObject(
                "{'contents':{'en':'" + message + "'},"+
                        "'include_player_ids':['" + notificationKey + "'],"+
                        "'headings':{'en':'" + heading + "'}}");
        OneSignal.postNotification(notificationObject,null);
    }



    private void sendImageMessage(Uri selectedImage) throws IOException {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Sending image...");
        progressDialog.show();
        if(selectedImage !=null)
        {
            String fileNAmePath="ChatImages/"+"post_"+System.currentTimeMillis();

            Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
            byte[] data=baos.toByteArray();
            final StorageReference filleReference= FirebaseStorage.getInstance().getReference().child(fileNAmePath);

            upLoadTask=filleReference.putBytes(data);
            upLoadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw  task.getException();
                    }
                    return filleReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        progressDialog.hide();
                        progressDialog.dismiss();

                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
                        HashMap<String,Object> newMeaaseMap=new HashMap<>();
                        newMeaaseMap.put("sender",fuser.getUid());
                        newMeaaseMap.put("receiver",useid);
                        newMeaaseMap.put("message",mUri);
                        newMeaaseMap.put("type","image");


                        reference.child("Chats").push().setValue(newMeaaseMap);

                        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("ChatList").child(fuser.getUid()).child(useid);
                        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists())
                                {
                                    chatRef.child("id").setValue(useid);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                    else {
                        Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(MessageActivity.this,"NO Image Selected",Toast.LENGTH_SHORT).show();
        }
    }

    private void openCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI = FileProvider.getUriForFile(MessageActivity.this,
                        "com.example.mymessage.fileprovider", photoFile);
                Log.d("Errora",photoFile.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 0);
            }
        }

//        startActivityForResult(takePictureIntent, 0);

    }


    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(new Date());
        String pictureFile = "Mymessage_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            File f = new File(currentPhotoPath);
            selectedImage=Uri.fromFile(f);
            Toast.makeText(getApplicationContext(),selectedImage.toString(),Toast.LENGTH_SHORT).show();
            try {
                sendImageMessage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            if (resultCode != RESULT_CANCELED) {
                switch (requestCode) {
                    case 0:
                        if (resultCode == RESULT_OK && data != null) {
                            File f = new File(currentPhotoPath);
                            selectedImage=Uri.fromFile(f);
                            Toast.makeText(getApplicationContext(),selectedImage.toString(),Toast.LENGTH_SHORT).show();
                            try {
                                sendImageMessage(selectedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        break;
                    case 1:
                        if (resultCode == RESULT_OK && data != null) {
                            selectedImage = data.getData();
                            try {
                                sendImageMessage(selectedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }
        }

    }
    //encryption method:
    public static byte[] encrypt(byte[] plaintext, SecretKey key) throws Exception
    {
        Cipher cipher = Cipher.getInstance("AES");
        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        IvParameterSpec ips = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec,ips);
        byte[] cipherText = cipher.doFinal(plaintext);
        return cipherText;
    }

//    public static byte[] RSAencrypt(byte[] plaintext, SecretKey key) throws Exception
//    {
//        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        PublicKey publicKey = keyFactory.generatePublic(publicSpec);
//
//        // 6. encrypt secret key using public key
//        Cipher cipher2 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
//        cipher2.init(Cipher.ENCRYPT_MODE, publicKey);
//        String encryptedSecretKey = Base64.encodeToString(cipher2.doFinal(secretKey.getEncoded()), Base64.DEFAULT);
//    }

    public static String decrypt(byte[] cipherText, SecretKey key)
    {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ips = new IvParameterSpec(iv);
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec,ips);
            byte[] decryptedText = cipher.doFinal(cipherText);
            return new String(decryptedText,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    private void setSupportActionBar(Toolbar toolbar) {
    }
}