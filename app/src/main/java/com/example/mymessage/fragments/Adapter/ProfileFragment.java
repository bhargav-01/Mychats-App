package com.example.mymessage.fragments.Adapter;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mymessage.MainActivity;
import com.example.mymessage.R;
import com.example.mymessage.Utils.Editprofile;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {


    TextView textView,textphone,textabout;
    ImageView imageView;
    DatabaseReference mDb;
    FirebaseUser fuser;
    ImageButton logout,edit;
    StorageReference storage;
    private  static  final int IMAGE_REQUEST=1;
    private  static  final int REQUEST_GALLERY_IMAGE=2;
    private Uri imageUri;
    private StorageTask upLoadTask;
    Switch aSwitch;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        imageView=view.findViewById(R.id.profile_Image2);
        textView=view.findViewById(R.id.username);
        logout=view.findViewById(R.id.logout);
//        textphone=view.findViewById(R.id.phone);
//        textabout=view.findViewById(R.id.about);
         aSwitch=view.findViewById(R.id.notification);
         edit=view.findViewById(R.id.edit);
         aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if(!isChecked)
                 {
                     OneSignal.setSubscription(false);
                 }
                 else
                 {
                     OneSignal.setSubscription(true);
                 }

             }
         });

         edit.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(getContext(), Editprofile.class));
             }
         });

         logout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 AlertDialog.Builder builder = new AlertDialog.Builder(
                         getContext());
                 builder.setTitle("Log out");
                 builder.setMessage("Do you want to Log out?");
                 builder.setNegativeButton("NO",
                         new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog,
                                                 int which) {

                             }
                         });
                 builder.setPositiveButton("YES",
                         new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog,
                                                 int which) {
                                 OneSignal.setSubscription(false);
                                 FirebaseAuth.getInstance().signOut();
                                 startActivity(new Intent(getContext(), MainActivity.class));
                             }
                         });


                 builder.show();
             }
         });
        storage=FirebaseStorage.getInstance().getReference("uploads");

//        Button save=view.findViewById(R.id.save);

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        mDb= FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());
        mDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phone = "";
                String name = "";
                String ImageURL = "";
                String about="";

                if (snapshot.child("phone").getValue() != null) {
                    phone = snapshot.child("phone").getValue().toString();
                }
                if (snapshot.child("about").getValue() != null) {
                    about = snapshot.child("about").getValue().toString();
                }
                if (snapshot.child("name").getValue() != null) {
                    name = snapshot.child("name").getValue().toString();
                }
                if (snapshot.child("ImageURL").getValue() != null) {
                    ImageURL = snapshot.child("ImageURL").getValue().toString();
                }
                //User user=new User(snapshot.getKey().toString(),name,phone,ImageURL);
//                textphone.setText(phone);
                textView.setText(name);
//                textabout.setText(about);
                if (ImageURL.equals("default")) {
                    imageView.setImageResource(R.drawable.ic_user);
                } else {
                    Glide.with(ProfileFragment.this).load(ImageURL).circleCrop().into(imageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(textView.getText()!=null)
//                {
//                    mDb=FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());
//
//                    HashMap<String,Object> map=new HashMap<>();
//                    map.put("name",textView.getText().toString());
//                    map.put("about",textabout.getText().toString());
//
//                    mDb.updateChildren(map);
//
//                    Toast.makeText(getContext(),"Save Succesfully",Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//        });



//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE);
//            }
//        });
        return  view;
    }

    private String getFileExtentatin(Uri uri)
    {
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private  void UploadMyImage() throws IOException
    {
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if(imageUri !=null)
        {
            Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(),imageUri);
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
            byte[] data=baos.toByteArray();
//            progressDialog.dismiss();
            final StorageReference filleReference= storage.child(System.currentTimeMillis()+"."+getFileExtentatin(imageUri));
            upLoadTask=filleReference.putBytes(data);
//            progressDialog.hide();
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

                        mDb = FirebaseDatabase.getInstance().getReference("user").child(fuser.getUid());

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("ImageURL", mUri);
                        mDb.updateChildren(map);


                    } else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(getContext(),"NO Image Selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_GALLERY_IMAGE && resultCode==RESULT_OK && data !=null && data.getData()!=null)
        {

           imageUri=data.getData();
            Uri u=imageUri;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + ".jpg";

            imageUri=Uri.fromFile(new File(getActivity().getCacheDir(), imageFileName));
            UCrop.of(u,imageUri)
                    .withAspectRatio(50, 50)
                    .start(getContext(), this);

//            CropImage.activity()
//                    .start(getContext(), this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
             imageUri = UCrop.getOutput(data);
            if(upLoadTask!=null && upLoadTask.isInProgress())
            {
                Toast.makeText(getContext(),"Upload in progress...",Toast.LENGTH_SHORT).show();
            }
            else
            {
                try {
                    UploadMyImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(getContext(), imageUri.toString(), Toast.LENGTH_SHORT).show();
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }


    }

}