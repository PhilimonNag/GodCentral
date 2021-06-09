package com.philimonnag.godcentral;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.philimonnag.godcentral.databinding.ActivityPreachAddBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PreachAdd extends AppCompatActivity {
  ActivityPreachAddBinding binding;
  FirebaseAuth mAuth;
  FirebaseFirestore db;
  DocumentReference documentReference;
    String url,uName,userId;
    SimpleDateFormat time;
    String timeStamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityPreachAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        time=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        timeStamp=time.format(new Date());
        mAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId  =  mAuth.getCurrentUser().getUid();

        documentReference = db.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                uName= value.getString("uName");
                url=value.getString("url");

            }
        });
        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String preach = binding.preachFor.getEditText().getText().toString();
                if(TextUtils.isEmpty(preach)){
                    binding.preachFor.setError(" Prayer Required");
                }else{documentReference = db.collection("Preachings").document();
                    Map<String,Object> amen= new HashMap<>();
                    amen.put("preach",preach);
                    amen.put("uName",uName);
                    amen.put("url",url);
                    amen.put("timeStamp",timeStamp);
                    documentReference.set(amen).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PreachAdd.this, "God bless you", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PreachAdd.this,MainActivity.class));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(PreachAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}