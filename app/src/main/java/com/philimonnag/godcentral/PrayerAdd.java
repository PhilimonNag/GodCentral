package com.philimonnag.godcentral;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.renderscript.ScriptGroup;
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
import com.philimonnag.godcentral.databinding.ActivityPrayerAddBinding;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PrayerAdd extends AppCompatActivity {
    private ActivityPrayerAddBinding binding;
    FirebaseAuth mAuth;
    DocumentReference documentReference;
    FirebaseFirestore db;
    String url,toall,uName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPrayerAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar= getSupportActionBar();
        actionBar.hide();
        mAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String userId  =  mAuth.getCurrentUser().getUid();


        documentReference = db.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                binding.gender.setText(value.getString("gender"));
                toall=value.getString("gender");
                uName= value.getString("uName");
                url=value.getString("url");
            }
        });
        binding.gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toall=toall;
            }
        });
        binding.All.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toall="all";
            }
        });
        binding.submmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prayer= binding.prayertext.getEditText().getText().toString();
                if(TextUtils.isEmpty(prayer)){
                    binding.prayertext.setError(" Prayer Required");
                }else{documentReference=db.collection("prayers").document(userId);
                    Map<String,Object> amen= new HashMap<>();
                    amen.put("prayer",prayer);
                    amen.put("whom",toall);
                    amen.put("uName",uName);
                    amen.put("url",url);
                    documentReference.set(amen).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PrayerAdd.this, "God bless you", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(PrayerAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });


    }
}