package com.philimonnag.godcentral;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philimonnag.godcentral.databinding.ActivityStatusBinding;
import com.philimonnag.godcentral.model.Status;
import com.philimonnag.godcentral.model.UserStatus;
import com.philimonnag.godcentral.model.Users;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StatusActivity extends AppCompatActivity {
    ActivityStatusBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference documentReference;
    String userId;
    String uName,url;
    FirebaseDatabase database;
    Uri statusUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();

        documentReference = db.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                uName= value.getString("uName");
                url=value.getString("url");

            }
        });
        binding.setimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 75);
            }
        });
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusUpload();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if(data.getData() != null) {
                statusUri=data.getData();
                Picasso.get().load(statusUri).into(binding.setimage);
            }
        }
    }

public void statusUpload(){
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Date date = new Date();
    StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");

    reference.putFile(statusUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete( Task<UploadTask.TaskSnapshot> task) {
            if(task.isSuccessful()) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserStatus userStatus = new UserStatus();
                        userStatus.setuName(uName);
                        userStatus.setUrl(url);
                        userStatus.setLastUpdated(date.getTime());

                        HashMap<String, Object> obj = new HashMap<>();
                        obj.put("uName", userStatus.getuName());
                        obj.put("url", userStatus.getUrl());
                        obj.put("lastUpdated", userStatus.getLastUpdated());
                        String imageUrl = uri.toString();
                        Status status = new Status(imageUrl, userStatus.getLastUpdated());
//                        documentReference = db.collection("stories").document(userId);
//                        documentReference.set(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull @NotNull Task<Void> task) {
//                                if(task.isSuccessful()){
//                                    Toast.makeText(StatusActivity.this, "God blees You", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                        documentReference = db.collection("stories").document(userId).collection("statuses").document();
//                        documentReference.set(status).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull @NotNull Task<Void> task) {
//                                if(task.isSuccessful()){
//                                    Toast.makeText(StatusActivity.this, "God blees You", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });

                        database.getReference()
                                .child("stories")
                                .child(userId)
                                .updateChildren(obj);

                        database.getReference().child("stories")
                                .child(userId)
                                .child("statuses")
                                .push()
                                .setValue(status);
                        startActivity(new Intent(StatusActivity.this,StoryActivity.class));

                    }
                });
            }
        }
    });
}
}