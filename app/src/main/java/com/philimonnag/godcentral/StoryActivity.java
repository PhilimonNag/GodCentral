package com.philimonnag.godcentral;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.philimonnag.godcentral.Adapters.PreachAdapter;
import com.philimonnag.godcentral.Adapters.UserStatusAdapter;
import com.philimonnag.godcentral.databinding.ActivityStatusBinding;
import com.philimonnag.godcentral.databinding.ActivityStoryBinding;
import com.philimonnag.godcentral.model.Status;
import com.philimonnag.godcentral.model.UserStatus;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StoryActivity extends AppCompatActivity {
   ActivityStoryBinding binding;
   FirebaseFirestore db;
   DocumentReference documentReference;
   FirebaseDatabase database;
   FirebaseAuth mAuth;
   ArrayList<UserStatus>userStatusArrayList;
   ArrayList<Status>statusArrayList;
   UserStatusAdapter userStatusAdapter;
    Status sampleStatus;
    ArrayList<Status> statuses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        UserStatus status = new UserStatus();
        userStatusArrayList= new ArrayList<>();
        database =FirebaseDatabase.getInstance();
        sampleStatus = new Status();
        statusArrayList=new ArrayList<>();
        statuses = new ArrayList<>();
        binding.story.setHasFixedSize(true);
        binding.story.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        userStatusAdapter= new UserStatusAdapter(this,userStatusArrayList);
        binding.story.setAdapter(userStatusAdapter);
        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    userStatusArrayList.clear();
                    for(DataSnapshot storySnapshot : snapshot.getChildren()) {
                        status.setuName(storySnapshot.child("uName").getValue(String.class));
                        status.setUrl(storySnapshot.child("url").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));
                        for(DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()) {
                            sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }
                        status.setStatuses(statuses);
                        userStatusArrayList.add(status);
                    }
                    userStatusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//
//        db.collection("stories").get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if(!queryDocumentSnapshots.isEmpty()){
//                            List<DocumentSnapshot> list =queryDocumentSnapshots.getDocuments();
//                            for (int i =0;i<list.size();i++){
//                                DocumentSnapshot documentSnapshot= list.get(i);
//                                userStatus.setUrl(documentSnapshot.getString("url"));
//                                userStatus.setuName(documentSnapshot.getString("uName"));
//                                userStatusArrayList.add(userStatus);
//                            }
//                            userStatusAdapter.notifyDataSetChanged();
//                        }else {
//                            Toast.makeText(StoryActivity.this, "I got Failed...", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull @NotNull Exception e) {
//                Toast.makeText(StoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//

    }
}