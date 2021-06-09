package com.philimonnag.godcentral.ui.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philimonnag.godcentral.MainActivity;
import com.philimonnag.godcentral.databinding.ActivityRegisterBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    ActivityRegisterBinding binding;
    StorageReference storageRef;
    UploadTask uploadTask;
    DocumentReference documentReference;
    Uri imgUri;
    String gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        binding= ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        binding.male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender="Male";
            }
        });
        binding.female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender="female";
            }
        });
        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            picImage();
            }
        });
    }

    private void register() {
        String uName=binding.userName.getEditText().getText().toString();
        String email=binding.email.getEditText().getText().toString();
        String password=binding.password.getEditText().getText().toString();
        if(TextUtils.isEmpty(email)){
            binding.email.setError("Email is Required");
        }else if(TextUtils.isEmpty(password)){
            binding.password.setError("Password is Required");
        }else if(password.length()<6){
            binding.password.setError("Password Length Must be greater than 6");
        }else {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Thank You", Toast.LENGTH_SHORT).show();
                            UploadUserData(uName,email);
                        }else {
                            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

      }
    }

    private void UploadUserData(String uName, String email) {
         String userid=mAuth.getCurrentUser().getUid();
         documentReference = db.collection("users").document(userid);
         storageRef = FirebaseStorage.getInstance().getReference("Profile Images").child(System.currentTimeMillis()+"."+getFileExt(imgUri));
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //here you can choose quality factor in third parameter(ex. i choosen 25)
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] fileInBytes = baos.toByteArray();

         uploadTask=storageRef.putBytes(fileInBytes);
         Task<Uri>uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
             @Override
             public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                 if (!task.isSuccessful()) {
                     throw task.getException();
                 }
                 return storageRef.getDownloadUrl();}
         }).addOnCompleteListener(new OnCompleteListener<Uri>() {
             @Override
             public void onComplete(@NonNull @NotNull Task<Uri> task) {
                 if(task.isSuccessful()){
                     Uri downloadUri=task.getResult();
                     String url=downloadUri.toString();
                     // Create a new Users with a first and last name
                     Map<String, Object> profile = new HashMap<>();
                     profile.put("uName", uName);
                     profile.put("email", email);
                     profile.put("url",url);
                     profile.put("gender",gender);

                     // Add a new document with a generated ID
                     documentReference.set(profile)
                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void unused) {
                                     Toast.makeText(RegisterActivity.this, "God bless You", Toast.LENGTH_SHORT).show();
                                     startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                                 }
                             });
                 }else {
                     Toast.makeText(RegisterActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                     startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                 }
             }
         });

    }


    private void picImage() {
        Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
           if(requestCode==1 &&resultCode== Activity.RESULT_OK && data!=null && data.getData()!=null){
               imgUri=data.getData();
               Picasso.get().load(imgUri).into(binding.imageView);
           }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

}


